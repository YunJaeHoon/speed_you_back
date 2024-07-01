package com.example.speed_you_back.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class TokenDto
{
    private String accessToken;
    private String refreshToken;
}
