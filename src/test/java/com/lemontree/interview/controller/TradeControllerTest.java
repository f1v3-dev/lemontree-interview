package com.lemontree.interview.controller;

import com.lemontree.interview.config.AbstractRestDocsTest;
import com.lemontree.interview.entity.Trade;
import com.lemontree.interview.exception.member.MemberNotFoundException;
import com.lemontree.interview.exception.trade.TradeNotFoundException;
import com.lemontree.interview.request.TradeRequest;
import com.lemontree.interview.response.TradeResponse;
import com.lemontree.interview.service.TradeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 거래 컨트롤러 테스트입니다.
 *
 * @author 정승조
 * @version 2024. 08. 13.
 */
@WebMvcTest(TradeController.class)
class TradeControllerTest extends AbstractRestDocsTest {

    @MockBean
    TradeService tradeService;


    @Test
    @DisplayName("거래 생성 요청 - 성공")
    void 거래생성_성공() throws Exception {

        // given
        Long memberId = 1L;

        TradeRequest request = new TradeRequest();
        ReflectionTestUtils.setField(request, "paymentAmount", BigDecimal.valueOf(10_000L));
        ReflectionTestUtils.setField(request, "paybackAmount", BigDecimal.valueOf(1_000L));
        String json = objectMapper.writeValueAsString(request);

        when(tradeService.requestTrade(anyLong(), any())).thenReturn(1L);

        // expected
        mockMvc.perform(post("/api/v1/members/{memberId}/trades", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.tradeId").value(1L))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId").description("결제 요청 유저 ID")
                        ),
                        requestFields(
                                fieldWithPath("paymentAmount").description("결제 금액"),
                                fieldWithPath("paybackAmount").description("페이백 금액")
                        ),
                        responseFields(
                                fieldWithPath("tradeId").description("생성된 거래 ID")
                        )
                ));
    }

    @Test
    @DisplayName("거래 생성 요청 - 실패 (존재하지 않는 유저)")
    void 거래생성_실패_존재하지않는유저() throws Exception {

        // given
        Long notExistsMemberId = 10L;

        TradeRequest request = new TradeRequest();
        ReflectionTestUtils.setField(request, "paymentAmount", BigDecimal.valueOf(10_000L));
        ReflectionTestUtils.setField(request, "paybackAmount", BigDecimal.valueOf(1_000L));
        String json = objectMapper.writeValueAsString(request);

        when(tradeService.requestTrade(anyLong(), any()))
                .thenThrow(new MemberNotFoundException());

        // expected
        mockMvc.perform(post("/api/v1/members/{memberId}/trades", notExistsMemberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value("NOT_FOUND"),
                        jsonPath("$.message").value("해당 유저를 찾을 수 없습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId").description("결제 요청 유저 ID")),
                        responseFields(
                                fieldWithPath("status").description("에러 상태"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("유효성 검사 오류")
                        )
                ));
    }

    @Test
    @DisplayName("거래 생성 요청 - 실패 (결제 및 페이백 금액 미입력)")
    void 거래생성_실패_금액미입력() throws Exception {
        // given
        Long memberId = 1L;
        TradeRequest request = new TradeRequest();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/v1/members/{memberId}/trades", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("잘못된 요청입니다."),
                        jsonPath("$.validation.paymentAmount").value("결제 금액을 입력해주세요."),
                        jsonPath("$.validation.paybackAmount").value("페이백 금액을 입력해주세요."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId").description("결제 요청 유저 ID")),
                        responseFields(
                                fieldWithPath("status").description("에러 상태"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation.paymentAmount").description("결제 금액 오류"),
                                fieldWithPath("validation.paybackAmount").description("페이백 금액 오류")
                        )
                ));
    }

    @Test
    @DisplayName("거래 생성 요청 - 실패 (결제 및 페이백 금액 음수)")
    void 거래생성_실패_금액음수() throws Exception {

        // given
        Long memberId = 1L;
        TradeRequest request = new TradeRequest();
        ReflectionTestUtils.setField(request, "paymentAmount", BigDecimal.valueOf(-10000L));
        ReflectionTestUtils.setField(request, "paybackAmount", BigDecimal.valueOf(-1000L));

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/v1/members/{memberId}/trades", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("잘못된 요청입니다."),
                        jsonPath("$.validation.paymentAmount").value("결제 금액을 0원 이상으로 입력해주세요."),
                        jsonPath("$.validation.paybackAmount").value("페이백 금액을 0원 이상으로 입력해주세요."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId").description("결제 요청 유저 ID")),
                        responseFields(
                                fieldWithPath("status").description("에러 상태"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation.paymentAmount").description("결제 금액 오류"),
                                fieldWithPath("validation.paybackAmount").description("페이백 금액 오류")
                        )
                ));
    }

    @Test
    @DisplayName("거래 조회 - 성공")
    void 거래조회_성공() throws Exception {

        // given
        Trade payment = Trade.builder()
                .memberId(1L)
                .paymentAmount(BigDecimal.valueOf(10000L))
                .paybackAmount(BigDecimal.valueOf(1000L))
                .build();

        ReflectionTestUtils.setField(payment, "id", 1L);

        TradeResponse response = new TradeResponse(payment);

        when(tradeService.getTrade(anyLong()))
                .thenReturn(response);

        // expected
        mockMvc.perform(get("/api/v1/trades/{tradeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.tradeId").value(1L),
                        jsonPath("$.memberId").value(1L),
                        jsonPath("$.paymentAmount").value(10000),
                        jsonPath("$.paymentStatus.description").value("결제 대기"),
                        jsonPath("$.paymentStatus.status").value("WAIT"),
                        jsonPath("$.paybackAmount").value(1000),
                        jsonPath("$.paybackStatus.description").value("페이백 대기"),
                        jsonPath("$.paybackStatus.status").value("WAIT"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("tradeId").description("조회 요청 거래 ID")
                        ),
                        responseFields(
                                fieldWithPath("tradeId").description("거래 ID"),
                                fieldWithPath("memberId").description("유저 ID"),
                                fieldWithPath("paymentAmount").description("결제 금액"),
                                fieldWithPath("paymentStatus.description").description("결제 상태 설명"),
                                fieldWithPath("paymentStatus.status").description("결제 상태"),
                                fieldWithPath("paybackAmount").description("페이백 금액"),
                                fieldWithPath("paybackStatus.description").description("페이백 상태 설명"),
                                fieldWithPath("paybackStatus.status").description("페이백 상태")
                        ))
                );
    }

    @Test
    @DisplayName("거래 조회 - 실패 (존재하지 않는 거래)")
    void 거래조회_실패_존재하지않는거래() throws Exception {

        // given
        Long notExistsTradeId = 10L;

        when(tradeService.getTrade(anyLong()))
                .thenThrow(new TradeNotFoundException());

        // expected
        mockMvc.perform(get("/api/v1/trades/{tradeId}", notExistsTradeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value("NOT_FOUND"),
                        jsonPath("$.message").value("해당 거래가 존재하지 않습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("tradeId").description("조회 요청 거래 ID")),
                        responseFields(
                                fieldWithPath("status").description("에러 상태"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("유효성 검사 오류")
                        ))
                );
    }

}