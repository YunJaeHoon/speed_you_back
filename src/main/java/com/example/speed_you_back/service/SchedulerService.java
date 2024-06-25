package com.example.speed_you_back.service;

import com.example.speed_you_back.repository.CodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SchedulerService
{
    @Autowired
    private CodeRepository codeRepository;

    @Transactional
    @Async
    @Scheduled(cron = "0 0 12 * * *")
    public void autoDeleteCode()
    {
        // 매일 오후 12시(정오)에 오래된 인증번호를 code 데이터베이스에서 삭제
        codeRepository.deleteOldCode(LocalDate.now().minusDays(1));
    }
}
