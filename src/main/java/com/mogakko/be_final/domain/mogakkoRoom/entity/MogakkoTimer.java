package com.mogakko.be_final.domain.mogakkoRoom.entity;

import com.mogakko.be_final.util.Timestamped;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;

@Entity
@NoArgsConstructor
public class MogakkoTimer extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Time mogakkoTimer;

    @Column
    private String nickname;

    public MogakkoTimer(Time mogakkoTimer, String nickname) {
        this.mogakkoTimer = mogakkoTimer;
        this.nickname = nickname;
    }
}
