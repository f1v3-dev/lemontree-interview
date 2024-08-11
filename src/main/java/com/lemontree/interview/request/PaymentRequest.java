package com.lemontree.interview.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 머니 결제 API 요청 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@Getter
public class PaymentRequest {

    @NotNull(message = "결제 금액을 입력해주세요.")
    @Positive(message = "결제 금액은 0보다 커야합니다.")
    private BigDecimal paymentAmount;

    @NotNull(message = "페이백 금액을 입력해주세요.")
    @PositiveOrZero(message = "페이백 금액을 0원 이상으로 입력해주세요.")
    private BigDecimal paybackAmount;
}
