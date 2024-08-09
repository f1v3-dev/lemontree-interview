package com.lemontree.interview.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotNull(message = "잔액을 입력해주세요.")
    private BigDecimal balance;

    @NotNull(message = "1회 한도를 입력해주세요.")
    private BigDecimal onceLimit;

    @NotNull(message = "1일 한도를 입력해주세요.")
    private BigDecimal dailyLimit;

    @NotNull(message = "1달 한도를 입력해주세요.")
    private BigDecimal monthlyLimit;

    private Boolean isDeleted;
}
