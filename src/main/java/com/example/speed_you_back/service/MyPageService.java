package com.example.speed_you_back.service;

import com.example.speed_you_back.dto.ProfileDto;
import com.example.speed_you_back.dto.ScoreDto;
import com.example.speed_you_back.entity.Profile;
import com.example.speed_you_back.entity.Score;
import com.example.speed_you_back.exception.CustomErrorCode;
import com.example.speed_you_back.exception.CustomException;
import com.example.speed_you_back.repository.ProfileRepository;
import com.example.speed_you_back.repository.ScoreRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class MyPageService
{
    @Autowired ProfileRepository profileRepository;
    @Autowired ScoreRepository scoreRepository;
    @Autowired BCryptPasswordEncoder encoder;

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
        // 요청을 보낸 사용자의 계정 이메일
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

    /* 게임 전적 서비스 */
    public List<ScoreDto.History> history(String game, String order, int page, Principal principal)
    {
        // 요청을 보낸 사용자의 계정 이메일
        String email = principal.getName();

        // 해당 사용자의 계정이 존재하는지 확인
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND, email));

        // Pageable 객체 생성 (size = 10개)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Score> scores = null;

        // game 값이 "All"이면, 모든 게임 전적 반환
        // 그 외의 game 값은 해당 게임에 대한 전적만 반환
        if(Objects.equals(game, "All"))
        {
            if(order.equals("NEWEST"))
                scores = scoreRepository.findByProfileAllNewest(profile.getProfile_id(), pageable);
            else if(order.equals("OLDEST"))
                scores = scoreRepository.findByProfileAllOldest(profile.getProfile_id(), pageable);
            else
                throw new CustomException(CustomErrorCode.INVALID_REQUEST, null);

            // 전적 정보가 없으면 예외 처리
            if(scores.isEmpty())
                throw new CustomException(CustomErrorCode.NO_RESULT, game);

            // 엔티티 리스트를 dto 리스트로 변환
            List<ScoreDto.History> scoreDtos = scores.stream()
                    .map(score -> {

                        // 해당 게임의 모든 결과 개수
                        Long countAllScores = scoreRepository.countAllScores(score.getGame());

                        double percentile;

                        if(!score.getGame().equals("Green"))
                            percentile = (double) Math.round(((double) (scoreRepository.countLargeScores(score.getGame(), score.getScore()) + 1) / countAllScores) * 100);
                        else
                            percentile = (double) Math.round(((double) (scoreRepository.countLittleScores(score.getGame(), score.getScore()) + 1) / countAllScores) * 100);

                        return ScoreDto.History.builder()
                                .score_id(score.getScore_id())
                                .game(score.getGame())
                                .score(score.getScore())
                                .percentile(percentile)
                                .created_at(score.getCreated_at())
                                .build();
                    })
                    .toList();

            return scoreDtos;
        }
        else if(
                Objects.equals(game, "Red") ||
                Objects.equals(game, "Orange") ||
                Objects.equals(game, "Yellow") ||
                Objects.equals(game, "Green") ||
                Objects.equals(game, "Skyblue") ||
                Objects.equals(game, "Blue") ||
                Objects.equals(game, "Purple") ||
                Objects.equals(game, "Pink") ||
                Objects.equals(game, "Black")
        )
        {
            if(order.equals("NEWEST"))
                scores = scoreRepository.findByProfileGameNewest(profile.getProfile_id(), game, pageable);
            else if(order.equals("OLDEST"))
                scores = scoreRepository.findByProfileGameOldest(profile.getProfile_id(), game, pageable);
            else if(order.equals("HIGHEST"))
                scores = scoreRepository.findByProfileGameHighest(profile.getProfile_id(), game, pageable);
            else if(order.equals("LOWEST"))
                scores = scoreRepository.findByProfileGameLowest(profile.getProfile_id(), game, pageable);
            else
                throw new CustomException(CustomErrorCode.INVALID_REQUEST, null);

            // 전적 정보가 없으면 예외 처리
            if(scores.isEmpty())
                throw new CustomException(CustomErrorCode.NO_RESULT, game);

            // 해당 게임의 모든 결과 개수
            Long countAllScores = scoreRepository.countAllScores(game);

            // 엔티티 리스트를 dto 리스트로 변환
            List<ScoreDto.History> scoreDtos = scores.stream()
                    .map(score -> {

                        double percentile;

                        if(!game.equals("Green"))
                            percentile = (double) Math.round(((double) (scoreRepository.countLargeScores(game, score.getScore()) + 1) / countAllScores) * 100);
                        else
                            percentile = (double) Math.round(((double) (scoreRepository.countLittleScores(game, score.getScore()) + 1) / countAllScores) * 100);

                        return ScoreDto.History.builder()
                                .score_id(score.getScore_id())
                                .game(score.getGame())
                                .score(score.getScore())
                                .percentile(percentile)
                                .created_at(score.getCreated_at())
                                .build();
                    })
                    .toList();

            return scoreDtos;
        }
        else
            throw new CustomException(CustomErrorCode.INVALID_REQUEST, null);
    }

    /* 게임 전적 개수 서비스 */
    public Long historyCount(String game, Principal principal)
    {
        // 요청을 보낸 사용자의 계정 이메일
        String email = principal.getName();

        // 해당 사용자의 계정이 존재하는지 확인
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND, email));

        // 전적 개수
        Long num = null;

        // game 값이 "All"이면, 모든 게임 전적 개수 반환
        // 그 외의 game 값은 해당 게임에 대한 전적 개수만 반환
        if(Objects.equals(game, "All"))
            num = scoreRepository.countAllScoresByProfile(profile.getProfile_id());
        else if(
                Objects.equals(game, "Red") ||
                Objects.equals(game, "Orange") ||
                Objects.equals(game, "Yellow") ||
                Objects.equals(game, "Green") ||
                Objects.equals(game, "Skyblue") ||
                Objects.equals(game, "Blue") ||
                Objects.equals(game, "Purple") ||
                Objects.equals(game, "Pink") ||
                Objects.equals(game, "Black")
        )
            num = scoreRepository.countGameScoresByProfile(profile.getProfile_id(), game);
        else
            throw new CustomException(CustomErrorCode.INVALID_REQUEST, null);

        return num;
    }

    /* 비밀번호 확인 서비스 */
    public void checkPassword(ProfileDto.CheckPassword dto, Principal principal)
    {
        // 요청을 보낸 사용자의 계정 이메일
        String email = principal.getName();

        // 해당 사용자의 계정이 존재하는지 확인
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND, email));

        if(!encoder.matches(dto.getPassword(), profile.getPassword()))
            throw new CustomException(CustomErrorCode.WRONG_PASSWORD, null);
    }

    /* 기본 프로필 정보 변경 서비스 */
    @Transactional
    public void updateBasic(ProfileDto.UpdateBasic dto, Principal principal)
    {
        // 요청을 보낸 사용자의 계정 이메일
        String email = principal.getName();

        // 해당 사용자의 계정이 존재하는지 확인
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND, email));

        // 닉네임이 2~16자리인지 체크
        if(dto.getUsername().length() < 2 || dto.getUsername().length() > 16)
            throw new CustomException(CustomErrorCode.INVALID_USERNAME, dto.getUsername());

        profile.setUsername(dto.getUsername());
        profileRepository.save(profile);
    }

    /* 비밀번호 변경 서비스 */
    @Transactional
    public void updatePassword(ProfileDto.UpdatePassword dto, Principal principal)
    {
        // 요청을 보낸 사용자의 계정 이메일
        String email = principal.getName();

        // 해당 사용자의 계정이 존재하는지 확인
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND, email));

        // 비밀번호가 8~16자리이고, 숫자와 영문을 포함하는지 체크
        boolean hasEnglish = false;
        boolean hasNumber = false;

        for (int i = 0; i < dto.getPassword().length(); i++) {
            char ch = dto.getPassword().charAt(i);
            if (Character.isLetter(ch)) {
                hasEnglish = true;
            } else if (Character.isDigit(ch)) {
                hasNumber = true;
            }

            if (hasEnglish && hasNumber) {
                break;
            }
        }

        if(
                dto.getPassword().length() < 8 ||
                        dto.getPassword().length() > 16 ||
                        !hasEnglish ||
                        !hasNumber
        ) {
            throw new CustomException(CustomErrorCode.INVALID_PASSWORD, dto.getPassword());
        }

        profile.setPassword(encoder.encode(dto.getPassword()));
        profileRepository.save(profile);
    }
}
