package com.mogakko.be_final.domain.friendship.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSearchRequestDto {
    private String searchRequestNickname;
    private Integer friendCode;
}
