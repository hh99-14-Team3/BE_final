package com.mogakko.be_final.domain.friendship.util;

import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@DisplayName("FriendshipServiceUtilMethodTest 테스트")
@ExtendWith(MockitoExtension.class)
class FriendshipServiceUtilMethodTest {
    @Mock
    FriendshipRepository friendshipRepository;
    @InjectMocks
    FriendshipServiceUtilMethod friendshipServiceUtilMethod;

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

    Members stranger = Members.builder()
            .id(2L)
            .email("test2@example.com")
            .nickname("nickname2")
            .password("password1!")
            .role(Role.USER)
            .codingTem(36.5)
            .mogakkoTotalTime(0L)
            .memberStatusCode(MemberStatusCode.BASIC)
            .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname2" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
            .friendCode(234567)
            .isTutorialCheck(false)
            .build();


    @DisplayName("친구 여부 확인 메서드 테스트 - 내가 요청을 보낸 경우")
    @Test
    void checkFriend_Sender() {
        // Given
        when(friendshipRepository.findBySenderAndReceiverAndStatus(member, stranger, FriendshipStatus.ACCEPT))
                .thenReturn(Optional.of(new Friendship()));
        // When
        boolean result = friendshipServiceUtilMethod.checkFriend(stranger, member);
        // Then
        assertTrue(result);
    }

    @DisplayName("친구 여부 확인 메서드 테스트 - 내가 요청을 받은 경우")
    @Test
    void checkFriend_Receiver() {
        // Given
        when(friendshipRepository.findBySenderAndReceiverAndStatus(stranger, member, FriendshipStatus.ACCEPT)).thenReturn(Optional.empty());
        when(friendshipRepository.findBySenderAndReceiverAndStatus(member, stranger, FriendshipStatus.ACCEPT)).thenReturn(Optional.of(new Friendship()));
        // When
        boolean result = friendshipServiceUtilMethod.checkFriend(member, stranger);
        // Then
        assertTrue(result);
    }

    @DisplayName("친구 여부 확인 메서드 테스트 - 내가 나에게 요청을 보낸 경우")
    @Test
    void checkFriend_Myself() {
        // Given
        boolean result;
        // When
        result = friendshipServiceUtilMethod.checkFriend(member, member);
        // Then
        assertTrue(result);
    }

    @DisplayName("친구 여부 확인 메서드 테스트 - 친구가 아닌 경우")
    @Test
    void checkFriend_notFriend() {
        when(friendshipRepository.findBySenderAndReceiverAndStatus(stranger, member, FriendshipStatus.ACCEPT)).thenReturn(Optional.empty());
        when(friendshipRepository.findBySenderAndReceiverAndStatus(member, stranger, FriendshipStatus.ACCEPT)).thenReturn(Optional.empty());
        // When
        boolean result = friendshipServiceUtilMethod.checkFriend(member, stranger);
        // Then
        assertFalse(result);
    }

    @DisplayName("친구 여부 상태 코드로 확인 메서드 테스트 - 내가 요청을 보낸 경우")
    @Test
    void checkFriendStatus_Sender() {
        // Given
        when(friendshipRepository.findBySenderAndReceiverAndStatus(member, stranger, FriendshipStatus.PENDING))
                .thenReturn(Optional.of(new Friendship()));
        // When
        boolean result = friendshipServiceUtilMethod.checkFriendStatus(stranger, member);
        // Then
        assertTrue(result);
    }

    @DisplayName("친구 여부 상태 코드로 확인 메서드 테스트 - 내가 요청을 받은 경우")
    @Test
    void checkFriendStatus_Receiver() {
        // Given
        when(friendshipRepository.findBySenderAndReceiverAndStatus(stranger, member, FriendshipStatus.PENDING)).thenReturn(Optional.empty());
        when(friendshipRepository.findBySenderAndReceiverAndStatus(member, stranger, FriendshipStatus.PENDING)).thenReturn(Optional.of(new Friendship()));
        // When
        boolean result = friendshipServiceUtilMethod.checkFriendStatus(member, stranger);
        // Then
        assertTrue(result);
    }

    @DisplayName("친구 여부 상태 코드로 확인 메서드 테스트 - 내가 나에게 요청을 보낸 경우")
    @Test
    void checkFriendStatus_Myself() {
        // Given
        boolean result;
        // When
        result = friendshipServiceUtilMethod.checkFriendStatus(member, member);
        // Then
        assertTrue(result);
    }

    @DisplayName("친구 여부 상태 코드로 확인 메서드 테스트 - 친구가 아닌 경우")
    @Test
    void checkFriendStatus_notFriend() {
        when(friendshipRepository.findBySenderAndReceiverAndStatus(stranger, member, FriendshipStatus.PENDING)).thenReturn(Optional.empty());
        when(friendshipRepository.findBySenderAndReceiverAndStatus(member, stranger, FriendshipStatus.PENDING)).thenReturn(Optional.empty());
        // When
        boolean result = friendshipServiceUtilMethod.checkFriendStatus(member, stranger);
        // Then
        assertFalse(result);
    }
}