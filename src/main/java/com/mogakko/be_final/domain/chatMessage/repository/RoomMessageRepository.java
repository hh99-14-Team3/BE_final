package com.mogakko.be_final.domain.chatMessage.repository;

import com.mogakko.be_final.domain.chatMessage.entity.RoomMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomMessageRepository extends JpaRepository<RoomMessage, Long> {
}
