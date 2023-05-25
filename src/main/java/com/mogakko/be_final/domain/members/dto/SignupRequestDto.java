package com.mogakko.be_final.domain.members.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

    @Email
    private String email;

    private String nickname;

    private String password;

    private String isAgreed;

}
