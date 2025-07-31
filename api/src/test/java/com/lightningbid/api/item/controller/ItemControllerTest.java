package com.lightningbid.api.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightningbid.api.item.dto.request.ItemCreateRequestDto;
import com.lightningbid.api.item.dto.request.ItemPatchRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@WithMockUser
class ItemControllerTest {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private MockMvc mockMvc; //  Mvc 테스트를 위한 객체

    @Autowired
    private ObjectMapper objectMapper; // Java Object <-> JSON 변환을 위한 객체

    @Test
    @DisplayName("판매할 상품을 등록한다.")
    void createItem() throws Exception {

        // given: 요청 준비
        ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder()
                .title("맥북프로 M1pro 판매합니다.")
                .description("맥북 M1pro 판매합니다. 상태 최상입니다.")
                .categoryId(101L)
                .imageIds(List.of("1", "2", "3"))
                .isDirectTrade(true)
                .location("서울시 강남구")
                .startPrice(BigDecimal.valueOf(1_300_000))
                .auctionDuration("PT25H")
                .build();
        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when: API 호출
        mockMvc.perform(post("/api/v1/items") // POST 요청을 이 URL로 보낸다.
                        .contentType(MediaType.APPLICATION_JSON) // 요청의 Content-Type
                        .content(requestJson) // 요청 본문(Body)에 위에서 만든 JSON을 담는다.
                        .with(csrf()) // CSRF 토큰을 포함시켜 403 Forbidden 에러가 발생하지 않도록 한다.
                )
                // then: 응답 검증
                .andDo(print()) // 요청/응답 전체 내용을 로그로 출력한다 (디버깅에 유용)
                .andExpect(status().isCreated()) // 응답 상태 코드가 201인지 확인한다

                .andExpect(jsonPath("$.data").exists()) // data 객체가 존재하는지 확인

                .andExpect(jsonPath("$.data.imageIds").isArray())
                .andExpect(jsonPath("$.data.imageIds").exists())
                .andExpect(jsonPath("$.data.imageUrls").isArray())
                .andExpect(jsonPath("$.data.imageUrls").exists())
                .andExpect(jsonPath("$.data.title").value(requestDto.getTitle()))
                .andExpect(jsonPath("$.data.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.data.categoryId").value(requestDto.getCategoryId()))
                .andExpect(jsonPath("$.data.isDirectTrade").value(requestDto.getIsDirectTrade()))
                .andExpect(jsonPath("$.data.location").value(requestDto.getLocation()))
                .andExpect(jsonPath("$.data.startPrice").value(requestDto.getStartPrice()))
                .andExpect(jsonPath("$.data.bidUnit").exists())
                .andExpect(jsonPath("$.data.auctionStartTime").exists())
                .andExpect(jsonPath("$.data.auctionEndTime").exists())
                .andExpect(jsonPath("$.data.status").exists())
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.seller").exists())
                .andExpect(jsonPath("$.data.seller.nickname").value("판매자_닉네임"))
                .andExpect(jsonPath("$.data.seller.userId").exists());
    }

    @Test
    @DisplayName("상품 목록을 조회한다.")
    void getItems() throws Exception {

        // given: 요청 파라미터 설정
        mockMvc.perform(get("/api/v1/items") // GET 요청
                        .param("keyword", "맥북")
                        .param("lastId", "12321")
                        .param("lastCreatedAt", "2025-07-17T13:30:00")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .param("sort", "viewCount,asc")
                ) // when & then: API 요청 및응답 검증
                .andDo(print())
                .andExpect(status().isOk()) // 200 OK 상태인지 확인

                .andExpect(jsonPath("$.data.content").isArray()) // content가 배열인지 확인
                .andExpect(jsonPath("$.data.content").exists())

                .andExpect(jsonPath("$.data.content[0].itemId").exists()) // 첫 번째 아이템의 필드 검증
                .andExpect(jsonPath("$.data.content[0].title").exists())
                .andExpect(jsonPath("$.data.content[0].thumbnailUrl").exists())
                .andExpect(jsonPath("$.data.content[0].location").exists())
                .andExpect(jsonPath("$.data.content[0].price").exists())
                .andExpect(jsonPath("$.data.content[0].currentBid").value(is(nullValue())))
                .andExpect(jsonPath("$.data.content[1].currentBid").exists())
                .andExpect(jsonPath("$.data.content[0].status").exists())
                .andExpect(jsonPath("$.data.content[0].createdAt").exists())
                .andExpect(jsonPath("$.data.content[0].viewCount").exists())
                .andExpect(jsonPath("$.data.content[0].likeCount").exists())
                .andExpect(jsonPath("$.data.content[0].bidCount").exists())

                .andExpect(jsonPath("$.data.content[0].seller").exists())
                .andExpect(jsonPath("$.data.content[0].seller.userId").exists())
                .andExpect(jsonPath("$.data.content[0].seller.nickname").exists())

                .andExpect(jsonPath("$.data.pageInfo").exists()) // pageInfo 객체 존재 확인
                .andExpect(jsonPath("$.data.pageInfo.size").exists())
                .andExpect(jsonPath("$.data.pageInfo.hasNext").exists()) // 다음 페이지 존재 여부
                .andExpect(jsonPath("$.data.pageInfo.nextCursor").exists())
                .andExpect(jsonPath("$.data.pageInfo.nextCursor.id").exists())
                .andExpect(jsonPath("$.data.pageInfo.nextCursor.createdAt").exists());
    }

    @Test
    @DisplayName("상품 상세 목록을 조회한다.")
    void getItemDetail() throws Exception {

        // given: path variable 설정 후 API 호출
        Long itemId = 12345L;
        mockMvc.perform(get("/api/v1/items/{itemId}", itemId))

                // when & then: API 호출 및 응답 검증
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data").exists())

                .andExpect(jsonPath("$.data.itemId").value(itemId))
                .andExpect(jsonPath("$.data.title").exists())
                .andExpect(jsonPath("$.data.description").exists())
                .andExpect(jsonPath("$.data.categoryId").exists())
                .andExpect(jsonPath("$.data.categoryName").exists())
                .andExpect(jsonPath("$.data.imageIds").exists())
                .andExpect(jsonPath("$.data.imageUrls").exists())
                .andExpect(jsonPath("$.data.status").exists())
                .andExpect(jsonPath("$.data.isDirectTrade").exists())
                .andExpect(jsonPath("$.data.location").exists())
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.viewCount").exists())
                .andExpect(jsonPath("$.data.likeCount").exists())
                .andExpect(jsonPath("$.data.chatCount").exists())
                .andExpect(jsonPath("$.data.isLiked").exists())
                .andExpect(jsonPath("$.data.isDepositPaid").exists())

                .andExpect(jsonPath("$.data.seller").exists())
                .andExpect(jsonPath("$.data.seller.userId").exists())
                .andExpect(jsonPath("$.data.seller.nickname").exists())
                .andExpect(jsonPath("$.data.seller.profileImageUrl").exists())

                .andExpect(jsonPath("$.data.auction").exists())
                .andExpect(jsonPath("$.data.auction.startPrice").exists())
                .andExpect(jsonPath("$.data.auction.currentBid").exists())
                .andExpect(jsonPath("$.data.auction.bidUnit").exists())
                .andExpect(jsonPath("$.data.auction.bidCount").exists())
                .andExpect(jsonPath("$.data.auction.auctionEndTime").exists());
    }

    @Test
    @DisplayName("상품 정보를 수정 합니다.")
    void patchItem() throws Exception {

        // given: 요청 준비
        ItemPatchRequestDto requestDto = ItemPatchRequestDto.builder()
                .title("게이밍 마우스 (모든 정보 수정)")
                .description("새로운 설명입니다. 직거래 장소는 강남역입니다.")
                .categoryId(201L)
                .imageIds(List.of("1", "4"))
                .isDirectTrade(true)
                .location("서울시 강남구")
                .startPrice(BigDecimal.valueOf(25000))
                .auctionEndTime(LocalDateTime.of(2025, 8, 22, 23, 0, 0))
                .build();
        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when: API 호출
        Long itemId = 12345L;
        mockMvc.perform(patch("/api/v1/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf())
                )
                // then: 응답 검증
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data").exists())

                .andExpect(jsonPath("$.data.itemId").value(itemId))
                .andExpect(jsonPath("$.data.title").value(requestDto.getTitle()))
                .andExpect(jsonPath("$.data.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.data.categoryId").value(requestDto.getCategoryId()))
                .andExpect(jsonPath("$.data.categoryName").exists())
                .andExpect(jsonPath("$.data.imageUrls").exists())
                .andExpect(jsonPath("$.data.status").exists())
                .andExpect(jsonPath("$.data.isDirectTrade").value(requestDto.getIsDirectTrade()))
                .andExpect(jsonPath("$.data.location").value(requestDto.getLocation()))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.viewCount").exists())
                .andExpect(jsonPath("$.data.likeCount").exists())
                .andExpect(jsonPath("$.data.chatCount").exists())
                .andExpect(jsonPath("$.data.isLiked").exists())
                .andExpect(jsonPath("$.data.isDepositPaid").exists())

                .andExpect(jsonPath("$.data.seller").exists())
                .andExpect(jsonPath("$.data.seller.userId").exists())
                .andExpect(jsonPath("$.data.seller.nickname").exists())
                .andExpect(jsonPath("$.data.seller.profileImageUrl").exists())

                .andExpect(jsonPath("$.data.auction").exists())
                .andExpect(jsonPath("$.data.auction.startPrice").value(requestDto.getStartPrice()))
                .andExpect(jsonPath("$.data.auction.currentBid").exists())
                .andExpect(jsonPath("$.data.auction.bidUnit").exists())
                .andExpect(jsonPath("$.data.auction.auctionEndTime").value(requestDto.getAuctionEndTime().format(formatter)));
    }

    @Test
    @DisplayName("상품을 삭제 합니다.")
    void deleteItem() throws Exception {

        Long itemId = 12345L;
        mockMvc.perform(delete("/api/v1/items/{itemId}", itemId)
                        .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}