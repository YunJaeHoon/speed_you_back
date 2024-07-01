package com.example.speed_you_back.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CustomErrorCode
{
    LOGIN_FAILURE(HttpStatus.BAD_REQUEST, "로그인에 실패하였습니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "계정이 존재하지 않습니다."),
    TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "token 정보가 유효하지 않습니다."),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "해당 이메일은 이미 사용 중입니다."),
    WRONG_CODE(HttpStatus.BAD_REQUEST, "인증번호가 틀렸습니다."),
    CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일로 전송된 인증번호가 존재하지 않습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일로 생성된 계정이 존재하지 않습니다."),
    EMAIL_NOT_SEND(HttpStatus.INTERNAL_SERVER_ERROR, "이메일이 전송되지 못했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
