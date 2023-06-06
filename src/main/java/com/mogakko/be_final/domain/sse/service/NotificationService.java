package com.mogakko.be_final.domain.sse.service;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.SelectFrom;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.sse.dto.NotificationResponseDto;
import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.entity.NotificationKey;
import com.mogakko.be_final.domain.sse.entity.NotificationType;
import com.mogakko.be_final.domain.sse.repository.EmitterRepository;
import com.mogakko.be_final.domain.sse.repository.EmitterRepositoryImpl;
import com.mogakko.be_final.domain.sse.repository.NotificationRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    //= new EmitterRepositoryImpl()
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final MembersRepository membersRepository;
    private final KeyComposite keyComposite;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;


    public SseEmitter subscribe(Long memberId, String lastEventId) {
        String emitterId = memberId + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        sendToClient(emitter, emitterId, "EventStream Created. [memberId=" + memberId + "]");

        Members eventReceiver = membersRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
//        List<Notification> missedNotifications = notificationRepository.findByKeyReceiverId(eventReceiver.getId());
//        for (Notification missedNotification : missedNotifications) {
//            sendToClient(emitter, emitterId, new NotificationResponseDto(missedNotification));
//            markAsRead(missedNotification.getKey());
//        }


        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }


    public void send(Members sender, Members receiver, NotificationType notificationType, String content, String url) {
        Notification notification = createNotification(sender , receiver, notificationType, content, url);
        notificationRepository.save(notification);
        String memberId = String.valueOf(receiver.getId());

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(memberId);
        sseEmitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendToClient(emitter, key, new NotificationResponseDto(notification));
                }
        );
    }

    private Notification createNotification(Members sender, Members receiver, NotificationType notificationType, String content, String url) {
        NotificationKey primaryKey = new NotificationKey(receiver.getId(), Instant.now(), notificationType);
        return Notification.builder()
                .key(primaryKey)
                .senderNickname(sender.getNickname())
                .receiverNickname(receiver.getNickname())
                .content(content)
                .url(url)
                .build();
    }


    public void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            throw new CustomException(ErrorCode.NOTIFICATION_SENDING_FAILED);
        }
    }

//    @Transactional
//    public void markAsRead(NotificationKey key) {
//        Notification notification = notificationRepository.findByKey(key)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID: " + key.getReceiverId()));
//        NotificationKey newKey = new NotificationKey(key.getReceiverId(),key.getCreatedAt(), notification.getKey().getNotificationType());
//        Notification newNotification = new Notification(newKey, notification.getSenderNickname(), notification.getReceiverNickname(),notification.getContent(), notification.getUrl());
//        notificationRepository.save(newNotification);
//    }


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

}

