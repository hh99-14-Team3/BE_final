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

    @Column
    private long weekTotalTime = 0L;

    public void addSun(long time) {
        this.sun += time;
        this.weekTotalTime += time;
    }

    public void addMon(long time) {
        this.mon += time;
        this.weekTotalTime += time;
    }

    public void addTue(long time) {
        this.tue += time;
        this.weekTotalTime += time;
    }

    public void addWed(long time) {
        this.wed += time;
        this.weekTotalTime += time;
    }

    public void addThu(long time) {
        this.thu += time;
        this.weekTotalTime += time;
    }

    public void addFri(long time) {
        this.fri += time;
        this.weekTotalTime += time;
    }

    public void addSat(long time) {
        this.sat += time;
        this.weekTotalTime += time;
    }

    public void init() {
        this.sun = 0;
        this.mon = 0;
        this.tue = 0;
        this.wed = 0;
        this.thu = 0;
        this.fri = 0;
        this.sat = 0;
        this.weekTotalTime = 0;
    }
}
