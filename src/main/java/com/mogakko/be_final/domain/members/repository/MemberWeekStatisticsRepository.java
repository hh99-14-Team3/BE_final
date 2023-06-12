package com.mogakko.be_final.domain.members.repository;

import com.mogakko.be_final.domain.members.entity.MemberWeekStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberWeekStatisticsRepository extends JpaRepository<MemberWeekStatistics, String> {

}
