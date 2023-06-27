package com.mogakko.be_final.domain.friendship.service;

import com.mogakko.be_final.domain.friendship.dto.response.FriendResponseDto;
import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
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

import static com.mogakko.be_final.exception.ErrorCode.CANNOT_FOUND_FRIEND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Friendship Controller - [GET] 테스트")
class FriendshipGetServiceTest {
    @Mock
    MembersRepository membersRepository;
    @Mock
    FriendshipRepository friendshipRepository;
    @InjectMocks
    FriendshipGetService friendshipGetService;

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


    @DisplayName("[GET] 친구 목록 조회 성공 테스트")
    @Test
    void getMyFriend() {
        // Given
        List<Friendship> friendshipList = new ArrayList<>();
        friendshipList.add(friendship1);
        friendshipList.add(friendship2);
        when(friendshipRepository.findAllByReceiverAndStatusOrSenderAndStatus(member, FriendshipStatus.ACCEPT, member, FriendshipStatus.ACCEPT)).thenReturn(friendshipList);

        // When
        List<FriendResponseDto> friendsList = new ArrayList<>();
        FriendResponseDto responseDto = new FriendResponseDto(member, false);
        friendsList.add(responseDto);
        when(membersRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        ResponseEntity<Message> response = friendshipGetService.getMyFriend(member);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("친구 목록 조회 성공", response.getBody().getMessage());
    }

    @DisplayName("[GET] 친구 목록 조회 성공 테스트 - 조회된 친구 없음")
    @Test
    void getMyFriend_NotFoundFriends() {
        // Given
        List<Friendship> friendshipList = new ArrayList<>();
        when(friendshipRepository.findAllByReceiverAndStatusOrSenderAndStatus(member, FriendshipStatus.ACCEPT, member, FriendshipStatus.ACCEPT)).thenReturn(friendshipList);

        // When
        ResponseEntity<Message> response = friendshipGetService.getMyFriend(member);

        // Then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getMessage(), "조회된 친구가 없습니다.");
    }

    @DisplayName("[GET] 친구 목록 조회 실패 테스트 - 유효하지 않은 id값")
    @Test
    void getMyFriend_Fail() {
        // Given
        List<Friendship> friendshipList = new ArrayList<>();
        friendshipList.add(friendship1);
        when(friendshipRepository.findAllByReceiverAndStatusOrSenderAndStatus(member, FriendshipStatus.ACCEPT, member, FriendshipStatus.ACCEPT)).thenReturn(friendshipList);
        List<FriendResponseDto> friendsList = new ArrayList<>();
        FriendResponseDto responseDto = new FriendResponseDto(new Members(), false);
        friendsList.add(responseDto);

        // When, Then
        CustomException customException = assertThrows(CustomException.class, () -> friendshipGetService.getMyFriend(member));
        assertEquals(customException.getErrorCode(), CANNOT_FOUND_FRIEND);
    }

    @DisplayName("[GET] 친구 요청 조회 성공 테스트")
    @Test
    void getMyFriendRequest() {
        // Given
        List<Friendship> friendRequestSenderList = new ArrayList<>();
        friendRequestSenderList.add(friendship1);
        friendRequestSenderList.add(friendship2);
        // When
        when(friendshipRepository.findAllByReceiverAndStatus(member, FriendshipStatus.PENDING)).thenReturn(friendRequestSenderList);
        ResponseEntity<Message> response = friendshipGetService.getMyFriendRequest(member);
        // Then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getMessage(), "친구 요청 목록 조회 성공");
    }


    @DisplayName("[GET] 친구 요청 조회 성공 테스트 - 수신된 요청 없음")
    @Test
    void getMyFriendRequest_NoRequest() {
        // Given
        List<Friendship> friendRequestSenderList = new ArrayList<>();
        // When
        when(friendshipRepository.findAllByReceiverAndStatus(member, FriendshipStatus.PENDING)).thenReturn(friendRequestSenderList);
        ResponseEntity<Message> response = friendshipGetService.getMyFriendRequest(member);
        // Then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getMessage(), "수신된 친구 요청이 없습니다");
    }
}