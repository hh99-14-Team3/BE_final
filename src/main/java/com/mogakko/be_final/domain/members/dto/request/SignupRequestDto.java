package com.mogakko.be_final.domain.members.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequestDto {

    @Email
    @Pattern(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", message = "올바른 형식의 이메일을 입력해주세요.")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z가-힣])[a-z가-힣]{2,8}$", message = "닉네임은 2~8글자의 영문 또는 한글로 생성해주세요" )
    private String nickname;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9!@#$%^&*()._-]{6,16}$",
            message = "비밀번호는 6~16자 영문, 숫자를 사용하세요.")
    private String password;

}
