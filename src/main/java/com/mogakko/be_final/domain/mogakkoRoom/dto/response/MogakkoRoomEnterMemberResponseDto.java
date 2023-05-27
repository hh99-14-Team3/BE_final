package com.mogakko.be_final.domain.mogakkoRoom.dto.response;

import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MogakkoRoomEnterMemberResponseDto {

    private MogakkoRoom mogakkoRoom;

    private Long memberId;

    private String nickname;

    private String email;

    private String ProfileImage;

    // 해당 방에 머문 시간 (재접속 할 경우 합산)
    private Time stayTime;

    // 해당 방에 머문 Days -> stayTime이 24시간이 넘을시 1씩 추가됨
    private Long stayDay;

    // 방장인지 확인
    private boolean roomMaster;

    // 방 접속시 모든 사용자의 정보를 보내기 때문에 현재 접속한 유저 구분용도
    private boolean nowMember;

    private String enterRoomToken;

    public MogakkoRoomEnterMemberResponseDto(MogakkoRoomMembers mogakkoRoomMembers, boolean roomMaster, boolean nowMembers){
        this.mogakkoRoom = mogakkoRoomMembers.getMogakkoRoom();
        this.memberId = mogakkoRoomMembers.getMemberId();
        this.nickname = mogakkoRoomMembers.getNickname();
        this.ProfileImage = mogakkoRoomMembers.getProfileImage();
        this.enterRoomToken = mogakkoRoomMembers.getEnterRoomToken();
        this.stayTime = mogakkoRoomMembers.getRoomStayTime();
        this.stayDay = mogakkoRoomMembers.getRoomStayDay();
        this.roomMaster = roomMaster;
        this.nowMember = nowMembers;
    }
}
