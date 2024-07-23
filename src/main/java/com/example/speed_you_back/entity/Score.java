package com.example.speed_you_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Score
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long score_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Profile profile;

    @Column(nullable = false) private String game;
    @Column(nullable = false) private int score;
    @Column(nullable = false) private LocalDateTime created_at;
}
