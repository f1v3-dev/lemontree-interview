package com.lemontree.interview.repository;

import com.lemontree.interview.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Member JPA Repository.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
}
