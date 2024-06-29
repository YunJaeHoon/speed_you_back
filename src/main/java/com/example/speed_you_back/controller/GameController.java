package com.example.speed_you_back.controller;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.ResponseDto;
import com.example.speed_you_back.dto.ScoreDto;
import com.example.speed_you_back.service.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class GameController
{
    @Autowired GameService gameService;
    @Autowired VersionProvider versionProvider;

    /* 점수 등록 컨트롤러 */
    @PostMapping("/api/game/insert-score")
    public ResponseEntity<ResponseDto.Success> insertScore(Principal principal, @Valid @RequestBody ScoreDto.Insert dto)
    {
        gameService.insertScore(principal, dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.Success.builder()
                        .data(null)
                        .message("점수 등록을 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }

    /* 결과 확인 컨트롤러 */
    @GetMapping("/api/game/result")
    public ResponseEntity<ResponseDto.Success> result(@RequestParam String game,
                                                      @RequestParam double score)
    {
        ScoreDto.Result resultDto = gameService.result(game, score);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(resultDto)
                        .message("결과 확인을 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }
}
