package com.mogakko.be_final.domain.sse.controller;

import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.repository.EmitterRepositoryImpl;
import com.mogakko.be_final.domain.sse.repository.NotificationRepository;
import com.mogakko.be_final.domain.sse.service.NotificationSearchService;
import com.mogakko.be_final.domain.sse.service.NotificationService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
@Tag(name = "SSE 알람 관련 API", description = "SSE 알람 관련 API 입니다.")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final NotificationSearchService notificationSearchService;

    @Operation(summary = "SSE 구독 API", description = "SSE 구독하는 메서드입니다. 해당 주소로 get 요청시 연결됩니다.")
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl membersDetails,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {

        return notificationService.subscribe(membersDetails.getMember().getId(), lastEventId);
    }

    @Operation(summary = "SSE 알림 조회 API", description = "SSE 생성된 알림을 조회 하는 메서드입니다.")
    @GetMapping("/notification")
    public ResponseEntity<Message> getAllNotification(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Notification> notifications = notificationRepository.findAll();
        return notificationSearchService.getMyNotification(userDetails);

    }
}
