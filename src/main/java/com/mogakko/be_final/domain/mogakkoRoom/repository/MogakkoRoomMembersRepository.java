package com.mogakko.be_final.domain.mogakkoRoom.repository;

import com.mogakko.be_final.domain.members.dto.MyMogakkoRoomListDto;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

public interface MogakkoRoomMembersRepository extends JpaRepository<MogakkoRoomMembers, Long> {

    @Where(clause = "is_entered = true")
    Optional<MogakkoRoomMembers> findByMogakkoRoomAndMemberId(MogakkoRoom mogakkoRoom, Long memberId);

    Optional<MogakkoRoomMembers> findByMemberIdAndMogakkoRoomAndIsEntered(Long userId, MogakkoRoom mogakkoRoom, boolean isEntered);

    List<MogakkoRoomMembers> findAllByMogakkoRoomAndIsEntered(MogakkoRoom mogakkoRoom, boolean isEntered);

    @Query("SELECT SEC_TO_TIME(sum(TIME_TO_SEC(m.roomStayTime))) from MogakkoRoomMembers m where m.email = :email and m.mogakkoRoom.isDeleted = false")
    Time findRoomStayTimeByEmail(@Param("email") String email);

    @Query("SELECT m FROM MogakkoRoomMembers m WHERE m.email = :email AND m.mogakkoRoom.isDeleted = false")
    List<MogakkoRoomMembers> findAllByEmailAndMogakkoRoomIsDeletedFalse(@Param("email") String email);
}
