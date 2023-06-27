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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.EMAIL_NOT_FOUND;
import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@DisplayName("EmailPostService 테스트코드")
@ExtendWith(MockitoExtension.class)
class EmailPostServiceTest {

    @Mock
    private JavaMailSender emailSender;
    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Mock
    private MembersRepository membersRepository;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private ChangePwRequestDto changePwRequestDto;
    @Mock
    private PasswordEncoder passwordEncoder;

    private EmailPostService emailPostService;

    @BeforeEach
    public void setup() {
        emailPostService = new EmailPostService(
                emailSender,
                confirmationTokenRepository,
                membersRepository,
                confirmationTokenService,
                passwordEncoder
        );
    }

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

    @DisplayName("[POST] 메일 전송 성공 테스트")
    @Test
    public void sendSimpleMessage_Success() throws Exception {
        // Given
        EmailConfirmRequestDto requestDto = new EmailConfirmRequestDto(member.getEmail());
        when(membersRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        MimeMessage message = new MimeMessage((Session) null);
        when(emailSender.createMimeMessage()).thenReturn(message);

        // When
        ResponseEntity<Message> response = emailPostService.sendSimpleMessage(requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("이메일을 성공적으로 보냈습니다.", response.getBody().getMessage());
    }

    @DisplayName("[POST] 메일 전송 실패 테스트 - 회원 찾을 수 없음")
    @Test
    public void sendSimpleMessage_cannotFoundMember() {
        // Given
        EmailConfirmRequestDto requestDto = new EmailConfirmRequestDto(member.getEmail());
        when(membersRepository.findByEmail(member.getEmail())).thenReturn(Optional.empty());
        // When
        CustomException customException = assertThrows(CustomException.class, () -> emailPostService.sendSimpleMessage(requestDto));
        // Then
        assertEquals(customException.getErrorCode(), EMAIL_NOT_FOUND);
    }

    @DisplayName("[POST] 이메일 검증 후 비밀번호 변경 성공 테스트")
    @Test
    void confirmEmailToFindPassword() {
        // Given
        String token = "validToken";
        String newPassword = "newPassword123";
        ChangePwRequestDto requestDto = new ChangePwRequestDto(newPassword);
        ConfirmationToken findConfirmationToken = new ConfirmationToken();
        when(confirmationTokenService.findByIdAndExpired(token)).thenReturn(findConfirmationToken);
        when(membersRepository.findByEmail(findConfirmationToken.getEmail())).thenReturn(Optional.of(member));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");
        // When
        ResponseEntity<Message> response = emailPostService.confirmEmailToFindPassword(token, requestDto);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("비밀번호 변경 성공", response.getBody().getMessage());
    }
}