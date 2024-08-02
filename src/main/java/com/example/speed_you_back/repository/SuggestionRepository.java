package com.example.speed_you_back.repository;

import com.example.speed_you_back.entity.Suggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long>
{
    // 페이지 개수 만큼의 Suggestion
    @Query(value = "SELECT * FROM suggestion ORDER BY created_at DESC, suggestion_id DESC",
            countQuery = "SELECT count(*) FROM suggestion",
            nativeQuery = true)
    Page<Suggestion> suggestionsByPage(Pageable pageable);

    // 특정 프로필, 특정 날짜의 Suggestion 개수
    @Query(value = "SELECT count(*) FROM suggestion WHERE profile = :profile AND created_at = :created_at", nativeQuery = true)
    int countByProfileAndCreateAt(@Param("profile") Long profile, @Param("created_at") LocalDate created_at);
}
