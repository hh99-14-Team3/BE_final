package com.mogakko.be_final.domain.sse.repository;

import com.mogakko.be_final.domain.sse.entity.Notification;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import reactor.core.publisher.Flux;


public interface NotificationReactiveRepository extends ReactiveCassandraRepository<Notification, Long> {

    Flux<Notification> findAllByReceiverIdAndReadStatus(Long receiverId, boolean status);
}
