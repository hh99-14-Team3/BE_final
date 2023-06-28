package com.mogakko.be_final.domain.directMessage.service;

import com.mogakko.be_final.domain.directMessage.dto.request.DirectMessageSendRequestDto;
import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import com.mogakko.be_final.domain.directMessage.repository.DirectMessageRepository;
import com.mogakko.be_final.domain.directMessage.util.DirectMessageServiceUtilMethod;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.members.util.MembersServiceUtilMethod;
import com.mogakko.be_final.domain.sse.service.NotificationSendService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.BadWordFiltering;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static com.mogakko.be_final.exception.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DirectMessage Service - [POST] 테스트")
public class DirectMessagePostServiceTest {
    @Mock
    private BadWordFiltering badWordFiltering;
    @Mock
    private MembersRepository membersRepository;
    @Mock
    private DirectMessageServiceUtilMethod directMessageServiceUtilMethod;
    @Mock
    private DirectMessageRepository directMessageRepository;
    @Mock
    private NotificationSendService notificationSendService;
    @InjectMocks
    private DirectMessagePostService directMessagePostService;


    private Members sender;
    private Members receiver;

    @BeforeEach
    public void setUp(){

       sender = Members.builder()
                .email("test1@example.com")
                .nickname("sender")
                .password("password1!")
                .role(Role.USER)
                .codingTem(36.5)
                .mogakkoTotalTime(0L)
                .memberStatusCode(MemberStatusCode.BASIC)
                .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
                .friendCode(123450)
                .isTutorialCheck(false)
                .build();
       receiver = Members.builder()
                .email("test2@example.com")
                .nickname("receiver")
                .password("password1!")
                .role(Role.USER)
                .codingTem(36.5)
                .mogakkoTotalTime(0L)
                .memberStatusCode(MemberStatusCode.BASIC)
                .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
                .friendCode(123456)
                .isTutorialCheck(false)
                .build();

    }


    @Nested
    @DisplayName("[POST] DM 전송 메소드 테스트")
    class SendDirectMessage {

        @Test
        @DisplayName("[POST] 닉네임으로 DM 전송 요청 성공 테스트 ")
        void shouldSendDirectMessageByNickname() {
            // Given
            DirectMessageSendRequestDto dto = DirectMessageSendRequestDto.builder()
                    .messageReceiverNickname(receiver.getNickname())
                    .content("Hello")
                    .build();

            when(membersRepository.findByNickname(receiver.getNickname())).thenReturn(Optional.of(receiver));
            when(badWordFiltering.checkBadWord("Hello")).thenReturn("Hello");

            // When
            ResponseEntity<Message> result = directMessagePostService.sendDirectMessage(sender, dto);

            // Then
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("쪽지 전송 성공", result.getBody().getMessage());
        }

        @Test
        @DisplayName("[POST] friendCode 로 DM 전송 요청 성공 테스트 ")
        void shouldSendDirectMessageByFriendCode(){
            // Given
            DirectMessageSendRequestDto dto = DirectMessageSendRequestDto.builder()
                    .messageReceiverNickname(receiver.getFriendCode().toString())
                    .content("Hello")
                    .build();

            when(membersRepository.findByFriendCode(receiver.getFriendCode())).thenReturn(Optional.of(receiver));
            when(badWordFiltering.checkBadWord("Hello")).thenReturn("Hello");

            // When
            ResponseEntity<Message> result = directMessagePostService.sendDirectMessage(sender, dto);

            // Then
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("쪽지 전송 성공", result.getBody().getMessage());

        }

        @Test
        @DisplayName("[POST] DM 전송 요청 실패 테스트 - 사용자가 존재하지 않을 때")
        void shouldThrowCustomExceptionWhenMemberNotFound() {
            // Given
            DirectMessageSendRequestDto dto = DirectMessageSendRequestDto.builder()
                    .messageReceiverNickname(receiver.getNickname())
                    .content("Hello")
                    .build();

            when(membersRepository.findByNickname(anyString())).thenReturn(Optional.empty());

            // Then
            CustomException exception = assertThrows(CustomException.class, () -> directMessagePostService.sendDirectMessage(receiver, dto));
            assertEquals(exception.getErrorCode(), USER_NOT_FOUND);
        }

        @Test
        @DisplayName("[POST] DM 전송 요청 실패 테스트 - sender 와 receiver 가 같을 때")
        void shouldThrowCustomExceptionWhenReceiverIsSender() {
            // Given
            DirectMessageSendRequestDto dto = DirectMessageSendRequestDto.builder()
                    .messageReceiverNickname(sender.getNickname())
                    .content("Hello")
                    .build();

            when(membersRepository.findByNickname(dto.getMessageReceiverNickname())).thenReturn(Optional.of(sender));


            // Then
            CustomException exception = assertThrows(CustomException.class, () -> directMessagePostService.sendDirectMessage(sender, dto));
            assertEquals(exception.getErrorCode(), CANNOT_REQUEST);
        }

        @Test
        @DisplayName("[POST] DM 전송 요청 실패 테스트 - content 가 비어 있을 때")
        void shouldThrowCustomExceptionWhenContentIsEmpty() {
            // Given
            DirectMessageSendRequestDto dto = DirectMessageSendRequestDto.builder()
                    .messageReceiverNickname(receiver.getNickname())
                    .content("")
                    .build();

            when(membersRepository.findByNickname(receiver.getNickname())).thenReturn(Optional.of(receiver));
            when(badWordFiltering.checkBadWord("")).thenReturn("");


            // Then
            CustomException exception = assertThrows(CustomException.class, () -> directMessagePostService.sendDirectMessage(sender, dto));
            assertEquals(exception.getErrorCode(), PLZ_INPUT);
        }
    }

    @Nested
    @DisplayName("DM 삭제 메소드 테스트")
    class DeleteDirectMessage {

        @Test
        @DisplayName("[POST] 삭제 요청 성공 - 사용자가 receiver 이면서 deletedBySender deletedByReceiver 모두 false 일 때")
        void whenMemberIsReceiverAndDeletedBySenderIsFalse() {
            // Given
            DirectMessage directMessage = DirectMessage.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .content("Hello")
                    .isRead(false)
                    .build();

            when(directMessageServiceUtilMethod.findDirectMessageById(anyLong())).thenReturn(directMessage);
            // When
            ResponseEntity<Message> response = directMessagePostService.deleteDirectMessage(receiver, Arrays.asList(1L));

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("쪽지 삭제가 완료되었습니다.", response.getBody().getMessage());

            assertEquals(receiver.getNickname(), directMessage.getReceiver().getNickname());
            assertTrue(directMessage.isDeleteByReceiver());
            assertFalse(directMessage.isDeleteBySender());

            // Additional assertions if needed
            verify(directMessageRepository, times(1)).save(any(DirectMessage.class));
            verify(directMessageRepository, times(0)).delete(any(DirectMessage.class));

        }

        @Test
        @DisplayName("[POST] 삭제 요청 성공 - 사용자가 receiver 이면서 deletedBySender 는 true, deletedByReceiver 는 false 일 때")
        void whenMemberIsReceiverAndDeletedBySenderIsTrue() {
            // Given
            DirectMessage directMessage = DirectMessage.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .content("Hello")
                    .isRead(false)
                    .build();

            directMessage.markDeleteBySenderTrue();
            when(directMessageServiceUtilMethod.findDirectMessageById(anyLong())).thenReturn(directMessage);
            // When
            ResponseEntity<Message> response = directMessagePostService.deleteDirectMessage(receiver, Arrays.asList(1L));

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("쪽지 삭제가 완료되었습니다.", response.getBody().getMessage());

            verify(directMessageRepository, times(1)).delete(any(DirectMessage.class));
        }

        @Test
        @DisplayName("[POST] 삭제 요청 성공 - 사용자가 sender 이면서 deletedBySender deletedByReceiver 모두 false 일 때")
        void deleteMessageWhenMemberIsSenderAndDeletedByReceiverIsFalse () {
            // Given
            DirectMessage directMessage = DirectMessage.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .content("Hello")
                    .isRead(false)
                    .build();

            when(directMessageServiceUtilMethod.findDirectMessageById(anyLong())).thenReturn(directMessage);

            // When
            ResponseEntity<Message> response = directMessagePostService.deleteDirectMessage(sender, Arrays.asList(1L));

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("쪽지 삭제가 완료되었습니다.", response.getBody().getMessage());

            assertEquals(sender.getNickname(), directMessage.getSender().getNickname());
            assertTrue(directMessage.isDeleteBySender());
            assertFalse(directMessage.isDeleteByReceiver());

            //Verify Method called
            verify(directMessageRepository, times(1)).save(any(DirectMessage.class));
            verify(directMessageRepository, times(0)).delete(any(DirectMessage.class));

        }

        @Test
        @DisplayName("[POST] 삭제 요청 성공 - 사용자가 sender 이면서 deletedBySender 는 false, deletedByReceiver 는 true 일 때")
        void whenMemberIsSenderAndDeletedByReceiverIsTrue() {
            // Given
            DirectMessage directMessage = DirectMessage.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .content("Hello")
                    .isRead(false)
                    .build();

            directMessage.markDeleteByReceiverTrue();
            when(directMessageServiceUtilMethod.findDirectMessageById(anyLong())).thenReturn(directMessage);

            // When
            ResponseEntity<Message> response = directMessagePostService.deleteDirectMessage(sender, Arrays.asList(1L));

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("쪽지 삭제가 완료되었습니다.", response.getBody().getMessage());

            verify(directMessageRepository, times(1)).delete(any(DirectMessage.class));

        }



        @Test
        @DisplayName("[POST] 삭제 요청 실패 - 사용자가 DirectMessage 의 sender, receiver 어느것도 일치하지 않을 때")
        void shouldThrowCustomExceptionWhenInvalidMember() {
            // Given
            Members invalidMember = Members.builder()
                    .email("invalid@test.com")
                    .nickname("invalid")
                    .password("invalid1234")
                    .role(Role.USER)
                    .codingTem(36.5)
                    .mogakkoTotalTime(0L)
                    .memberStatusCode(MemberStatusCode.BASIC)
                    .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
                    .friendCode(123498)
                    .isTutorialCheck(false)
                    .build();

            List<Long> messageIdList = Arrays.asList(1L);

            DirectMessage directMessage = DirectMessage.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .content("Hello")
                    .isRead(false)
                    .build();

            when(directMessageServiceUtilMethod.findDirectMessageById(anyLong())).thenReturn(directMessage);

            // Then
            CustomException exception = assertThrows(CustomException.class, () -> directMessagePostService.deleteDirectMessage(invalidMember, messageIdList));
            assertEquals(exception.getErrorCode(), USER_MISMATCH_ERROR);
        }
    }

    @Test
    @DisplayName("[POST] 삭제 요청 실패 - 사용자가 DirectMessage 의 receiver 와 일치하지만, deleteByReceiver 가 true 일 때")
    void shouldThrowCustomExceptionWhenValidMemberIsReceiver(){
        // Given
        DirectMessage directMessage = DirectMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content("Hello")
                .isRead(false)
                .build();

        directMessage.markDeleteByReceiverTrue();
        when(directMessageServiceUtilMethod.findDirectMessageById(anyLong())).thenReturn(directMessage);
        List<Long> messageIdList = Arrays.asList(1L);

        // Then
        CustomException exception = assertThrows(CustomException.class, () -> directMessagePostService.deleteDirectMessage(receiver, messageIdList));
        assertEquals(exception.getErrorCode(), MESSAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("[POST] 삭제 요청 실패 - 사용자가 DirectMessage 의 sender 와 일치하지만, deleteBySender 가 true 일 때")
    void shouldThrowCustomExceptionWhenValidMemberIsSender(){
        // Given
        DirectMessage directMessage = DirectMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content("Hello")
                .isRead(false)
                .build();

        directMessage.markDeleteBySenderTrue();
        when(directMessageServiceUtilMethod.findDirectMessageById(anyLong())).thenReturn(directMessage);
        List<Long> messageIdList = Arrays.asList(1L);

        // Then
        CustomException exception = assertThrows(CustomException.class, () -> directMessagePostService.deleteDirectMessage(sender, messageIdList));
        assertEquals(exception.getErrorCode(), MESSAGE_NOT_FOUND);
    }

}
