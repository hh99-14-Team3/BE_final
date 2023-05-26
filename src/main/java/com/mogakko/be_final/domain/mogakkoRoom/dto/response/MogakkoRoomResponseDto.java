package com.mogakko.be_final.domain.mogakkoRoom.dto.response;

import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MogakkoRoomResponseDto {

    private String sessionId;
    private String title;
    private boolean isOpened;
    private String password;
    private LanguageEnum language;
    private String master;

    private List<MogakkoRoomMemberResponseDto> chatRoomUserList;
    private Long cntUser;

}
