package com.mogakko.be_final.domain.mogakkoRoom.repository;

import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MogakkoRoomMembersRepository extends JpaRepository<MogakkoRoomMembers, Long> {

    @Where(clause = "is_entered = true")
    Optional<MogakkoRoomMembers> findByMogakkoRoomAndMemberId(MogakkoRoom mogakkoRoom, Long memberId);

    Optional<MogakkoRoomMembers> findByMemberIdAndMogakkoRoomAndIsEntered(Long userId, MogakkoRoom mogakkoRoom, boolean isEntered);

    List<MogakkoRoomMembers> findAllByMogakkoRoomAndIsEntered(MogakkoRoom mogakkoRoom, boolean isEntered);
}
