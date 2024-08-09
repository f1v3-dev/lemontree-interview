package com.lemontree.interview.repository;

import com.lemontree.interview.entity.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

/**
 * Payment JPA Repository.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 결제 ID로 결제 정보를 조회합니다. (비관적 락 사용)
     *
     * @param paymentId 결제 ID
     * @return 결제 정보
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Payment> findWithPessimisticLockById(Long paymentId);
}
