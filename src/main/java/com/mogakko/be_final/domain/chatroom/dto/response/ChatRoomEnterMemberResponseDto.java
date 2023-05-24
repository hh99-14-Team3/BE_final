package com.mogakko.be_final.domain.chatroom.dto.response;

import com.mogakko.be_final.domain.chatroom.entity.ChatRoomMembers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomEnterMemberResponseDto {

    // 채팅방
    private String sessionId;

    // 멤버
    private Long memberId;

    private String nickname;

    private String email;

    private String ProfileImage;

    /*해당 방에 머물은 시간(재 접속 할경우 +해서 계산됨)*/
    private Time stayTime;

    /*해당 방에 머물은 Days -> stayTime이 24시간이 넘을시 1씩 추가됨*/
    private Long stayDay;

    // 방장인지 확인
    private boolean roomMaster;

    // 방 접속시 모든 사용자의 정보를 보내기 때문에 현재 접속한 유저 구분용도
    private boolean nowMember;

    private String enterRoomToken;

    public ChatRoomEnterMemberResponseDto(ChatRoomMembers entity, boolean roomMaster, boolean nowMembers){
        this.sessionId = entity.getSessionId();
        this.memberId = entity.getMemberId();
        this.nickname = entity.getNickname();
        this.email = entity.getEmail();
        this.ProfileImage = entity.getProfileImage();
        this.enterRoomToken = entity.getEnterRoomToken();
        this.stayTime = entity.getRoomStayTime();
        this.stayDay = entity.getRoomStayDay();
        this.roomMaster = roomMaster;
        this.nowMember = nowMembers;
    }

}
