package com.mogakko.be_final.domain.chatMessage.entity;

import com.mogakko.be_final.domain.chatMessage.dto.request.ChatMessageRequestDto;
import com.mogakko.be_final.util.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RoomMessage extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;
    private String nickname;
    private String profile;
    private String message;
    private String sessionId; // 방 sessionId
    private boolean isDelete = false;

    public RoomMessage(ChatMessageRequestDto chatMessageRequestDto) {
        this.nickname = chatMessageRequestDto.getNickname();
        this.profile = chatMessageRequestDto.getProfile();
        this.message = chatMessageRequestDto.getMessage();
        this.sessionId = chatMessageRequestDto.getSessionId();
    }
}
