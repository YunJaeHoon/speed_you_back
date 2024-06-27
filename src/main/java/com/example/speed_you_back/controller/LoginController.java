package com.example.speed_you_back.controller;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.EmailDto;
import com.example.speed_you_back.dto.ProfileDto;
import com.example.speed_you_back.dto.ResponseDto;
import com.example.speed_you_back.service.LoginService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Slf4j
public class LoginController
{
    @Autowired LoginService loginService;
    @Autowired VersionProvider versionProvider;

    /* 회원가입 컨트롤러 */
    @PostMapping("/api/join")
    public ResponseEntity<ResponseDto.Success> join(@Valid @RequestBody ProfileDto.Join dto)
    {
        loginService.join(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.Success.builder()
                        .data(dto.getEmail())
                        .message("회원가입을 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }

    /* 이메일 인증번호 전송 컨트롤러 */
    @PostMapping("/api/join/send-email")
    public ResponseEntity<ResponseDto.Success> sendEmail(@Valid @RequestBody EmailDto.SendEmail dto)
    {
        loginService.sendEmail(dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(dto.getEmail())
                        .message("인증번호 이메일 전송에 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }

    /* 이메일 인증번호 확인 컨트롤러 */
    @PostMapping("/api/join/check-email")
    public ResponseEntity<ResponseDto.Success> checkEmail(@Valid @RequestBody EmailDto.CheckEmail dto)
    {
        loginService.checkEmail(dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(dto.getEmail())
                        .message("인증번호 이메일 확인에 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }

    /* 비밀번호 초기화 컨트롤러 */
    @PostMapping("/api/reset-password")
    public ResponseEntity<ResponseDto.Success> resetPassword(@Valid @RequestBody EmailDto.SendEmail dto)
    {
        loginService.resetPassword(dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(dto.getEmail())
                        .message("비밀번호가 성공적으로 초기화 되었습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }

    /* 계정 권한 확인 컨트롤러 */
    @GetMapping("/api/get-role")
    public ResponseEntity<ResponseDto.Success> getRole(Principal principal)
    {
        String role = loginService.getRole(principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseDto.Success.builder()
                                .data(role)
                                .message("계정 권한 확인이 완료되었습니다.")
                                .version(versionProvider.getVersion())
                                .build()
                );
    }
}
