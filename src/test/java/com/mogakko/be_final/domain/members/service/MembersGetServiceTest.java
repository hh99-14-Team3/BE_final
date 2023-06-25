package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.domain.members.dto.response.LanguageDto;
import com.mogakko.be_final.domain.members.dto.response.MemberPageResponseDto;
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
import com.mogakko.be_final.util.TimeUtil;
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

import static com.mogakko.be_final.exception.ErrorCode.DUPLICATE_IDENTIFIER;
import static com.mogakko.be_final.exception.ErrorCode.DUPLICATE_NICKNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Test
    void getMemberProfile() {
    }

    @Test
    void searchMembersByNickname() {
    }

    @Test
    void searchMemberByFriendsCode() {
    }

    @Test
    void readBestMembers() {
    }
}