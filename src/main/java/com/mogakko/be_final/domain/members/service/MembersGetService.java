package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.domain.friendship.util.FriendshipServiceUtilMethod;
import com.mogakko.be_final.domain.members.dto.response.BestMembersResponseDto;
import com.mogakko.be_final.domain.members.dto.response.LanguageDto;
import com.mogakko.be_final.domain.members.dto.response.MemberPageResponseDto;
import com.mogakko.be_final.domain.members.dto.response.MemberSimpleResponseDto;
import com.mogakko.be_final.domain.members.entity.MemberWeekStatistics;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.members.util.MembersServiceUtilMethod;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersLanguageStatisticsRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import com.mogakko.be_final.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MembersGetService {

    private final MembersRepository membersRepository;
    private final MogakkoRoomMembersLanguageStatisticsRepository mogakkoRoomMembersLanguageStatisticsRepository;
    private final MemberWeekStatisticsRepository memberWeekStatisticsRepository;
    private final MembersServiceUtilMethod membersServiceUtilMethod;
    private final FriendshipServiceUtilMethod friendshipServiceUtilMethod;

    @Transactional(readOnly = true)
    public ResponseEntity<Message> checkEmail(String email) {
        if (membersRepository.findByEmail(email).isPresent()) {
            throw new CustomException(DUPLICATE_IDENTIFIER);
        }
        return new ResponseEntity<>(new Message("중복 확인 성공", null), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> checkNickname(String nickname) {
        if (membersRepository.findByNickname(nickname).isPresent()) {
            throw new CustomException(DUPLICATE_NICKNAME);
        }
        return new ResponseEntity<>(new Message("중복 확인 성공", null), HttpStatus.OK);
    }

    // 마이페이지 조회 - 모각코 총 참여시간, 요일별 통계, 언어 선택 통계, 나의 On도, 개인정보
    @Transactional(readOnly = true)
    public ResponseEntity<Message> readMyPage(Members member) {
        // 모각코 참여시간 통계 (전체 총합 + 요일별)
        String email = member.getEmail();
        String allTimeTotal = TimeUtil.changeSecToTime(member.getMogakkoTotalTime());

        // 언어 선택 통계
        List<LanguageDto> languagePercentage = mogakkoRoomMembersLanguageStatisticsRepository.countByEmailAndLanguage(email);

        MemberPageResponseDto mypage = new MemberPageResponseDto(member, allTimeTotal, membersServiceUtilMethod.weekTimeParse(email, LocalDateTime.now().getDayOfWeek().getValue()), languagePercentage);
        return new ResponseEntity<>(new Message("마이페이지 조회 성공", mypage), HttpStatus.OK);
    }

    // 다른 유저 프로필 조회
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getMemberProfile(Members member, Long memberId) {
        Members findMember = membersRepository.findById(memberId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        // 모각코 참여시간 통계 (전체 총합 + 요일별)
        String email = findMember.getEmail();
        String allTimeTotal = TimeUtil.changeSecToTime(findMember.getMogakkoTotalTime());

        // 언어 선택 통계
        List<LanguageDto> languagePercentage = mogakkoRoomMembersLanguageStatisticsRepository.countByEmailAndLanguage(email);

        MemberPageResponseDto userPage = new MemberPageResponseDto(findMember, allTimeTotal,
                membersServiceUtilMethod.weekTimeParse(email, LocalDateTime.now().getDayOfWeek().getValue()), languagePercentage, friendshipServiceUtilMethod.checkFriend(member, findMember), friendshipServiceUtilMethod.checkFriendStatus(member, findMember));
        return new ResponseEntity<>(new Message("프로필 조회 성공", userPage), HttpStatus.OK);
    }

    // 멤버 검색 (닉네임)
    @Transactional
    public ResponseEntity<Message> searchMembersByNickname(String nickname, Members member) {
        if (nickname.equals("")) throw new CustomException(PLZ_INPUT_CONTENT);

        List<Members> memberList = membersRepository.findByNicknameLike(nickname);

        List<MemberSimpleResponseDto> members = new ArrayList<>();
        for (Members mb : memberList) {
            MemberSimpleResponseDto memberSimple = new MemberSimpleResponseDto(mb, friendshipServiceUtilMethod.checkFriend(member, mb), friendshipServiceUtilMethod.checkFriendStatus(member, mb));
            members.add(memberSimple);
        }
        if (members.size() == 0) return new ResponseEntity<>(new Message("검색된 멤버가 없습니다.", null), HttpStatus.OK);
        return new ResponseEntity<>(new Message("멤버 검색 성공", members), HttpStatus.OK);
    }

    // 멤버 검색 (친구코드)
    @Transactional
    public ResponseEntity<Message> searchMemberByFriendsCode(String friendCode, Members member) {
        if (friendCode.equals("")) throw new CustomException(PLZ_INPUT_CONTENT);
        if (friendCode.length() != 6) throw new CustomException(INVALID_FRIEND_CODE);

        int friendCodeNum;
        try {
            friendCodeNum = Integer.parseInt(friendCode);
        } catch (NumberFormatException e) {
            throw new CustomException(INVALID_FRIEND_CODE);
        }

        Optional<Members> findMember = membersRepository.findByFriendCode(friendCodeNum);
        Members foundMember;
        if (findMember.isPresent()) foundMember = findMember.get();
        else return new ResponseEntity<>(new Message("검색된 멤버가 없습니다.", null), HttpStatus.OK);

        List<MemberSimpleResponseDto> memberSimple = new ArrayList<>();

        memberSimple.add(new MemberSimpleResponseDto(foundMember, friendshipServiceUtilMethod.checkFriend(member, foundMember), friendshipServiceUtilMethod.checkFriendStatus(member, foundMember)));
        return new ResponseEntity<>(new Message("멤버 검색 성공", memberSimple), HttpStatus.OK);
    }

    // 최고의 ON:s 조회
    @Transactional
    public ResponseEntity<Message> readBestMembers() {
        List<MemberWeekStatistics> topMembers = memberWeekStatisticsRepository.findTop8ByOrderByWeekTotalTimeDesc();
        List<BestMembersResponseDto> topMemberList = new ArrayList<>();
        for (MemberWeekStatistics topMember : topMembers) {
            String email = topMember.getEmail();
            Members member = membersRepository.findByEmail(email).get();
            BestMembersResponseDto responseMember = new BestMembersResponseDto(member, membersServiceUtilMethod.weekTimeParse(email, LocalDateTime.now().getDayOfWeek().getValue()));
            topMemberList.add(responseMember);
        }
        return new ResponseEntity<>(new Message("최고의 ON:s 조회 성공", topMemberList), HttpStatus.OK);
    }
}
