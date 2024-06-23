package com.example.speed_you_back.repository;

import com.example.speed_you_back.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long>
{
    Optional<Profile> findByEmail(String email);
}
