package com.mogakko.be_final.domain.directMessage.controller;

import com.mogakko.be_final.domain.directMessage.service.DirectMessageGetService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "DM 관련 GET 요청 API", description = "DM 관련 GET 요청 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/directMessage")
public class DirectMessageGetController {

    private final DirectMessageGetService directMessageGetService;

    @Operation(summary = "받은 쪽지 조회 API", description = "받은 쪽지를 조회하는 메서드입니다.")
    @GetMapping("/received")
    public ResponseEntity<Message> receivedDirectMessage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return directMessageGetService.searchReceivedMessage(userDetails.getMember());
    }

    @Operation(summary = "보낸 쪽지 조회 API", description = "보낸 쪽지를 조회하는 메서드입니다.")
    @GetMapping("/sent")
    public ResponseEntity<Message> sentDirectMessage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return directMessageGetService.searchSentMessage(userDetails.getMember());
    }

    @Operation(summary = "쪽지 조회 API", description = "특정 쪽지를 조회하는 메서드입니다.")
    @GetMapping("/read/{messageId}")
    public ResponseEntity<Message> readDirectMessage(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long messageId) {
        return directMessageGetService.readDirectMessage(userDetails.getMember(), messageId);
    }
}
