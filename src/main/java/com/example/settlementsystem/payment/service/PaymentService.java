package com.example.settlementsystem.payment.service;

import com.example.settlementsystem.payment.entity.Payment;
import com.example.settlementsystem.payment.repository.PaymentRepository;
import com.example.settlementsystem.payment.util.PaymentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentClient paymentClient;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    /**
     * 모든 결제 내역 조회
     *
     * @return 모든 Payment 엔티티 리스트
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    /**
     * 새로운 결제 내역 저장
     *
     * @param payment 저장할 Payment 엔티티
     * @return 저장된 Payment 엔티티
     */
    @Transactional
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    /**
     * 결제 내역 삭제
     *
     * @param uid 포트원 거래고유번호
     */
    @Transactional
    public void canclePayment(String uid) {
        // 외부 API로 결제 취소 요청
        String result = paymentClient.cancelPayment(uid);

        if (result.contains("ERROR")) {
            throw new RuntimeException("Payment cancellation failed during recovery process.");
        }

        // impUid로 Payment 엔티티 조회
        Payment payment = paymentRepository.findByImpUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with impUid: " + uid));

        // status 필드를 "cancel"로 변경
        payment.setStatus("cancel");
    }

}
