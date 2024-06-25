package com.example.speed_you_back.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

public class ProfileDto
{
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    @ToString
    public static class Profile
    {
        @NotNull(message = "[profile_id] cannot be null.")
        private Long profile_id;

        @NotBlank(message = "[email] cannot be blank.")
        @Email(message = "[email] should be email format.")
        private String email;

        @NotBlank(message = "[password] cannot be blank.")
        private String password;

        @NotBlank(message = "[username] cannot be blank.")
        private String username;

        @NotNull(message = "[created_at] cannot be null.")
        private LocalDate created_at;

        @NotBlank(message = "[role] cannot be blank.")
        private String role;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Getter
    public static class Join
    {
        @NotBlank(message = "[email] cannot be blank.")
        @Email(message = "[email] should be email format.")
        private String email;

        @NotBlank(message = "[password] cannot be blank.")
        private String password;

        @NotBlank(message = "[username] cannot be blank.")
        private String username;

        @NotBlank(message = "[code] cannot be blank.")
        private String code;
    }
}
