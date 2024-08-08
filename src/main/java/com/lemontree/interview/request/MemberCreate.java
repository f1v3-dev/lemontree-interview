package com.lemontree.interview.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 유저 생성 Request 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 08.
 */
@Getter
public class MemberCreate {

    @NotBlank
    private String name;

    @NotNull
    private Long balance;

    @NotNull
    private Long onceLimit;

    @NotNull
    private Long dailyLimit;

    @NotNull
    private Long monthlyLimit;

    private Boolean isDeleted;
}
