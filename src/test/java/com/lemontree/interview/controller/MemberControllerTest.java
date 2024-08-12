package com.lemontree.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemontree.interview.entity.Member;
import com.lemontree.interview.request.MemberCreate;
import com.lemontree.interview.response.MemberResponse;
import com.lemontree.interview.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
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
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @DisplayName("유저 생성 테스트")
    void createMember() throws Exception {

        // given
        when(memberService.createMember(any())).thenReturn(1L);

        MemberCreate request = new MemberCreate();
        ReflectionTestUtils.setField(request, "name", "정승조");
        ReflectionTestUtils.setField(request, "balance", BigDecimal.valueOf(10000L));
        ReflectionTestUtils.setField(request, "balanceLimit", BigDecimal.valueOf(100000L));
        ReflectionTestUtils.setField(request, "onceLimit", BigDecimal.valueOf(5000L));
        ReflectionTestUtils.setField(request, "dailyLimit", BigDecimal.valueOf(10000L));
        ReflectionTestUtils.setField(request, "monthlyLimit", BigDecimal.valueOf(15000L));

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.memberId").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("유저 조회 테스트")
    void getMember() throws Exception {

        Member member = Member.builder()
                .name("정승조")
                .balance(BigDecimal.valueOf(10000L))
                .balanceLimit(BigDecimal.valueOf(100000L))
                .onceLimit(BigDecimal.valueOf(5000L))
                .dailyLimit(BigDecimal.valueOf(10000L))
                .monthlyLimit(BigDecimal.valueOf(15000L))
                .isDeleted(Boolean.FALSE)
                .build();

        MemberResponse response = new MemberResponse(member);

        // given
        when(memberService.getMember(anyLong())).thenReturn(response);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/members/{memberId}", 1L)
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
                .andDo(print());

    }

}