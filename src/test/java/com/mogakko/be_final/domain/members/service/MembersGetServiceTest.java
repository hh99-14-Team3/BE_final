package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.members.util.MembersServiceUtilMethod;
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

import java.util.Optional;

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

    @Test
    void readMyPage() {
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