package com.mogakko.be_final.domain.directMessage.dto.response;

import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class DirectMessageSearchResponseDto {
    private Long id;
    private String content;
    private Boolean isRead;
    private String senderNickname;
    private String receiverNickname;
    private String createdAt;


    public DirectMessageSearchResponseDto(DirectMessage directMessage) {
        this.id = directMessage.getId();
        this.content = directMessage.getContent();
        this.isRead = directMessage.isRead();
        this.senderNickname = directMessage.getSender().getNickname();
        this.receiverNickname = directMessage.getReceiver().getNickname();
        this.createdAt = directMessage.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
