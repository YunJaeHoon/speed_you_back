package com.example.speed_you_back.service;

import com.example.speed_you_back.dto.EmailDto;
import com.example.speed_you_back.dto.ProfileDto;
import com.example.speed_you_back.entity.Profile;
import com.example.speed_you_back.entity.Code;
import com.example.speed_you_back.exception.CustomErrorCode;
import com.example.speed_you_back.exception.CustomException;
import com.example.speed_you_back.repository.ProfileRepository;
import com.example.speed_you_back.repository.CodeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class LoginService
{
    @Autowired ProfileRepository profileRepository;
    @Autowired CodeRepository codeRepository;
    @Autowired BCryptPasswordEncoder encoder;
    @Autowired JavaMailSender javaMailSender;

    /* 회원가입 서비스 */
    @Transactional
    public void join(ProfileDto.join dto)
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
    public void sendEmail(EmailDto.sendEmail dto)
    {
        // 이메일 중복 체크
        profileRepository.findByEmail(dto.getEmail())
                .ifPresent(nope -> {
                    throw new CustomException(CustomErrorCode.EMAIL_DUPLICATED, dto.getEmail());
                });

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
            String content = String.format("Speed.you <br> 인증 번호 <br><br> %s", verification_number);

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
        catch (CustomException e) {
            throw new CustomException(CustomErrorCode.EMAIL_NOT_SEND, dto.getEmail());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /* 이메일 인증번호 확인 서비스 */
    @Transactional
    public void checkEmail(EmailDto.checkEmail dto)
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
    public void resetPassword(EmailDto.sendEmail dto)
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
            String content = String.format("Speed.you <br> 임시 비밀번호 <br><br> %s", newPassword);

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(dto.getEmail());    // 메일 수신자
            mimeMessageHelper.setSubject("Speed.you 임시 비밀번호 발급");   // 메일 제목
            mimeMessageHelper.setText(content, true);   // 메일 내용

            javaMailSender.send(mimeMessage);
        }
        catch (CustomException e) {
            throw new CustomException(CustomErrorCode.EMAIL_NOT_SEND, dto.getEmail());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /* 로그인 여부 확인 서비스 */
    public boolean isLogin(Principal principal)
    {
        // 로그인을 하지 않은 사용자는 false 반환
        if(principal == null)
            return false;

        // 로그인 여부 확인 요청을 보낸 사용자의 계정 이메일
        String email = principal.getName();

        // 해당 사용자의 계정이 존재하는지 확인한 뒤, 해당 여부를 반환
        Optional<Profile> profile = profileRepository.findByEmail(email);

        return profile.isPresent();
    }
}
