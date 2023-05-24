package com.mogakko.be_final.domain.chatroom.repository;

import com.mogakko.be_final.domain.chatroom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    void delete(ChatRoom room);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ChatRoom> findBySessionIdAndIsDeleted(String chatRoomId, boolean isDeleted);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ChatRoom> findBySessionId(String chatRoomId);


//    // 반경 5km 이내 모각코 조회 (언어 선택안함)
//    @Query("SELECT cr, (6371 * acos(cos(radians(:myLatitudeY)) * cos(radians(cr.latitudeY)) * cos(radians(cr.longitudeX) - radians(:myLongitudeX)) + sin(radians(:myLatitudeY)) * sin(radians(cr.latitudeY)))) AS distance FROM ChatRoom cr HAVING distance <= 5")
//    List<ChatRoom> findAllByLongitudeXAndLatitudeY(@Param("myLatitudeY") double myLatitudeY, @Param("myLongitudeX") double myLongitudeX);
//
//    // 반경 5km 이내 모각코 조회 (언어 선택)
//    @Query("SELECT cr, (6371 * acos(cos(radians(:myLatitudeY)) * cos(radians(cr.latitudeY)) * cos(radians(cr.longitudeX) - radians(:myLongitudeX)) + sin(radians(:myLatitudeY)) * sin(radians(cr.latitudeY)))) AS distance FROM ChatRoom cr WHERE cr.language = :language HAVING distance <= 5")
//    List<ChatRoom> findAllByLongitudeXAndLatitudeYAndLanguage(@Param("myLatitudeY") double myLatitudeY, @Param("myLongitudeX") double myLongitudeX, @Param("language") String language);


    // 모각코 검색
    @Query(value = "SELECT c FROM ChatRoom c WHERE c.title LIKE %:searchKeyword% AND c.language = :languageEnum")
    List<ChatRoom> findAllBySearchKeyword(@Param("searchKeyword") String searchKeyword, @Param("searchKeyword") String languageEnum);


    /*히스토리 채팅방 전체 조회.*/
//    @Query(
//            value = "SELECT distinct room FROM ChatRoom room "
//                    "left JOIN room.chatRoomMembersList members " +
//                    "WHERE room.isDelete = false " +
//                    "AND members.membersId = :membersId " +
//                    "AND room.cntMembers > 0 " +
//                    "ORDER BY room.modifiedAt DESC"
//    )
//    Page<ChatRoom> findByMembersId(@Param("membersId") Long membersId, Pageable pageable);

    // 해당 Category 전체 채팅방 조회하기. -> 스케줄러
//    @Query(
//            value = "SELECT distinct room FROM ChatRoom room "
//                    "left JOIN FETCH room.chatRoomMembersList members " +
//                    "WHERE room.isDelete = false " +
//                    "AND members.isDelete = false " +
//                    "AND room.cntMembers > 0 " +
//                    "and room.category = :category " +
//                    "or room.master = '코딩왕' " +
//                    "and room.category = :category " +
//                    "ORDER BY room.modifiedAt DESC",
//            countQuery = "SELECT count(distinct room) FROM ChatRoom room " +
//                    "left JOIN room.chatRoomMembersList members " +
//                    "WHERE room.isDelete = false " +
//                    "AND members.isDelete = false " +
//                    "AND room.cntMembers > 0 " +
//                    "and room.category = :category " +
//                    "or room.master = '코딩왕'" +
//                    "and room.category = :category "
//    )
    //    Page<ChatRoom> findByCategory(@Param("category") Category category, Pageable pageable);
    List<ChatRoom> findAllByIsDeleted(boolean isDelete);
}
