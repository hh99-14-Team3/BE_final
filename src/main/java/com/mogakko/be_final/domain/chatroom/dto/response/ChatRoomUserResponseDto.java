package com.mogakko.be_final.domain.chatroom.dto.response;

import com.mogakko.be_final.domain.chatroom.entity.ChatRoomMembers;
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
public class ChatRoomUserResponseDto {


    // 채팅방
    private String sessionId;

    // 소셜 로그인 방법
    private String social;

    // 멤버
    private Long userId;

    private String nickname;

    private String email;

    private String profileImage;

    private String enterRoomToken;

    // 방에 들어온 시간 기록.
    private LocalDateTime roomEnterTime;

    // 방에서 나간 시간 기록
    private LocalDateTime roomExitTime;


    /*방에서 총 머문 일자 (재 입장 다 합쳐서)*/
    private Long roomStayDay;


    /*방에서 총 머문 시간 (재 입장 다 합쳐서)*/
    private Time roomStayTime;

    public ChatRoomUserResponseDto(ChatRoomMembers entity){
        this.sessionId = entity.getSessionId();
        this.userId = entity.getMemberId();
        this.nickname = entity.getNickname();
        this.email = entity.getEmail();
        this.profileImage = entity.getProfileImage();
        this.enterRoomToken = entity.getEnterRoomToken();
        this.roomEnterTime = entity.getRoomEnterTime();
        this.roomExitTime = entity.getRoomExitTime();
        this.roomStayDay = entity.getRoomStayDay();
        this.roomStayTime = entity.getRoomStayTime();

    }

}
