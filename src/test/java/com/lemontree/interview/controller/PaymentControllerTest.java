package com.lemontree.interview.controller;

import com.lemontree.interview.config.AbstractRestDocsTest;
import com.lemontree.interview.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
    @DisplayName("걸제 처리 - 성공")
    void 결제처리_성공() throws Exception {

        // given
        Long tradeId = 1L;

        // expected
        mockMvc.perform(post("/api/v1/trades/{tradeId}/payments", tradeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("결제 취소 - 성공")
    void 결제취소_성공() throws Exception {

        // given
        Long tradeId = 1L;

        // expected
        mockMvc.perform(delete("/api/v1/trades/{tradeId}/payments", tradeId))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("tradeId").description("취소 요청 거래 ID")
                        )
                ));
    }
}