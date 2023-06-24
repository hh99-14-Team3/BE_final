package com.mogakko.be_final.domain.sse.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Column;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Table
@Getter
@NoArgsConstructor
public class Notification {
    @PrimaryKeyColumn(name = "receiver_id", type = PrimaryKeyType.PARTITIONED, ordinal = 0)
    private Long receiverId;

    @PrimaryKeyColumn(name = "read_status", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 1)
    private boolean readStatus;

    @PrimaryKeyColumn(name = "notification_id", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 2)
    @CassandraType(type = CassandraType.Name.TIMEUUID)
    private UUID notificationId;

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    private Instant createdAt;

    @Column
    private String content;

    @Column("sender_nickname")
    private String senderNickname;

    @Column("receiver_nickname")
    private String receiverNickname;

    @Column("sender_profile_url")
    private String senderProfileUrl;

    @Column
    private String url;

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationType type;


    @Builder
    public Notification(Long receiverId, boolean readStatus, UUID notificationId, Instant createdAt, String content, String senderNickname, String receiverNickname, String url, NotificationType type, String senderProfileUrl) {
        this.receiverId = receiverId;
        this.readStatus = readStatus;
        this.notificationId = notificationId;
        this.createdAt = createdAt;
        this.content = content;
        this.senderNickname = senderNickname;
        this.receiverNickname = receiverNickname;
        this.url = url;
        this.type = type;
        this.senderProfileUrl = senderProfileUrl;
    }
    public void changeReadStatus(){
        this.readStatus = true;
    }
}

