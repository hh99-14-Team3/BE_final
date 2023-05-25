package com.mogakko.be_final.domain.chatroom.dto.response;

import com.mogakko.be_final.domain.chatroom.entity.LanguageEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class ChatRoomResponseDto {


    private String sessionId;
    private String title;
    private boolean isOpened;
    private String password;
    private LanguageEnum language;
    private String master;

    private List<ChatRoomUserResponseDto> chatRoomUserList;
    private Long cntUser;

}
