package com.mogakko.be_final.domain.members.dto.response;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.List;

@Getter
@NoArgsConstructor
public class UserPageResponseDto {

    private Members member;
    private String totalTimer;
    private String totalTimerWeek;
    private Boolean isFriend;

    public UserPageResponseDto(Members member, String totalTimer, String totalTimerWeek) {
        this.member = member;
        this.totalTimer = totalTimer;
        this.totalTimerWeek = totalTimerWeek;
    }
    public UserPageResponseDto(Members member, String totalTimer, String totalTimerWeek, boolean isFriend) {
        this.member = member;
        this.totalTimer = totalTimer;
        this.totalTimerWeek = totalTimerWeek;
        this.isFriend = isFriend;
    }
}
