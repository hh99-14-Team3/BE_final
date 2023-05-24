package com.mogakko.be_final.domain.members.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

    private String email;
    private String nickname;
    private String password;
    private String isAgreed;

}
