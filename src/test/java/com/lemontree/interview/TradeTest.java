package com.lemontree.interview;

import com.lemontree.interview.entity.Member;
import com.lemontree.interview.repository.MemberRepository;
import com.lemontree.interview.repository.TradeRepository;
import com.lemontree.interview.request.TradeRequest;
import com.lemontree.interview.service.PaybackService;
import com.lemontree.interview.service.PaymentService;
import com.lemontree.interview.service.TradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
 * 거래 통합 테스트입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@ActiveProfiles("test")
@SpringBootTest
class TradeTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PaymentService paymentService;

    @Autowired
    TradeRepository paymentRepository;

    @Autowired
    TradeService tradeService;

    @Autowired
    PaybackService paybackService;

    Member savedMember;


    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .name("정승조")
                .balance(BigDecimal.valueOf(10_000L))
                .balanceLimit(BigDecimal.valueOf(100_000L))
                .onceLimit(BigDecimal.valueOf(5_000L))
                .dailyLimit(BigDecimal.valueOf(1_0000L))
                .monthlyLimit(BigDecimal.valueOf(15_000L))
                .isDeleted(Boolean.FALSE)
                .build();

        savedMember = memberRepository.save(member);
    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("결제 비관적 락 테스트 - 100번 동시에 결제해도 1번만 결제된다.")
    void payment_lock() throws Exception {


        // 5000원 결제 요청
        TradeRequest paymentRequest = new TradeRequest();
        ReflectionTestUtils.setField(paymentRequest, "paymentAmount", BigDecimal.valueOf(500L));
        ReflectionTestUtils.setField(paymentRequest, "paybackAmount", BigDecimal.valueOf(10L));

        Long tradeId = tradeService.requestTrade(savedMember.getId(), paymentRequest);

        // 동시에 결제를 진행하는 테스트
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);  // 모든 스레드가 동시에 시작하도록 조정하는 용도
        CountDownLatch latch = new CountDownLatch(threadCount);  // 모든 스레드의 작업이 끝날 때까지 기다리기 위한 용도

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    startLatch.await();  // 모든 스레드가 준비되길 기다림
                    paymentService.processPayment(tradeId);
                    success.incrementAndGet();
                } catch (Exception e) {
                    fail.incrementAndGet();
                } finally {
                    latch.countDown();  // 스레드가 종료되면 latch 감소
                }
            });
        }

        startLatch.countDown();
        latch.await();

        // then
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertEquals(0, findMember.getBalance().compareTo(BigDecimal.valueOf(9_500L)));
        assertEquals(0, findMember.getDailyAccumulate().compareTo(BigDecimal.valueOf(500L)));
        assertEquals(0, findMember.getMonthlyAccumulate().compareTo(BigDecimal.valueOf(500L)));

        assertEquals(1, success.intValue());
        assertEquals(99, fail.intValue());
        assertEquals(threadCount, success.intValue() + fail.intValue());
    }

    @Test
    @DisplayName("결제 비관적 락 테스트 - 1000번 동시에 결제해도 1번만 결제된다.")
    void payment_lock2() throws Exception {

        TradeRequest paymentRequest = new TradeRequest();
        ReflectionTestUtils.setField(paymentRequest, "paymentAmount", BigDecimal.valueOf(5L));
        ReflectionTestUtils.setField(paymentRequest, "paybackAmount", BigDecimal.valueOf(1L));

        Long tradeId = tradeService.requestTrade(savedMember.getId(), paymentRequest);

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        int threadCount = 1000;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    startLatch.await();
                    paymentService.processPayment(tradeId);
                    success.incrementAndGet();
                } catch (Exception e) {
                    fail.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        startLatch.countDown();
        latch.await();


        assertEquals(1, success.intValue());
        assertEquals(threadCount - 1, fail.intValue());
        assertEquals(threadCount, success.intValue() + fail.intValue());

        executorService.shutdown();
    }

    @Test
    @DisplayName("동일 거래의 페이백을 100번 요청해도 1번만 성공한다.")
    void payback() throws InterruptedException {

        TradeRequest paymentRequest = new TradeRequest();
        ReflectionTestUtils.setField(paymentRequest, "paymentAmount", BigDecimal.valueOf(500L));
        ReflectionTestUtils.setField(paymentRequest, "paybackAmount", BigDecimal.valueOf(100L));

        Long tradeId = tradeService.requestTrade(savedMember.getId(), paymentRequest);

        paymentService.processPayment(tradeId);

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    startLatch.await();
                    paybackService.processPayback(tradeId);
                    success.incrementAndGet();
                } catch (Exception e) {
                    fail.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        startLatch.countDown();
        latch.await();


        // then
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertEquals(0, findMember.getBalance().compareTo(BigDecimal.valueOf(9_600L)));

        assertEquals(1, success.intValue());
        assertEquals(threadCount - 1, fail.intValue());
    }

    @Test
    @DisplayName("페이백 취소를 100번 요청해도 1번만 성공한다.")
    void cancel_payback() throws InterruptedException {

        TradeRequest paymentRequest = new TradeRequest();
        ReflectionTestUtils.setField(paymentRequest, "paymentAmount", BigDecimal.valueOf(500L));
        ReflectionTestUtils.setField(paymentRequest, "paybackAmount", BigDecimal.valueOf(100L));

        Long tradeId = tradeService.requestTrade(savedMember.getId(), paymentRequest);

        paymentService.processPayment(tradeId);
        paybackService.processPayback(tradeId);

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    startLatch.await();
                    paybackService.cancelPayback(tradeId);
                    success.incrementAndGet();
                } catch (Exception e) {
                    fail.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        startLatch.countDown();
        latch.await();


        // then
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertEquals(0, findMember.getBalance().compareTo(BigDecimal.valueOf(9_500L)));

        assertEquals(1, success.intValue());
        assertEquals(threadCount - 1, fail.intValue());
    }
}
