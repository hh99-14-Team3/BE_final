package com.mogakko.be_final.domain.chatroom.entity;

import com.mogakko.be_final.domain.members.entity.Members;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends Timestamped {

    // 방 번호
    @Id
    private String sessionId;

    // 방 제목
    @Column(nullable = false)
    private String title;

    // 방 공개여부
    @Column
    private boolean isOpened;

    // 비공개 시 사용할 패스워드
    @Column
    private String password;

    // 주특기 언어 카테고리
    @Column
    private LanguageEnum language;

    // 방장
    @Column
    private String master;

    // 방장 고유번호
    @Column
    private Long masterMemberId;

    // 방 최대 인원
    @Column
    private Long maxMembers;

    // 현재 방 인원
    @Column
    private Long cntMembers;

    // 방 삭제 여부
    @Column
    private Boolean isDeleted;

    // 방이 삭제된 시간
    @Column
    private LocalDateTime roomDeleteTime;

    // 위치 값 - 경도
    @Column
    private double longitudeX;

    // 위치 값 - 위도
    @Column
    private double latitudeY;


    //    @OneToMany(mappedBy = "sessionId", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    //    private List<ChatRoomMembers> chatRoomMembersList;

    public void updateCntMembers(Long cntMembers) {
        this.cntMembers = cntMembers;
    }

    public void deleteRoom(LocalDateTime roomDeleteTime) {
        this.isDeleted = true;
        this.roomDeleteTime = roomDeleteTime;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    // TODO : 뭐임?
    public boolean validateMembers(Members member) {
        return !this.master.equals(member.getSocialUid());
    }

}