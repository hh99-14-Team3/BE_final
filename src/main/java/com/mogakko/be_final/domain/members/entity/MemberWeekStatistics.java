package com.mogakko.be_final.domain.members.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberWeekStatistics {

    @Id
    private String email;

    @Column
    private long sun = 0L;

    @Column
    private long mon = 0L;

    @Column
    private long tue = 0L;

    @Column
    private long wed = 0L;

    @Column
    private long thu = 0L;

    @Column
    private long fri = 0L;

    @Column
    private long sat = 0L;

    public void addSun(long time) {
        this.sun += time;
    }

    public void addMon(long time) {
        this.mon += time;
    }

    public void addTue(long time) {
        this.tue += time;
    }

    public void addWed(long time) {
        this.wed += time;
    }

    public void addThu(long time) {
        this.thu += time;
    }

    public void addFri(long time) {
        this.fri += time;
    }

    public void addSat(long time) {
        this.sat += time;
    }

}
