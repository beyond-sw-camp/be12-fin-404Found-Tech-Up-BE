package com.example.backend.notification.repository;

import com.example.backend.notification.model.Notification;
import com.example.backend.notification.model.UserNotification;
import com.example.backend.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    // 전체 알림 목록 (최신순 정렬)
    Page<UserNotification> findByUser(User user, Pageable pageable);

    // 읽음 알림 목록 (최신순 정렬)
    Page<UserNotification> findByUserAndIsReadTrue(User user, Pageable pageable);

    // 안읽음 알림 목록 (최신순 정렬)
    Page<UserNotification> findByUserAndIsReadFalse(User user, Pageable pageable);

    long countByUserAndIsReadFalse(User user);


    List<UserNotification> findByTemplate(Notification notification);
}
