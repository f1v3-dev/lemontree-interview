package com.lemontree.interview.enums;

import lombok.AllArgsConstructor;

/**
 * 결제 상태 ENUM.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@AllArgsConstructor
public enum PaymentStatus implements JsonEnum {

    // 결제 대기, 결제 완료, 결제 취소 3가지 상태를 가집니다.
    WAIT("결제 대기"),
    DONE("결제 완료"),
    CANCEL("결제 취소");

    private final String description;

    @Override
    public String getStatus() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
