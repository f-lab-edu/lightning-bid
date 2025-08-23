package com.lightningbid.payments.domain.repository;

import com.lightningbid.payments.domain.enums.PaymentsStatus;
import com.lightningbid.payments.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByAuctionIdAndUserId(Long auctionId, Long userId);

    Optional<Payment> findByOrderId(String orderId);

    Optional<Payment> findFirstByAuctionIdAndUserIdAndStatusOrderByIdDesc(long auctionId, Long userId, PaymentsStatus status);
}
