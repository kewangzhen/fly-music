package com.example.flymusic.repository;

import com.example.flymusic.entity.UserInterestProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInterestProfileRepository extends JpaRepository<UserInterestProfile, Long> {
    
    Optional<UserInterestProfile> findByUserId(Long userId);
    
    void deleteByUserId(Long userId);
}
