package com.mogakko.be_final.domain.chatMessage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {

    private MessageType type; // 메시지 타입
    private String sessionId; // 방번호
    private String nickname; // 메시지 보낸사람
    private String message; // 메시지

    @Builder
    public ChatMessage(MessageType type, String sessionId, String nickname, String message) {
        this.type = type;
        this.sessionId = sessionId;
        this.nickname = nickname;
        this.message = message;
    }

    // 메시지 타입 : 입장, 퇴장, 채팅
    public enum MessageType {
        ENTER, TALK
    }
}
