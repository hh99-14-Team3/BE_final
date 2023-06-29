package com.mogakko.be_final.domain.mogakkoRoom.dto.response;

import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MogakkoRoomReadResponseDto {
    private MogakkoRoom mogakkoRoom;
    private String elapsedTime;

    public MogakkoRoomReadResponseDto(MogakkoRoom mogakkoRoom, String elapsedTime) {
        this.mogakkoRoom = mogakkoRoom;
        this.elapsedTime = elapsedTime;
    }
}
