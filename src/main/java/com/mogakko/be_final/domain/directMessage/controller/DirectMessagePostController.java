package com.mogakko.be_final.domain.directMessage.controller;

import com.mogakko.be_final.domain.directMessage.dto.request.DirectMessageDeleteRequestDto;
import com.mogakko.be_final.domain.directMessage.dto.request.DirectMessageSendRequestDto;
import com.mogakko.be_final.domain.directMessage.service.DirectMessagePostService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "DM 관련 POST 요청 API", description = "DM 관련 POST 요청 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/directMessage")
public class DirectMessagePostController {
    private final DirectMessagePostService directMessagePostService;

    @Operation(summary = "쪽지 전송 API", description = "쪽지를 전송하는 메서드입니다.")
    @PostMapping("/send")
    public ResponseEntity<Message> sendDirectMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody DirectMessageSendRequestDto requestDto) {
        return directMessagePostService.sendDirectMessage(userDetails.getMember(), requestDto);
    }

    @Operation(summary = "쪽지 삭제 API", description = "특정 쪽지를 삭제하는 메서드입니다.")
    @PostMapping("/delete")
    public ResponseEntity<Message> deleteDirectMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @RequestBody DirectMessageDeleteRequestDto directMessageDeleteRequestDto) {
        return directMessagePostService.deleteDirectMessage(userDetails.getMember(), directMessageDeleteRequestDto.getDirectMessageList());
    }
}
