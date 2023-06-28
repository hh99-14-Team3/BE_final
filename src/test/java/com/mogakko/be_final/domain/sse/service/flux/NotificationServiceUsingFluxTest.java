package com.mogakko.be_final.domain.sse.service.flux;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.sse.dto.response.NotificationResponseDto;
import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.entity.NotificationType;
import com.mogakko.be_final.domain.sse.repository.NotificationReactiveRepository;
import com.mogakko.be_final.domain.sse.service.NotificationProcessor;
import com.mogakko.be_final.domain.sse.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.util.TestLogger;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceUsingFluxTest {
    @Mock
    private NotificationProcessor notificationProcessor;

    @Mock
    private NotificationReactiveRepository notificationReactiveRepository;

    @InjectMocks
    private NotificationService notificationService;

    Members sender = Members.builder()
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

    @Test
    void testSubscribeWithSuccessMessage() {
        when(notificationProcessor.stream()).thenReturn(Flux.empty());
        when(notificationReactiveRepository.findAllByReceiverIdAndReadStatus(anyLong(), anyBoolean()))
                .thenReturn(Flux.just(notification));

        StepVerifier.create(notificationService.subscribeWithSuccessMessage(sender))
                .expectNextMatches(serverSentEvent -> serverSentEvent.data().toString().contains("연결에 성공했습니다. 사용자 닉네임: nickname"))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testSubscribe() {
        NotificationResponseDto notificationResponseDto = new NotificationResponseDto(notification);
        when(notificationProcessor.stream()).thenReturn(Flux.just(notificationResponseDto));
        when(notificationReactiveRepository.findAllByReceiverIdAndReadStatus(anyLong(), anyBoolean()))
                .thenReturn(Flux.empty());

        StepVerifier.create(notificationService.subscribe(sender.getId()))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testSubscribeWithFilter() {
        NotificationResponseDto notificationResponseDto = new NotificationResponseDto(notification);
        when(notificationProcessor.stream()).thenReturn(Flux.just(notificationResponseDto));
        when(notificationReactiveRepository.findAllByReceiverIdAndReadStatus(anyLong(), anyBoolean()))
                .thenReturn(Flux.just(notification));

        StepVerifier.create(notificationService.subscribe(receiver.getId()))
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }

    @Test
    void testSubscribeWithError() {
        when(notificationProcessor.stream()).thenReturn(Flux.error(new RuntimeException("Test exception")));
        when(notificationReactiveRepository.findAllByReceiverIdAndReadStatus(anyLong(), anyBoolean()))
                .thenReturn(Flux.just(notification));

        StepVerifier.create(notificationService.subscribe(sender.getId()))
                .expectNextCount(1)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testSend() {
        NotificationType notificationType = NotificationType.LOGIN;
        String content = "content";
        String url = "url";
        Notification notification = Notification.builder()
                .receiverId(receiver.getId())
                .readStatus(false)
                .notificationId(Uuids.timeBased())
                .createdAt(Instant.now())
                .senderNickname(sender.getNickname())
                .receiverNickname(receiver.getNickname())
                .content(content)
                .url(url)
                .type(notificationType)
                .senderProfileUrl(sender.getProfileImage())
                .build();

        when(notificationReactiveRepository.save(any(Notification.class))).thenReturn(Mono.just(notification));
        doNothing().when(notificationProcessor).publish(any(NotificationResponseDto.class));

        StepVerifier.create(notificationService.send(sender, receiver, notificationType, content, url))
                .verifyComplete();

        verify(notificationReactiveRepository, times(1)).save(any(Notification.class));
        verify(notificationProcessor, times(1)).publish(any(NotificationResponseDto.class));
    }

    @Test
    void testSendWithError() {
        NotificationType notificationType = NotificationType.LOGIN;
        String content = "content";
        String url = "url";

        RuntimeException exception = new RuntimeException("Test exception");
        when(notificationReactiveRepository.save(any(Notification.class))).thenReturn(Mono.error(exception));

        // Act
        StepVerifier.create(notificationService.send(sender, receiver, notificationType, content, url))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testFindUnreadNotificationList(){
        Long memberId = 1L;
        RuntimeException exception = new RuntimeException("Test exception");
        when(notificationReactiveRepository.findAllByReceiverIdAndReadStatus(memberId, false))
                .thenReturn(Flux.error(exception));


        // Act
        StepVerifier.create(notificationService.findUnreadNotificationList(memberId))
                .expectError(RuntimeException.class)
                .verify();

    }
}
