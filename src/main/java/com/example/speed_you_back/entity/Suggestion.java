package com.example.speed_you_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Suggestion
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long suggestion_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Profile profile;

    @Column(nullable = false) private String type;
    @Column(columnDefinition = "TEXT", nullable = false) private String detail;
    @Column(nullable = false) private LocalDate created_at;
}
