package com.example.speed_you_back.controller;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.*;
import com.example.speed_you_back.service.MyPageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                        .message("기본 프로필 정보 반환에 성공하였습니다.")
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

    /* 게임 전적 컨트롤러 */
    @GetMapping("/api/mypage/history")
    public ResponseEntity<ResponseDto.Success> history(@RequestParam("game") String game,
                                                       @RequestParam("order") String order,
                                                       @RequestParam("page") int page,
                                                       Principal principal)
    {
        List<ScoreDto.History> data = myPageService.history(game, order, page - 1, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(data)
                        .message("게임 전적 반환에 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }

    /* 게임 전적 개수 컨트롤러 */
    @GetMapping("/api/mypage/history/count")
    public ResponseEntity<ResponseDto.Success> historyCount(@RequestParam("game") String game, Principal principal)
    {
        Long num = myPageService.historyCount(game, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(num)
                        .message("게임 전적 개수 반환에 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }

    /* 비밀번호 확인 컨트롤러 */
    @PostMapping("/api/mypage/check-password")
    public ResponseEntity<ResponseDto.Success> checkPassword(@Valid @RequestBody ProfileDto.CheckPassword dto,
                                                             Principal principal)
    {
        myPageService.checkPassword(dto, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(null)
                        .message("비밀번호가 확인되었습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }

    /* 기본 프로필 정보 변경 컨트롤러 */
    @PostMapping("/api/mypage/update-basic")
    public ResponseEntity<ResponseDto.Success> updateBasic(@Valid @RequestBody ProfileDto.UpdateBasic dto,
                                                           Principal principal)
    {
        myPageService.updateBasic(dto, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(null)
                        .message("기본 프로필 정보가 변경되었습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }

    /* 비밀번호 변경 컨트롤러 */
    @PostMapping("/api/mypage/update-password")
    public ResponseEntity<ResponseDto.Success> updatePassword(@Valid @RequestBody ProfileDto.UpdatePassword dto,
                                                              Principal principal)
    {
        myPageService.updatePassword(dto, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(null)
                        .message("비밀번호가 변경되었습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }
}
