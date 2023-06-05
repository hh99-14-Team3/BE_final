package com.mogakko.be_final.domain.members.dto.response;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.mogakkoRoom.dto.response.MogakkoTimerResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.List;

@Getter
@NoArgsConstructor
public class MyPageResponseDto {

    private List<MogakkoRoomMembers> mogakkoRoomList;
    private Time mogakkoTotalTime;
    private Members member;
    private MogakkoTimerResponseDto mogakkoTime;

    public MyPageResponseDto(List<MogakkoRoomMembers> mogakkoRoomList, Time mogakkoTotalTime, Members member, MogakkoTimerResponseDto mogakkoTime) {
        this.mogakkoRoomList = mogakkoRoomList;
        this.mogakkoTotalTime = mogakkoTotalTime;
        this.member = member;
        this.mogakkoTime = mogakkoTime;
    }
}
