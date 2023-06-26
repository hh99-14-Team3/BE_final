package com.mogakko.be_final.domain.friendship.controller;

import com.mogakko.be_final.domain.friendship.dto.request.DeleteFriendRequestDto;
import com.mogakko.be_final.domain.friendship.dto.request.DetermineRequestDto;
import com.mogakko.be_final.domain.friendship.dto.request.FriendRequestByCodeDto;
import com.mogakko.be_final.domain.friendship.dto.request.FriendRequestDto;
import com.mogakko.be_final.domain.friendship.service.FriendshipPostService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Friendship Controller - [POST] 테스트")
class FriendshipPostControllerTest {

    @Mock
    private FriendshipPostService friendshipPostService;
    @Mock
    private FriendRequestDto friendRequestDto;
    @Mock
    private FriendRequestByCodeDto friendRequestByCodeDto;
    @Mock
    private DetermineRequestDto determineRequestDto;
    @Mock
    private DeleteFriendRequestDto deleteFriendRequestDto;
    @InjectMocks
    private FriendshipPostController friendshipPostController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(friendshipPostController).build();
    }

    Message message;
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

    UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());

    @DisplayName("[POST] 닉네임으로 친구 요청 테스트")
    @Test
    void friendRequest() {
        message = new Message("친구 요청 완료", null);
        when(friendshipPostService.friendRequest(any(FriendRequestDto.class), any(Members.class))).thenReturn(ResponseEntity.ok(message));

        ResponseEntity<Message> response = friendshipPostController.friendRequest(friendRequestDto, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @DisplayName("[POST] 친구코드로 친구 요청 테스트")
    @Test
    void friendRequestByCode() {
        message = new Message("친구 요청 완료", null);
        when(friendshipPostService.friendRequestByCode(any(FriendRequestByCodeDto.class), any(Members.class))).thenReturn(ResponseEntity.ok(message));

        ResponseEntity<Message> response = friendshipPostController.friendRequestByCode(friendRequestByCodeDto, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @DisplayName("[POST] 친구 요청 결정 테스트")
    @Test
    void determineRequest() {
        message = new Message("친구요청을 수락하였습니다.", null);
        when(friendshipPostService.determineRequest(any(DetermineRequestDto.class), any(Members.class))).thenReturn(ResponseEntity.ok(message));

        ResponseEntity<Message> response = friendshipPostController.determineRequest(determineRequestDto, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @DisplayName("[POST] 친구 삭제 테스트")
    @Test
    void deleteFriend() {
        message = new Message("친구 삭제 완료", null);
        when(friendshipPostService.deleteFriend(any(DeleteFriendRequestDto.class), any(Members.class))).thenReturn(ResponseEntity.ok(message));

        ResponseEntity<Message> response = friendshipPostController.deleteFriend(deleteFriendRequestDto, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }
}