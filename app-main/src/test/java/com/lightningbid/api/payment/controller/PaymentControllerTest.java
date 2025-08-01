package com.lightningbid.api.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightningbid.payment.controller.PaymentController;
import com.lightningbid.payment.dto.request.DepositPreparationRequestDto;
import com.lightningbid.payment.dto.request.PaymentCompletionRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@WithMockUser
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc; //  Mvc 테스트를 위한 객체

    @Autowired
    private ObjectMapper objectMapper; // Java Object <-> JSON 변환을 위한 객체

    @Test
    @DisplayName("결제 준비를 한다.")
    void prepareDepositPayment() throws Exception {

        // given: 요청 준비
        DepositPreparationRequestDto requestDto = DepositPreparationRequestDto.builder().amount(30000).build();
        String requestJson = objectMapper.writeValueAsString(requestDto);
        long itemId = 12345;

        // when (API 호출)
        mockMvc.perform(post("/api/v1/{itemId}/deposits", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf())
                )
                // then (응답 검증)
                .andDo(print())
                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.orderId").exists())
                .andExpect(jsonPath("$.data.itemId").exists())
                .andExpect(jsonPath("$.data.amount").exists())

                .andExpect(jsonPath("$.data.bidder").exists())
                .andExpect(jsonPath("$.data.bidder.userId").exists())
                .andExpect(jsonPath("$.data.bidder.nickname").exists())
                .andExpect(jsonPath("$.data.bidder.profileImageUrl").exists());
    }


    @Test
    @DisplayName("결제 완료를 확인한다.")
    void confirmDepositPayment() throws Exception {

        // given: 요청 준비
        PaymentCompletionRequestDto requestDto = PaymentCompletionRequestDto.builder()
                .paymentKey("a1b2c3d4e5f6g7h8")
                .orderId("order_uuid_12345678")
                .amount(30000)
                .build();
        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when (API 호출)
        mockMvc.perform(post("/api/v1/payments/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf())
                )
                // then (응답 검증)
                .andDo(print())
                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.depositId").exists())
                .andExpect(jsonPath("$.data.orderId").exists())
                .andExpect(jsonPath("$.data.itemId").exists())
                .andExpect(jsonPath("$.data.amount").exists())
                .andExpect(jsonPath("$.data.paymentMethod").exists())
                .andExpect(jsonPath("$.data.paidAt").exists());
    }
}