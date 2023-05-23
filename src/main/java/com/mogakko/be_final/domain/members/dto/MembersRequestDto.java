package com.mogakko.be_final.domain.members.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MembersRequestDto {

    private String email;
    private String nickname;
    private String password;


}
