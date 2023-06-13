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

    @ManyToOne
    @JoinColumn(name = "session_id")
    private MogakkoRoom mogakkoRoom;

    @Column
    private Long memberId;

    @Column
    private String enterRoomToken;

    @Column
    private boolean isEntered = false;


    // 모각코 퇴장
    public void deleteRoomMembers() {
        this.isEntered = false;
    }

    // 모각코 재입장
    public void reEnterRoomMembers(String enterRoomToken) {
        this.isEntered = true;
        this.enterRoomToken = enterRoomToken;
    }
}

