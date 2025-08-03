package com.lightningbid.auction.service;

import static org.junit.jupiter.api.Assertions.*;

import com.lightningbid.auction.domain.model.Auction;
import com.lightningbid.auction.domain.repository.AuctionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @Mock // Mock 객체 생성
    private AuctionRepository auctionRepository;

    @Mock
    private BidUnitService bidUnitService;

    @InjectMocks // @Mock 으로 생성한 가짜 객체를 해당 객체에 주입
    private AuctionService auctionService;

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("provideValidAuctionArguments")
    @DisplayName("경매 생성 성공")
    void createAuction_Success(String description, Auction auction, LocalDateTime now) {

        // given
        Auction savedAuction = Auction.builder()
                .id(1L)
                .startPrice(BigDecimal.valueOf(10000))
                .auctionStartTime(now)
                .auctionEndTime(now.plusDays(3))
                .bidUnit(BigDecimal.valueOf(1000))
                .build();

        // auctionRepository.findBidUnit() 메서드가 호출 될 시 행동 지시(willReturn)
        given(bidUnitService.getBidUnit(any(BigDecimal.class))).willReturn(BigDecimal.valueOf(1000));

        // auctionRepository.save() 메서드가 어떤 Auction 객체로 호출되면, 파라미터로 받은 Auction 객체를 그대로 반환하라고 지시
        given(auctionRepository.save(any(Auction.class))).willReturn(savedAuction);

        // when
        Auction createdAuction = auctionService.createAuction(auction);

        // then
        assertThat(createdAuction).isNotNull();
        assertThat(createdAuction.getId()).isEqualTo(1L);
        assertThat(createdAuction.getBidUnit()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(createdAuction.getAuctionStartTime()).isNotNull();
        assertThat(createdAuction.getAuctionEndTime()).isNotNull();

        // repository의 메서드들이 정확히 호출되었는지 확인
        verify(bidUnitService).getBidUnit(auction.getStartPrice());
        // verify(auctionRepository, times(1)).findBidUnit(auction.getStartPrice()); // times(1) 은 기본값으로 생략해도 위 코드랑 동일. 1번 호출었는지 체크.
        verify(auctionRepository).save(auction);
    }

    private static Stream<Arguments> provideValidAuctionArguments() {
        LocalDateTime auctionStartTime = LocalDateTime.now();
        LocalDateTime auctionEndTime = auctionStartTime.plusDays(3);

        return Stream.of(
                Arguments.of(
                        "경매 종료일은 14일 이내여야 합니다.",
                        Auction.builder()
                                .auctionStartTime(auctionStartTime)
                                .auctionEndTime(auctionStartTime.plus(Duration.parse("P13DT23H59M59S"))) // 13일 23시간 59분 59초
                                .startPrice(BigDecimal.valueOf(10000))
                                .build(),
                        auctionStartTime
                ), // 즉시판매가 null
                Arguments.of(
                        "경매 기간은 최소 24시간 이상 이어야 합니다.",
                        Auction.builder()
                                .auctionStartTime(auctionStartTime)
                                .auctionEndTime(auctionStartTime.plus(Duration.parse("PT24H"))) // 24시간
                                .startPrice(BigDecimal.valueOf(10000))
                                .build(),
                        auctionStartTime
                ), // 정상
                Arguments.of(
                        "즉시 판매 가격은 경매 시작가보다 높아야 합니다.",
                        Auction.builder()
                                .auctionStartTime(auctionStartTime)
                                .auctionEndTime(auctionEndTime)
                                .startPrice(BigDecimal.valueOf(10000))
                                .instantSalePrice(BigDecimal.valueOf(11000))
                                .build(),
                        auctionStartTime
                )  // 경계값
        );
    }

    @ParameterizedTest(name = "{index}: {1}")
    @MethodSource("provideInvalidAuctionArguments")
    @DisplayName("경매 생성 실패 - 유효하지 않은 입력값")
    void createAuction_Fail(Auction auction, String expectedErrorMessage) {

        // given
        given(bidUnitService.getBidUnit(any(BigDecimal.class))).willReturn(BigDecimal.valueOf(1000));

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            auctionService.createAuction(auction);
        });

        assertThat(exception.getMessage()).contains(expectedErrorMessage);

        // 실패 시에는 save 메서드가 절대 호출되지 않는지 검증
        verify(bidUnitService).getBidUnit(auction.getStartPrice());
        verify(auctionRepository, never()).save(any(Auction.class));
    }

    private static Stream<Arguments> provideInvalidAuctionArguments() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(Auction.builder()
                                .auctionStartTime(now)
                                .auctionEndTime(now.plusHours(12))
                                .startPrice(BigDecimal.valueOf(20000))
                                .build(),
                        "최소 24시간 이상 이어야 합니다."),
                Arguments.of(Auction.builder()
                                .auctionStartTime(now)
                                .auctionEndTime(now.plusDays(15))
                                .startPrice(BigDecimal.valueOf(20000))
                                .build(),
                        "14일 이내여야 합니다."),
                Arguments.of(Auction.builder()
                                .auctionStartTime(now)
                                .auctionEndTime(now.plusDays(10))
                                .startPrice(BigDecimal.valueOf(20000))
                                .instantSalePrice(BigDecimal.valueOf(10000))
                                .build(),
                        "즉시 판매 가격은 경매 시작가보다 높아야 합니다."
                ),
                Arguments.of(Auction.builder()
                                .auctionStartTime(now)
                                .auctionEndTime(now.plusDays(10))
                                .startPrice(BigDecimal.valueOf(20000))
                                .instantSalePrice(BigDecimal.valueOf(20001))
                                .build(),
                        "즉시 판매 가격은 입찰 단위보다 높아야 합니다."
                )
        );
    }

//    @Test
//    @DisplayName("경매 상세 조회 - 성공")
//    void findAuctionByItemId() {
//
//        // given
//        Long itemId = 1L;
//        Auction mockAuction = new Auction();
//        given(auctionRepository.findByItemId(itemId)).willReturn(Optional.of(mockAuction));
//
//        // when
//        auctionService.findAuctionByItemId(itemId);
//
//        // then
//        verify(auctionRepository).findByItemId(itemId);
//    }
//
//    @Test
//    @DisplayName("경매 상세 조회 - 실패")
//    void findAuctionByItemId_Failure_NotFound() {
//
//        // given
//        Long itemId = 99L;
//        given(auctionRepository.findByItemId(itemId)).willReturn(Optional.empty());
//
//        // when & then
//        assertThrows(AuctionNotFoundException.class, () -> {
//            auctionService.findAuctionByItemId(itemId);
//        });
//    }
}