package com.mogakko.be_final.domain.mogakkoRoom.entity;


import com.mogakko.be_final.util.TimeUtil;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalTime;

@Entity
@NoArgsConstructor
public class MogakkoRoomTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;

    @Column
    private String member;

    @Column
    private Time mogakkoRoomTime;

    public MogakkoRoomTime(String member, Time mogakkoRoomTime) {
        this.member = member;
        this.mogakkoRoomTime = mogakkoRoomTime;
    }

    public void stopTime(LocalTime mogakkoRoomTime) {
        this.mogakkoRoomTime = TimeUtil.addTimes(this.mogakkoRoomTime, Time.valueOf(mogakkoRoomTime));
    }
}