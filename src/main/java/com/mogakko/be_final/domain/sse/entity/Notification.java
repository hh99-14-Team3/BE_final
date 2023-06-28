package com.mogakko.be_final.domain.sse.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.Instant;

@Table
@Getter
@Builder
@NoArgsConstructor
public class Notification {
    @PrimaryKeyColumn(name = "receiver_id", type = PrimaryKeyType.PARTITIONED, ordinal = 0)
    private Long receiverId;

    @PrimaryKeyColumn(name = "read_status", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 1)
    private boolean readStatus;

    @PrimaryKeyColumn(name = "created_at", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 2)
    private Instant createdAt;

    @Column
    private String content;

    @Column("sendernickname")
    private String senderNickname;

    @Column("receivernickname")
    private String receiverNickname;

    @Column
    private String url;

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Builder
    public Notification(Long receiverId, boolean readStatus, Instant createdAt, String content, String senderNickname, String receiverNickname, String url, NotificationType type) {
        this.receiverId = receiverId;
        this.readStatus = readStatus;
        this.createdAt = createdAt;
        this.content = content;
        this.senderNickname = senderNickname;
        this.receiverNickname = receiverNickname;
        this.url = url;
        this.type = type;
    }

    public Notification(Notification notification) {
        this.receiverId = notification.getReceiverId();
        this.readStatus = notification.isReadStatus();
        this.createdAt = notification.getCreatedAt();
        this.content = notification.getContent();
        this.senderNickname = notification.getSenderNickname();
        this.receiverNickname = notification.getReceiverNickname();
        this.url = notification.getUrl();
        this.type = notification.getType();
    }

    public void changeReadStatus() {
        this.readStatus = true;
    }
}

