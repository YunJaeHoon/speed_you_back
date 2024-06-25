package com.example.speed_you_back.repository;

import com.example.speed_you_back.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, Long> {
}
