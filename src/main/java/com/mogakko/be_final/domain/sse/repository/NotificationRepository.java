package com.mogakko.be_final.domain.sse.repository;

import com.mogakko.be_final.domain.sse.entity.Notification;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends CassandraRepository<Notification, Long> {

    List<Notification> findAllByReceiverId(Long receiverId);

    Optional<Notification> findByReceiverIdAndReadStatusAndNotificationId(
            Long receiverId, boolean readStatus, UUID notificationId);

    List<Notification> findAllByReceiverIdAndReadStatusFalse(Long receiverId);

}
