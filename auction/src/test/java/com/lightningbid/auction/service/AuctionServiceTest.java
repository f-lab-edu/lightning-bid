package com.lightningbid.auction.service;

import com.lightningbid.auction.domain.Auction;
import com.lightningbid.auction.repository.AuctionRepository;
import com.lightningbid.common.exception.ResourceNotFoundException;
import com.lightningbid.item.item.service.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @Mock // Mock 객체 생성
    private AuctionRepository auctionRepository;

    @Mock
    private ItemService itemService;

    @InjectMocks // @Mock 으로 생성한 가짜 객체를 해당 객체에 주입
    private AuctionService auctionService;

    @Test
    void createAuction() {

        // given
        Auction auction = new Auction();
        auction.setStartPrice(new BigDecimal("10000"));
        String validDuration = "P3DT2H";

        // auctionRepository.findBidUnit() 메서드가 호출 될 시 행동 지시(willReturn)
        given(auctionRepository.findBidUnit(any(BigDecimal.class))).willReturn(1000);

        // auctionRepository.save() 메서드가 어떤 Auction 객체로 호출되면, 파라미터로 받은 Auction 객체를 그대로 반환하라고 지시
        given(auctionRepository.save(any(Auction.class))).will(i -> i.getArgument(0));

        // when
        Auction createdAuction = auctionService.createAuction(auction, validDuration);

        // then
        assertThat(createdAuction).isNotNull();
        assertThat(createdAuction.getBidUnit()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(createdAuction.getAuctionStartTime()).isNotNull();
        assertThat(createdAuction.getAuctionEndTime()).isNotNull();
        assertThat(Duration.between(createdAuction.getAuctionStartTime(), createdAuction.getAuctionEndTime()))
                .isEqualTo(Duration.parse(validDuration));

        // repository의 메서드들이 정확히 호출되었는지 확인
        verify(auctionRepository).findBidUnit(auction.getStartPrice());
        // verify(auctionRepository, times(1)).findBidUnit(auction.getStartPrice()); // times(1) 은 기본값으로 생략해도 위 코드랑 동일. 1번 호출었는지 체크.
        verify(auctionRepository).save(auction);
    }

    @DisplayName("경매 생성 실패 - 유효하지 않은 입력값")
    @ParameterizedTest(name = "{index}: {2}")
    @MethodSource("provideInvalidAuctionArguments")
    void failAuctionCreation(Auction auction, String duration, String expectedErrorMessage) {

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            auctionService.createAuction(auction, duration);
        });

        assertThat(exception.getMessage()).contains(expectedErrorMessage);

        // 실패 시에는 save 메서드가 절대 호출되지 않는지 검증
        verify(auctionRepository, never()).save(any(Auction.class));
    }

    private static Stream<Arguments> provideInvalidAuctionArguments() {

        return Stream.of(
                Arguments.of(new Auction(), "3D", "유효하지 않은 기간 형식입니다."),
                Arguments.of(new Auction(), "PT23H59M59S", "최소 24시간 이상 이어야 합니다."),
                Arguments.of(new Auction(), "P14DT1S", "14일 이내여야 합니다."),
                Arguments.of(
                        Auction.builder().startPrice(BigDecimal.valueOf(20000)).instantSalePrice(BigDecimal.valueOf(10000)).build(),
                        "P3D",
                        "즉시 판매 가격은 경매 시작가보다 높아야 합니다."
                )
        );
    }

    @Test
    @DisplayName("경매 상세 조회 - 성공")
    void findAuctionByItemId() {

        // given
        Long itemId = 1L;
        Auction mockAuction = new Auction();
        given(auctionRepository.findWithItemByItemId(itemId)).willReturn(Optional.of(mockAuction));

        // when
        auctionService.findAuctionByItemId(itemId);

        // then
        verify(auctionRepository).findWithItemByItemId(itemId);
        verify(itemService).increaseViewCount(itemId);
    }

    @Test
    @DisplayName("경매 상세 조회 - 실패")
    void findAuctionByItemId_Failure_NotFound() {

        // given
        Long itemId = 99L;
        given(auctionRepository.findWithItemByItemId(itemId)).willReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            auctionService.findAuctionByItemId(itemId);
        });

        verify(itemService, never()).increaseViewCount(itemId);
    }
}