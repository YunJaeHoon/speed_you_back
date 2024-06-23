package com.example.speed_you_back.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


public class EmailDto
{
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    public static class sendEmail
    {
        @NotBlank(message = "[email] cannot be blank.")
        @Email(message = "[email] should be email format.")
        private String email;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    public static class checkEmail
    {
        @NotBlank(message = "[email] cannot be blank.")
        @Email(message = "[email] should be email format.")
        private String email;

        @NotBlank(message = "[code] cannot be blank.")
        private String code;
    }
}
