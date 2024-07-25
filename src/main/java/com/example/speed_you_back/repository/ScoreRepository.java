package com.example.speed_you_back.repository;

import com.example.speed_you_back.entity.Profile;
import com.example.speed_you_back.entity.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long>
{
    @Query(value = "SELECT COUNT(*) FROM score WHERE game = :game", nativeQuery = true)
    Long countAllScores(String game);

    @Query(value = "SELECT COUNT(*) FROM score WHERE game = :game AND score > :score", nativeQuery = true)
    Long countLargeScores(String game, double score);
    @Query(value = "SELECT COUNT(*) FROM score WHERE game = :game AND score < :score", nativeQuery = true)
    Long countLittleScores(String game, double score);

    @Query(value = "SELECT * FROM score WHERE game = :game ORDER BY score DESC LIMIT 10", nativeQuery = true)
    List<Score> largeTopTen(String game);
    @Query(value = "SELECT * FROM score WHERE game = :game ORDER BY score ASC LIMIT 10", nativeQuery = true)
    List<Score> littleTopTen(String game);

    @Query(value = "SELECT score FROM score WHERE game = :game ORDER BY score ASC LIMIT :boundary, 1", nativeQuery = true)
    Integer largeBoundaryScore(String game, Long boundary);
    @Query(value = "SELECT score FROM score WHERE game = :game ORDER BY score DESC LIMIT :boundary, 1", nativeQuery = true)
    Integer littleBoundaryScore(String game, Long boundary);

    @Query(value = "SELECT * FROM score WHERE profile = :profile AND game = :game ORDER BY score DESC LIMIT 1", nativeQuery = true)
    Score highestScore(@Param("profile") Long profile, String game);
    @Query(value = "SELECT * FROM score WHERE profile = :profile AND game = :game ORDER BY score ASC LIMIT 1", nativeQuery = true)
    Score lowestScore(@Param("profile") Long profile, String game);

    @Query(value = "SELECT * FROM score WHERE profile = :profile ORDER BY created_at DESC, score_id DESC",
            countQuery = "SELECT count(*) FROM score WHERE profile = :profile",
            nativeQuery = true)
    Page<Score> findByProfileAllNewest(@Param("profile") Long profile, Pageable pageable);
    @Query(value = "SELECT * FROM score WHERE profile = :profile ORDER BY created_at ASC, score_id ASC",
            countQuery = "SELECT count(*) FROM score WHERE profile = :profile",
            nativeQuery = true)
    Page<Score> findByProfileAllOldest(@Param("profile") Long profile, Pageable pageable);

    @Query(value = "SELECT * FROM score WHERE profile = :profile AND game = :game ORDER BY created_at DESC, score_id DESC",
            countQuery = "SELECT count(*) FROM score WHERE profile = :profile AND game = :game",
            nativeQuery = true)
    Page<Score> findByProfileGameNewest(@Param("profile") Long profile, String game, Pageable pageable);
    @Query(value = "SELECT * FROM score WHERE profile = :profile AND game = :game ORDER BY created_at ASC, score_id ASC",
            countQuery = "SELECT count(*) FROM score WHERE profile = :profile AND game = :game",
            nativeQuery = true)
    Page<Score> findByProfileGameOldest(@Param("profile") Long profile, String game, Pageable pageable);
    @Query(value = "SELECT * FROM score WHERE profile = :profile AND game = :game ORDER BY score DESC, score_id DESC",
            countQuery = "SELECT count(*) FROM score WHERE profile = :profile AND game = :game",
            nativeQuery = true)
    Page<Score> findByProfileGameHighest(@Param("profile") Long profile, String game, Pageable pageable);
    @Query(value = "SELECT * FROM score WHERE profile = :profile AND game = :game ORDER BY score ASC, score_id DESC",
            countQuery = "SELECT count(*) FROM score WHERE profile = :profile AND game = :game",
            nativeQuery = true)
    Page<Score> findByProfileGameLowest(@Param("profile") Long profile, String game, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM score WHERE profile = :profile", nativeQuery = true)
    Long countAllScoresByProfile(Long profile);
    @Query(value = "SELECT COUNT(*) FROM score WHERE profile = :profile AND game = :game", nativeQuery = true)
    Long countGameScoresByProfile(Long profile, String game);
}
