package com.mogakko.be_final.domain.directMessage.controller;

import com.mogakko.be_final.domain.directMessage.dto.DirectMessageSearchResponseDto;
import com.mogakko.be_final.domain.directMessage.dto.DirectMessageSendRequestDto;
import com.mogakko.be_final.domain.directMessage.service.DirectMessageSearchService;
import com.mogakko.be_final.domain.directMessage.service.DirectMessageSendService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import com.mogakko.be_final.util.Timestamped;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DM 관련 API", description = "DM 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/directMessage")
public class DirectMessageController{
    private final DirectMessageSendService directMessageSendService;
    private final DirectMessageSearchService directMessageSearchService;

    @PostMapping("/send")
    public ResponseEntity<Message> sendDirectMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     DirectMessageSendRequestDto directMessageSendRequestDto){
        return directMessageSendService.sendDirectMessage(userDetails, directMessageSendRequestDto);
    }

    @GetMapping("/received")
    public ResponseEntity<Message> receivedDirectMessage(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return directMessageSearchService.searchReceivedMessage(userDetails);
    }

    @GetMapping("/sent")
    public ResponseEntity<Message> sentDirectMessage(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return directMessageSearchService.searchSentMessage(userDetails);
    }

    @GetMapping("/read/{messageId}")
    public ResponseEntity<Message> readDirectMessage(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long messageId){
        return directMessageSearchService.readDirectMessage(userDetails, messageId);
    }


}
