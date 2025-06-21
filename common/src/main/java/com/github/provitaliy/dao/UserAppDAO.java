package com.github.provitaliy.dao;

import com.github.provitaliy.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAppDAO extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTelegramUserId(Long id);
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
