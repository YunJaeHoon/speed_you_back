package com.example.speed_you_back.dto;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
}
