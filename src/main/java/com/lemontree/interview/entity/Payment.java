package com.lemontree.interview.entity;

import com.lemontree.interview.enums.PaybackStatus;
import com.lemontree.interview.enums.PaymentStatus;
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
@Table(name = "payment",
        indexes = {
                @Index(name = "idx_payment_member_id", columnList = "member_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    // 결제 금액
    @Column(nullable = false)
    private Long paymentAmount;

    // 결제 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private PaymentStatus paymentStatus;

    // 페이백 금액
    @Column(nullable = true)
    private Long paybackAmount;

    // 페이백 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, columnDefinition = "VARCHAR(10)")
    private PaybackStatus paybackStatus;

    @Builder
    public Payment(Member member, Long paymentAmount) {
        this.member = member;
        this.paymentAmount = paymentAmount;
        this.paymentStatus = PaymentStatus.WAIT;
    }
}
