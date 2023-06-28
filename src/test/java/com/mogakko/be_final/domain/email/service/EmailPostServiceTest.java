package com.mogakko.be_final.domain.email.service;

import com.mogakko.be_final.domain.email.dto.request.EmailConfirmRequestDto;
import com.mogakko.be_final.domain.email.entity.ConfirmationToken;
import com.mogakko.be_final.domain.email.repository.ConfirmationTokenRepository;
import com.mogakko.be_final.domain.members.dto.request.ChangePwRequestDto;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
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
import org.springframework.lang.Nullable;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.internet.MimeMessage;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.EMAIL_NOT_FOUND;
import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailPostServiceTest {

    @Mock
    JavaMailSender emailSender;
    @Mock
    ConfirmationTokenRepository confirmationTokenRepository;
    @Mock
    ConfirmationTokenService confirmationTokenService;
    @Mock
    MembersRepository membersRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    EmailPostService emailPostService;

    Members member = Members.builder()
            .id(1L)
            .email("test@test.com")
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

    Members member2 = Members.builder()
            .id(1L)
            .email("tesTT@test.com")
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
    @DisplayName("createMessage Method 테스트")
    class CreateMessage {
        @DisplayName("createMessage 성공 테스트")
        @Test
        void createMessage_success() throws Exception {
            // given
            String email = "test@test.com";

            when(membersRepository.findByEmail(email)).thenReturn(Optional.of(member));

            MimeMessage mimeMessageMock = mock(MimeMessage.class);
            when(emailSender.createMimeMessage()).thenReturn(mimeMessageMock);

            // when
            emailPostService.createMessage(email);

            // then
            verify(emailSender).createMimeMessage();
            verify(confirmationTokenRepository).save(any());
        }

        @DisplayName("createMessage 실패 테스트")
        @Test
        void createMessage_fail() throws Exception {
            // given
            String email = "test@example.com";

            when(membersRepository.findByEmail(email)).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> emailPostService.createMessage(email));
            assertEquals(EMAIL_NOT_FOUND, customException.getErrorCode());
        }
    }

    @Nested
    @DisplayName("이메일 전송 테스트")
    class SendSimpleMessage {
        @DisplayName("이메일 전송 성공 테스트")
        @Test
        void sendSimpleMessage_success() throws Exception {
            // given
            EmailConfirmRequestDto emailConfirmRequestDto = EmailConfirmRequestDto.builder()
                    .email("tesTT@test.com")
                    .build();

            MimeMessage mimeMessageMock = mock(MimeMessage.class);
            when(membersRepository.findByEmail(emailConfirmRequestDto.getEmail())).thenReturn(Optional.of(member2));
            when(emailSender.createMimeMessage()).thenReturn(mimeMessageMock);

            // when
            ResponseEntity<Message> response = emailPostService.sendSimpleMessage(emailConfirmRequestDto);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("이메일을 성공적으로 보냈습니다.", response.getBody().getMessage());
        }

        @DisplayName("이메일 전송 실패 테스트")
        @Test
        void sendSimpleMessage_fail() throws Exception {
            // given
            EmailConfirmRequestDto emailConfirmRequestDto = EmailConfirmRequestDto.builder()
                    .email("tesTT@test.com")
                    .build();

            MimeMessage mimeMessageMock = mock(MimeMessage.class);
            when(membersRepository.findByEmail(emailConfirmRequestDto.getEmail())).thenReturn(Optional.of(member2));
            when(emailSender.createMimeMessage()).thenReturn(mimeMessageMock);

            MailException mailException = new MailException("mail error") {
                @Nullable
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            };

            doThrow(mailException).when(emailSender).send(mimeMessageMock);

            // when & then
            IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> emailPostService.sendSimpleMessage(emailConfirmRequestDto));
            assertNull(illegalArgumentException.getMessage());
        }
    }

    @Nested
    @DisplayName("이메일 검증 후 비밀번호 변경 테스트")
    class ConfirmEmailToFindPassword {
        @DisplayName("비밀번호 변경 성공 테스트")
        @Test
        void confirmEmailToFindPassword_success() {
            // given
            String token = "token";

            ChangePwRequestDto changePwRequestDto = ChangePwRequestDto.builder()
                    .password("password!")
                    .build();

            ConfirmationToken confirmationToken = ConfirmationToken.builder()
                    .email("test@test.com")
                    .build();

            when(confirmationTokenService.findByIdAndExpired(token)).thenReturn(confirmationToken);
            when(membersRepository.findByEmail(confirmationToken.getEmail())).thenReturn(Optional.of(member));

            // when
            ResponseEntity<Message> response = emailPostService.confirmEmailToFindPassword(token, changePwRequestDto);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("비밀번호 변경 성공", response.getBody().getMessage());
        }

        @DisplayName("비밀번호 변경 실패 테스트")
        @Test
        void confirmEmailToFindPassword_failWithMemberNotFound() {
            // given
            String token = "token";

            ChangePwRequestDto changePwRequestDto = ChangePwRequestDto.builder()
                    .password("password!")
                    .build();

            ConfirmationToken confirmationToken = ConfirmationToken.builder()
                    .email("test@test.com")
                    .build();

            when(confirmationTokenService.findByIdAndExpired(token)).thenReturn(confirmationToken);
            when(membersRepository.findByEmail(confirmationToken.getEmail())).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> emailPostService.confirmEmailToFindPassword(token, changePwRequestDto));
            assertEquals(USER_NOT_FOUND, customException.getErrorCode());
        }
    }

}