package com.mogakko.be_final.domain.sse.entity;

import lombok.*;

import javax.persistence.*;

@Table
@Getter
@NoArgsConstructor
public class Notification {
    @EmbeddedId
    private NotificationKey key;

    @Column
    private String content;

    @Column(name = "sender_nickname")
    private String senderNickname;

    @Column(name = "receiver_nickname")
    private String receiverNickname;

    @Column
    private String url;

    @Builder
    public Notification(NotificationKey key, String senderNickname, String receiverNickname, String content, String url) {
        this.key = new NotificationKey(key.getReceiverId(), key.getCreatedAt(), key.getNotificationType());
        this.senderNickname = senderNickname;
        this.receiverNickname = receiverNickname;
        this.url = url;
        this.content = content;
    }

}

