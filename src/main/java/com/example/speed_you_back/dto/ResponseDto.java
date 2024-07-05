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
        private String message;
        private Object data;
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
        private String code;
        private String message;
        private Object data;
        private String version;
    }
}
