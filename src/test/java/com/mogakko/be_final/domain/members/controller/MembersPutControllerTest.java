package com.mogakko.be_final.domain.members.controller;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.service.MembersPutService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Members Controller - [PUT] 테스트")
@ExtendWith(MockitoExtension.class)
class MembersPutControllerTest {
    @Mock
    private MembersPutService membersPutService;
    @InjectMocks
    private MembersPutController membersPutController;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(membersPutController).build();
    }

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

    @DisplayName("[PUT] 프로필 정보 수정 테스트")
    @Test
    void profilePhotoUpdate() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());
        Message expectedMessage = new Message("프로필 정보 변경 성공", null);
        // MockMultipartFile 생성
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());

        when(membersPutService.profileUpdate(any(MultipartFile.class), anyString(), any(Members.class))).thenReturn(ResponseEntity.ok(expectedMessage));

        ResponseEntity<Message> response = membersPutController.profileUpdate(imageFile, member.getNickname(), userDetails);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
    }

    @DisplayName("[PUT] 마이페이지 프로필사진 삭제 테스트")
    @Test
    void profileDelete() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());
        Message expectedMessage = new Message("프로필 사진 삭제 성공", null);

        when(membersPutService.profileDelete(any(Members.class))).thenReturn(ResponseEntity.ok(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.put("/members/mypage/delete")
                        .principal(userDetails))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage.getMessage()));
        verify(membersPutService, times(1)).profileDelete(userDetails.getMember());
    }

    @DisplayName("[PUT] 튜토리얼 체크 메서드 테스트")
    @Test
    void tutorialCheck() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());
        Message expectedMessage = new Message("튜토리얼 확인 요청 성공", null);

        when(membersPutService.tutorialCheck(any(Members.class))).thenReturn(ResponseEntity.ok(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.put("/members/tutorial-check")
                        .principal(userDetails))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage.getMessage()));
        verify(membersPutService, times(1)).tutorialCheck(userDetails.getMember());
    }
}