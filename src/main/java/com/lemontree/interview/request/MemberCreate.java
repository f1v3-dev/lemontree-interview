package com.lemontree.interview.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 유저 생성 Request 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 08.
 */
@Getter
public class MemberCreate {

    @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하로 입력해주세요.")
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @PositiveOrZero(message = "잔액은 0 이상의 값으로 입력해주세요.")
    @NotNull(message = "잔액을 입력해주세요.")
    private BigDecimal balance;

    @PositiveOrZero(message = "잔액 한도는 0 이상의 값으로 입력해주세요.")
    @NotNull(message = "잔액 한도를 입력해주세요.")
    private BigDecimal balanceLimit;

    @PositiveOrZero(message = "1회 한도는 0 이상의 값으로 입력해주세요.")
    @NotNull(message = "1회 한도를 입력해주세요.")
    private BigDecimal onceLimit;

    @PositiveOrZero(message = "1일 한도는 0 이상의 값으로 입력해주세요.")
    @NotNull(message = "1일 한도를 입력해주세요.")
    private BigDecimal dailyLimit;

    @PositiveOrZero(message = "1달 한도는 0 이상의 값으로 입력해주세요.")
    @NotNull(message = "1달 한도를 입력해주세요.")
    private BigDecimal monthlyLimit;

    private Boolean isDeleted;
}
