package com.mogakko.be_final.domain.members.dto.response;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyMogakkoRoomListDto {

    private MogakkoRoom mogakkoRoom;
    private Members members;

    public MyMogakkoRoomListDto(MogakkoRoom mogakkoRoom, Members members){
        this.mogakkoRoom = mogakkoRoom;
        this.members = members;
    }
}
