package com.lightningbid.item.service;

import com.lightningbid.auction.domain.model.Auction;
import com.lightningbid.auction.service.AuctionService;
import com.lightningbid.auction.domain.exception.AuctionNotFoundException;
import com.lightningbid.item.domain.enums.ItemStatus;
import com.lightningbid.item.domain.model.Item;
import com.lightningbid.item.domain.repository.ItemRepository;
import com.lightningbid.item.web.dto.request.ItemCreateRequestDto;
import com.lightningbid.item.web.dto.response.AuctionDto;
import com.lightningbid.item.web.dto.response.ItemCreateResponseDto;
import com.lightningbid.item.web.dto.response.ItemResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private AuctionService auctionService;

    @Mock
    private ItemLikeService itemLikeService;

    @InjectMocks
    private ItemService itemService;

    @Test
    @DisplayName("판매 상품 등록 - 성공")
    void createItemWithAuction_Success() {

        // given
        // 테스트 데이터 변수화
        String expectedTitle = "게시글 제목";
        Long expectedCategoryId = 1L;
        String expectedCategoryName = "카테고리 명";
        BigDecimal expectedStartPrice = BigDecimal.valueOf(10000);
        BigDecimal expectedInstantSalePrice = BigDecimal.valueOf(15000);
        String durationString = "PT30H";
        LocalDateTime now = LocalDateTime.now();

        given(categoryService.findCategoryNameById(expectedCategoryId)).willReturn(expectedCategoryName);

        ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder()
                .title(expectedTitle)
                .categoryId(expectedCategoryId)
                .isDirectTrade(true)
                .instantSalePrice(expectedStartPrice)
                .auctionDuration(durationString)
                .startPrice(expectedStartPrice)
                .instantSalePrice(expectedInstantSalePrice)
                .build();

        // given/willReturn에 사용될 가짜 반환 객체
        Auction savedAuction = Auction.builder()
                .id(1L)
                .startPrice(expectedStartPrice)
                .auctionStartTime(now)
                .auctionEndTime(now.plus(Duration.parse(durationString)))
                .bidUnit(BigDecimal.valueOf(1000))
                .item(Item.builder()
                        .id(1L)
                        .title(expectedTitle)
                        .categoryId(expectedCategoryId)
                        .categoryName(expectedCategoryName)
                        .status(ItemStatus.ACTIVE)
                        .isDirectTrade(true)
                        .build())
                .build();
        given(auctionService.createAuction(any(Auction.class))).willReturn(savedAuction);

        // when
        ItemCreateResponseDto responseDto = itemService.createItemWithAuction(requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getItemId()).isEqualTo(savedAuction.getItem().getId());
        assertThat(responseDto.getTitle()).isEqualTo(savedAuction.getItem().getTitle());
        assertThat(responseDto.getCategoryName()).isEqualTo(savedAuction.getItem().getCategoryName());
        assertThat(responseDto.getStatus()).isEqualTo(ItemStatus.ACTIVE.getCode());
        assertThat(responseDto.getStartPrice()).isEqualTo(savedAuction.getStartPrice());
        assertThat(responseDto.getBidUnit()).isEqualTo(savedAuction.getBidUnit());
        assertThat(responseDto.getAuctionEndTime()).isEqualTo(savedAuction.getAuctionEndTime());

        ArgumentCaptor<Auction> auctionCaptor = ArgumentCaptor.forClass(Auction.class);
        verify(auctionService).createAuction(auctionCaptor.capture());
        // service 로직에서 생성한 Auction 객체
        // Auction resultAuction = auctionService.createAuction(auction);
        // 위 서비스 코드에서 auctionService.createAuction(auction); 의 인자값인 auction과 capturedAuction은 같다.
        // resultAuction (서비스) == savedAuction (테스트)
        // auction (서비스) == capturedAuction (테스트)
        Auction capturedAuction = auctionCaptor.getValue();
        assertThat(capturedAuction.getStartPrice()).isEqualTo(requestDto.getStartPrice());
        assertThat(capturedAuction.getItem().getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(capturedAuction.getItem().getCategoryName()).isEqualTo(expectedCategoryName);

        verify(categoryService).findCategoryNameById(requestDto.getCategoryId());
    }

    @Test
    @DisplayName("판매 상품 등록 - 실패")
    void createItemWithAuction_Fail() {
        String durationString = "30";
        ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder().auctionDuration(durationString).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> itemService.createItemWithAuction(requestDto));

        assertThat(exception.getMessage()).contains("유효하지 않은 기간 형식입니다.");

        verify(categoryService, never()).findCategoryNameById(any(Long.class));
        verify(auctionService, never()).createAuction(any(Auction.class));
    }

    @Test
    @DisplayName("상품 상세 조회 - 성공")
    void findItemWithAuctionByItemId_Success() {

        Long itemId = 1L;
        String expectedCategoryName = "카테고리 명";
        int viewCount = 10;
        boolean isLike = false;

        LocalDateTime auctionStartTime = LocalDateTime.now();
        Auction findAuction = Auction.builder()
                .startPrice(BigDecimal.valueOf(10000))
                .currentBid(BigDecimal.valueOf(14000))
                .bidUnit(BigDecimal.valueOf(1000))
                .bidCount(4)
                .auctionStartTime(auctionStartTime)
                .auctionEndTime(auctionStartTime.plusDays(3))
                .item(Item.builder()
                        .id(itemId)
                        .title("제목")
                        .description("설명")
                        .categoryId(1L)
                        .categoryName(expectedCategoryName)
                        .status(ItemStatus.ACTIVE)
                        .location("판매 지역")
                        .viewCount(viewCount)
                        .likeCount(1)
                        .chatCount(2)
                        .isDirectTrade(true)
                        .build())
                .build();
        // given
        given(auctionService.findAuctionByItemId(any(Long.class))).willReturn(findAuction);
        given(itemLikeService.checkUserLikeStatus(any(Long.class), any(Long.class))).willReturn(isLike);
        given(categoryService.findCategoryNameById(any(Long.class))).willReturn(expectedCategoryName);

        // when
        ItemResponseDto responseDto = itemService.findItemWithAuctionByItemId(itemId);

        // then
        assertThat(responseDto).isNotNull();

        Item findItem = findAuction.getItem();
        assertThat(responseDto.getItemId()).isEqualTo(findItem.getId());
        assertThat(responseDto.getTitle()).isEqualTo(findItem.getTitle());
        assertThat(responseDto.getDescription()).isEqualTo(findItem.getDescription());
        assertThat(responseDto.getCategoryId()).isEqualTo(findItem.getCategoryId());
        assertThat(responseDto.getStatus()).isEqualTo(findItem.getStatus().getCode());
        assertThat(responseDto.getIsDirectTrade()).isEqualTo(findItem.getIsDirectTrade());
        assertThat(responseDto.getLocation()).isEqualTo(findItem.getLocation());
        assertThat(responseDto.getViewCount()).isEqualTo(findItem.getViewCount() + 1);
        assertThat(responseDto.getLikeCount()).isEqualTo(findItem.getLikeCount());
        assertThat(responseDto.getChatCount()).isEqualTo(findItem.getChatCount());

        AuctionDto auction = responseDto.getAuction();
        assertThat(auction.getStartPrice()).isEqualTo(findAuction.getStartPrice());
        assertThat(auction.getCurrentBid()).isEqualTo(findAuction.getCurrentBid());
        assertThat(auction.getBidUnit()).isEqualTo(findAuction.getBidUnit());
        assertThat(auction.getBidCount()).isEqualTo(findAuction.getBidCount());
        assertThat(auction.getAuctionStartTime()).isEqualTo(findAuction.getAuctionStartTime());
        assertThat(auction.getAuctionEndTime()).isEqualTo(findAuction.getAuctionEndTime());
    }

    @Test
    @DisplayName("상품 상세 조회 - 실패")
    void findItemWithAuctionByItemId_Fail() {

        // given: 존재하지 않는 ID와, 해당 ID로 조회 시 예외가 발생한다고 가정
        Long invalidItemId = 999L;
        String errorMessage = "조회된 상품이 없습니다.";
        given(auctionService.findAuctionByItemId(invalidItemId))
                .willThrow(new AuctionNotFoundException(errorMessage));

        // when
        AuctionNotFoundException exception = assertThrows(AuctionNotFoundException.class, () -> {
            itemService.findItemWithAuctionByItemId(invalidItemId);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo(errorMessage);

        verify(itemLikeService, never()).checkUserLikeStatus(anyLong(), anyLong());
        verify(categoryService, never()).findCategoryNameById(anyLong());
    }

}