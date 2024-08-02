package com.example.speed_you_back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

public class SuggestionDto
{
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    @ToString
    public static class Insert {
        @NotBlank(message = "[type] cannot be blank.")
        private String type;

        @NotBlank(message = "[detail] cannot be blank.")
        private String detail;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    @ToString
    public static class Basic
    {
        @NotNull(message = "[suggestion_id] cannot be null.")
        private Long suggestion_id;

        @NotBlank(message = "[type] cannot be blank.")
        private String type;

        @NotBlank(message = "[email] cannot be blank.")
        private String email;

        @NotBlank(message = "[username] cannot be blank.")
        private String username;

        @NotNull(message = "[created_at] cannot be null.")
        private LocalDate created_at;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    @ToString
    public static class Detail
    {
        @NotNull(message = "[suggestion_id] cannot be null.")
        private Long suggestion_id;

        @NotBlank(message = "[type] cannot be blank.")
        private String type;

        @NotBlank(message = "[detail] cannot be blank.")
        private String detail;

        @NotBlank(message = "[email] cannot be blank.")
        private String email;

        @NotBlank(message = "[username] cannot be blank.")
        private String username;

        @NotNull(message = "[created_at] cannot be null.")
        private LocalDate created_at;
    }
}
