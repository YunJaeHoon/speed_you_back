package com.example.speed_you_back.service;

import com.example.speed_you_back.dto.ProfileDto;
import com.example.speed_you_back.entity.Profile;
import com.example.speed_you_back.entity.Score;
import com.example.speed_you_back.exception.CustomErrorCode;
import com.example.speed_you_back.exception.CustomException;
import com.example.speed_you_back.repository.ProfileRepository;
import com.example.speed_you_back.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class MyPageService
{
    @Autowired ProfileRepository profileRepository;
    @Autowired ScoreRepository scoreRepository;

    /* 기본 프로필 정보 서비스 */
    public ProfileDto.MyPage getMyPage(Principal principal)
    {
        // 계정 권한 확인 요청을 보낸 사용자의 계정 이메일
        String email = principal.getName();

        // 해당 사용자의 계정이 존재하는지 확인
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND, email));

        return ProfileDto.MyPage.builder()
                .profile_id(profile.getProfile_id())
                .email(profile.getEmail())
                .username(profile.getUsername())
                .created_at(profile.getCreated_at())
                .role(profile.getRole())
                .build();
    }
}
