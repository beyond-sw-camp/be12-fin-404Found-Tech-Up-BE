package com.example.backend.notification.model;

public enum NotificationType {
    // 기존 타입
    SCHEDULE,    // 스케줄 기반 알림
    ADMIN,       // 관리자 알림
    PERSONAL,    // 사용자 전용 쿠폰 발급 알림으로 재사용
    NOTIFICATION, // 전체 공지용 알림

    // 실시간 알림 타입 추가
    LOW_STOCK,   // 품절 임박 알림
    RESTOCK,     // 재입고 알림
    PRICE_DROP,  // 가격 인하 알림
    SPECIAL_DEAL, // 특가 알림
    ORDER_COMPLETE
}