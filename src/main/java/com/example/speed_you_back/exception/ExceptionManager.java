package com.example.speed_you_back.exception;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager
{

    @Autowired
    VersionProvider versionProvider;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseDto.Error> customExceptionHandler(CustomException e)
    {
        return ResponseEntity.status(e.getError_code().getHttpStatus())
                .body(
                        ResponseDto.Error.builder()
                                .code(e.getError_code().name())
                                .message(e.getError_code().getMessage())
                                .data(e.getData())
                                .version(versionProvider.getVersion())
                                .build()
                );
    }

    // @Valid 어노테이션으로 수행한 유효성 검사 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto.Error> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ResponseDto.Error.builder()
                                .code("INVALID_DATA")
                                .message(e.getMessage())
                                .data(null)
                                .version(versionProvider.getVersion())
                                .build()
                );
    }

    // 원인 불명으로 발생한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto.Error> unexpectedExceptionHandler(Exception e)
    {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ResponseDto.Error.builder()
                                .code("UNEXPECTED_ERROR")
                                .message(e.getMessage())
                                .data(null)
                                .version(versionProvider.getVersion())
                                .build()
                );
    }
}
