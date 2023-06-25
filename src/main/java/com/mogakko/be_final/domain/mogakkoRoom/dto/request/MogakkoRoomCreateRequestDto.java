package com.mogakko.be_final.domain.mogakkoRoom.dto.request;

import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
public class MogakkoRoomCreateRequestDto {

    @NotBlank(message = "제목을 입력해 주세요!")
    @Size(min = 1, max = 15, message = "모각코 이름은 15글자 이내로 생성해주세요")
    private String title;

    @NotNull(message = "주특기 언어를 선택해 주세요!")
    private LanguageEnum language;

    @NotNull(message = "최대 인원을 설정해주세요!")
    private Long maxMembers;

    @NotNull(message = "방의 공개여부를 설정해 주세요!")
    private Boolean isOpened;

    @NotNull(message = "동네를 설정해 주세요!")
    private String neighborhood;

    private String password;

    private double lon;

    private double lat;

}

