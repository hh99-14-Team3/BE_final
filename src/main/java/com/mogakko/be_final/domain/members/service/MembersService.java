package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.S3.S3Uploader;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.dto.request.GithubIdRequestDto;
import com.mogakko.be_final.domain.members.dto.request.LoginRequestDto;
import com.mogakko.be_final.domain.members.dto.request.SignupRequestDto;
import com.mogakko.be_final.domain.members.dto.response.BestMembersResponseDto;
import com.mogakko.be_final.domain.members.dto.response.LoginResponseDto;
import com.mogakko.be_final.domain.members.dto.response.UserPageResponseDto;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomTime;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomTimeRepository;
import com.mogakko.be_final.domain.mogakkoRoom.service.MogakkoService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.security.jwt.JwtProvider;
import com.mogakko.be_final.security.jwt.TokenDto;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembersService {
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    private final S3Uploader s3Uploader;
    private final PasswordEncoder passwordEncoder;
    private final MogakkoRoomTimeRepository mogakkoRoomTimeRepository;
    private final FriendshipRepository friendshipRepository;
    private final MembersRepository membersRepository;
    private final MogakkoService mogakkoService;

    // 회원가입
    @Transactional
    public ResponseEntity<Message> signup(SignupRequestDto signupRequestDto) {
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String nickname = signupRequestDto.getNickname();

        if (membersRepository.findByEmail(email).isPresent()) {
            throw new CustomException(ALREADY_JOIN_USER);
        }

        Members member = Members.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .role(Role.USER)
                .codingTem(36.5)
                .mogakkoTotalTime(0L)
                .mogakkoWeekTime(0L)
                .memberStatusCode(MemberStatusCode.BASIC)
                .profileImage("https://source.boringavatars.com/beam/120/$" + nickname + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
                .build();

        membersRepository.save(member);

        MogakkoRoomTime mogakkoRoomTimes = new MogakkoRoomTime(email, Time.valueOf("00:00:00"));
        mogakkoRoomTimeRepository.save(mogakkoRoomTimes);
        return new ResponseEntity<>(new Message("회원 가입 성공", null), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> checkEmail(String email) {
        if (membersRepository.findByEmail(email).isPresent()) {
            log.info("중복된 이메일 입니다.");
            throw new CustomException(DUPLICATE_IDENTIFIER);
        }
        return new ResponseEntity<>(new Message("중복 확인 성공", null), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> checkNickname(String nickname) {
        if (membersRepository.findByNickname(nickname).isPresent()) {
            log.info("중복된 닉네임 입니다.");
            throw new CustomException(DUPLICATE_NICKNAME);
        }
        return new ResponseEntity<>(new Message("중복 확인 성공", null), HttpStatus.OK);
    }

    // 로그인
    @Transactional
    public ResponseEntity<Message> login(LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
        Members member = membersRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }

        TokenDto tokenDto = jwtProvider.createAllToken(member.getEmail());
        String refreshToken = tokenDto.getRefreshToken();
        redisUtil.set(member.getEmail(), refreshToken, Duration.ofDays(7).toMillis());
        httpServletResponse.addHeader(JwtProvider.ACCESS_KEY, tokenDto.getAccessToken());
        String nickname = member.getNickname();
        String profileImage = member.getProfileImage();

        //로그인시 최근 1주일 모각코 순공 시간 동기화
        Long totalTimerWeek = mogakkoService.totalTimer(nickname, "week");
        member.setTime(totalTimerWeek);
        membersRepository.save(member);

        return new ResponseEntity<>(new Message("로그인 성공", new LoginResponseDto(nickname, profileImage)), HttpStatus.OK);
    }

    // 로그아웃
    @Transactional
    public ResponseEntity<Message> logout(Members member, HttpServletRequest request) {
        String accessToken = request.getHeader("ACCESS_KEY").substring(7);
        String refreshToken = redisUtil.get(member.getEmail());
        if (!refreshToken.isEmpty()) {
            long tokenTime = jwtProvider.getExpirationTime(accessToken);
            redisUtil.setBlackList(accessToken, "access_token", tokenTime);
            redisUtil.delValues(member.getEmail());
            return new ResponseEntity<>(new Message("로그아웃 성공", member.getEmail()), HttpStatus.OK);
        }
        throw new CustomException(USER_NOT_FOUND);
    }


    // 마이페이지 조회 - 총 참여 시간, 매너 ON:도 등 개인 정보
    @Transactional(readOnly = true)
    public ResponseEntity<Message> readMyPage(Members member) {
        String nickname = member.getNickname();
        Long totalTimerSec = mogakkoService.totalTimer(nickname, "total");
        Long totalTimerWeekSec = mogakkoService.totalTimer(nickname, "week");
        String totalTimer = mogakkoService.changeSecToTime(totalTimerSec);
        String totalTimerWeek = mogakkoService.changeSecToTime(totalTimerWeekSec);
        UserPageResponseDto userPageResponseDto = new UserPageResponseDto(member, totalTimer, totalTimerWeek);
        return new ResponseEntity<>(new Message("마이페이지 조회 성공", userPageResponseDto), HttpStatus.OK);
    }

    // 마이페이지 - 프로필 사진, 닉네임 수정
    @Transactional
    public ResponseEntity<Message> profileUpdate(MultipartFile imageFile, String nickname, Members member) throws IOException {
        if (member.getMemberStatusCode().equals(MemberStatusCode.BASIC)) {
            member.changeMemberStatusCode(MemberStatusCode.NORMAL);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            s3Uploader.delete(member.getProfileImage());
            String profileImage = s3Uploader.uploadFile(imageFile);
            member.updateProfileImage(profileImage);
        }

        if (nickname != null && !nickname.isEmpty()) {
            member.updateNickname(nickname);
        }

        membersRepository.save(member);
        return new ResponseEntity<>(new Message("프로필 정보 변경 성공", null), HttpStatus.OK);
    }

    // 마이페이지 - 프로필 사진 삭제
    public ResponseEntity<Message> profileDelete(Members member) {
        member.deleteProfile(member.getNickname());
        // TODO : SSE 개선 및 OSIV 설정 변경 후 제거 예정
        membersRepository.save(member);
        return new ResponseEntity<>(new Message("프로필 사진 삭제 성공", null), HttpStatus.OK);
    }

    // 다른 유저 프로필 조회
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getMemberProfile(Members member, Long memberId) {
        Members findmember = membersRepository.findById(memberId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        String nickname = findmember.getNickname();
        Long totalTimerSec = mogakkoService.totalTimer(nickname, "total");
        Long totalTimerWeekSec = mogakkoService.totalTimer(nickname, "week");
        String totalTimer = mogakkoService.changeSecToTime(totalTimerSec);
        String totalTimerWeek = mogakkoService.changeSecToTime(totalTimerWeekSec);
        boolean isFriend = false;
        if (friendshipRepository.findBySenderAndReceiverAndStatus(findmember, member, FriendshipStatus.ACCEPT).isPresent())
            isFriend = !isFriend;
        else if (friendshipRepository.findBySenderAndReceiverAndStatus(member, findmember, FriendshipStatus.ACCEPT).isPresent())
            isFriend = !isFriend;
        else if (member.getId().equals(memberId)) isFriend = !isFriend;
        UserPageResponseDto userPageResponseDto = new UserPageResponseDto(findmember, totalTimer, totalTimerWeek, isFriend);
        return new ResponseEntity<>(new Message("프로필 조회 성공", userPageResponseDto), HttpStatus.OK);
    }

    // 깃허브 아이디 등록
    @Transactional
    public ResponseEntity<Message> addGithub(GithubIdRequestDto githubIdRequestDto, Members member) {
        String githubId = githubIdRequestDto.getGithubId();
        if (membersRepository.findByGithubId(githubId).isPresent()) {
            log.info("중복된 깃허브 아이디 입니다.");
            throw new CustomException(DUPLICATE_GITHUB_ID);
        }
        member.setGithubId(githubId);

        // TODO : OSIV 설정으로 필요한 코드임 (리팩토링 후 삭제 예정)
        membersRepository.save(member);
        return new ResponseEntity<>(new Message("깃허브 아이디 등록 성공", githubId), HttpStatus.OK);
    }

    // 최고의 ON:s 조회
    @Transactional
    public ResponseEntity<Message> readBestMembers() {
        List<Members> topMembers = membersRepository.findTop8ByOrderByMogakkoWeekTimeDesc();
        List<BestMembersResponseDto> topMemberList = new ArrayList<>();
        for (int i = 0; i < topMembers.size(); i++) {
            Members member = topMembers.get(i);
            BestMembersResponseDto responseMember = new BestMembersResponseDto(member,
                    mogakkoService.changeSecToTime(member.getMogakkoTotalTime()),
                    mogakkoService.changeSecToTime(member.getMogakkoWeekTime()));
            topMemberList.add(responseMember);
        }
        return new ResponseEntity<>(new Message("최고의 ON:s 조회 성공", topMemberList), HttpStatus.OK);
    }
}
