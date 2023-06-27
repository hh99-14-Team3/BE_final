package com.mogakko.be_final.domain.mogakkoRoom.dto.response;

import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MogakkoRoomCreateResponseDto {

    private String sessionId;
    private String title;
    private boolean isOpened;
    private LanguageEnum language;
    private String password;


    // 방 생성 시간
    private LocalDateTime createdAt;
    // 방 수정 시간
    private LocalDateTime modifiedAt;

}
