package com.lightningbid.api.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightningbid.item.web.dto.request.ItemCreateRequestDto;
import com.lightningbid.item.web.dto.request.ItemPatchRequestDto;
import com.lightningbid.item.web.dto.response.AuctionDto;
import com.lightningbid.item.web.dto.response.ItemCreateResponseDto;
import com.lightningbid.item.web.dto.response.ItemResponseDto;
import com.lightningbid.user.web.dto.response.UserResponseDto;
import com.lightningbid.common.dto.CommonResponseDto;
import com.lightningbid.item.domain.enums.ItemStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
class ItemControllerTest {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @LocalServerPort
    private int port;

    private String baseUrl;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.baseUrl = "http://localhost:" + port + "/api/v1/items";
    }

    @Test
    @DisplayName("판매할 상품을 등록한다.")
    void createItem() throws Exception {

        ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder()
                .title("맥북프로 M1pro 판매합니다.")
                .description("맥북 M1pro 판매합니다. 상태 최상입니다.")
                .categoryId(1L)
                .imageIds(List.of("1", "2", "3"))
                .isDirectTrade(true)
                .location("서울시 강남구")
                .startPrice(BigDecimal.valueOf(1_300_000))
                .auctionDuration("PT25H")
                .build();
        String requestJson = objectMapper.writeValueAsString(requestDto);

        System.out.println("requestJson = " + requestJson);
        
        
        // HttpHeaders 생성 및 Content-Type 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 생성: 요청 본문(requestJson)과 헤더를 포함
        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

        ResponseEntity<CommonResponseDto<ItemCreateResponseDto>> responseEntity = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<CommonResponseDto<ItemCreateResponseDto>>() {}
        );

        // --- 응답 JSON 출력  ---
        log.info("--- API Response JSON ---");
        log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseEntity.getBody()));

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();

        CommonResponseDto<ItemCreateResponseDto> commonResponse = responseEntity.getBody();
        assertThat(commonResponse.getStatus()).isEqualTo(201);
        assertThat(commonResponse.getMessage()).isNotNull();
        assertThat(commonResponse.getData()).isNotNull();

        ItemCreateResponseDto itemCreateResponse = commonResponse.getData();
        assertThat(itemCreateResponse).isNotNull();

        assertThat(itemCreateResponse.getItemId()).isNotNull();
        assertThat(itemCreateResponse.getTitle()).isNotBlank();
        assertThat(itemCreateResponse.getDescription()).isNotNull();
        assertThat(itemCreateResponse.getCategoryId()).isNotNull();
        assertThat(itemCreateResponse.getCategoryName()).isNotNull();
        assertThat(itemCreateResponse.getImageIds()).isNotEmpty();
        assertThat(itemCreateResponse.getImageUrls()).isNotEmpty();
        assertThat(itemCreateResponse.getStatus()).isEqualTo(ItemStatus.ACTIVE.getCode());
        assertThat(itemCreateResponse.getIsDirectTrade()).isNotNull();
        assertThat(itemCreateResponse.getLocation()).isNotNull();
        assertThat(itemCreateResponse.getStartPrice()).isNotNull();
        assertThat(itemCreateResponse.getBidUnit()).isNotNull();

        UserResponseDto seller = itemCreateResponse.getSeller();
        assertThat(seller).isNotNull();
        assertThat(seller.getUserId()).isNotNull();
        assertThat(seller.getNickname()).isNotBlank();
        assertThat(seller.getProfileImageUrl()).isNotNull();
    }

    @Test
    @DisplayName("상품 상세 목록을 조회한다.")
    void getItems() throws Exception {

        Long itemId = 1L; // 예시 아이템 ID
        String url = baseUrl + "/" +itemId;

        ResponseEntity<CommonResponseDto<ItemResponseDto>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<CommonResponseDto<ItemResponseDto>>() {}
        );

        // --- 응답 JSON 출력  ---
        log.info("--- API Response JSON ---");
        log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseEntity.getBody()));

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        CommonResponseDto<ItemResponseDto> commonResponse = responseEntity.getBody();
        assertThat(commonResponse.getStatus()).isEqualTo(200);
        assertThat(commonResponse.getMessage()).isNotNull();
        assertThat(commonResponse.getData()).isNotNull();

        ItemResponseDto itemDetail = commonResponse.getData();
        assertThat(itemDetail).isNotNull();

        assertThat(itemDetail.getItemId()).isEqualTo(itemId);
        assertThat(itemDetail.getTitle()).isNotNull();
        assertThat(itemDetail.getDescription()).isNotBlank();
        assertThat(itemDetail.getCategoryId()).isNotNull();
        assertThat(itemDetail.getCategoryName()).isNotNull();
        assertThat(itemDetail.getImageIds()).isNotEmpty();
        assertThat(itemDetail.getImageUrls()).isNotEmpty();
        assertThat(itemDetail.getStatus()).isEqualTo(ItemStatus.ACTIVE.getCode());
        assertThat(itemDetail.getIsDirectTrade()).isNotNull();
        assertThat(itemDetail.getLocation()).isNotNull();
        assertThat(itemDetail.getViewCount()).isNotNull();
        assertThat(itemDetail.getLikeCount()).isNotNull();
        assertThat(itemDetail.getChatCount()).isNotNull();
        assertThat(itemDetail.getIsLiked()).isNotNull();
        assertThat(itemDetail.getIsDepositPaid()).isNotNull();

        UserResponseDto seller = itemDetail.getSeller();
        assertThat(seller).isNotNull();
        assertThat(seller.getUserId()).isNotNull();
        assertThat(seller.getNickname()).isNotBlank();
        assertThat(seller.getProfileImageUrl()).isNotNull();

        AuctionDto auction = itemDetail.getAuction();
        assertThat(auction).isNotNull();
        assertThat(auction.getStartPrice()).isNotNull();
        assertThat(auction.getCurrentBid()).isNotNull();
        assertThat(auction.getBidUnit()).isNotNull();
        assertThat(auction.getBidCount()).isNotNull();
        assertThat(auction.getAuctionEndTime().format(formatter)).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("상품 정보를 수정 합니다.")
    void patchItem() throws Exception {

        Long itemId = 12345L; // 예시 아이템 ID
        String url = baseUrl + "/" +itemId;

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

        // HttpHeaders 생성 및 Content-Type 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 생성: 요청 본문(requestJson)과 헤더를 포함
        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

        ResponseEntity<CommonResponseDto<ItemResponseDto>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                requestEntity,
                new ParameterizedTypeReference<CommonResponseDto<ItemResponseDto>>() {}
        );

        // --- 응답 JSON 출력  ---
        log.info("--- API Response JSON ---");
        log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseEntity.getBody()));

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        CommonResponseDto<ItemResponseDto> commonResponse = responseEntity.getBody();
        assertThat(commonResponse.getStatus()).isEqualTo(200);
        assertThat(commonResponse.getMessage()).isNotNull();
        assertThat(commonResponse.getData()).isNotNull();

        ItemResponseDto itemDetail = commonResponse.getData();
        assertThat(itemDetail).isNotNull();

        assertThat(itemDetail.getItemId()).isEqualTo(itemId);
        assertThat(itemDetail.getTitle()).isNotNull();
        assertThat(itemDetail.getDescription()).isNotBlank();
        assertThat(itemDetail.getCategoryId()).isNotNull();
        assertThat(itemDetail.getCategoryName()).isNotNull();
        assertThat(itemDetail.getImageIds()).isNotEmpty();
        assertThat(itemDetail.getImageUrls()).isNotEmpty();
        assertThat(itemDetail.getStatus()).isEqualTo(ItemStatus.ACTIVE.getCode());
        assertThat(itemDetail.getIsDirectTrade()).isNotNull();
        assertThat(itemDetail.getLocation()).isNotNull();
        assertThat(itemDetail.getViewCount()).isNotNull();
        assertThat(itemDetail.getLikeCount()).isNotNull();
        assertThat(itemDetail.getChatCount()).isNotNull();
        assertThat(itemDetail.getIsLiked()).isNotNull();
        assertThat(itemDetail.getIsDepositPaid()).isNotNull();

        UserResponseDto seller = itemDetail.getSeller();
        assertThat(seller).isNotNull();
        assertThat(seller.getUserId()).isNotNull();
        assertThat(seller.getNickname()).isNotBlank();
        assertThat(seller.getProfileImageUrl()).isNotNull();

        AuctionDto auction = itemDetail.getAuction();
        assertThat(auction).isNotNull();
        assertThat(auction.getStartPrice()).isNotNull();
        assertThat(auction.getCurrentBid()).isNotNull();
        assertThat(auction.getBidUnit()).isNotNull();
        assertThat(auction.getBidCount()).isNotNull();
        assertThat(auction.getAuctionEndTime().format(formatter)).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("상품을 삭제 합니다.")
    void deleteItem() {

        long itemId = 12345;
        String url = baseUrl + "/" +itemId;

        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(responseEntity.getBody()).isNull();
    }

}