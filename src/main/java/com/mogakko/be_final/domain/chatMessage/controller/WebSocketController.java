package com.mogakko.be_final.domain.chatMessage.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/chat") // 클라이언트로부터의 메시지를 수신할 엔드포인트
    @SendTo("/topic/messages") // 구독 중인 클라이언트에게 메시지를 브로드캐스트
    public String sendMessage(String message) {
        return message;
    }
}
