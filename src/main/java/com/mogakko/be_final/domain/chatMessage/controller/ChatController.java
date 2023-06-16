package com.mogakko.be_final.domain.chatMessage.controller;

import com.mogakko.be_final.domain.chatMessage.dto.ChatMessage;
import com.mogakko.be_final.domain.chatMessage.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatMessageService chatMessageService;

    @ResponseBody
    @MessageMapping("/chat/room")
    public void message(ChatMessage chatMessage) {
        chatMessageService.sendChatMessage(chatMessage);
    }
}
