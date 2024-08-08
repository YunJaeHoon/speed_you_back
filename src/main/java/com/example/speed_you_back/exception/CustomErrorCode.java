package com.example.speed_you_back.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CustomErrorCode
{
    LOGIN_FAILURE(HttpStatus.BAD_REQUEST, "로그인에 실패하였습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "이미 만료된 토큰입니다."),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "해당 이메일은 이미 사용 중입니다."),
    WRONG_CODE(HttpStatus.BAD_REQUEST, "인증번호가 틀렸습니다."),
    CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일로 전송된 인증번호가 존재하지 않습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일로 생성된 계정이 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호는 영문과 숫자를 포함한 8~16자리입니다."),
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "닉네임은 2~16자리입니다."),
    LOW_SCORE(HttpStatus.BAD_REQUEST, "점수가 너무 낮습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 형식이 잘못되었습니다."),
    NO_RESULT(HttpStatus.NOT_FOUND, "정보가 존재하지 않습니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 건의사항 종류입니다."),
    TOO_LONG_DETAIL(HttpStatus.BAD_REQUEST, "건의사항의 세부 내용은 1000자까지 가능합니다."),
    TOO_MANY_SUGGESTIONS(HttpStatus.FORBIDDEN, "건의사항은 하루에 최대 10개씩 등록 가능합니다."),
    SUGGESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 건의사항이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
