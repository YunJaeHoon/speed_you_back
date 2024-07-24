package com.example.speed_you_back.service;

import com.example.speed_you_back.dto.ProfileDto;
import com.example.speed_you_back.dto.ScoreDto;
import com.example.speed_you_back.entity.Profile;
import com.example.speed_you_back.entity.Score;
import com.example.speed_you_back.exception.CustomErrorCode;
import com.example.speed_you_back.exception.CustomException;
import com.example.speed_you_back.repository.ProfileRepository;
import com.example.speed_you_back.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MyPageService
{
    @Autowired ProfileRepository profileRepository;
    @Autowired ScoreRepository scoreRepository;

    /* 기본 프로필 정보 서비스 */
    public ProfileDto.MyPage getMyPage(Principal principal)
    {
        // 계정 권한 확인 요청을 보낸 사용자의 계정 이메일
        String email = principal.getName();

        // 해당 사용자의 계정이 존재하는지 확인
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND, email));

        return ProfileDto.MyPage.builder()
                .profile_id(profile.getProfile_id())
                .email(profile.getEmail())
                .username(profile.getUsername())
                .created_at(profile.getCreated_at())
                .role(profile.getRole())
                .build();
    }

    /* 게임별 최고 점수 서비스 */
    public List<ScoreDto.History> highestScore(Principal principal)
    {
        // 계정 권한 확인 요청을 보낸 사용자의 계정 이메일
        String email = principal.getName();

        // 해당 사용자의 계정이 존재하는지 확인
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND, email));

        // 게임 종류 배열
        String[] games = {"Red", "Orange", "Yellow", "Green", "Skyblue", "Blue", "Purple", "Pink", "Black"};

        // 게임별 최고 점수 반환
        List<ScoreDto.History> result = new ArrayList<>();
        for(int i = 0; i < games.length; i++)
        {
            // 해당 게임의 모든 결과 개수
            Long countAllScores = scoreRepository.countAllScores(games[i]);

            // 해당 게임의 최고 점수 정보
            Score score = null;
            Double percentile = null;

            if(!games[i].equals("Green")) {
                score = scoreRepository.highestScore(profile.getProfile_id(), games[i]);
                if (score != null) {
                    percentile = (double) Math.round(((double) (scoreRepository.countLargeScores(games[i], score.getScore()) + 1) / countAllScores) * 100);
                }
            }
            else {
                score = scoreRepository.lowestScore(profile.getProfile_id(), games[i]);
                if (score != null) {
                    percentile = (double) Math.round(((double) (scoreRepository.countLittleScores(games[i], score.getScore()) + 1) / countAllScores) * 100);
                }
            }

            if(score != null) {
                result.add(
                        ScoreDto.History.builder()
                                .score_id(score.getScore_id())
                                .game(games[i])
                                .score(score.getScore())
                                .percentile(percentile)
                                .created_at(score.getCreated_at())
                                .build()
                );
            }
            else {
                result.add(
                        ScoreDto.History.builder()
                                .score_id(null)
                                .game(games[i])
                                .score(null)
                                .percentile(null)
                                .created_at(null)
                                .build()
                );
            }

        }

        return result;
    }
}
