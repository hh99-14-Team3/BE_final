package com.mogakko.be_final.domain.members.dto;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.List;

@Getter
@NoArgsConstructor
public class MyPageResponseDto {

    private List<MogakkoRoomMembers> mogakkoRoomList;
    private Time time;
    private Members member;

    public MyPageResponseDto(List<MogakkoRoomMembers> mogakkoRoomList, Time time, Members member){
        this.mogakkoRoomList = mogakkoRoomList;
        this.time = time;
        this.member = member;
    }
}
