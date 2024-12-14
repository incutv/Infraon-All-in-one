package com.example.settlementsystem.payment.controller;

import com.example.settlementsystem.payment.entity.Payment;
import com.example.settlementsystem.payment.entity.requset.PaymentReq;
import com.example.settlementsystem.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/portone")
    public ResponseEntity<String> savePortone(@RequestBody PaymentReq paymentRequest) {
        try {
            paymentService.savePayment(Payment.of(paymentRequest));
            return ResponseEntity.ok("Payment processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process payment.");
        }
    }

    /**
     * 모든 결제 내역 조회
     *
     * @return 모든 Payment 엔티티 리스트
     */
    @GetMapping("/list")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/cancle/{uid}")
    public ResponseEntity<String> canclePayment(@PathVariable("uid")String uid) {
        paymentService.canclePayment(uid);
        return ResponseEntity.ok("Payment cancle processed successfully.");
    }

}
