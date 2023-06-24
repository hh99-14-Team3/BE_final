package com.mogakko.be_final.domain.sse.service;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.sse.dto.response.NotificationResponseDto;
import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.entity.NotificationType;
import com.mogakko.be_final.domain.sse.repository.NotificationReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationProcessor notificationProcessor;
    private final NotificationReactiveRepository notificationReactiveRepository;


    public  Flux<ServerSentEvent<Object>> subscribeWithSuccessMessage(Members member){
        log.info("===== SSE 구독 확인 : [{}]님 구독", member.getNickname());

        String successMessage = "연결에 성공했습니다. 사용자 닉네임: " + member.getNickname();

        ServerSentEvent<Object> sendSuccessMessage = ServerSentEvent.builder()
                .event("notification-event")
                .data(successMessage)
                .build();

        return Flux.concat(
                Flux.just(sendSuccessMessage),
                subscribe(member.getId())
        );
    }

    public Flux<ServerSentEvent<Object>> subscribe(Long memberId) {
        Flux<Notification> missedNotifications = findUnreadNotificationList(memberId);

        Flux<ServerSentEvent<Object>> missedNotificationFlux = missedNotifications
                .map(notification -> new NotificationResponseDto(notification))
                .map(notificationResponseDto -> ServerSentEvent.builder()
                        .event("notification-event")
                        .data(notificationResponseDto)
                        .build());

        Flux<ServerSentEvent<Object>> newNotificationFlux = this.notificationProcessor.stream()
                .filter(notification -> notification.getReceiverId().equals(memberId))
                .map(notificationResponseDto -> ServerSentEvent.builder()
                        .event("notification-event")
                        .data(notificationResponseDto)
                        .build());

        return Flux.concat(missedNotificationFlux, newNotificationFlux)
                .onBackpressureBuffer()
                .doOnError( error -> log.error("Id {} 에 대한 알림을 처리하는 동안 오류가 발생했습니다", memberId))
                .doOnTerminate(() -> log.info("Id {} 와 연결이 끊겼습니다.", memberId));
    }

    public Mono<Void> send(Members sender, Members receiver, NotificationType notificationType, String content, String url) {
        Notification notification = createNotification(sender, receiver, notificationType, content, url);
        return notificationReactiveRepository.save(notification)
                .doOnSuccess(savedNotification -> {
                    NotificationResponseDto notificationResponseDto = new NotificationResponseDto(savedNotification);
                    this.notificationProcessor.publish(notificationResponseDto);
                })
                .then()
                .doOnError( error -> log.error("Id {} 에 대한 알림을 처리하는 동안 오류가 발생했습니다", receiver.getId()));
    }

    private Notification createNotification(Members sender, Members receiver, NotificationType notificationType, String content, String url) {
        return Notification.builder()
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
    }



    public Flux<Notification> findUnreadNotificationList(Long memberId){
        return notificationReactiveRepository.findAllByReceiverIdAndReadStatus(
                memberId, false)
                .onBackpressureBuffer()
                .doOnError( error -> log.error("Id {} 에 대한 알림을 처리하는 동안 오류가 발생했습니다",memberId));
    }

}