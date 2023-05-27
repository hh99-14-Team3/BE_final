package com.mogakko.be_final.domain.mogakkoRoom.dto.response;

import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MogakkoRoomMemberResponseDto {

    // 방 번호
    private MogakkoRoom mogakkoRoom;

    private Long memberId;

    private String nickname;

    private String email;

    private String profileImage;

    private String enterRoomToken;

    // 방에 들어온 시간
    private LocalDateTime roomEnterTime;

    // 방에서 나간 시간
    private LocalDateTime roomExitTime;

    // 방에서 총 머문 일자
    private Long roomStayDay;

    // 방에서 총 머문 시간
    private Time roomStayTime;

    public MogakkoRoomMemberResponseDto(MogakkoRoomMembers responseDto){
        this.mogakkoRoom = responseDto.getMogakkoRoom();
        this.memberId = responseDto.getMemberId();
        this.nickname = responseDto.getNickname();
        this.profileImage = responseDto.getProfileImage();
        this.enterRoomToken = responseDto.getEnterRoomToken();
        this.roomEnterTime = responseDto.getRoomEnterTime();
        this.roomExitTime = responseDto.getRoomExitTime();
        this.roomStayDay = responseDto.getRoomStayDay();
        this.roomStayTime = responseDto.getRoomStayTime();
    }
}
