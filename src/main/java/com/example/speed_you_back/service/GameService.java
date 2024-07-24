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
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class GameService
{
    @Autowired ProfileRepository profileRepository;
    @Autowired ScoreRepository scoreRepository;

    /* 점수 등록 서비스 */
    @Transactional
    public void insertScore(Principal principal, ScoreDto.Insert dto)
    {
        // 점수가 너무 낮다면, 예외 처리
        if((!Objects.equals(dto.getGame(), "Green") && dto.getScore() <= 0) || (Objects.equals(dto.getGame(), "Green") && dto.getScore() >= 25000))
        {
            throw new CustomException(CustomErrorCode.LOW_SCORE, dto.getScore());
        }

        // 로그인 한 사용자라면, 해당 사용자의 계정이 존재하는지 확인
        Profile profile = null;
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
                .created_at(LocalDateTime.now())
                .build();

        scoreRepository.save(score);
    }

    /* 결과 확인 서비스 */
    public ScoreDto.Result result(String game, int score)
    {
        // 해당 게임의 모든 결과 개수
        Long countAllScores = scoreRepository.countAllScores(game);

        // Green 게임이면 점수가 낮을수록, 높은 순위
        // 그 외에는 점수가 높을수록, 높은 순위
        if(Objects.equals(game, "Green")) {
            Long countLittleScores = scoreRepository.countLittleScores(game, score);

            return ScoreDto.Result.builder()
                    .count_all(countAllScores)
                    .rank(countLittleScores + 1)
                    .percentile(Math.round(((double) (countLittleScores + 1) / countAllScores) * 100))
                    .build();
        }
        else {
            Long countLargeScores = scoreRepository.countLargeScores(game, score);

            return ScoreDto.Result.builder()
                    .count_all(countAllScores)
                    .rank(countLargeScores + 1)
                    .percentile(Math.round(((double) (countLargeScores + 1) / countAllScores) * 100))
                    .build();
        }
    }
}
