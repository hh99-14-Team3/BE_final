package com.mogakko.be_final.domain.mogakkoRoom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mogakko.be_final.util.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE mogakko_room SET is_deleted = true WHERE session_id = ? ")
public class MogakkoRoom extends Timestamped {

    // 방 번호
    @Id
    private String sessionId;

    // 방 제목
    @Column(nullable = false)
    private String title;

    // 방 공개여부
    @Column(name = "is_opened")
    private boolean isOpened;

    // 비공개 시 사용할 패스워드
    @Column
    @JsonIgnore
    private String password;

    // 주특기 언어 카테고리
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LanguageEnum language;

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
    @Builder.Default
    private boolean isDeleted = false;

    // 방이 삭제된 시간
    @Column
    private LocalDateTime roomDeleteTime;

    @Column
    private double lon;

    @Column
    private double lat;

    @Column
    private String neighborhood;


    public void updateCntMembers(Long cntMembers) {
        this.cntMembers = cntMembers;
    }

    public void deleteRoom() {
        this.isDeleted = true;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}