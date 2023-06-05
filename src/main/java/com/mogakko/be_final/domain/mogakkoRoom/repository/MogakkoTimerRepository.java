package com.mogakko.be_final.domain.mogakkoRoom.repository;

import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoTimer;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Time;
import java.time.Duration;
import java.util.List;

public interface MogakkoTimerRepository extends JpaRepository<MogakkoTimer, Long> {
    @Query(value = "SELECT TIME_TO_SEC(mogakkoTimer) FROM MogakkoTimer where nickname =:nickname")
    List<Long> findAllByNicknameAndMogakkoTimer(@Param("nickname") String nickname);

//    @Query(value = "SELECT TIME_TO_SEC(mogakkoTimer) FROM MogakkoTimer where DATE_COLUMN BETWEEN DATE_ADD(NOW(), INTERVAL -1 WEEK ) AND NOW()")
//    List<Long> findAllByNicknameAndMogakkoTimerAndCreatedAt(@Param("nickname") String nickname);





}
