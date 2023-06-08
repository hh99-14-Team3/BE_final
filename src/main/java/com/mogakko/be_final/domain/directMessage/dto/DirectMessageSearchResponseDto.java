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
    private String createdAt;

    LocalDateTime time = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedNow = time.format(formatter);

    public DirectMessageSearchResponseDto(DirectMessage directMessage) {
        this.id = directMessage.getId();
        this.content = directMessage.getContent();
        this.isRead = directMessage.isRead();
        this.senderNickname = directMessage.getSender().getNickname();
        this.receiverNickname = directMessage.getReceiver().getNickname();
        this.createdAt = formattedNow;
    }
}
