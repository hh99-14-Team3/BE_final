package com.mogakko.be_final.domain.directMessage.service;

import com.mogakko.be_final.domain.directMessage.dto.response.DirectMessageSearchResponseDto;
import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import com.mogakko.be_final.domain.directMessage.repository.DirectMessageRepository;
import com.mogakko.be_final.domain.directMessage.util.DirectMessageServiceUtilMethod;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.entity.SocialType;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DirectMessageGetServiceTest {

    @Mock
    DirectMessageRepository directMessageRepository;
    @Mock
    DirectMessageServiceUtilMethod directMessageServiceUtilMethod;
    @InjectMocks
    DirectMessageGetService directMessageGetService;

    Members member1 = Members.builder()
            .id(1L)
            .email("test@example.com1")
            .nickname("nickname1")
            .memberStatusCode(MemberStatusCode.BASIC)
            .mogakkoTotalTime(0L)
            .githubId("github")
            .profileImage("image")
            .role(Role.USER)
            .socialUid("id")
            .socialType(SocialType.GOOGLE)
            .password("1q2w3e4r")
            .codingTem(36.5)
            .build();

    Members member2 = Members.builder()
            .id(2L)
            .email("test@example.com2")
            .nickname("nickname2")
            .memberStatusCode(MemberStatusCode.BASIC)
            .mogakkoTotalTime(0L)
            .githubId("github")
            .profileImage("image")
            .role(Role.USER)
            .socialUid("id")
            .socialType(SocialType.GOOGLE)
            .password("1q2w3e4r")
            .codingTem(36.5)
            .build();

    DirectMessage directMessage = DirectMessage.builder()
            .receiver(member1)
            .sender(member2)
            .content("content")
            .deleteByReceiver(false)
            .deleteBySender(false)
            .build();

    @BeforeEach
    void setDirectMessageCreatedAt(){
        directMessage.setCreatedAt(LocalDateTime.of(2023, 3, 3, 0,0));
    }


    @Nested
    @DisplayName("받은 쪽지 조회 테스트")
    class searchReceivedMessage {
        @DisplayName("받은 쪽지 조회 성공 테스트")
        @Test
        void searchReceivedMessage_success() {
            // given
            List<DirectMessage> directMessageList = new ArrayList<>();
            directMessageList.add(directMessage);

            when(directMessageRepository.findAllByReceiverAndDeleteByReceiverFalse(member1)).thenReturn(directMessageList);

            // when
            ResponseEntity<Message> response = directMessageGetService.searchReceivedMessage(member1);

            // then
            List<DirectMessageSearchResponseDto> responseDtoList = (List<DirectMessageSearchResponseDto>) response.getBody().getData();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("쪽지 목록 조회 완료", response.getBody().getMessage());
            assertEquals(directMessageList.get(0).getReceiver().getNickname(), responseDtoList.get(0).getReceiverNickname());
            assertEquals(directMessageList.get(0).getSender().getNickname(), responseDtoList.get(0).getSenderNickname());
            assertEquals(directMessageList.get(0).getContent(), responseDtoList.get(0).getContent());
        }

        @DisplayName("받은 쪽지 없음 테스트")
        @Test
        void searchReceivedMessage_noSearch() {
            // given
            List<DirectMessage> directMessageList = new ArrayList<>();

            when(directMessageRepository.findAllByReceiverAndDeleteByReceiverFalse(member1)).thenReturn(directMessageList);

            // when
            ResponseEntity<Message> response = directMessageGetService.searchReceivedMessage(member1);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("도착한 쪽지가 없습니다.", response.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("보낸 쪽지 조회 테스트")
    class SearchSentMessage {
        @DisplayName("보낸 쪽지 조회 성공 테스트")
        @Test
        void searchSentMessage_success() {
            // given
            List<DirectMessage> directMessageList = new ArrayList<>();
            directMessageList.add(directMessage);

            when(directMessageRepository.findAllBySenderAndDeleteBySenderFalse(member2)).thenReturn(directMessageList);

            // when
            ResponseEntity<Message> response = directMessageGetService.searchSentMessage(member2);

            // then
            List<DirectMessageSearchResponseDto> responseDtoList = (List<DirectMessageSearchResponseDto>) response.getBody().getData();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("쪽지 목록 조회 완료", response.getBody().getMessage());
            assertEquals(directMessageList.get(0).getReceiver().getNickname(), responseDtoList.get(0).getReceiverNickname());
            assertEquals(directMessageList.get(0).getSender().getNickname(), responseDtoList.get(0).getSenderNickname());
            assertEquals(directMessageList.get(0).getContent(), responseDtoList.get(0).getContent());
        }

        @DisplayName("보낸 쪽지 없음 테스트")
        @Test
        void searchSentMessage_noSearch() {
            // given
            List<DirectMessage> directMessageList = new ArrayList<>();

            when(directMessageRepository.findAllBySenderAndDeleteBySenderFalse(member2)).thenReturn(directMessageList);

            // when
            ResponseEntity<Message> response = directMessageGetService.searchSentMessage(member2);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("보낸 쪽지가 없습니다.", response.getBody().getMessage());
        }
    }

    @Test
    void readDirectMessage() {
    }

    @Test
    void testSearchReceivedMessage() {
    }

    @Test
    void testSearchSentMessage() {
    }

    @Test
    void testReadDirectMessage() {
    }
}