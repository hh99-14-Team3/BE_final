package com.mogakko.be_final.domain.members.controller;

import com.mogakko.be_final.domain.members.dto.request.GithubIdRequestDto;
import com.mogakko.be_final.domain.members.dto.request.LoginRequestDto;
import com.mogakko.be_final.domain.members.dto.request.SignupRequestDto;
import com.mogakko.be_final.domain.members.dto.response.MemberResponseDto;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.service.MembersPostService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Members Controller - [POST] 테스트")
public class MembersPostControllerTest {

    @Mock
    private MembersPostService membersPostService;

    @Mock
    private SignupRequestDto signupRequestDto;

    @Mock
    private LoginRequestDto loginRequestDto;

    @Mock
    private GithubIdRequestDto githubIdRequestDto;

    @Mock
    private UserDetailsImpl userDetails;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    private MembersPostController membersPostController;

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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        membersPostController = new MembersPostController(membersPostService);
    }

    @DisplayName("[POST] 회원가입 테스트")
    @Test
    public void testSignup() {
        Message message = new Message("회원가입 성공", null);
        when(membersPostService.signup(any(SignupRequestDto.class))).thenReturn(ResponseEntity.ok(message));

        ResponseEntity<Message> response = membersPostController.signup(signupRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @DisplayName("[POST] 로그인 테스트")
    @Test
    public void testLogin() {
        Message message = new Message("로그인 성공", new MemberResponseDto(member.getNickname(), member.getProfileImage(), member.isTutorialCheck(), member.getRole()));
        when(membersPostService.login(any(LoginRequestDto.class), any(HttpServletResponse.class))).thenReturn(ResponseEntity.ok(message));

        ResponseEntity<Message> response = membersPostController.login(loginRequestDto, httpServletResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @DisplayName("[POST] 로그아웃 테스트")
    @Test
    public void testLogout() {
        Message message = new Message("로그아웃 성공", member.getEmail());
        when(membersPostService.logout(any(), any(HttpServletRequest.class))).thenReturn(ResponseEntity.ok(message));

        ResponseEntity<Message> response = membersPostController.logout(userDetails, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }


    @DisplayName("[POST] 깃허브 아이디 등록 테스트")
    @Test
    public void testAddGithub() {
        Message message = new Message("깃허브 아이디 등록 성공", member.getGithubId());
        when(membersPostService.addGithub(any(GithubIdRequestDto.class), any())).thenReturn(ResponseEntity.ok(message));

        ResponseEntity<Message> response = membersPostController.addGithub(githubIdRequestDto, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

}
