package com.example.speed_you_back.controller;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.RankDto;
import com.example.speed_you_back.dto.ResponseDto;
import com.example.speed_you_back.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RankController
{
    @Autowired RankService rankService;
    @Autowired VersionProvider versionProvider;

    /* 랭킹 데이터 반환 컨트롤러 */
    @GetMapping("/api/rank")
    public ResponseEntity<ResponseDto.Success> getRank(@RequestParam String game)
    {
        RankDto rankDto = rankService.getRank(game);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(rankDto)
                        .message("랭킹 데이터 반환에 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }
}
