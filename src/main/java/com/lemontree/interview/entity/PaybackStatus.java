package com.lemontree.interview.entity;

/**
 * 페이백 상태 ENUM.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
public enum PaybackStatus {

    // 페이백 대기, 페이백 완료, 페이백 취소 3가지 상태를 가집니다.
    WAIT("페이백 대기"),
    DONE("페이백 완료"),
    CANCEL("페이백 취소");

    private final String description;

    PaybackStatus(String description) {
        this.description = description;
    }
}
