package com.example.backend.global.response.responseStatus;


/*
enum(열거형)이란?
enum은 일정한 개수의 상수를 하나의 그룹으로 묶어서 관리하는 데이터 타입
가독성을 높이고, 실수로 잘못된 값을 사용하는 것을 방지하는 역할
자바에서는 enum을 클래스로 관리하며, 변수와 메서드를 가질 수 있음
*/

// 인터페이스로 변환
public interface BaseResponseStatus {
    boolean isSuccess();
    int getCode();
    String getMessage();
}

