package com.mogakko.be_final.domain.sse.service;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.sse.dto.response.NotificationResponseDto;
import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.entity.NotificationType;
import com.mogakko.be_final.domain.sse.repository.EmitterRepository;
import com.mogakko.be_final.domain.sse.repository.NotificationRepository;
import com.mogakko.be_final.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static com.mogakko.be_final.exception.ErrorCode.NOTIFICATION_SENDING_FAILED;
import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final MembersRepository membersRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;


    public SseEmitter subscribe(Long memberId, String lastEventId) {
        String emitterId = memberId + "_" + System.currentTimeMillis();

        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        sendToClient(emitter, emitterId, "EventStream Created. [memberId=" + memberId + "]");

        Members eventReceiver = membersRepository.findById(memberId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        List<Notification> missedNotifications = findUnreadNotificationList(eventReceiver.getId());

        for (Notification missedNotification : missedNotifications) {
            String senderNickname = missedNotification.getSenderNickname();
            Members sender = membersRepository.findByNickname(senderNickname).orElseThrow(
                    () -> new CustomException(USER_NOT_FOUND)
            );
            String senderProfileUrl = sender.getProfileImage();
            sendToClient(emitter, emitterId, new NotificationResponseDto(missedNotification, senderProfileUrl), missedNotification);
        }
        return emitter;
    }

    public void send(Members sender, Members receiver, NotificationType notificationType, String content, String url) {
        Notification notification = createNotification(sender, receiver, notificationType, content, url);
        notificationRepository.save(notification);
        String memberId = String.valueOf(receiver.getId());


        emitterRepository.saveEventCache(memberId, notification);

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(memberId);
        sseEmitters.forEach(
                (key, emitter) -> {
                    sendToClient(emitter, key, new NotificationResponseDto(notification, sender.getProfileImage()), notification);
                }
        );
    }

    private Notification createNotification(Members sender, Members receiver, NotificationType notificationType, String content, String url) {
        return Notification.builder()
                .receiverId(receiver.getId())
                .readStatus(false)
                .createdAt(Instant.now())
                .senderNickname(sender.getNickname())
                .receiverNickname(receiver.getNickname())
                .content(content)
                .url(url)
                .type(notificationType)
                .build();
    }

    public void sendToClient(SseEmitter emitter, String emitterId, Object data, Notification notification) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
            if (data instanceof NotificationResponseDto) {
                markAsRead(notification);
            }
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            throw new CustomException(NOTIFICATION_SENDING_FAILED);
        }
    }

    public void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            throw new CustomException(NOTIFICATION_SENDING_FAILED);
        }
    }

    public void markAsRead(Notification notification) {
        notificationRepository.delete(notification);
        notification.changeReadStatus();
        notificationRepository.save(notification);
    }

    @Scheduled(fixedDelay = 30000)  // every 30 seconds
    public void sendHeartbeat() {
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitter();
        sseEmitters.forEach(
                (key, emitter) -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .id(key)
                                .name("heartbeat")
                                .data(""));
                    } catch (IOException exception) {
                        emitterRepository.deleteById(key);
                    }
                }
        );
    }

    public List<Notification> findUnreadNotificationList(Long memberId) {
        return notificationRepository.findAllByReceiverIdAndReadStatusAndCreatedAtLessThan(
                memberId, false, Instant.now(Clock.system(ZoneId.of("Asia/Seoul"))));
    }
}

