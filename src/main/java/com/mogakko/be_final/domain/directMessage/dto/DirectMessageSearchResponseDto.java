package com.mogakko.be_final.domain.directMessage.dto;

import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class DirectMessageSearchResponseDto {
    private Long id;
    private String content;
    private Boolean isRead;
    private String senderNickname;
    private String receiverNickname;
    private LocalDateTime createdAt;

    public DirectMessageSearchResponseDto(DirectMessage directMessage) {
        this.id = directMessage.getId();
        this.content = directMessage.getContent();
        this.isRead = directMessage.isRead();
        this.senderNickname = directMessage.getSender().getNickname();
        this.receiverNickname = directMessage.getReceiver().getNickname();
        this.createdAt = directMessage.getCreatedAt();
    }
}
