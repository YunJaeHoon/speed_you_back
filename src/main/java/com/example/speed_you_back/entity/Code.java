package com.example.speed_you_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class Code
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long code_id;

    @Column(nullable = false) private String email;
    @Column(nullable = false) private String code;
    @Column(nullable = false) private LocalDate created_at;
}
