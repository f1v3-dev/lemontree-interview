package com.lemontree.interview.controller;

import com.lemontree.interview.config.AbstractRestDocsTest;
import com.lemontree.interview.entity.Payment;
import com.lemontree.interview.exception.member.MemberNotFoundException;
import com.lemontree.interview.request.PaymentRequest;
import com.lemontree.interview.response.PaymentResponse;
import com.lemontree.interview.service.PaymentService;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 결제 컨트롤러 테스트입니다.
 *
 * @author 정승조
 * @version 2024. 08. 12.
 */
@WebMvcTest(PaymentController.class)
class PaymentControllerTest extends AbstractRestDocsTest {

    @MockBean
    PaymentService paymentService;

    @Test
    @DisplayName("결제건 생성 요청 - 성공")
    void 결제건생성_요청_성공() throws Exception {

        // given
        Long memberId = 1L;

        PaymentRequest request = new PaymentRequest();
        ReflectionTestUtils.setField(request, "paymentAmount", BigDecimal.valueOf(10_000L));
        ReflectionTestUtils.setField(request, "paybackAmount", BigDecimal.valueOf(1_000L));
        String json = objectMapper.writeValueAsString(request);

        when(paymentService.createPayment(anyLong(), any())).thenReturn(1L);

        // expected
        mockMvc.perform(post("/api/v1/members/{memberId}/payments", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.paymentId").value(1L))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId").description("결제 요청 유저 ID")
                        ),
                        requestFields(
                                fieldWithPath("paymentAmount").description("결제 금액"),
                                fieldWithPath("paybackAmount").description("페이백 금액")
                        ),
                        responseFields(
                                fieldWithPath("paymentId").description("완료된 결제 ID")
                        )
                ));
    }

    @Test
    @DisplayName("결제건 생성 요청 - 실패 (존재하지 않는 유저)")
    void 결제건생성_요청_실패_존재하지않는유저() throws Exception {

        // given
        Long notExistsMemberId = 10L;

        PaymentRequest request = new PaymentRequest();
        ReflectionTestUtils.setField(request, "paymentAmount", BigDecimal.valueOf(10_000L));
        ReflectionTestUtils.setField(request, "paybackAmount", BigDecimal.valueOf(1_000L));
        String json = objectMapper.writeValueAsString(request);

        when(paymentService.createPayment(anyLong(), any()))
                .thenThrow(new MemberNotFoundException());

        // expected
        mockMvc.perform(post("/api/v1/members/{memberId}/payments", notExistsMemberId)
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
    @DisplayName("결제 요청 - 실패 (결제 금액 미입력)")
    void 결제_요청_실패_결제금액_미입력() throws Exception {

        // given
        Long memberId = 1L;
        PaymentRequest request = new PaymentRequest();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/v1/members/{memberId}/payments", memberId)
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
    @DisplayName("결제 요청 - 실패 (결제 금액 음수)")
    void 결제_요청_실패_결제금액_음수() throws Exception {

        // given
        Long memberId = 1L;
        PaymentRequest request = new PaymentRequest();
        ReflectionTestUtils.setField(request, "paymentAmount", BigDecimal.valueOf(-10000L));
        ReflectionTestUtils.setField(request, "paybackAmount", BigDecimal.valueOf(-1000L));

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/v1/members/{memberId}/payments", memberId)
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
    @DisplayName("결제 취소 - 성공")
    void 결제_취소_성공() throws Exception {

        // given
        Long memberId = 1L;
        Long paymentId = 1L;

        // expected
        mockMvc.perform(delete("/api/v1/members/{memberId}/payments/{paymentId}/cancel", memberId, paymentId))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId").description("취소 요청 유저 ID"),
                                parameterWithName("paymentId").description("취소 요청 결제 ID")
                        )
                ));
    }

    @Test
    @DisplayName("결제 조회 - 성공")
    void 결제_조회_성공() throws Exception {

        // given
        Payment payment = Payment.builder()
                .memberId(1L)
                .paymentAmount(BigDecimal.valueOf(10000L))
                .paybackAmount(BigDecimal.valueOf(1000L))
                .build();

        ReflectionTestUtils.setField(payment, "id", 1L);

        PaymentResponse response = new PaymentResponse(payment);

        when(paymentService.getPayment(anyLong()))
                .thenReturn(response);

        // expected
        mockMvc.perform(get("/api/v1/payments/{paymentId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.paymentId").value(1L),
                        jsonPath("$.memberId").value(1L),
                        jsonPath("$.paymentAmount").value(10000),
                        jsonPath("$.paymentStatus.description").value("결제 대기"),
                        jsonPath("$.paymentStatus.status").value("WAIT"),
                        jsonPath("$.paybackAmount").value(1000),
                        jsonPath("$.paybackStatus.description").value("페이백 대기"),
                        jsonPath("$.paybackStatus.status").value("WAIT"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("paymentId").description("조회 요청 결제 ID")
                        ),
                        responseFields(
                                fieldWithPath("paymentId").description("결제 ID"),
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
}