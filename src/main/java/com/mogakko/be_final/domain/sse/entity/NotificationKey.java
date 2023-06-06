package com.mogakko.be_final.domain.sse.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.Instant;

@PrimaryKeyClass
@Getter
@NoArgsConstructor
public class NotificationKey implements Serializable {
    @PrimaryKeyColumn(name = "receiver_id", type = PrimaryKeyType.PARTITIONED)
    private Long receiverId;

    @PrimaryKeyColumn(name = "created_at", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @PrimaryKeyColumn(name = "notification_type", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private NotificationType notificationType;

    public NotificationKey(Long receiverId, Instant createdAt, NotificationType notificationType) {
        this.receiverId = receiverId;
        this.createdAt = createdAt;
        this.notificationType = notificationType;
    }
}
