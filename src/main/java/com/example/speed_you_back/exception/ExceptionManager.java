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
                                .data(e.getData())
                                .message(e.getError_code().getMessage())
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
                                .data(null)
                                .message(e.getMessage())
                                .version(versionProvider.getVersion())
                                .build()
                );
    }

    // 이메일 전송 시, 원인 불명으로 발생한 예외 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDto.Error> runtimeExceptionHandler(RuntimeException e)
    {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ResponseDto.Error.builder()
                                .data(null)
                                .message(e.getMessage())
                                .version(versionProvider.getVersion())
                                .build()
                );
    }

    // 권한이 없는 api에 접근시 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDto.Error> accessDeniedExceptionHandler(AccessDeniedException e)
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ResponseDto.Error.builder()
                                .data(null)
                                .message(e.getMessage())
                                .version(versionProvider.getVersion())
                                .build()
                );
    }
}
