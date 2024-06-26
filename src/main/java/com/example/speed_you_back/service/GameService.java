package com.example.speed_you_back.service;

import com.example.speed_you_back.dto.ProfileDto;
import com.example.speed_you_back.dto.ScoreDto;
import com.example.speed_you_back.entity.Code;
import com.example.speed_you_back.entity.Profile;
import com.example.speed_you_back.entity.Score;
import com.example.speed_you_back.exception.CustomErrorCode;
import com.example.speed_you_back.exception.CustomException;
import com.example.speed_you_back.repository.ProfileRepository;
import com.example.speed_you_back.repository.ScoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GameService
{
    @Autowired ProfileRepository profileRepository;
    @Autowired ScoreRepository scoreRepository;

    /* 점수 등록 서비스 */
    @Transactional
    public void insertScore(Principal principal, ScoreDto.Insert dto)
    {
        Profile profile = null;

        // 로그인 한 사용자라면, 해당 사용자의 계정이 존재하는지 확인
        if(principal != null)
        {
            String email = principal.getName();

            profile = profileRepository.findByEmail(email)
                    .orElse(null);
        }

        Score score = Score.builder()
                .profile(profile)
                .game(dto.getGame())
                .score(dto.getScore())
                .created_at(LocalDate.now())
                .build();

        scoreRepository.save(score);

    }

    /* 결과 확인 서비스 */
    public ScoreDto.Result result(String game, double score)
    {
        // 해당 게임의 모든 결과 개수
        Long countAllScores = scoreRepository.countAllScores(game);

        // 해당 게임에서 해당 점수보다 높은 결과 개수
        Long countLargeScores = scoreRepository.countLargeScores(game, score);

        ScoreDto.Result result = ScoreDto.Result.builder()
                .count_all(countAllScores)
                .rank(countLargeScores + 1)
                .percentile(Math.round(((double) (countLargeScores + 1) / countAllScores) * 100))
                .build();

        return result;
    }
}
