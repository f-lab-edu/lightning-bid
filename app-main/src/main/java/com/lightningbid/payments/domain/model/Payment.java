package com.lightningbid.payments.domain.model;

import com.lightningbid.auction.domain.model.Auction;
import com.lightningbid.common.entity.BaseEntity;
import com.lightningbid.payments.domain.enums.PaymentsStatus;
import com.lightningbid.user.domain.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymetns_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;

    private String paymentKey;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentsStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @Builder.Default
    @OneToMany(mappedBy = "payment", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<TossPayment> tossPayments = new ArrayList<>();

    public void paymentSuccess(String paymentKey) {

        this.paymentKey = paymentKey;
        this.status = PaymentsStatus.PAID;
    }

    public void paymentFail() {

        this.status = PaymentsStatus.FAILED;
    }
}
