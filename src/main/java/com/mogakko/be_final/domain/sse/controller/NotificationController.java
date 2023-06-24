package com.mogakko.be_final.domain.sse.controller;

import com.mogakko.be_final.domain.sse.service.NotificationSearchService;
import com.mogakko.be_final.domain.sse.service.NotificationService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
@Tag(name = "SSE 알람 관련 API", description = "SSE 알람 관련 API 입니다.")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSearchService notificationSearchService;

    @Operation(summary = "SSE 구독 API", description = "SSE 구독하는 메서드입니다. 해당 주소로 get 요청시 연결됩니다.")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<ServerSentEvent<Object>> subscribe(@AuthenticationPrincipal UserDetailsImpl membersDetails) {
        return notificationService.subscribeWithSuccessMessage(membersDetails.getMember());
    }

    @Operation(summary = "SSE 알림 조회 API", description = "SSE 생성된 알림을 조회 하는 메서드입니다.")
    @GetMapping("/notification")
    public ResponseEntity<Message> getAllNotification(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationSearchService.getMyNotification(userDetails);
    }

    @PostMapping("/notification/read/{notificationId}")
    public ResponseEntity<Message> readNotification(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID notificationId){
        return notificationSearchService.readNotification(userDetails.getMember().getId(), notificationId);
    }

    @PostMapping("/notification/read/all")
    public ResponseEntity<Message> readAllNotification(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return notificationSearchService.readAllNotification(userDetails.getMember().getId());
    }
}
