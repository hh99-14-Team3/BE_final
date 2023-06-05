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

    private Time mogakkoTotalTime;
    private Members member;
    private double memberStatusCode;

    public UserPageResponseDto(Members member, Time mogakkoTotalTime) {
        this.member = member;
        this.mogakkoTotalTime = mogakkoTotalTime;
    }
}
