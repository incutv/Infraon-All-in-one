package com.example.settlementsystem.payment.entity.requset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReq {
    private Long partnerId;
    private Long userId;
    private Long orderId;
    private LocalDate paymentDate;

    @JsonProperty("imp_uid")
    private String impUid;       // imp_uid

    @JsonProperty("pay_method")
    private String payMethod;    // pay_method

    @JsonProperty("merchant_uid")
    private String merchantUid;  // merchant_uid

    @JsonProperty("paid_amount")
    private int paidAmount;      // paid_amount

    @JsonProperty("pg_provider")

    private String pgProvider;   // pg_provider

    @JsonProperty("pg_type")
    private String pgType;       // pg_type

    @JsonProperty("pg_tid")
    private String pgTid;        // pg_tid
    private String status;       // status
    @JsonProperty("card_name")
    private String cardName;     // card_name

    @JsonProperty("card_number")
    private String cardNumber;   // card_number
}
