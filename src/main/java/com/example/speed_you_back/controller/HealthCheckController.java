package com.example.speed_you_back.controller;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController
{
    @Autowired VersionProvider versionProvider;

    @GetMapping("/health-check")
    public ResponseEntity<ResponseDto.Success> healthCheck()
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(null)
                        .message("로드밸런서 타켓 그룹 health-check")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }
}
