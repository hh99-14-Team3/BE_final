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
    private Members member;
    private String totalTimer;
    private String totalTimerWeek;

    public MyPageResponseDto(List<MogakkoRoomMembers> mogakkoRoomList, Members member, String totalTimer, String totalTimerWeek) {
        this.mogakkoRoomList = mogakkoRoomList;
        this.member = member;
        this.totalTimer = totalTimer;
        this.totalTimerWeek = totalTimerWeek;
    }
}
