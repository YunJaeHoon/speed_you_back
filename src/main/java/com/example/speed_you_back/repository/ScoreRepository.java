package com.example.speed_you_back.repository;

import com.example.speed_you_back.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScoreRepository extends JpaRepository<Score, Long>
{
    @Query(value = "SELECT COUNT(*) FROM score WHERE game = :game", nativeQuery = true)
    Long countAllScores(String game);

    @Query(value = "SELECT COUNT(*) FROM score WHERE game = :game AND score > :score", nativeQuery = true)
    Long countLargeScores(String game, double score);

    @Query(value = "SELECT COUNT(*) FROM score WHERE game = :game AND score < :score", nativeQuery = true)
    Long countLittleScores(String game, double score);
}
