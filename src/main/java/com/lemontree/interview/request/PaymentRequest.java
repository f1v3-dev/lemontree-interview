package com.lemontree.interview.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 머니 결제 API 요청 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@Getter
public class PaymentRequest {

    @NotNull(message = "결제 금액을 입력해주세요.")
    private Long amount;
}
