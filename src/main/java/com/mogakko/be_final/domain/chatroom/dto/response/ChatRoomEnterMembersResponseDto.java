package com.mogakko.be_final.domain.chatroom.dto.response;

import com.mogakko.be_final.domain.chatroom.entity.ChatRoom;
import com.mogakko.be_final.domain.chatroom.entity.LanguageEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ChatRoomEnterMembersResponseDto {

    private String sessionId;
    private String title;
    private boolean isOpened;
    private String password;
    private LanguageEnum language;
    private String master;
    private Long cntMember;

    List<ChatRoomEnterMemberResponseDto> chatRoomMemberList;


    public ChatRoomEnterMembersResponseDto(ChatRoom room, List<ChatRoomEnterMemberResponseDto> chatRoomMemberResponseDtos) {

        this.sessionId = room.getSessionId();
        this.title = room.getTitle();
        this.isOpened = room.isOpened();
        this.password = room.getPassword();
        this.language = room.getLanguage();
        this.master = room.getMaster();
        this.cntMember = room.getCntMembers();
        this.chatRoomMemberList = chatRoomMemberResponseDtos;
    }


}
