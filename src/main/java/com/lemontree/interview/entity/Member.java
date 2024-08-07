package com.lemontree.interview.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * 유저 Entity 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@Getter
@Entity
@Table(name = "member")
@SQLDelete(sql = "UPDATE member SET is_deleted = true WHERE member_id = ?")
@SQLRestriction("is_deleted = FALSE") // 삭제되지 않은 데이터만 조회
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long money;

    @Column(nullable = false)
    private Long onceLimit;

    @Column(nullable = false)
    private Long dailyLimit;

    @Column(nullable = false)
    private Long monthlyLimit;

    @Column(nullable = false)
    private Long dailyAccumulate;

    @Column(nullable = false)
    private Long monthlyAccumulate;

    @Column(nullable = false)
    private Boolean isDeleted;

    /**
     * 유저 생성자입니다. (Builder Pattern)
     *
     * @param name         유저 이름
     * @param money        유저의 보유 금액
     * @param onceLimit    유저가 한 번에 사용할 수 있는 금액
     * @param dailyLimit   유저가 하루에 사용할 수 있는 금액
     * @param monthlyLimit 유저가 한 달에 사용할 수 있는 금액
     */
    @Builder
    public Member(String name, Long money, Long onceLimit, Long dailyLimit, Long monthlyLimit) {
        this.name = name;
        this.money = money;
        this.onceLimit = onceLimit;
        this.dailyLimit = dailyLimit;
        this.monthlyLimit = monthlyLimit;
        this.dailyAccumulate = 0L;
        this.monthlyAccumulate = 0L;
        this.isDeleted = Boolean.FALSE;

    }
}
