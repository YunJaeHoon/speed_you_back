package com.example.speed_you_back.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException
{
    private CustomErrorCode error_code;
    private Object data;
}
