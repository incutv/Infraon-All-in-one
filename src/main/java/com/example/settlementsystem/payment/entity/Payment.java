package com.example.settlementsystem.payment.entity;

import com.example.settlementsystem.payment.entity.requset.PaymentReq;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partner_id")
    private Long partnerId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "payment_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal paymentAmount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "imp_uid", length = 50)
    private String impUid;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "merchant_uid", length = 50)
    private String merchantUid;

    @Column(name = "pg_provider", length = 20)
    private String pgProvider;

    @Column(name = "pg_type", length = 20)
    private String pgType;

    @Column(name = "pg_tid", length = 50)
    private String pgTid;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "card_name", length = 20)
    private String cardName;

    @Column(name = "card_number", length = 50)
    private String cardNumber;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        paymentDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Payment of(PaymentReq paymentReq) {
        return Payment.builder()
                .partnerId(paymentReq.getPartnerId())
                .userId(paymentReq.getUserId())
                .orderId(paymentReq.getOrderId())
                .impUid(paymentReq.getImpUid())
                .paymentMethod(paymentReq.getPayMethod())
                .merchantUid(paymentReq.getMerchantUid())
                .paymentAmount(BigDecimal.valueOf(paymentReq.getPaidAmount()))
                .pgProvider(paymentReq.getPgProvider())
                .pgType(paymentReq.getPgType())
                .pgTid(paymentReq.getPgTid())
                .status(paymentReq.getStatus())
                .cardName(paymentReq.getCardName())
                .cardNumber(paymentReq.getCardNumber())
                .build();
    }
}
