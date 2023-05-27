package com.mogakko.be_final.domain.mogakkoRoom.repository;

import com.mogakko.be_final.domain.mogakkoRoom.dto.response.NeighborhoodResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
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

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    Optional<ChatRoom> findBySessionIdAndIsDeleted(String chatRoomId, boolean isDeleted);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<MogakkoRoom> findBySessionId(String chatRoomId);


    // 반경 5km 이내 모각코 조회
    @Query("SELECT cr FROM MogakkoRoom cr WHERE (6371 * acos(cos(radians(:lon)) * cos(radians(cr.lat)) * cos(radians(cr.lon) - radians(:lat)) + sin(radians(:lon)) * sin(radians(cr.lat)))) <= 5")
    List<MogakkoRoom> findAllByLonAndLat(@Param("lat") double lat, @Param("lon") double lon);

    // 반경 5km 이내 모각코 조회 (언어 선택)
    @Query("SELECT cr FROM MogakkoRoom cr WHERE (6371 * acos(cos(radians(:lon)) * cos(radians(cr.lat)) * cos(radians(cr.lon) - radians(:lat)) + sin(radians(:lon)) * sin(radians(cr.lat)))) <= 5" +
            "AND cr.language = :languageEnum")
    List<MogakkoRoom> findAllByLonAndLatAndLanguage(@Param("lat") double lat, @Param("lon") double lon, @Param("language") LanguageEnum language);


    // 모각코 검색
    @Query(value = "SELECT c FROM MogakkoRoom c WHERE c.title LIKE %:searchKeyword%")
    List<MogakkoRoom> findAllBySearchKeyword(@Param("searchKeyword") String searchKeyword);

    @Query(value = "SELECT c FROM MogakkoRoom c WHERE c.title LIKE %:searchKeyword% AND c.language = :languageEnum")
    List<MogakkoRoom> findAllBySearchKeywordAndLanguage(@Param("searchKeyword") String searchKeyword, @Param("languageEnum") LanguageEnum languageEnum);

    @Query(value = "SELECT c FROM MogakkoRoom c WHERE c.language = :languageEnum")
    List<MogakkoRoom> findAllByLanguage(@Param("languageEnum") LanguageEnum languageEnum);

    @Query("SELECT NEW com.mogakko.be_final.domain.mogakkoRoom.dto.response.NeighborhoodResponseDto(COUNT(m), m.neighborhood) FROM MogakkoRoom m GROUP BY m.neighborhood ORDER BY COUNT(m) DESC")
    List<NeighborhoodResponseDto> findByTop8DongNeCount();

}
