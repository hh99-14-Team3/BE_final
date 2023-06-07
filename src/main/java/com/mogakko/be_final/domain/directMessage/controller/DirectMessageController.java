package com.mogakko.be_final.domain.directMessage.controller;

import com.mogakko.be_final.domain.directMessage.dto.DirectMessageSendRequestDto;
import com.mogakko.be_final.domain.directMessage.service.DirectMessageSearchService;
import com.mogakko.be_final.domain.directMessage.service.DirectMessageSendService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DM 관련 API", description = "DM 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/directMessage")
public class DirectMessageController {
    private final DirectMessageSendService directMessageSendService;
    private final DirectMessageSearchService directMessageSearchService;

    @Operation(summary = "쪽지 전송 API", description = "쪽지를 전송하는 메서드입니다.")
    @PostMapping("/send")
    public ResponseEntity<Message> sendDirectMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody DirectMessageSendRequestDto directMessageSendRequestDto) {
        return directMessageSendService.sendDirectMessage(userDetails.getMember(), directMessageSendRequestDto);
    }

    @Operation(summary = "받은 쪽지 조회 API", description = "받은 쪽지를 조회하는 메서드입니다.")
    @GetMapping("/received")
    public ResponseEntity<Message> receivedDirectMessage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return directMessageSearchService.searchReceivedMessage(userDetails.getMember());
    }

    @Operation(summary = "보낸 쪽지 조회 API", description = "보낸 쪽지를 조회하는 메서드입니다.")
    @GetMapping("/sent")
    public ResponseEntity<Message> sentDirectMessage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return directMessageSearchService.searchSentMessage(userDetails.getMember());
    }

    @Operation(summary = "쪽지 조회 API", description = "특정 쪽지를 조회하는 메서드입니다.")
    @GetMapping("/read/{messageId}")
    public ResponseEntity<Message> readDirectMessage(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long messageId) {
        return directMessageSearchService.readDirectMessage(userDetails.getMember(), messageId);
    }


}
