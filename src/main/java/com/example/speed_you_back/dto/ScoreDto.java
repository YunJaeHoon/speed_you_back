package com.example.speed_you_back.dto;

import com.example.speed_you_back.entity.Profile;
import com.example.speed_you_back.entity.Score;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

public class ScoreDto
{
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    @ToString
    public static class Insert
    {
        @NotBlank(message = "[game] cannot be blank.")
        private String game;

        @NotNull(message = "[score] cannot be null.")
        private double score;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    @ToString
    public static class Result
    {
        @NotNull(message = "[count_all] cannot be null.")
        private Long count_all;

        @NotNull(message = "[rank] cannot be null.")
        private Long rank;

        @NotNull(message = "[percentile] cannot be null.")
        private double percentile;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    @ToString
    public static class Rank
    {
        @NotNull(message = "[score_id] cannot be null.")
        private Long score_id;

        private String username;

        @NotBlank(message = "[game] cannot be blank.")
        private String game;

        @NotNull(message = "[score] cannot be null.")
        private double score;

        @NotNull(message = "[created_at] cannot be null.")
        private LocalDateTime created_at;
    }

    public static ScoreDto.Rank entityToRank(Score score)
    {
        return Rank.builder()
                .score_id(score.getScore_id())
                .username(score.getProfile() != null ? score.getProfile().getUsername() : null)
                .game(score.getGame())
                .score(score.getScore())
                .created_at(score.getCreated_at())
                .build();
    }
}
