package com.mogakko.be_final.domain.friendship.controller;

import com.mogakko.be_final.domain.friendship.dto.response.FriendResponseDto;
import com.mogakko.be_final.domain.friendship.service.FriendshipGetService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Friends Controller - [GET] 테스트")
class FriendshipGetControllerTest {

    @Mock
    private FriendshipGetService friendshipGetService;
    @InjectMocks
    private FriendshipGetController friendshipGetController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(friendshipGetController).build();
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

    @DisplayName("[GET] 친구 목록 조회 테스트")
    @Test
    void getMyFriend() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());
        List<FriendResponseDto> friendsList = new ArrayList<>();
        Message expectedMessage = new Message("친구 목록 조회 성공", friendsList);

        when(friendshipGetService.getMyFriend(userDetails.getMember())).thenReturn(ResponseEntity.ok(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/friendship/requests/accepted")
                        .principal(userDetails))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage.getMessage()));

        verify(friendshipGetService, times(1)).getMyFriend(userDetails.getMember());
    }
}