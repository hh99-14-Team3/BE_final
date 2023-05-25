package com.mogakko.be_final.domain.chatroom.dto.request;

import com.mogakko.be_final.domain.chatroom.entity.LanguageEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor
public class ChatRoomCreateRequestDto {

    @NotBlank(message = "제목을 입력해 주세요!")
    private String title;

    @NotBlank(message = "주특기 언어를 선택해 주세요!")
    private LanguageEnum language;

    @NotNull(message = "최대 인원을 설정해주세요!")
    private Long maxMembers;

    @NotNull(message = "방의 상태를 설정해 주세요!")
    private Boolean isOpened;

    private String password;

    private double longitudeX;

    private double latitudeY;

}

