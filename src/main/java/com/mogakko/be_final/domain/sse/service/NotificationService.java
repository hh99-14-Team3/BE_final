package com.mogakko.be_final.domain.sse.service;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.sse.dto.NotificationResponseDto;
import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.entity.NotificationType;
import com.mogakko.be_final.domain.sse.repository.EmitterRepository;
import com.mogakko.be_final.domain.sse.repository.EmitterRepositoryImpl;
import com.mogakko.be_final.domain.sse.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();
    private final NotificationRepository notificationRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SseEmitter subscribe(Long memberId, String lastEventId) {
        String emitterId = memberId + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        sendToClient(emitter, emitterId, "EventStream Created. [memberId=" + memberId + "]");

        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    public void send(Members receiver, NotificationType notificationType, String content, String url) {
        Notification notification = createNotification(receiver, notificationType, content, url);
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

    private Notification createNotification(Members receiver, NotificationType notificationType, String content, String url) {
        return Notification.builder()
                .receiver(receiver)
                .notificationType(notificationType)
                .content(content)
                .url(url)
                .build();
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            throw new IllegalArgumentException("Unprocessable SSE Event.", exception);
        }
    }

    public void sendLoginNotification(Members receiver) {
        String content = receiver.getNickname() + " 님은 " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        String url = "/api/members/login";
        send(receiver, NotificationType.LOGIN, content, url);
    }
}

