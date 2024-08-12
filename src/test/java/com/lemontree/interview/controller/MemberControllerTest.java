package com.lemontree.interview.controller;

import com.lemontree.interview.config.AbstractRestDocsTest;
import com.lemontree.interview.entity.Member;
import com.lemontree.interview.request.MemberCreate;
import com.lemontree.interview.response.MemberResponse;
import com.lemontree.interview.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 유저 컨트롤러 테스트입니다.
 *
 * @author 정승조
 * @version 2024. 08. 11.
 */

@WebMvcTest(MemberController.class)
class MemberControllerTest extends AbstractRestDocsTest {

    @MockBean
    MemberService memberService;

    @Test
    @DisplayName("유저 생성 테스트")
    void 유저_생성() throws Exception {

        // given
        when(memberService.createMember(any())).thenReturn(1L);

        MemberCreate request = new MemberCreate();
        ReflectionTestUtils.setField(request, "name", "정승조");
        ReflectionTestUtils.setField(request, "balance", BigDecimal.valueOf(10000L));
        ReflectionTestUtils.setField(request, "balanceLimit", BigDecimal.valueOf(100000L));
        ReflectionTestUtils.setField(request, "onceLimit", BigDecimal.valueOf(5000L));
        ReflectionTestUtils.setField(request, "dailyLimit", BigDecimal.valueOf(10000L));
        ReflectionTestUtils.setField(request, "monthlyLimit", BigDecimal.valueOf(15000L));
        ReflectionTestUtils.setField(request, "isDeleted", Boolean.FALSE);

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.memberId").value(1))
                .andDo(print())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("name").description("유저 이름"),
                                fieldWithPath("balance").description("잔액"),
                                fieldWithPath("balanceLimit").description("잔액 한도"),
                                fieldWithPath("onceLimit").description("한번 결제 한도"),
                                fieldWithPath("dailyLimit").description("일일 결제 한도"),
                                fieldWithPath("monthlyLimit").description("월 결제 한도"),
                                fieldWithPath("isDeleted").description("탈퇴 여부")
                        ),
                        responseFields(
                                fieldWithPath("memberId").description("유저 ID")
                        )));
    }

    @Test
    @DisplayName("유저 조회 테스트")
    void 유저_조회() throws Exception {

        // given
        Member member = Member.builder()
                .name("정승조")
                .balance(BigDecimal.valueOf(10000L))
                .balanceLimit(BigDecimal.valueOf(100000L))
                .onceLimit(BigDecimal.valueOf(5000L))
                .dailyLimit(BigDecimal.valueOf(10000L))
                .monthlyLimit(BigDecimal.valueOf(15000L))
                .isDeleted(Boolean.FALSE)
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);
        MemberResponse response = new MemberResponse(member);

        when(memberService.getMember(anyLong())).thenReturn(response);

        // expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/members/{memberId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name").value("정승조"),
                        jsonPath("$.balance").value(10000),
                        jsonPath("$.balanceLimit").value(100000),
                        jsonPath("$.onceLimit").value(5000),
                        jsonPath("$.dailyLimit").value(10000),
                        jsonPath("$.monthlyLimit").value(15000),
                        jsonPath("$.isDeleted").value(false))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId").description("유저 ID")),
                        responseFields(
                                fieldWithPath("memberId").description("유저 ID"),
                                fieldWithPath("name").description("유저 이름"),
                                fieldWithPath("balance").description("잔액"),
                                fieldWithPath("balanceLimit").description("잔액 한도"),
                                fieldWithPath("onceLimit").description("한번 결제 한도"),
                                fieldWithPath("dailyLimit").description("일일 결제 한도"),
                                fieldWithPath("monthlyLimit").description("월 결제 한도"),
                                fieldWithPath("dailyAccumulate").description("일 사용 누적 금액"),
                                fieldWithPath("monthlyAccumulate").description("월 사용 누적 금액"),
                                fieldWithPath("isDeleted").description("탈퇴 여부")
                        )
                ));

    }

}