package com.mogakko.be_final.domain.mogakkoRoom.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MogakkoRoomMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMemberId;

    // 방 번호
    @ManyToOne
    @JoinColumn(name = "session_id")
    private MogakkoRoom mogakkoRoom;

    @Column
    private Long memberId;

    @Column
    private String nickname;

    @Column
    private String profileImage;

    @Column
    private String enterRoomToken;

    // 입/퇴장 여부
    @Column
    @Builder.Default
    private boolean isEntered = false;

    // 방에 들어온 시간
    @Column
    private LocalDateTime roomEnterTime;

    // 방에서 나간 시간 기록
    @Column
    private LocalDateTime roomExitTime;

    // 방에서 총 머문 일자 (재 입장 다 합쳐서)
    @Column
    private Long roomStayDay;

    // 방에서 총 머문 시간 (재 입장 다 합쳐서)
    @Column
    private Time roomStayTime;


    // 방에서 나가는 경우
    public void deleteRoomMembers(LocalDateTime roomExitTime, LocalTime roomStayTime, Long roomStayDay) {
        this.isEntered = false;
        this.roomExitTime = roomExitTime;
        this.roomStayTime = Time.valueOf(roomStayTime);
        this.roomStayDay = roomStayDay;
    }

    // 방에 재입장 하는 경우
    public void reEnterRoomMembers(String enterRoomToken, String nickname) {
        this.isEntered = true;
        this.roomEnterTime = Timestamp.valueOf(LocalDateTime.now()).toLocalDateTime();
        this.roomExitTime = null;
        this.enterRoomToken = enterRoomToken;
        this.nickname = nickname;
    }

}

