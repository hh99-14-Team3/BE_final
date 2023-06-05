package com.mogakko.be_final.domain.mogakkoRoom.repository;

import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoTimer;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MogakkoTimerRepository extends JpaRepository<MogakkoTimer, Long> {
    @Query(value = "SELECT TIME_TO_SEC(mogakkoTimer) FROM MogakkoTimer where nickname =:nickname")
    List<Long> findAllByNicknameAndMogakkoTimer(@Param("nickname") String nickname);

    @Query("SELECT TIME_TO_SEC(m.mogakkoTimer) FROM MogakkoTimer m WHERE m.nickname = :nickname AND m.createdAt >= :weekAgo")
    List<Long> findAllByNicknameAndMogakkoTimer(@Param("nickname") String nickname, @Param("weekAgo") LocalDateTime weekAgo);

}
