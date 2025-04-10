package com.example.backend.user.repository;

import com.example.backend.user.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // TODO: N+1 문제 해결
    Optional<User> findByUserEmail(String email);
    List<User> findAllByCreatedAtAfter(Date createdAt);
    Optional<User> findByUserNickname(String userNickname);
}
