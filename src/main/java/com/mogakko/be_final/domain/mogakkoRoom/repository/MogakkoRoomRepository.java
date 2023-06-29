package com.mogakko.be_final.domain.mogakkoRoom.repository;

import com.mogakko.be_final.domain.mogakkoRoom.dto.response.NeighborhoodResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface MogakkoRoomRepository extends JpaRepository<MogakkoRoom, String> {

    void delete(MogakkoRoom room);

    List<MogakkoRoom> findAllByIsDeleted(boolean isDeleted);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<MogakkoRoom> findBySessionId(String chatRoomId);

    // 반경 12km 이내 모각코 조회
    @Query("SELECT m FROM MogakkoRoom m WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(m.lat)) * cos(radians(m.lon) - radians(:lon)) + sin(radians(:lat)) * sin(radians(m.lat)))) <= 12")
    List<MogakkoRoom> findAllByLatAndLon(@Param("lat") double lat, @Param("lon") double lon);

    // 반경 12km 이내 모각코 조회 (언어 선택)
    @Query("SELECT m FROM MogakkoRoom m WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(m.lat)) * cos(radians(m.lon) - radians(:lon)) + sin(radians(:lat)) * sin(radians(m.lat)))) <= 12" +
            "AND m.language = :language")
    List<MogakkoRoom> findAllByLatAndLonAndLanguage(@Param("lat") double lat, @Param("lon") double lon, @Param("language") LanguageEnum language);

    // 모각코 검색
    @Query(value = "SELECT m FROM MogakkoRoom m WHERE m.title LIKE %:searchKeyword% " +
            "AND (6371 * acos(cos(radians(:lat)) * cos(radians(m.lat)) * cos(radians(m.lon) - radians(:lon)) + sin(radians(:lat)) * sin(radians(m.lat)))) <= 12")
    List<MogakkoRoom> findAllBySearchKeywordAndLatAndLon(@Param("searchKeyword") String searchKeyword, @Param("lat") double lat, @Param("lon") double lon);

    @Query(value = "SELECT m FROM MogakkoRoom m WHERE m.title LIKE %:searchKeyword% AND m.language = :languageEnum " +
            "AND (6371 * acos(cos(radians(:lat)) * cos(radians(m.lat)) * cos(radians(m.lon) - radians(:lon)) + sin(radians(:lat)) * sin(radians(m.lat)))) <= 12")
    List<MogakkoRoom> findAllBySearchKeywordAndLanguageAndLatAndLon(@Param("searchKeyword") String searchKeyword, @Param("languageEnum") LanguageEnum languageEnum, @Param("lat") double lat, @Param("lon") double lon);

    @Query("SELECT NEW com.mogakko.be_final.domain.mogakkoRoom.dto.response.NeighborhoodResponseDto(COUNT(m), m.neighborhood) FROM MogakkoRoom m GROUP BY m.neighborhood ORDER BY COUNT(m) DESC")
    List<NeighborhoodResponseDto> findTop4NeighborhoodsOrderByCountDesc();

}
