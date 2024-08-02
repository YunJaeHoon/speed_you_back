package com.example.speed_you_back.controller;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.ResponseDto;
import com.example.speed_you_back.dto.SuggestionDto;
import com.example.speed_you_back.service.SuggestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class SuggestionController
{
    @Autowired SuggestionService suggestionService;
    @Autowired VersionProvider versionProvider;

    /* 건의사항 등록 컨트롤러 */
    @PostMapping("/api/suggestion/insert-suggestion")
    public ResponseEntity<ResponseDto.Success> insertSuggestion(Principal principal, @Valid @RequestBody SuggestionDto.Insert dto)
    {
        suggestionService.insertSuggestion(principal, dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.Success.builder()
                        .data(null)
                        .message("건의사항 등록을 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }

    /* 건의사항 간단 조회 컨트롤러 */
    @GetMapping("/api/suggestion/basic")
    public ResponseEntity<ResponseDto.Success> basicSuggestion(@RequestParam("page") int page)
    {
        List<SuggestionDto.Basic> data = suggestionService.basicSuggestion(page - 1);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(data)
                        .message("건의사항 간단 조회를 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }

    /* 건의사항 상세 조회 컨트롤러 */
    @GetMapping("/api/suggestion/detail")
    public ResponseEntity<ResponseDto.Success> detailSuggestion(@RequestParam("suggestion_id") Long suggestion_id)
    {
        SuggestionDto.Detail data = suggestionService.detailSuggestion(suggestion_id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(data)
                        .message("건의사항 상세 조회를 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }

    /* 건의사항 개수 컨트롤러 */
    @GetMapping("/api/suggestion/count")
    public ResponseEntity<ResponseDto.Success> countSuggestion()
    {
        Long number = suggestionService.countSuggestion();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.Success.builder()
                        .data(number)
                        .message("건의사항 개수 반환에 성공하였습니다.")
                        .version(versionProvider.getVersion())
                        .build()
                );
    }
}
