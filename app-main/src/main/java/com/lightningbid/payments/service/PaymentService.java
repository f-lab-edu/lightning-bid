package com.lightningbid.payments.service;

import com.lightningbid.auction.domain.model.Auction;
import com.lightningbid.auction.service.AuctionService;
import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.item.domain.enums.ItemStatus;
import com.lightningbid.item.exception.ItemNotActiveException;
import com.lightningbid.payments.domain.enums.PaymentsStatus;
import com.lightningbid.payments.domain.model.Payment;
import com.lightningbid.payments.domain.model.TossPayment;
import com.lightningbid.payments.domain.repository.PaymentRepository;
import com.lightningbid.payments.exception.*;
import com.lightningbid.payments.web.TossPaymentsApiClient;
import com.lightningbid.payments.web.dto.request.PaymentReadyRequestDto;
import com.lightningbid.payments.web.dto.response.PaymentReadyResponseDto;
import com.lightningbid.payments.web.dto.response.TossConfirmResponseDto;
import com.lightningbid.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final UserRepository userRepository;

    private final AuctionService auctionService;

    private final PaymentFailureService paymentFailureService;

    private final TossPaymentsApiClient tossPaymentsApiClient;

    private final BigDecimal DEPOSIT_PAYMENT_PERCENT = BigDecimal.valueOf(5);

    @Transactional
    public PaymentReadyResponseDto readyDepositPayment(PaymentReadyRequestDto requestDto, Long userId) {
        // TODO 동시에 두번의 요청을 보낼 경우 처리

        Long auctionId = requestDto.getAuctionId();
        Auction auction = auctionService.findAuctionByAuctionId(auctionId);

        if (!ItemStatus.ACTIVE.equals(auction.getItem().getStatus()))
            throw new ItemNotActiveException();


        Optional<Payment> optionalPayments = paymentRepository.findByAuctionIdAndUserId(auctionId, userId);
        if (optionalPayments.isPresent()) {

            Payment payment = optionalPayments.get();

            if (PaymentsStatus.READY.equals(payment.getStatus())) {

                return PaymentReadyResponseDto.builder()
                        .amount(payment.getAmount())
                        .orderId(payment.getOrderId())
                        .status(payment.getStatus())
                        .build();

            } else if (PaymentsStatus.PAID.equals(payment.getStatus())) {

                throw new PaymentAlreadyCompletedException();
            }
        }

        BigDecimal startPrice = auction.getStartPrice();

        // 경매 시작 금액의 5% 계산
        BigDecimal paymentAmount = startPrice.multiply(DEPOSIT_PAYMENT_PERCENT).divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);

        // 최소 결제 금액은 200원
        if (paymentAmount.compareTo(BigDecimal.valueOf(200)) < 0)
            paymentAmount = BigDecimal.valueOf(200);

        Payment savedPayment = paymentRepository.save(Payment.builder()
                .orderId(UUID.randomUUID().toString())
                .amount(paymentAmount)
                .status(PaymentsStatus.READY)
                .user(userRepository.getReferenceById(userId))
                .auction(auction)
                .build());

        return PaymentReadyResponseDto.builder()
                .amount(savedPayment.getAmount())
                .orderId(savedPayment.getOrderId())
                .status(savedPayment.getStatus())
                .build();
    }

    @Transactional
    public void processSuccessfulPayment(String paymentKey, String orderId, BigDecimal amount) {

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(PaymentNotFoundException::new);

        if (!PaymentsStatus.READY.equals(payment.getStatus()))
            throw new PaymentNotReadyException();

        // 결제 금액이 다를 경우
        if (payment.getAmount().compareTo(amount) != 0)
            throw new PaymentAmountMismatchException();

        try {

            TossConfirmResponseDto response = tossPaymentsApiClient.confirmPayment(paymentKey, orderId, amount);

            payment.paymentSuccess(response.getPaymentKey());
            saveTossPaymentOnSuccess(payment, response);

        } catch (TossPaymentException e) {

            paymentFailureService.recordFailure(payment.getId(), e.getCode(), e.getMessage());

            ErrorCode errorCode = switch (e.getCode()) {
                // 401 Unauthorized
                case "UNAUTHORIZED_KEY" -> ErrorCode.PAYMENT_UNAUTHORIZED;

                // 403 Forbidden (사용자 정책 관련 거절)
                case "REJECT_ACCOUNT_PAYMENT", "REJECT_CARD_PAYMENT", "REJECT_CARD_COMPANY", "FORBIDDEN_REQUEST",
                     "REJECT_TOSSPAY_INVALID_ACCOUNT", "EXCEED_MAX_AUTH_COUNT", "EXCEED_MAX_ONE_DAY_AMOUNT",
                     "NOT_AVAILABLE_BANK", "INVALID_PASSWORD", "INCORRECT_BASIC_AUTH_FORMAT", "FDS_ERROR" ->
                        ErrorCode.PAYMENT_FORBIDDEN;

                // 404 Not Found
                case "NOT_FOUND_PAYMENT", "NOT_FOUND_PAYMENT_SESSION" -> ErrorCode.PAYMENT_NOT_FOUND;

                // 500 Internal Server Error
                case "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING", "FAILED_INTERNAL_SYSTEM_PROCESSING",
                     "UNKNOWN_PAYMENT_ERROR" -> ErrorCode.PAYMENT_SERVER_ERROR;

                // 그 외 모든 400 Bad Request 및 기타 에러
                default -> ErrorCode.PAYMENT_BAD_REQUEST;
            };

            log.error("결제 승인에 실패하였습니다. 토스 에러 메시지: {} (코드: {})", e.getMessage(), e.getCode());

            throw new TossPaymentException(e.getMessage(), errorCode);
        }

    }

    private void saveTossPaymentOnSuccess(Payment payment, TossConfirmResponseDto response) {

        payment.getTossPayments().add(TossPayment.builder()
                .orderId(response.getOrderId())
                .paymentKey(response.getPaymentKey())
                .status(response.getStatus())
                .totalAmount(response.getTotalAmount())
                .method(response.getMethod())
                .balanceAmount(response.getBalanceAmount())
                .requestedAt(response.getRequestedAt())
                .approvedAt(response.getApprovedAt())
                .payment(payment)
                .build());
    }

    private void saveTossPaymentOnFailure(Payment payment, String errorCode, String errorMessage) {

        payment.getTossPayments().add(TossPayment.builder()
                .orderId(payment.getOrderId())
                .failureCode(errorCode)
                .failureMessage(errorMessage)
                .payment(payment)
                .build());
    }

    @Transactional
    public void processFailedPayment(String code, String message, String orderId) {

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(PaymentNotFoundException::new);

        if (!payment.getStatus().equals(PaymentsStatus.READY))
            return;

        payment.paymentFail();

        saveTossPaymentOnFailure(payment, code, message);
    }

    @Transactional(readOnly = true)
    public Optional<Payment> findPaymentByAuctionIdAndUserIdAndStatus(Long auctionId, Long userId, PaymentsStatus status) {

        return paymentRepository.findFirstByAuctionIdAndUserIdAndStatusOrderByIdDesc(auctionId, userId, status);
    }
}
