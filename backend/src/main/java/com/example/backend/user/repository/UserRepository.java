package com.example.backend.user.repository;

import com.example.backend.user.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // TODO: N+1 문제 해결
    Optional<User> findByUserEmail(String email);

    Optional<User> findByUserNickname(String userNickname);
}
