package com.mogakko.be_final.domain.mogakkoRoom.repository;

import com.mogakko.be_final.domain.members.dto.response.LanguageDto;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembersLanguageStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MogakkoRoomMembersLanguageStatisticsRepository extends JpaRepository<MogakkoRoomMembersLanguageStatistics, Long> {
    @Query("SELECT NEW com.mogakko.be_final.domain.members.dto.response.LanguageDto(m.language, COUNT(m.language), (SELECT COUNT(ml.language) FROM MogakkoRoomMembersLanguageStatistics ml WHERE ml.email = :email)) FROM MogakkoRoomMembersLanguageStatistics m WHERE m.email = :email GROUP BY m.language")
    List<LanguageDto> countByEmailAndLanguage(@Param("email") String email);
}
