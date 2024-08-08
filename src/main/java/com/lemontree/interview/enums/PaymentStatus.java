package com.lemontree.interview.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;

/**
 * 결제 상태 ENUM.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor
public enum PaymentStatus {

    // 결제 대기, 결제 완료, 결제 취소 3가지 상태를 가집니다.
    WAIT("결제 대기"),
    DONE("결제 완료"),
    CANCEL("결제 취소");

    private final String description;
}
