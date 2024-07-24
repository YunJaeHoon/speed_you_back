package com.example.speed_you_back.controller;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.ProfileDto;
import com.example.speed_you_back.dto.RankDto;
import com.example.speed_you_back.dto.ResponseDto;
import com.example.speed_you_back.dto.ScoreDto;
import com.example.speed_you_back.service.MyPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class MyPageController
{
    @Autowired MyPageService myPageService;
    @Autowired VersionProvider versionProvider;

    /* 기본 프로필 정보 컨트롤러 */
    @GetMapping("/api/mypage/info")
    public ResponseEntity<ResponseDto.Success> getMyPage(Principal principal)
    {
        ProfileDto.MyPage data = myPageService.getMyPage(principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(data)
                        .message("마이페이지 정보 반환에 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }

    /* 게임별 최고 점수 컨트롤러 */
    @GetMapping("/api/mypage/highest-score")
    public ResponseEntity<ResponseDto.Success> highestScore(Principal principal)
    {
        List<ScoreDto.History> data = myPageService.highestScore(principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(data)
                        .message("게임별 최고 점수 반환에 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }
}
