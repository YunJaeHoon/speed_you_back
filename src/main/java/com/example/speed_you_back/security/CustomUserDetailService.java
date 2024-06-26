package com.example.speed_you_back.security;

import com.example.speed_you_back.entity.Profile;
import com.example.speed_you_back.exception.CustomErrorCode;
import com.example.speed_you_back.exception.CustomException;
import com.example.speed_you_back.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/* 로그인 처리 서비스 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService
{
    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        // profile 데이터베이스에서 해당 이메일의 계정 검색
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.LOGIN_FAILURE, email));

        // 해당 이메일의 계정이 존재하면, Spring security에서 제공하는 User 클래스를 빌드
        return new CustomUserDetails(profile);
    }
}
