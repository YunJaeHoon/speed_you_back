package com.example.speed_you_back.controller;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.ResponseDto;
import com.example.speed_you_back.dto.SuggestionDto;
import com.example.speed_you_back.service.SuggestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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
}
