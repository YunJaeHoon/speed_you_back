package com.example.speed_you_back.service;

import com.example.speed_you_back.dto.ScoreDto;
import com.example.speed_you_back.dto.SuggestionDto;
import com.example.speed_you_back.entity.Profile;
import com.example.speed_you_back.entity.Suggestion;
import com.example.speed_you_back.exception.CustomErrorCode;
import com.example.speed_you_back.exception.CustomException;
import com.example.speed_you_back.repository.ProfileRepository;
import com.example.speed_you_back.repository.SuggestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Objects;

@Service
public class SuggestionService
{
    @Autowired ProfileRepository profileRepository;
    @Autowired SuggestionRepository suggestionRepository;

    @Transactional
    public void insertSuggestion(Principal principal, SuggestionDto.Insert dto)
    {
        // 요청을 보낸 사용자의 계정 이메일
        String email = principal.getName();

        // 해당 사용자의 계정이 존재하는지 확인
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND, email));

        // 건의사항 종류가 형식에 맞지 않으면, 예외 처리
        if(
                !Objects.equals(dto.getType(), "BUG_REPORT") &&
                !Objects.equals(dto.getType(), "INQUIRY ") &&
                !Objects.equals(dto.getType(), "ADVICE ")
        )
            throw new CustomException(CustomErrorCode.INVALID_TYPE, dto.getType());

        if(dto.getDetail().length() > 1000)
            throw new CustomException(CustomErrorCode.TOO_LONG_DETAIL, dto.getDetail().length());

        // 해당 사용자가 오늘 등록한 건의사항 개수 확인
        int countPrevSuggestions = suggestionRepository.countByProfileAndCreateAt(profile.getProfile_id(), LocalDate.now());

        // 만약 해당 사용자가 오늘 등록한 건의사항 개수가 10개 이상이라면, 예외 처리
        if(countPrevSuggestions >= 10)
            throw new CustomException(CustomErrorCode.TOO_MANY_SUGGESTIONS, null);

        // 건의사항 등록
        Suggestion suggestion = Suggestion.builder()
                .profile(profile)
                .type(dto.getType())
                .detail(dto.getDetail())
                .created_at(LocalDate.now())
                .build();

        suggestionRepository.save(suggestion);
    }
}
