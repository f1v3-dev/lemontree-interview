package com.lemontree.interview.service;

import com.lemontree.interview.entity.Member;
import com.lemontree.interview.exception.member.BalanceExceededException;
import com.lemontree.interview.exception.member.DailyLimitExceedsMonthlyLimitException;
import com.lemontree.interview.exception.member.MemberNotFoundException;
import com.lemontree.interview.exception.member.OnceLimitExceedsDailyLimitException;
import com.lemontree.interview.repository.MemberRepository;
import com.lemontree.interview.request.MemberCreate;
import com.lemontree.interview.response.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 유저 서비스 테스트입니다.
 *
 * @author 정승조
 * @version 2024. 08. 12.
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("유저 생성 - 실패 (잔액이 한도보다 많은 경우)")
    void 유저_생성_실패_잔액() {

        // given
        MemberCreate member = new MemberCreate();
        ReflectionTestUtils.setField(member, "name", "정승조");
        ReflectionTestUtils.setField(member, "balance", BigDecimal.valueOf(10_000L));
        ReflectionTestUtils.setField(member, "balanceLimit", BigDecimal.valueOf(5_000L));
        ReflectionTestUtils.setField(member, "onceLimit", BigDecimal.valueOf(1_000L));
        ReflectionTestUtils.setField(member, "dailyLimit", BigDecimal.valueOf(2_000L));
        ReflectionTestUtils.setField(member, "monthlyLimit", BigDecimal.valueOf(5_000L));

        // expected
        assertThrows(BalanceExceededException.class,
                () -> memberService.createMember(member));

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("유저 생성 - 실패 (1회 한도가 1일 한도보다 큰 경우)")
    void 유저_생성_실패_1회1일_한도() {

        // given
        MemberCreate member = new MemberCreate();
        ReflectionTestUtils.setField(member, "name", "정승조");
        ReflectionTestUtils.setField(member, "balance", BigDecimal.valueOf(10_000L));
        ReflectionTestUtils.setField(member, "balanceLimit", BigDecimal.valueOf(50_000L));
        ReflectionTestUtils.setField(member, "onceLimit", BigDecimal.valueOf(5_000L));
        ReflectionTestUtils.setField(member, "dailyLimit", BigDecimal.valueOf(1_000L));
        ReflectionTestUtils.setField(member, "monthlyLimit", BigDecimal.valueOf(3_000L));

        // expected
        assertThrows(OnceLimitExceedsDailyLimitException.class,
                () -> memberService.createMember(member));

        verify(memberRepository, never()).save(any());
    }


    @Test
    @DisplayName("유저 생성 - 실패 (1일 한도가 1달 한도보다 큰 경우)")
    void 유저_생성_실패_1일1달_한도() {

        // given
        MemberCreate member = new MemberCreate();
        ReflectionTestUtils.setField(member, "name", "정승조");
        ReflectionTestUtils.setField(member, "balance", BigDecimal.valueOf(10_000L));
        ReflectionTestUtils.setField(member, "balanceLimit", BigDecimal.valueOf(50_000L));
        ReflectionTestUtils.setField(member, "onceLimit", BigDecimal.valueOf(5_000L));
        ReflectionTestUtils.setField(member, "dailyLimit", BigDecimal.valueOf(10_000L));
        ReflectionTestUtils.setField(member, "monthlyLimit", BigDecimal.valueOf(3_000L));

        // expected
        assertThrows(DailyLimitExceedsMonthlyLimitException.class,
                () -> memberService.createMember(member));

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("유저 생성 - 성공")
    void 유저_생성_성공() {

        // given
        MemberCreate member = new MemberCreate();
        ReflectionTestUtils.setField(member, "name", "정승조");
        ReflectionTestUtils.setField(member, "balance", BigDecimal.valueOf(10_000L));
        ReflectionTestUtils.setField(member, "balanceLimit", BigDecimal.valueOf(50_000L));
        ReflectionTestUtils.setField(member, "onceLimit", BigDecimal.valueOf(5_000L));
        ReflectionTestUtils.setField(member, "dailyLimit", BigDecimal.valueOf(10_000L));
        ReflectionTestUtils.setField(member, "monthlyLimit", BigDecimal.valueOf(30_000L));

        Member expected = Member.builder()
                .name("정승조")
                .balance(BigDecimal.valueOf(10_000L))
                .balanceLimit(BigDecimal.valueOf(50_000L))
                .onceLimit(BigDecimal.valueOf(5_000L))
                .dailyLimit(BigDecimal.valueOf(10_000L))
                .monthlyLimit(BigDecimal.valueOf(30_000L))
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(expected, "id", 1L);

        when(memberRepository.save(any()))
                .thenReturn(expected);

        // expected
        Long memberId = memberService.createMember(member);

        assertEquals(1L, memberId);
        verify(memberRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("유저 조회 - 실패 (존재하지 않는 유저)")
    void 유저_조회_실패() {

        // given
        Long notExistsMemberId = 1L;

        // expected
        assertThrows(MemberNotFoundException.class,
                () -> memberService.getMember(notExistsMemberId));

        verify(memberRepository, times(1)).findById(notExistsMemberId);
    }

    @Test
    @DisplayName("유저 조쇠 - 성공")
    void 유저_조회_성공() {

        // given
        Long memberId = 1L;

        Member member = Member.builder()
                .name("정승조")
                .balance(BigDecimal.valueOf(10_000L))
                .balanceLimit(BigDecimal.valueOf(50_000L))
                .onceLimit(BigDecimal.valueOf(5_000L))
                .dailyLimit(BigDecimal.valueOf(10_000L))
                .monthlyLimit(BigDecimal.valueOf(30_000L))
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        // expected
        MemberResponse expected = memberService.getMember(memberId);

        assertAll(
                () -> assertEquals(memberId, expected.getMemberId()),
                () -> assertEquals("정승조", expected.getName()),
                () -> assertEquals(BigDecimal.valueOf(10_000L), expected.getBalance()),
                () -> assertEquals(BigDecimal.valueOf(50_000L), expected.getBalanceLimit()),
                () -> assertEquals(BigDecimal.valueOf(5_000L), expected.getOnceLimit()),
                () -> assertEquals(BigDecimal.valueOf(10_000L), expected.getDailyLimit()),
                () -> assertEquals(BigDecimal.valueOf(30_000L), expected.getMonthlyLimit()),
                () -> assertFalse(expected.getIsDeleted())
        );
    }
}