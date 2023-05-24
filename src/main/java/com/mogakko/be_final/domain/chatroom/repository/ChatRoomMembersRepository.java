package com.mogakko.be_final.domain.chatroom.repository;

import com.mogakko.be_final.domain.chatroom.entity.ChatRoomMembers;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMembersRepository extends JpaRepository<ChatRoomMembers, Long> {

    @Where(clause = "is_deleted = true")
    Optional<ChatRoomMembers> findBySessionIdAndMemberId(String sessionId, Long userId);

    Optional<ChatRoomMembers> findByMemberIdAndSessionIdAndIsEntered(Long userId, String sessionId, boolean isEntered);

    List<ChatRoomMembers> findAllBySessionIdAndIsEntered(String sessionId, boolean isEntered);

}
