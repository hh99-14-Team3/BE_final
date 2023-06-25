package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.domain.members.dto.request.GithubIdRequestDto;
import com.mogakko.be_final.domain.members.dto.request.LoginRequestDto;
import com.mogakko.be_final.domain.members.dto.request.SignupRequestDto;
import com.mogakko.be_final.domain.members.dto.response.MemberResponseDto;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.security.jwt.JwtProvider;
import com.mogakko.be_final.security.jwt.TokenDto;
import com.mogakko.be_final.util.BadWordFiltering;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class MembersPostServiceTest {
    @Mock
    private MemberWeekStatisticsRepository memberWeekStatisticsRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private BadWordFiltering badWordFiltering;
    @Mock
    private RedisUtil redisUtil;
    @Mock
    private MembersRepository membersRepository;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private JwtProvider jwtProvider;
    @InjectMocks
    private MembersPostService membersPostService;

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
    @DisplayName("회원가입 테스트")
    class signup {
        @DisplayName("회원 가입 성공 테스트")
        @Test
        void signup_success() {
            // given
            SignupRequestDto requestDto = new SignupRequestDto();
            requestDto.setEmail("test@example.com");
            requestDto.setPassword("Password1!");
            requestDto.setNickname("nickname");

            // when
            ResponseEntity<Message> response = membersPostService.signup(requestDto);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("회원 가입 성공", response.getBody().getMessage());
        }

        @DisplayName("중복된 이메일로 회원 가입 시도")
        @Test
        void signup_alreadyJoinedEmail() {
            // given
            SignupRequestDto duplicatedRequestDto = new SignupRequestDto();
            duplicatedRequestDto.setEmail("test@example.com");
            duplicatedRequestDto.setPassword("Password1!");
            duplicatedRequestDto.setNickname("nicknameEX");
            String email = duplicatedRequestDto.getEmail();

            // when & then
            when(membersRepository.findByEmail(email)).thenThrow(new CustomException(ALREADY_JOIN_USER));
            CustomException exception = assertThrows(CustomException.class, () -> membersPostService.signup(duplicatedRequestDto));
            assertEquals(exception.getErrorCode(), ALREADY_JOIN_USER);
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class login {
        @DisplayName("로그인 성공 테스트")
        @Test
        void login_success() {
            //given
            LoginRequestDto requestDto = LoginRequestDto.builder().email("test@example.com").password("password1!").build();

            TokenDto tokenDto = new TokenDto("access_token", "refresh_token");

            when(membersRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(member));
            when(passwordEncoder.matches(requestDto.getPassword(), member.getPassword())).thenReturn(true);
            when(jwtProvider.createAllToken(member.getEmail())).thenReturn(tokenDto);

            //when
            ResponseEntity<Message> response = membersPostService.login(requestDto, httpServletResponse);

            //then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals("로그인 성공", response.getBody().getMessage());

            MemberResponseDto responseDto = (MemberResponseDto) response.getBody().getData();
            assertEquals(responseDto.getNickname(), member.getNickname());
            assertEquals(responseDto.getProfileImage(), member.getProfileImage());
            assertEquals(responseDto.isTutorialCheck(), member.isTutorialCheck());
            assertEquals(responseDto.getRole(), member.getRole());
        }

        @DisplayName("로그인 없는 회원 테스트")
        @Test
        void login_notFoundMember() {
            //given
            LoginRequestDto requestDto = LoginRequestDto.builder().email("tset@example.com").password("password1!").build();

            when(membersRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());

            // when & then
            CustomException exception = assertThrows(CustomException.class, () -> membersPostService.login(requestDto, httpServletResponse));
            assertEquals(exception.getErrorCode(), USER_NOT_FOUND);
        }

        @DisplayName("로그인 이메일 유효성 검사 실패 테스트")
        @Test
        void login_invalidEmail() {
            //given
            LoginRequestDto requestDto = LoginRequestDto.builder().email("tesT@example.com").password("password1!").build();

            when(membersRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(member));

            // when & then
            CustomException exception = assertThrows(CustomException.class, () -> membersPostService.login(requestDto, httpServletResponse));
            assertEquals(exception.getErrorCode(), INVALID_EMAIL);
        }

        @DisplayName("로그인 틀린 비밀번호 테스트")
        @Test
        void login_wrongPassword() {
            //given
            LoginRequestDto requestDto = LoginRequestDto.builder().email("test@example.com").password("password2!").build();

            when(membersRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(member));

            // when & then
            CustomException exception = assertThrows(CustomException.class, () -> membersPostService.login(requestDto, httpServletResponse));
            assertEquals(exception.getErrorCode(), INVALID_PASSWORD);
        }
    }

    @Test
    void logout() {
    }

    @Test
    void addGithub() {
    }
}