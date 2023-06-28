package com.mogakko.be_final.domain.sse.service;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.sse.dto.response.NotificationResponseDto;
import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.entity.NotificationType;
import com.mogakko.be_final.domain.sse.repository.EmitterRepository;
import com.mogakko.be_final.domain.sse.repository.NotificationRepository;
import com.mogakko.be_final.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.*;

import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    EmitterRepository emitterRepository;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    MembersRepository membersRepository;
    @InjectMocks
    NotificationService notificationService;

    Members member1 = Members.builder()
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

    Members member2 = Members.builder()
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

    Notification notification = Notification.builder()
            .senderNickname("nickname")
            .receiverId(1L)
            .content("content")
            .url("url")
            .receiverNickname("receiverNickname")
            .type(NotificationType.LOGIN)
            .createdAt(Instant.ofEpochSecond(1_000_000_000))
            .readStatus(false)
            .build();

    @Nested
    @DisplayName("subscribe Method 테스트")
    class Subscribe {
        @DisplayName("subscribe 성공 테스트")
        @Test
        void subscribe_success() {
            // given
            Long memberId = 1L;
            String lastEventId = "lastEventId";

            SseEmitter sseEmitter = mock(SseEmitter.class);

            List<Notification> notificationList = new ArrayList<>();
            notificationList.add(notification);

            List<Members> membersList = new ArrayList<>();
            membersList.add(member2);

            when(emitterRepository.save(any(), any())).thenReturn(sseEmitter);
            when(membersRepository.findById(memberId)).thenReturn(Optional.of(member1));
            when(notificationRepository.findAllByReceiverIdAndReadStatusAndCreatedAtLessThan(any(), eq(false), any()))
                    .thenReturn(notificationList);
            for (int i = 0; i < notificationList.size(); i++) {
                when(membersRepository.findByNickname(notificationList.get(i).getSenderNickname()))
                        .thenReturn(Optional.of(membersList.get(i)));
            }

            // when & then
            notificationService.subscribe(memberId, lastEventId);
        }

        @DisplayName("subscribe 실패 (eventReceiverNotFound) 테스트")
        @Test
        void subscribe_failWithEventReceiverNotFound() {
            // given
            Long memberId = 1L;
            String lastEventId = "lastEventId";

            SseEmitter sseEmitter = mock(SseEmitter.class);

            when(emitterRepository.save(any(), any())).thenReturn(sseEmitter);
            when(membersRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> notificationService.subscribe(memberId, lastEventId));
            assertEquals(USER_NOT_FOUND, customException.getErrorCode());
        }

        @DisplayName("subscribe 실패 (senderNotFound) 테스트")
        @Test
        void subscribe_failWithSenderNotFound() {
            // given
            Long memberId = 1L;
            String lastEventId = "lastEventId";

            SseEmitter sseEmitter = mock(SseEmitter.class);

            List<Notification> notificationList = new ArrayList<>();
            notificationList.add(notification);

            List<Members> membersList = new ArrayList<>();
            membersList.add(member2);

            when(emitterRepository.save(any(), any())).thenReturn(sseEmitter);
            when(membersRepository.findById(memberId)).thenReturn(Optional.of(member1));
            when(notificationRepository.findAllByReceiverIdAndReadStatusAndCreatedAtLessThan(any(), eq(false), any()))
                    .thenReturn(notificationList);
            when(membersRepository.findByNickname(notification.getSenderNickname())).thenReturn(Optional.empty());


            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> notificationService.subscribe(memberId, lastEventId));
            assertEquals(USER_NOT_FOUND, customException.getErrorCode());
        }

        @DisplayName("subscribe Method sseEmitter Completion 테스트")
        @Test
        void subscribe_emitterOnCompletion() {
            // given
            Long memberId1 = 1L;
            SseEmitter sseEmitter = mock(SseEmitter.class);

            List<Notification> notificationList = new ArrayList<>();
            notificationList.add(notification);

            List<Members> membersList = new ArrayList<>();
            membersList.add(member2);

            when(emitterRepository.save(any(), any())).thenReturn(sseEmitter);
            when(membersRepository.findById(memberId1)).thenReturn(Optional.of(member1));
            when(notificationRepository.findAllByReceiverIdAndReadStatusAndCreatedAtLessThan(any(), eq(false), any()))
                    .thenReturn(notificationList);
            for (int i = 0; i < notificationList.size(); i++) {
                when(membersRepository.findByNickname(notificationList.get(i).getSenderNickname()))
                        .thenReturn(Optional.of(membersList.get(i)));
            }
            ArgumentCaptor<Runnable> completionCaptor = ArgumentCaptor.forClass(Runnable.class);
            doNothing().when(sseEmitter).onCompletion(completionCaptor.capture());
            doNothing().when(emitterRepository).deleteById(anyString());

            // when
            notificationService.subscribe(memberId1, "lastEventId");

            // then
            Runnable completionTask = completionCaptor.getValue();
            completionTask.run();
            verify(emitterRepository, times(1)).deleteById(anyString());
        }

        @DisplayName("subscribe Method sseEmitter TimeOut 테스트")
        @Test
        void subscribe_emitterOnTimeOut() {
            // given
            Long memberId = 1L;
            SseEmitter sseEmitter = mock(SseEmitter.class);

            List<Notification> notificationList = new ArrayList<>();
            notificationList.add(notification);

            List<Members> membersList = new ArrayList<>();
            membersList.add(member2);

            when(emitterRepository.save(any(), any())).thenReturn(sseEmitter);
            when(membersRepository.findById(memberId)).thenReturn(Optional.of(member1));
            when(notificationRepository.findAllByReceiverIdAndReadStatusAndCreatedAtLessThan(any(), eq(false), any()))
                    .thenReturn(notificationList);
            for (int i = 0; i < notificationList.size(); i++) {
                when(membersRepository.findByNickname(notificationList.get(i).getSenderNickname()))
                        .thenReturn(Optional.of(membersList.get(i)));
            }

            ArgumentCaptor<Runnable> timeoutCaptor = ArgumentCaptor.forClass(Runnable.class);
            doNothing().when(sseEmitter).onTimeout(timeoutCaptor.capture());
            doNothing().when(emitterRepository).deleteById(anyString());

            // when
            notificationService.subscribe(memberId, "lastEventId");

            //then
            Runnable timeoutTask = timeoutCaptor.getValue();
            timeoutTask.run();
            verify(emitterRepository, times(1)).deleteById(anyString());
        }
    }

    @Nested
    @DisplayName("send Method 테스트")
    class Send {
        @DisplayName("send 성공 테스트")
        @Test
        void send() {
            // given
            String content = "content";
            String url = "url";

            Map<String, SseEmitter> sseEmitters = new HashMap<>();
            sseEmitters.put("key1", new SseEmitter());
            sseEmitters.put("key2", new SseEmitter());
            when(emitterRepository.findAllEmitterStartWithByMemberId(anyString())).thenReturn(sseEmitters);

            // when
            notificationService.send(member1, member2, NotificationType.LOGIN, content, url);

            // then
            verify(emitterRepository).saveEventCache(anyString(), any());
            verify(emitterRepository).findAllEmitterStartWithByMemberId(anyString());
        }
    }

    @Test
    void sendToClient() {
    }

    @Test
    void testSendToClient() {
    }

    @Test
    void markAsRead() {
    }

    @Test
    void sendHeartbeat() {
    }

    @Test
    void findUnreadNotificationList() {
    }
}