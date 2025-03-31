package com.example.backend.noti.repository;

import com.example.backend.noti.model.Noti;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotiRepository extends JpaRepository<Noti, Long> {
}
