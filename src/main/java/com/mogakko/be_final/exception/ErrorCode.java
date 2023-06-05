package com.mogakko.be_final.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR"),
    ALREADY_JOIN_USER(HttpStatus.BAD_REQUEST, "이미 가입한 회원입니다. 로그인해주세요"),
    DUPLICATE_IDENTIFIER(HttpStatus.BAD_REQUEST, "중복된 이메일 입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임 입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호 입니다."),
    PLZ_INPUT_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요"),
    PLZ_INPUT_SEARCHKEYWORD(HttpStatus.BAD_REQUEST, "검색어를 입력해주세요"),
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "이메일을 찾을 수 없습니다."),
    USED_TOKEN(HttpStatus.BAD_REQUEST, "사용된 토큰입니다."),
    MOGAKKO_IS_FULL(HttpStatus.BAD_REQUEST, "모각코 인원이 마감되었습니다."),
    ALREADY_OUT_MEMBER(HttpStatus.BAD_REQUEST, "이미 방에서 나간 유저 입니다."),
    ALREADY_ENTER_MEMBER(HttpStatus.BAD_REQUEST, "이미 입장한 유저 입니다."),
    NOT_MOGAKKO_MEMBER(HttpStatus.BAD_REQUEST, "방에 있는 멤버가 아닙니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    MOGAKKO_NOT_FOUND(HttpStatus.NOT_FOUND, "모각코 방이 없습니다."),
    NOT_SUPPORTED_SOCIALTYPE(HttpStatus.NOT_FOUND, "지원하지 않는 소셜로그인 입니다."),
    INVALID_NOTIFICATION_ID(HttpStatus.BAD_REQUEST,"해당 알림이 존재하지 않습니다."),
    FRIEND_NOT_FOUND(HttpStatus.BAD_REQUEST, "이미 친구가 아닙니다"),
    NOTIFICATION_SENDING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "알림 발송에 실패했습니다"),


    //인증
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String data;
}
