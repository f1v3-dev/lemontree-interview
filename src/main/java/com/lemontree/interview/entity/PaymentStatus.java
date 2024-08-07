package com.lemontree.interview.entity;

import lombok.Getter;
import lombok.ToString;

/**
 * 결제 상태 ENUM.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@Getter
@ToString
public enum PaymentStatus {

    // 결제 대기, 결제 완료, 결제 취소 3가지 상태를 가집니다.
    WAIT("결제 대기"),
    DONE("결제 완료"),
    CANCEL("결제 취소");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }
}
