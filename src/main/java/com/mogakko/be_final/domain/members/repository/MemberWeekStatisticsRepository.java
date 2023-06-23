package com.mogakko.be_final.domain.members.repository;

import com.mogakko.be_final.domain.members.entity.MemberWeekStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberWeekStatisticsRepository extends JpaRepository<MemberWeekStatistics, String> {

    List<MemberWeekStatistics> findTop8ByOrderByWeekTotalTimeDesc();

    void deleteByEmail(String email);
}
