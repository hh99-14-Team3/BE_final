package com.mogakko.be_final.domain.sse.controller;

import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.repository.NotificationRepository;
import com.mogakko.be_final.domain.sse.service.NotificationService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "SSE 알람 관련 API", description = "SSE 알람 관련 API 입니다.")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;


    @Operation(summary = "SSE 구독 API", description = "SSE 구독하는 메서드입니다.")
    @GetMapping(value = "/api/subscribe", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl membersDetails,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(membersDetails.getMember().getId(), lastEventId);
    }

    @Operation(summary = "SSE 알림 생성 API", description = "SSE 알림 생성을 하는 메서드입니다.")
    @GetMapping("/api/notification")
    public ResponseEntity<List<Notification>> getAllNotification() {
        List<Notification> notifications = notificationRepository.findAll();
        return ResponseEntity.ok(notifications);

    }
}
