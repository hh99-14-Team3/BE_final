package com.mogakko.be_final.domain.mogakkoRoom.dto.response;

import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MogakkoRoomEnterMembersResponseDto {

    private String sessionId;
    private String title;
    private boolean isOpened;
    private String password;
    private LanguageEnum language;
    private String master;
    private Long cntMember;
    List<MogakkoRoomEnterMemberResponseDto> mogakkoRoomMemberList;


    public MogakkoRoomEnterMembersResponseDto(MogakkoRoom room, List<MogakkoRoomEnterMemberResponseDto> mogakkoRoomMemberResponseDtos) {

        this.sessionId = room.getSessionId();
        this.title = room.getTitle();
        this.isOpened = room.isOpened();
        this.password = room.getPassword();
        this.language = room.getLanguage();
        this.master = room.getMaster();
        this.cntMember = room.getCntMembers();
        this.mogakkoRoomMemberList = mogakkoRoomMemberResponseDtos;
    }


}
