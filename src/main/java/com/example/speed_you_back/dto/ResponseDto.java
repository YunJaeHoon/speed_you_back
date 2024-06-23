package com.example.speed_you_back.dto;

import lombok.*;

public class ResponseDto
{
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Getter
    @Builder
    public static class Success
    {
        @Builder.Default private String status = "SUCCESS";
        private Object data;
        private String message;
        private String version;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Getter
    @Builder
    public static class Error
    {
        @Builder.Default private String status = "ERROR";
        private Object data;
        private String message;
        private String version;
    }
}
