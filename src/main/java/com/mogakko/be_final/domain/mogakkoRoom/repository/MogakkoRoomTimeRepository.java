package com.mogakko.be_final.domain.mogakkoRoom.repository;

import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomTime;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Time;

public interface MogakkoRoomTimeRepository extends JpaRepository<MogakkoRoomTime, Long> {
    @Query("select m.mogakkoRoomTime from MogakkoRoomTime m where m.email = :email")
    Time findMogakkoRoomTimeByEmail(@Param("email") String email);

    MogakkoRoomTime findByEmail(String mail);
}
