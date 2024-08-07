package com.lemontree.interview.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 결제 Entity 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@Getter
@Entity
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private Long paymentAmount;

    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = true)
    private Long paybackAmount;

    @Column(nullable = true)
    private PaybackStatus paybackStatus;

    @Builder
    public Payment(Member member, Long paymentAmount) {
        this.member = member;
        this.paymentAmount = paymentAmount;
        this.paymentStatus = PaymentStatus.WAIT;
    }
}
