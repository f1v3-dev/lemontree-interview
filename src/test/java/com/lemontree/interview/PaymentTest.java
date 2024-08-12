package com.lemontree.interview;

import com.lemontree.interview.entity.Member;
import com.lemontree.interview.repository.MemberRepository;
import com.lemontree.interview.repository.PaymentRepository;
import com.lemontree.interview.request.PaymentRequest;
import com.lemontree.interview.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration testing for Payment.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
class PaymentTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("결제 비관적 락 테스트")
    void payment_lock() throws Exception {

        Member member = Member.builder()
                .name("정승조")
                .balance(BigDecimal.valueOf(10000L))
                .balanceLimit(BigDecimal.valueOf(100000L))
                .onceLimit(BigDecimal.valueOf(5000L))
                .dailyLimit(BigDecimal.valueOf(10000L))
                .monthlyLimit(BigDecimal.valueOf(15000L))
                .isDeleted(Boolean.FALSE)
                .build();

        Member savedMember = memberRepository.save(member);

        // 5000원 결제 요청
        PaymentRequest paymentRequest = new PaymentRequest();
        ReflectionTestUtils.setField(paymentRequest, "paymentAmount", BigDecimal.valueOf(5000L));
        ReflectionTestUtils.setField(paymentRequest, "paybackAmount", BigDecimal.valueOf(1000L));

        // 동시에 결제를 진행하는 테스트
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);  // 모든 스레드가 동시에 시작하도록 조정하는 용도
        CountDownLatch latch = new CountDownLatch(threadCount);  // 모든 스레드의 작업이 끝날 때까지 기다리기 위한 용도

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    startLatch.await();  // 모든 스레드가 준비되길 기다림
                    paymentService.processPayment(savedMember.getId(), paymentRequest);
                    success.incrementAndGet();
                } catch (Exception e) {
                    fail.incrementAndGet();
                } finally {
                    latch.countDown();  // 스레드가 종료되면 latch 감소
                }
            });
        }

        // 모든 스레드가 준비되면 startLatch 해제하여 동시에 시작하게 함
        startLatch.countDown();


        // 모든 스레드가 작업을 마칠 때까지 기다림
        latch.await();

        // then
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertEquals(0, findMember.getBalance().compareTo(BigDecimal.valueOf(5000L)));
        assertEquals(0, findMember.getDailyAccumulate().compareTo(BigDecimal.valueOf(5000L)));
        assertEquals(0, findMember.getMonthlyAccumulate().compareTo(BigDecimal.valueOf(5000L)));

        log.info("success count = {}", success.intValue());
        log.info("fail count = {}", fail.intValue());
        assertEquals(threadCount, success.intValue() + fail.intValue());
    }

    @Test
    @DisplayName("결제 취소 테스트")
    void cancel_payment() {

        Member member = Member.builder()
                .name("정승조")
                .balance(BigDecimal.valueOf(10000L))
                .balanceLimit(BigDecimal.valueOf(100000L))
                .onceLimit(BigDecimal.valueOf(5000L))
                .dailyLimit(BigDecimal.valueOf(10000L))
                .monthlyLimit(BigDecimal.valueOf(15000L))
                .isDeleted(Boolean.FALSE)
                .build();

        Member savedMember = memberRepository.save(member);

        // 500원 결제 요청
        PaymentRequest paymentRequest = new PaymentRequest();
        ReflectionTestUtils.setField(paymentRequest, "paymentAmount", BigDecimal.valueOf(500L));
        ReflectionTestUtils.setField(paymentRequest, "paybackAmount", BigDecimal.valueOf(100L));

        Long paymentId = paymentService.processPayment(savedMember.getId(), paymentRequest);

        // 결제 취소
        paymentService.cancelPayment(savedMember.getId(), paymentId);

        // then
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertEquals(0, findMember.getBalance().compareTo(BigDecimal.valueOf(10000L)));
        assertEquals(0, findMember.getDailyAccumulate().compareTo(BigDecimal.ZERO));
        assertEquals(0, findMember.getMonthlyAccumulate().compareTo(BigDecimal.ZERO));

    }
}
