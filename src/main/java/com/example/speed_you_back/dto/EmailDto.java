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
    public static class SendEmail
    {
        @NotBlank(message = "[email] cannot be blank.")
        @Email(message = "[email] should be email format.")
        private String email;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    public static class CheckEmail
    {
        @NotBlank(message = "[email] cannot be blank.")
        @Email(message = "[email] should be email format.")
        private String email;

        @NotBlank(message = "[code] cannot be blank.")
        private String code;
    }
}
