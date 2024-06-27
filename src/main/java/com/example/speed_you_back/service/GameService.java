package com.example.speed_you_back.service;

import com.example.speed_you_back.dto.ProfileDto;
import com.example.speed_you_back.dto.ScoreDto;
import com.example.speed_you_back.entity.Code;
import com.example.speed_you_back.entity.Profile;
import com.example.speed_you_back.exception.CustomErrorCode;
import com.example.speed_you_back.exception.CustomException;
import com.example.speed_you_back.repository.ScoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Objects;

@Service
public class GameService
{
    @Autowired ScoreRepository scoreRepository;

    /* 점수 등록 서비스 */
    @Transactional
    public void insertScore(Principal principal, ScoreDto.Insert dto)
    {

    }

    /* 결과 확인 서비스 */
    public ScoreDto.Result result(ScoreDto.Insert dto)
    {
        return null;
    }
}
