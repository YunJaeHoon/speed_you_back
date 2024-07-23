package com.example.speed_you_back.dto;

import com.example.speed_you_back.entity.Score;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

public class RankDto
{
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    @ToString
    public static class Rank {
        @NotBlank(message = "[game] cannot be blank.")
        private String game;

        @NotNull(message = "[top_ten] cannot be null.")
        private List<ScoreDto.Rank> top_ten;

        @NotNull(message = "[boundary] cannot be null.")
        private List<RankDto.Boundary> boundary;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    @ToString
    public static class Boundary {
        @NotBlank(message = "[description] cannot be blank.")
        private String description;

        @NotNull(message = "[score] cannot be null.")
        private int score;
    }

}
