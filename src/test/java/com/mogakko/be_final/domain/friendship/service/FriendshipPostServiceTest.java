package com.mogakko.be_final.domain.friendship.service;

import com.mogakko.be_final.domain.friendship.dto.request.DeleteFriendRequestDto;
import com.mogakko.be_final.domain.friendship.dto.request.DetermineRequestDto;
import com.mogakko.be_final.domain.friendship.dto.request.FriendRequestByCodeDto;
import com.mogakko.be_final.domain.friendship.dto.request.FriendRequestDto;
import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.util.MembersServiceUtilMethod;
import com.mogakko.be_final.domain.sse.service.NotificationSendService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    Friendship friendship1 = Friendship.builder().id(1L).sender(member).receiver(receiver).status(FriendshipStatus.ACCEPT).build();
    Friendship friendship2 = Friendship.builder().id(2L).sender(receiver).receiver(member).status(FriendshipStatus.ACCEPT).build();

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

    @DisplayName("[POST] 친구 요청 수락 성공 테스트")
    @Test
    void determineRequest() {
        // Given
        DetermineRequestDto requestDto = DetermineRequestDto.builder().requestSenderNickname(receiver.getNickname()).determineRequest(true).build();
        when(membersServiceUtilMethod.findMemberByNickname(requestDto.getRequestSenderNickname())).thenReturn(receiver);
        when(friendshipRepository.findBySenderAndReceiverAndStatus(receiver, member, FriendshipStatus.PENDING)).thenReturn(Optional.of(new Friendship()));
        // When
        ResponseEntity<Message> response = friendshipPostService.determineRequest(requestDto, member);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("친구요청을 수락하였습니다.", response.getBody().getMessage());
    }

    @DisplayName("[POST] 친구 요청 수락 실패 테스트 - 요청을 찾을 수 없음")
    @Test
    void determineRequest_CannotFoundRequest() {
        // Given
        DetermineRequestDto requestDto = DetermineRequestDto.builder().requestSenderNickname(receiver.getNickname()).determineRequest(true).build();
        when(membersServiceUtilMethod.findMemberByNickname(requestDto.getRequestSenderNickname())).thenReturn(receiver);
        when(friendshipRepository.findBySenderAndReceiverAndStatus(receiver, member, FriendshipStatus.PENDING)).thenReturn(Optional.empty());
        // When
        CustomException exception = assertThrows(CustomException.class, () -> friendshipPostService.determineRequest(requestDto, member));
        // Then
        assertEquals(exception.getErrorCode(), NOT_FOUND);
    }

    @DisplayName("[POST] 친구 요청 거절 테스트")
    @Test
    void determineRequest_Refuse() {
        // Given
        DetermineRequestDto requestDto = DetermineRequestDto.builder().requestSenderNickname(receiver.getNickname()).determineRequest(false).build();
        when(membersServiceUtilMethod.findMemberByNickname(requestDto.getRequestSenderNickname())).thenReturn(receiver);
        when(friendshipRepository.findBySenderAndReceiverAndStatus(receiver, member, FriendshipStatus.PENDING)).thenReturn(Optional.of(new Friendship()));
        // When
        ResponseEntity<Message> response = friendshipPostService.determineRequest(requestDto, member);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("친구요청을 거절하였습니다.", response.getBody().getMessage());
    }

    @DisplayName("[POST] 친구 삭제 성공 테스트")
    @Test
    void deleteFriend() {
        // Given
        List<String> names = new ArrayList<>();
        names.add("nickname1");
        DeleteFriendRequestDto requestDto = DeleteFriendRequestDto.builder().receiverNickname(names).build();
        List<String> deleteMemberList = new ArrayList<>();
        deleteMemberList.add("nickname1");

        // When
        when(membersServiceUtilMethod.findMemberByNickname("nickname1")).thenReturn(receiver);
        when(friendshipRepository.findBySenderAndReceiver(member, receiver)).thenReturn(Optional.of(friendship1));
        ResponseEntity<Message> response = friendshipPostService.deleteFriend(requestDto, member);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("친구 삭제 완료", response.getBody().getMessage());
    }

    @DisplayName("[POST] 친구 삭제 실패 테스트 - 친구가 아님")
    @Test
    void deleteFriend_NotFriend() {
        // Given
        List<String> names = new ArrayList<>();
        names.add("nickname1");
        DeleteFriendRequestDto requestDto = DeleteFriendRequestDto.builder().receiverNickname(names).build();
        List<String> deleteMemberList = new ArrayList<>();
        deleteMemberList.add("nickname1");

        // When
        when(membersServiceUtilMethod.findMemberByNickname("nickname1")).thenReturn(receiver);
        when(friendshipRepository.findBySenderAndReceiver(member, receiver)).thenReturn(Optional.empty());
        CustomException customException = assertThrows(CustomException.class, () -> friendshipPostService.deleteFriend(requestDto, member));

        // Then
        assertEquals(customException.getErrorCode(), USER_NOT_FOUND);
    }
}