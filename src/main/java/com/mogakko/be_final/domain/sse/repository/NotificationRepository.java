package com.mogakko.be_final.domain.sse.repository;

import com.mogakko.be_final.domain.sse.entity.Notification;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository extends CassandraRepository<Notification, Long> {

//    Optional<Notification> findByKey(NotificationKey key);
//
//    List<Notification> findAllByKey(NotificationKey key);

    List<Notification> findByReceiverId(Long receiverId);

    List<Notification> findAllByReceiverId(Long receiverId);

    List<Notification> findAllByReceiverIdAndReadStatusAndCreatedAtLessThan(Long receiverId, boolean status, Instant createdAt);

}
