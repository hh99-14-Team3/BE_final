package com.mogakko.be_final.domain.sse.repository;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.sse.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAll();

    List<Notification> findAllByReceiverAndIsReadFalse(Members members);
}
