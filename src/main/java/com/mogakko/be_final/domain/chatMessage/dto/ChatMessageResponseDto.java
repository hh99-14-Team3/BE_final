package com.mogakko.be_final.domain.chatMessage.dto;

import com.mogakko.be_final.domain.chatMessage.entity.RoomMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
@NoArgsConstructor
public class ChatMessageResponseDto {
    private Long messageId;
    private String sessionId;
    private String nickname;
    private String profile;
    private String message;
    private Boolean isDelete;
    private String createdAt;
    private String modifiedAt;

    public ChatMessageResponseDto(RoomMessage roomMessage) {
        this.messageId = roomMessage.getMessageId();
        this.sessionId = roomMessage.getSessionId();
        this.nickname = roomMessage.getNickname();
        this.profile = roomMessage.getProfile();
        this.message = roomMessage.getMessage();
        this.isDelete = roomMessage.isDelete();
        this.createdAt = roomMessage.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.modifiedAt = roomMessage.getModifiedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

}
