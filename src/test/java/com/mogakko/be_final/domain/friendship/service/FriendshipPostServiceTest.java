package com.mogakko.be_final.domain.friendship.service;

import com.mogakko.be_final.domain.friendship.dto.request.FriendRequestByCodeDto;
import com.mogakko.be_final.domain.friendship.dto.request.FriendRequestDto;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.util.MembersServiceUtilMethod;
import com.mogakko.be_final.domain.sse.service.NotificationSendService;
import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Friendship Controller - [POST] 테스트")
class FriendshipPostServiceTest {

    @Mock
    FriendshipRepository friendshipRepository;
    @Mock
    NotificationSendService notificationSendService;
    @Mock
    RedisUtil redisUtil;
    @Mock
    MembersServiceUtilMethod membersServiceUtilMethod;

    @InjectMocks
    FriendshipPostService friendshipPostService;

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

    Members receiver = Members.builder()
            .id(2L)
            .email("test1@example.com")
            .nickname("nickname1")
            .password("password2!")
            .role(Role.USER)
            .codingTem(36.5)
            .mogakkoTotalTime(0L)
            .memberStatusCode(MemberStatusCode.BASIC)
            .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
            .friendCode(123456)
            .isTutorialCheck(false)
            .build();

    @DisplayName("[POST] 닉네임으로 친구 요청 성공 테스트")
    @Test
    void friendRequest() {
        // Given
        FriendRequestDto requestDto = FriendRequestDto.builder().requestReceiverNickname("nickname1").build();
        when(membersServiceUtilMethod.findMemberByNickname(requestDto.getRequestReceiverNickname())).thenReturn(receiver);
        // When
        ResponseEntity<Message> response = friendshipPostService.friendRequest(requestDto, member);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("친구 요청 완료", response.getBody().getMessage());
    }

    @DisplayName("[POST] 친구코드로 친구 요청 성공 테스트")
    @Test
    void friendRequestByCode() {
        // Given
        FriendRequestByCodeDto requestDto = FriendRequestByCodeDto.builder().requestReceiverFriendCode(123456).build();
        when(membersServiceUtilMethod.findMemberByFriendCode(requestDto.getRequestReceiverFriendCode())).thenReturn(receiver);
        // When
        ResponseEntity<Message> response = friendshipPostService.friendRequestByCode(requestDto, member);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("친구 요청 완료", response.getBody().getMessage());
    }


    @Test
    void determineRequest() {
    }

    @Test
    void deleteFriend() {
    }
}