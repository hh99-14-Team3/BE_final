package com.mogakko.be_final.domain.chatMessage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequestDto {
    private String sessionId;
    private String nickname;
    private String profile;
    private String message;
    private String imgByteCode;
}
