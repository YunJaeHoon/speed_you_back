package com.example.speed_you_back.service;

import com.example.speed_you_back.dto.RankDto;
import com.example.speed_you_back.dto.ScoreDto;
import com.example.speed_you_back.entity.Score;
import com.example.speed_you_back.repository.ScoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class RankService
{
    @Autowired ScoreRepository scoreRepository;

    /* 랭킹 데이터 서비스 */
    public RankDto.Rank getRank(String game)
    {
        // 해당 게임의 모든 결과 개수
        Long countAllScores = scoreRepository.countAllScores(game);

        // 상위 10개의 score
        List<Score> topTen;

        if(Objects.equals(game, "Green")) {
            topTen = scoreRepository.littleTopTen(game);
        }
        else {
            topTen = scoreRepository.largeTopTen(game);
        }

        // 엔티티 -> dto 변환
        List<ScoreDto.Rank> topTenDto = topTen.stream().map(
                ScoreDto::entityToRank
        ).toList();

        // 점수 경계 리스트
        List<RankDto.Boundary> boundary = new ArrayList<>();
        long count = countAllScores / 10;

        if(Objects.equals(game, "Green")) {

            boundary.add(
                    RankDto.Boundary.builder()
                            .description("100%")
                            .score(25000)
                            .build()
            );

            for(int i = 1; i < 10; i++)
            {
                int score = scoreRepository.littleBoundaryScore(game, count);

                boundary.add(
                        RankDto.Boundary.builder()
                                .description(100 - (i * 10) + "%")
                                .score(score)
                                .build()
                );

                count = count + (countAllScores / 10) + ((countAllScores % 10) >= (10 - i) ? 1 : 0);
            }

            int top = scoreRepository.littleBoundaryScore(game, countAllScores - 1);

            boundary.add(
                    RankDto.Boundary.builder()
                            .description("0%")
                            .score(top)
                            .build()
            );
        }
        else {
            boundary.add(
                    RankDto.Boundary.builder()
                            .description("100%")
                            .score(0)
                            .build()
            );

            for(int i = 1; i < 10; i++)
            {
                int score = scoreRepository.largeBoundaryScore(game, count);

                boundary.add(
                        RankDto.Boundary.builder()
                                .description(100 - (i * 10) + "%")
                                .score(score)
                                .build()
                );

                count = count + (countAllScores / 10) + ((countAllScores % 10) >= (10 - i) ? 1 : 0);
            }

            int top = scoreRepository.largeBoundaryScore(game, countAllScores - 1);

            boundary.add(
                    RankDto.Boundary.builder()
                            .description("0%")
                            .score(top)
                            .build()
            );
        }

        return RankDto.Rank.builder()
                .game(game)
                .top_ten(topTenDto)
                .boundary(boundary)
                .build();
    }
}
