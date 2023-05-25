package com.mogakko.be_final.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR"),
    DUPLICATE_IDENTIFIER(HttpStatus.BAD_REQUEST, "중복된 이메일 입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임 입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호 입니다."),
    PLZ_INPUT_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요"),
    PLZ_INPUT_SEARCHKEYWORD(HttpStatus.BAD_REQUEST, "검색어를 입력해주세요"),
    INVALID_AUTH_KEY(HttpStatus.BAD_REQUEST, "이메일의 인증 키가 잘못되었습니다"),
    EXPIRED_AUTH_KEY(HttpStatus.BAD_REQUEST, "만료된 링크입니다"),
    NOT_VERIFIED_EMAIL(HttpStatus.BAD_REQUEST, "인증되지 않은 이메일입니다."),
    IS_NOT_AGREED(HttpStatus.BAD_REQUEST, "필수 항목에 동의하지 않았습니다"),
    MOGAKKO_IS_FULL(HttpStatus.BAD_REQUEST, "모각코 인원이 마감되었습니다."),
    ALREADY_OUT_MEMBER(HttpStatus.BAD_REQUEST, "이미 방에서 나간 유저 입니다."),
    ALREADY_ENTER_MEMBER(HttpStatus.BAD_REQUEST, "이미 입장한 유저 입니다."),
    NOT_MOGAKKO_MEMBER(HttpStatus.BAD_REQUEST, "방에 있는 멤버가 아닙니다."),

    //404 NOT_FOUND,
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    MOGAKKO_NOT_FOUND(HttpStatus.BAD_REQUEST, "모각코 방이 없습니다."),

    //인증
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String data;

}
