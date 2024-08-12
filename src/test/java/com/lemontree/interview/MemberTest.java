package com.lemontree.interview;

import com.lemontree.interview.entity.Member;
import com.lemontree.interview.repository.MemberRepository;
import com.lemontree.interview.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 유저 테스트입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@ActiveProfiles("test")
@SpringBootTest
class MemberTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PaymentService paymentService;

    @Test
    @DisplayName("회원 저장 테스트")
    void 회원_저장_테스트() {

        // given
        Member member = Member.builder()
                .name("정승조")
                .balance(BigDecimal.valueOf(50000L))
                .balanceLimit(BigDecimal.valueOf(100000L))
                .onceLimit(BigDecimal.valueOf(1000L))
                .dailyLimit(BigDecimal.valueOf(10000L))
                .monthlyLimit(BigDecimal.valueOf(300000L))
                .isDeleted(Boolean.FALSE)
                .build();

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertNotNull(savedMember.getId());

        memberRepository.delete(savedMember);
    }


    @Test
    @DisplayName("회원 저장 및 Soft Delete 테스트")
    void 회원_저장_및_Soft_delete_테스트() {

        // given
        Member member = Member.builder()
                .name("정승조")
                .balance(BigDecimal.valueOf(50000L))
                .balanceLimit(BigDecimal.valueOf(100000L))
                .onceLimit(BigDecimal.valueOf(1000L))
                .dailyLimit(BigDecimal.valueOf(10000L))
                .monthlyLimit(BigDecimal.valueOf(300000L))
                .build();

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertNotNull(savedMember.getId());

        // @SQLDelete 사용으로 인해 실제로 삭제하는 것이 아닌, is_deleted = true 로 변경합니다.
        memberRepository.deleteById(savedMember.getId());

        // @SQLRestriction 사용으로 인해 is_deleted = false 데이터만 조회합니다.
        List<Member> memberList = memberRepository.findAll();
        assertEquals(0, memberList.size());

        Optional<Member> optionalMember = memberRepository.findById(savedMember.getId());
        assertTrue(optionalMember.isEmpty());
    }
}