package com.example.settlementsystem.payment.repository;


import com.example.settlementsystem.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 기본 CRUD 메서드는 JpaRepository가 제공
    Optional<Payment> findByImpUid(String impUid);
    List<Payment> findByPaymentDateBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, String status);
}
