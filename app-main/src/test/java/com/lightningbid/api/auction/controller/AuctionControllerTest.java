package com.lightningbid.api.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightningbid.auction.web.controller.AuctionController;
import com.lightningbid.auction.web.dto.request.BidCreateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionController.class)
@WithMockUser
class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc; //  Mvc 테스트를 위한 객체

    @Autowired
    private ObjectMapper objectMapper; // Java Object <-> JSON 변환을 위한 객체

    @Test
    @DisplayName("입찰 등록")
    void createBid() throws Exception {

        // given: 요청 준비
        BidCreateRequestDto requestDto = BidCreateRequestDto.builder().price(BigDecimal.valueOf(25000)).build();
        String requestJson = objectMapper.writeValueAsString(requestDto);
        long itemId = 12345;

        // when: API 호출
        mockMvc.perform(post("/api/v1/items/{itemId}/bids", itemId) // POST 요청을 이 URL로 보낸다.
                        .contentType(MediaType.APPLICATION_JSON) // 요청의 Content-Type
                        .content(requestJson) // 요청 본문(Body)에 위에서 만든 JSON을 담는다.
                        .with(csrf()) // CSRF 토큰을 포함시켜 403 Forbidden 에러가 발생하지 않도록 한다.
                )
                .andDo(print())

                // then: 응답 검증
                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.bidId").exists())
                .andExpect(jsonPath("$.data.itemId").value(itemId))
                .andExpect(jsonPath("$.data.currentBid").value(requestDto.getPrice()))
                .andExpect(jsonPath("$.data.bidCount").exists());
    }

    @Test
    @DisplayName("입찰 목록을 조회한다.")
    void getBidsByItemId() throws Exception {

        // given: 요청 준비
        long itemId = 12345;

        // when: API 호출
        mockMvc.perform(get("/api/v1/items/{itemId}/bids", itemId))
                .andDo(print())

                // then: 응답 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.bids").isArray())
                .andExpect(jsonPath("$.data.bids[0].bidId").exists())
                .andExpect(jsonPath("$.data.bids[0].price").exists())
                .andExpect(jsonPath("$.data.bids[0].bidAt").exists())

                .andExpect(jsonPath("$.data.bids[0].bidder").exists())
                .andExpect(jsonPath("$.data.bids[0].bidder.userId").exists())
                .andExpect(jsonPath("$.data.bids[0].bidder.nickname").exists())
                .andExpect(jsonPath("$.data.bids[0].bidder.profileImageUrl").exists());
    }

    @Test
    @DisplayName("입찰 취소한다.")
    void cancelBid() throws Exception {

        // given: 요청 준비
        long itemId = 12345;
        long bidId = 1;

        // when: API 호출
        mockMvc.perform(delete("/api/v1/items/{itemId}/bids/{bidId}", itemId, bidId).with(csrf()))
                .andDo(print())

                // then: 응답 검증
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("거래 확정")
    void confirmDeal() throws Exception {

        // given: 요청 준비
        long itemId = 12345;

        // when: API 호출
        mockMvc.perform(post("/api/v1/items/{itemId}/deals/confirm", itemId).with(csrf()))
                .andDo(print())

                // then: 응답 검증
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data").exists())

                .andExpect(jsonPath("$.data.itemId").value(itemId))
                .andExpect(jsonPath("$.data.status").exists())
                .andExpect(jsonPath("$.data.sellerConfirmationStatus").exists())
                .andExpect(jsonPath("$.data.buyerConfirmationStatus").exists());
    }

    @Test
    @DisplayName("거래 불발 신고")
    void reportDealCancellation() throws Exception {

        // given: 요청 준비
        long itemId = 12345;

        // when: API 호출
        mockMvc.perform(patch("/api/v1/items/{itemId}/deals/cancel", itemId).with(csrf()))
                .andDo(print())

                // then: 응답 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.itemId").value(itemId))
                .andExpect(jsonPath("$.data.status").exists());
    }

    @Test
    @DisplayName("판매자가 경매 취소한다.")
    void stopAuction() throws Exception {

        // given: 요청 준비
        long itemId = 12345;

        // when: API 호출
        mockMvc.perform(patch("/api/v1/items/{itemId}/cancel", itemId).with(csrf()))
                .andDo(print())

                // then: 응답 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.itemId").value(itemId))
                .andExpect(jsonPath("$.data.status").exists());
    }
}