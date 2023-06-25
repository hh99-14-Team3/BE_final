package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.domain.members.dto.response.LanguageDto;
import com.mogakko.be_final.domain.members.dto.response.MemberPageResponseDto;
import com.mogakko.be_final.domain.members.dto.response.MemberSimpleResponseDto;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.members.util.MembersServiceUtilMethod;
import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersLanguageStatisticsRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static com.mogakko.be_final.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class MembersGetServiceTest {

    @Mock
    MembersRepository membersRepository;
    @Mock
    MogakkoRoomMembersLanguageStatisticsRepository mogakkoRoomMembersLanguageStatisticsRepository;
    @Mock
    MemberWeekStatisticsRepository memberWeekStatisticsRepository;
    @Mock
    MembersServiceUtilMethod membersServiceUtilMethod;
    @InjectMocks
    MembersGetService membersGetService;

    Members member = Members.builder()
            .id(1L)
            .email("test@example.com")
            .nickname("nickname")
            .password("password1!")
            .role(Role.USER)
            .codingTem(36.5)
            .mogakkoTotalTime(0L)
            .memberStatusCode(MemberStatusCode.BASIC)
            .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
            .friendCode(123456)
            .isTutorialCheck(false)
            .build();


    @Nested
    @DisplayName("이메일 중복 확인 테스트")
    class checkEmail {
        @DisplayName("이메일 중복 확인 성공 테스트")
        @Test
        void checkEmail_success() {
            // given
            String email = "test@gmail.com";

            when(membersRepository.findByEmail(email)).thenReturn(Optional.empty());

            // when
            ResponseEntity<Message> response = membersGetService.checkEmail(email);

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "중복 확인 성공");
        }

        @DisplayName("이메일 중복 확인 실패 테스트")
        @Test
        void checkEmail_fail() {
            // given
            String email = "test@gmail.com";

            when(membersRepository.findByEmail(email)).thenReturn(Optional.of(member));

            // when & then
            CustomException exception = assertThrows(CustomException.class, () -> membersGetService.checkEmail(email));
            assertEquals(exception.getErrorCode(), DUPLICATE_IDENTIFIER);
        }
    }

    @Nested
    @DisplayName("닉네임 중복 확인 테스트")
    class checkNickname {
        @DisplayName("닉네임 중복 확인 성공 테스트")
        @Test
        void checkEmail_success() {
            // given
            String nickname = "been1118";

            when(membersRepository.findByNickname(nickname)).thenReturn(Optional.empty());

            // when
            ResponseEntity<Message> response = membersGetService.checkNickname(nickname);

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "중복 확인 성공");
        }

        @DisplayName("닉네임 중복 확인 실패 테스트")
        @Test
        void checkEmail_fail() {
            // given
            String nickname = "been1118";

            when(membersRepository.findByNickname(nickname)).thenReturn(Optional.of(member));

            // when & then
            CustomException exception = assertThrows(CustomException.class, () -> membersGetService.checkNickname(nickname));
            assertEquals(exception.getErrorCode(), DUPLICATE_NICKNAME);
        }
    }

    @Nested
    @DisplayName("마이페이지 조회 테스트")
    class ReadMyPage {
        @DisplayName("마이페이지 조회 성공 테스트")
        @Test
        void readMyPage_success() {
            // given
            String email = member.getEmail();
            List<LanguageDto> languageList = new ArrayList<>();
            LanguageDto languageDtoC = new LanguageDto(LanguageEnum.C, 3, 6);
            LanguageDto languageDtoJ = new LanguageDto(LanguageEnum.JAVA, 3, 6);
            languageList.add(languageDtoC);
            languageList.add(languageDtoJ);

            Map<String, String> weekMap = new HashMap<>();
            weekMap.put("sun", "20H32H");

            when(membersServiceUtilMethod.weekTimeParse(email)).thenReturn(weekMap);
            when(mogakkoRoomMembersLanguageStatisticsRepository.countByEmailAndLanguage(email)).thenReturn(languageList);

            // when
            ResponseEntity<Message> response = membersGetService.readMyPage(member);

            // then
            MemberPageResponseDto memberPageResponseDto = (MemberPageResponseDto) response.getBody().getData();
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "마이페이지 조회 성공");
            assertEquals(memberPageResponseDto.getMember(), member);
            assertEquals(memberPageResponseDto.getTotalTimer(), "00H00M");
            assertEquals(memberPageResponseDto.getTimeOfWeek(), weekMap);
            assertEquals(memberPageResponseDto.getLanguageList(), languageList);
        }
    }

    @Nested
    @DisplayName("유저 페이지 조회 테스트")
    class GetMemberProfile {
        @DisplayName("유저 페이지 조회 성공 테스트")
        @Test
        void getMemberProfile_success() {
            // given
            String email = member.getEmail();
            List<LanguageDto> languageList = new ArrayList<>();
            LanguageDto languageDtoC = new LanguageDto(LanguageEnum.C, 3, 6);
            LanguageDto languageDtoJ = new LanguageDto(LanguageEnum.JAVA, 3, 6);
            languageList.add(languageDtoC);
            languageList.add(languageDtoJ);

            Map<String, String> weekMap = new HashMap<>();
            weekMap.put("sun", "20H32H");

            when(membersRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(membersServiceUtilMethod.weekTimeParse(email)).thenReturn(weekMap);
            when(mogakkoRoomMembersLanguageStatisticsRepository.countByEmailAndLanguage(email)).thenReturn(languageList);

            // when
            ResponseEntity<Message> response = membersGetService.getMemberProfile(member, 1L);

            // then
            MemberPageResponseDto memberPageResponseDto = (MemberPageResponseDto) response.getBody().getData();
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "프로필 조회 성공");
            assertEquals(memberPageResponseDto.getMember(), member);
            assertEquals(memberPageResponseDto.getTotalTimer(), "00H00M");
            assertEquals(memberPageResponseDto.getTimeOfWeek(), weekMap);
            assertEquals(memberPageResponseDto.getLanguageList(), languageList);
        }

        @DisplayName("없는 유저 페이지 조회 테스트")
        @Test
        void getMemberProfile_notFoundMember() {
            // given
            when(membersRepository.findById(2L)).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> membersGetService.getMemberProfile(member, 2L));
            assertEquals(customException.getErrorCode(), USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("멤버 닉네임 검색 테스트")
    class SearchMembersByNickname {
        @DisplayName("멤버 닉네임 검색 성공 테스트")
        @Test
        void searchMembersByNickname_success() {
            // given
            String nickname = "be";
            List<Members> membersList = new ArrayList<>();
            Members member1 = Members.builder().nickname("been1118").build();
            Members member2 = Members.builder().nickname("bean1118").build();
            membersList.add(member1);
            membersList.add(member2);

            List<MemberSimpleResponseDto> memberSimpleResponseDtoList = new ArrayList<>();
            MemberSimpleResponseDto memberSimpleResponseDto1 = new MemberSimpleResponseDto(member1, true, false);
            MemberSimpleResponseDto memberSimpleResponseDto2 = new MemberSimpleResponseDto(member2, false, true);
            memberSimpleResponseDtoList.add(memberSimpleResponseDto1);
            memberSimpleResponseDtoList.add(memberSimpleResponseDto2);

            when(membersRepository.findByNicknameLike(nickname)).thenReturn(membersList);
            when(membersServiceUtilMethod.checkFriend(member, member1)).thenReturn(true);
            when(membersServiceUtilMethod.checkFriend(member, member2)).thenReturn(false);
            when(membersServiceUtilMethod.checkFriendStatus(member, member1)).thenReturn(false);
            when(membersServiceUtilMethod.checkFriendStatus(member, member2)).thenReturn(true);

            // when
            ResponseEntity<Message> response = membersGetService.searchMembersByNickname(nickname, member);

            // then
            List<MemberSimpleResponseDto> memberSimpleResponseDtos = (List<MemberSimpleResponseDto>) response.getBody().getData();
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "멤버 검색 성공");
            assertEquals(memberSimpleResponseDtos.get(0).getNickname(), "been1118");
            assertEquals(memberSimpleResponseDtos.get(0).isFriend(), true);
            assertEquals(memberSimpleResponseDtos.get(0).isPending(), false);
            assertEquals(memberSimpleResponseDtos.get(1).getNickname(), "bean1118");
            assertEquals(memberSimpleResponseDtos.get(1).isFriend(), false);
            assertEquals(memberSimpleResponseDtos.get(1).isPending(), true);
        }

        @DisplayName("닉네임 검색 빈 입력값 테스트")
        @Test
        void searchMembersByNickname_void() {
            // given
            String nickname = "";

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> membersGetService.searchMembersByNickname(nickname, member));
            assertEquals(customException.getErrorCode(), PLZ_INPUT_CONTENT);
        }

        @DisplayName("닉네임 검색 결과 없음 테스트")
        @Test
        void searchMembersByNickname_noSearchResults() {
            // given
            String nickname = "be";
            List<Members> membersList = new ArrayList<>();

            when(membersRepository.findByNicknameLike(nickname)).thenReturn(membersList);

            // when
            ResponseEntity<Message> response = membersGetService.searchMembersByNickname(nickname, member);

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "검색된 멤버가 없습니다.");
        }
    }

    @Nested
    @DisplayName("멤버 친구코드 검색 테스트")
    class searchMemberByFriendsCode {
        @DisplayName("멤버 친구코드 검색 성공 테스트")
        @Test
        void searchMemberByFriendsCode_success() {
            // given
            String friendCode = "123456";

            when(membersRepository.findByFriendCode(anyInt())).thenReturn(Optional.of(member));
            when(membersServiceUtilMethod.checkFriend(member, member)).thenReturn(false);
            when(membersServiceUtilMethod.checkFriendStatus(member, member)).thenReturn(false);

            // when
            ResponseEntity<Message> response = membersGetService.searchMemberByFriendsCode(friendCode, member);

            // then
            List<MemberSimpleResponseDto> memberSimpleResponseDtoList = (List<MemberSimpleResponseDto>) response.getBody().getData();
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "멤버 검색 성공");
            assertEquals(memberSimpleResponseDtoList.get(0).getId(), member.getId());
            assertEquals(memberSimpleResponseDtoList.get(0).getNickname(), member.getNickname());
            assertEquals(memberSimpleResponseDtoList.get(0).getProfileImage(), member.getProfileImage());
            assertEquals(memberSimpleResponseDtoList.get(0).isFriend(), false);
            assertEquals(memberSimpleResponseDtoList.get(0).isPending(), false);
        }

        @DisplayName("친구 코드 빈 입력값 검색 테스트")
        @Test
        void searchMemberByFriendsCode_void() {
            // given
            String friendCode = "";

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> membersGetService.searchMemberByFriendsCode(friendCode, member));
            assertEquals(customException.getErrorCode(), PLZ_INPUT_CONTENT);
        }

        @DisplayName("친구 코드 길이 유효성 검사 실패 테스트")
        @Test
        void searchMemberByFriendsCode_invalidLength() {
            // given
            String friendCode = "222";

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> membersGetService.searchMemberByFriendsCode(friendCode, member));
            assertEquals(customException.getErrorCode(), INVALID_FRIEND_CODE);
        }

        @DisplayName("친구 코드 유형 유효성 검사 실패 테스트")
        @Test
        void searchMemberByFriendsCode_invalidCode() {
            // given
            String friendCode = "friend";

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> membersGetService.searchMemberByFriendsCode(friendCode, member));
            assertEquals(customException.getErrorCode(), INVALID_FRIEND_CODE);
        }

        @DisplayName("멤버 친구코드 검색 결과 없음 테스트")
        @Test
        void searchMemberByFriendsCode_notFound() {
            // given
            String friendCode = "555555";

            when(membersRepository.findByFriendCode(anyInt())).thenReturn(Optional.empty());

            // when
            ResponseEntity<Message> response = membersGetService.searchMemberByFriendsCode(friendCode, member);

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "검색된 멤버가 없습니다.");
        }
    }

    @Test
    void readBestMembers() {
    }
}