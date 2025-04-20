package com.example.backend.notification.repository;

import com.example.backend.notification.model.Notification;
import com.example.backend.notification.model.UserNotification;
import com.example.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    // 전체 알림
    List<UserNotification> findByUserOrderByCreatedAtDesc(User user);

    // 읽지 않은 알림
    List<UserNotification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

    // 읽은 알림
    List<UserNotification> findByUserAndIsReadTrueOrderByCreatedAtDesc(User user);

    List<UserNotification> findByTemplate(Notification notification);
}
