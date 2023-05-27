package com.mogakko.be_final.domain.mogakkoRoom.repository;

import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MogakkoRoomTimeRepository extends JpaRepository<MogakkoRoomTime, Long> {
    MogakkoRoomTime findByMember(String mail);

}
