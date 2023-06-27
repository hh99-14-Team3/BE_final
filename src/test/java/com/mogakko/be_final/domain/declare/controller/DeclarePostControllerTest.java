package com.mogakko.be_final.domain.declare.controller;

import com.mogakko.be_final.domain.declare.dto.request.DeclareRequestDto;
import com.mogakko.be_final.domain.declare.service.DeclaredMembersGetService;
import com.mogakko.be_final.domain.declare.service.DeclaredMembersPostService;
import com.mogakko.be_final.domain.members.dto.request.SignupRequestDto;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Declare Controller - [POST] 테스트")
class DeclarePostControllerTest {

    @Mock
    private DeclaredMembersPostService declaredMembersPostService;
    @Mock
    private DeclareRequestDto declareRequestDto;
    @InjectMocks
    private DeclarePostController declarePostController;

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

    @DisplayName("[POST] 유저 신고 테스트")
    @Test
    void declareMember() {
        UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());
        Message message = new Message("회원가입 성공", null);
        when(declaredMembersPostService.declareMember(any(DeclareRequestDto.class), any(Members.class))).thenReturn(ResponseEntity.ok(message));

        ResponseEntity<Message> response = declarePostController.declareMember(declareRequestDto, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }
}