package com.lemontree.interview.repository;

import com.lemontree.interview.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Payment JPA Repository.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
