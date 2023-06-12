package com.mogakko.be_final.domain.mogakkoRoom.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private String enterRoomToken;

    // 입/퇴장 여부
    @Column
    private boolean isEntered = false;


    // 방에서 나가는 경우
    public void deleteRoomMembers() {
        this.isEntered = false;
    }

    // 방에 재입장 하는 경우
    public void reEnterRoomMembers(String enterRoomToken) {
        this.isEntered = true;
        this.enterRoomToken = enterRoomToken;
    }

}

