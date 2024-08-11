package com.example.speed_you_back.service;

import com.example.speed_you_back.dto.EmailDto;
import com.example.speed_you_back.dto.ProfileDto;
import com.example.speed_you_back.dto.TokenDto;
import com.example.speed_you_back.entity.Profile;
import com.example.speed_you_back.entity.Code;
import com.example.speed_you_back.exception.CustomErrorCode;
import com.example.speed_you_back.exception.CustomException;
import com.example.speed_you_back.repository.ProfileRepository;
import com.example.speed_you_back.repository.CodeRepository;
import com.example.speed_you_back.security.CustomUserDetailsService;
import com.example.speed_you_back.security.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Random;

@Service
@Slf4j
public class LoginService
{
    @Autowired ProfileRepository profileRepository;
    @Autowired CodeRepository codeRepository;
    @Autowired BCryptPasswordEncoder encoder;
    @Autowired JavaMailSender javaMailSender;
    @Autowired JwtUtil jwtUtil;
    @Autowired CustomUserDetailsService customUserDetailsService;
    @Autowired RedisTemplate<String, String> redisTemplate;

    /* 회원가입 서비스 */
    @Transactional
    public void join(ProfileDto.Join dto)
    {
        // 이메일 중복 체크
        profileRepository.findByEmail(dto.getEmail())
                .ifPresent(nope -> {
                    throw new CustomException(CustomErrorCode.EMAIL_DUPLICATED, dto.getEmail());
                });

        // 해당 이메일로 발송된 인증번호가 있는지 체크
        Code actualCode = codeRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(CustomErrorCode.CODE_NOT_FOUND, dto.getEmail()));

        // 입력한 인증번호가 맞는지 체크
        if(!Objects.equals(dto.getCode(), actualCode.getCode()))
            throw new CustomException(CustomErrorCode.WRONG_CODE, dto.getEmail());

        // 비밀번호가 8~16자리이고, 숫자와 영문을 포함하는지 체크
        boolean hasEnglish = false;
        boolean hasNumber = false;

        for (int i = 0; i < dto.getPassword().length(); i++) {
            char ch = dto.getPassword().charAt(i);
            if (Character.isLetter(ch)) {
                hasEnglish = true;
            } else if (Character.isDigit(ch)) {
                hasNumber = true;
            }

            if (hasEnglish && hasNumber) {
                break;
            }
        }

        if(
            dto.getPassword().length() < 8 ||
            dto.getPassword().length() > 16 ||
            !hasEnglish ||
            !hasNumber
        ) {
            throw new CustomException(CustomErrorCode.INVALID_PASSWORD, dto.getPassword());
        }

        // 닉네임이 2~16자리인지 체크
        if(dto.getUsername().length() < 2 || dto.getUsername().length() > 16)
            throw new CustomException(CustomErrorCode.INVALID_USERNAME, dto.getUsername());

        // 엔티티 생성
        Profile profile = Profile.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .username(dto.getUsername())
                .created_at(LocalDate.now())
                .role("ROLE_USER")
                .build();

        // 엔티티 저장
        profileRepository.save(profile);
    }

    /* 이메일 인증번호 전송 서비스 */
    @Transactional
    public void sendEmail(EmailDto.SendEmail dto)
    {
        // 인증번호 생성
        Random random = new Random();
        StringBuilder random_number = new StringBuilder();
        for(int i = 0; i < 8; i++) {
            int word = random.nextInt(10);
            random_number.append(word);
        }
        String verification_number = random_number.toString();

        // 인증번호 발송
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            String content = String.format(new String(Files.readAllBytes(Paths.get(new ClassPathResource("email/VerificationNumber.txt").getURI()))), verification_number);

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(dto.getEmail());    // 메일 수신자
            mimeMessageHelper.setSubject("Speed.you 인증 번호");    // 메일 제목
            mimeMessageHelper.setText(content, true);   // 메일 내용
            javaMailSender.send(mimeMessage);

            // 이미 인증번호가 발송된 이메일인 경우, 데이터베이스에서 인증번호 정보 삭제
            Code code = codeRepository.findByEmail(dto.getEmail()).orElse(null);
            if(code != null)
                codeRepository.delete(code);

            // 새로 생성된 인증번호를 entity로 변환
            code = Code.builder()
                    .email(dto.getEmail())
                    .code(verification_number)
                    .created_at(LocalDate.now())
                    .build();

            // 해당 entity를 code 데이터베이스에 저장
            codeRepository.save(code);
        }
        catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    /* 이메일 인증번호 확인 서비스 */
    @Transactional
    public void checkEmail(EmailDto.CheckEmail dto)
    {
        // 이메일 중복 체크
        profileRepository.findByEmail(dto.getEmail())
                .ifPresent(nope -> {
                    throw new CustomException(CustomErrorCode.EMAIL_DUPLICATED, dto.getEmail());
                });

        // 해당 이메일로 발송된 인증번호가 있는지 체크
        Code acutalCode = codeRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(CustomErrorCode.CODE_NOT_FOUND, dto.getEmail()));

        // 사용자가 입력한 인증번호와 실제 인증번호가 다른 경우
        if(!Objects.equals(dto.getCode(), acutalCode.getCode()))
            throw new CustomException(CustomErrorCode.WRONG_CODE, dto.getEmail());
    }

    /* 비밀번호 초기화 서비스 */
    @Transactional
    public void resetPassword(EmailDto.SendEmail dto)
    {
        // 이메일에 해당하는 계정 존재 여부 체크
        Profile profile = profileRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND, dto.getEmail()));

        // 임시 비밀번호 생성
        Random random = new Random();
        StringBuilder randomString = new StringBuilder();
        for(int i = 0; i < 10; i++) {
            char word = (char)(random.nextInt(26) + 'a');
            randomString.append(word);
        }
        String newPassword = randomString.toString();

        // 임시 비밀번호를 해당 엔티티의 비밀번호로 설정
        profile.setPassword(encoder.encode(newPassword));

        // 번경 내용 적용
        profileRepository.save(profile);

        // 임시 비밀번호를 이메일로 전송
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            String content = String.format(new String(Files.readAllBytes(Paths.get(new ClassPathResource("email/ResetPassword.txt").getURI()))), newPassword);

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(dto.getEmail());    // 메일 수신자
            mimeMessageHelper.setSubject("Speed.you 임시 비밀번호 발급");   // 메일 제목
            mimeMessageHelper.setText(content, true);   // 메일 내용

            javaMailSender.send(mimeMessage);
        }
        catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    /* 계정 권한 확인 서비스 */
    public String getRole(Principal principal)
    {
        // 계정 권한 확인 요청을 보낸 사용자의 계정 이메일
        String email = principal.getName();

        // 해당 사용자의 계정이 존재하는지 확인
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND, email));

        // 계정이 존재한다면 role 반환
        return profile.getRole();
    }

    /* access token 재발급 서비스 */
    public String refreshToken(HttpServletRequest request)
    {
        String authorizationHeader = request.getHeader("Authorization");

        // 헤더에 JWT token이 존재하는지 체크
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
        {
            String token = authorizationHeader.substring(7);

            // JWT 유효성 검증
            if(jwtUtil.validateToken(token))
            {
                Long profile_id = jwtUtil.getProfileId(token);
                UserDetails userDetails = customUserDetailsService.loadUserByProfileId(profile_id);

                if(userDetails != null)
                {
                    Profile profile = profileRepository.findById(profile_id)
                            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND, null));

                    ProfileDto.Profile profileDto = ProfileDto.Profile.builder()
                            .profile_id(profile.getProfile_id())
                            .email(profile.getEmail())
                            .password(profile.getPassword())
                            .username(profile.getUsername())
                            .created_at(profile.getCreated_at())
                            .role(profile.getRole())
                            .build();

                    String email = profile.getEmail();

                    // access token 발행
                    TokenDto tokenDto = jwtUtil.returnToken(profileDto, false);

                    // redis에 access token 정보 저장
                    redisTemplate.opsForValue().set(email, tokenDto.getAccessToken());

                    // access token 반환
                    return tokenDto.getAccessToken();
                }
            }
        }

        throw new CustomException(CustomErrorCode.INVALID_TOKEN, null);
    }
}
