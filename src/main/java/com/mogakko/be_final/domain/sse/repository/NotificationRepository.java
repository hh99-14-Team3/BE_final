package com.mogakko.be_final.domain.sse.repository;

import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.entity.NotificationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface NotificationRepository extends CassandraRepository<Notification, NotificationKey> {

    Optional<Notification> findByKey(NotificationKey key);

    List<Notification> findByKeyReceiverId(Long receiverId);

    List<Notification> findAllByKeyReceiverId(Long receiverId);



}
