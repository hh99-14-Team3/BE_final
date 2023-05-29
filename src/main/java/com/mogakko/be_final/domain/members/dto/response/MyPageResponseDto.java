package com.mogakko.be_final.domain.members.dto.response;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomTime;
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

    public MyPageResponseDto(List<MogakkoRoomMembers> mogakkoRoomList, Time mogakkoTotalTime, Members member) {
        this.mogakkoRoomList = mogakkoRoomList;
        this.mogakkoTotalTime = mogakkoTotalTime;
        this.member = member;
    }
}
