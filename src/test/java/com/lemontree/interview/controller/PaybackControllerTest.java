package com.lemontree.interview.controller;

import com.lemontree.interview.config.AbstractRestDocsTest;
import com.lemontree.interview.service.PaybackService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 페이백 컨트롤러 테스트입니다.
 *
 * @author 정승조
 * @version 2024. 08. 12.
 */
@AutoConfigureRestDocs
@WebMvcTest(PaybackController.class)
class PaybackControllerTest extends AbstractRestDocsTest {

    @MockBean
    PaybackService paybackService;

    @Test
    @DisplayName("페이백 요청 - 성공")
    void 페이백_요청_성공() throws Exception {

        mockMvc.perform(post("/api/v1/payments/{paymentId}/payback", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("paymentId").description("결제 ID")
                        ))
                );

        verify(paybackService).processPayback(1L);
    }

    @Test
    @DisplayName("페이백 취소 - 성공")
    void 페이백_취소_성공() throws Exception {

        mockMvc.perform(delete("/api/v1/payments/{paymentId}/payback", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("paymentId").description("결제 ID")
                        )
                ));


        verify(paybackService).cancelPayback(1L);
    }
}