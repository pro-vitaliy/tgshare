package com.github.provitaliy.userservice.repository;

import com.github.provitaliy.userservice.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTelegramUserId(Long id);
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
