package com.example.settlementsystem.settlement.service;

import com.example.settlementsystem.payment.entity.Payment;
import com.example.settlementsystem.payment.repository.PaymentRepository;
import com.example.settlementsystem.settlement.entity.Settlement;
import com.example.settlementsystem.settlement.repository.SettlementRepository;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SettlementScheduledTasks {
    public static final String PAYMENT_COMPLETED = "paid";
    private final PaymentRepository paymentRepository;

    private final SettlementRepository settlementRepository;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SettlementScheduledTasks(PaymentRepository paymentRepository, SettlementRepository settlementRepository, JdbcTemplate jdbcTemplate) {
        this.paymentRepository = paymentRepository;
        this.settlementRepository = settlementRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    //@Scheduled(cron = "0 * * * * ?")
    @SchedulerLock(name = "ScheduledTask_run")
    public void dailySettlement() {
        // 어제의 날짜를 가져옴
        LocalDate yesterday = LocalDate.now().minusDays(1);
        // 어제의 시작 시각 설정 (2024-10-26 00:00:00)
        LocalDateTime startDate = yesterday.atStartOfDay();
        // 어제의 끝 시각 설정 (2024-10-26 23:59:59)
        LocalDateTime endDate = yesterday.atTime(LocalTime.of(23, 59, 59));

        // 해당 기간 동안의 결제 내역 조회 및 집계
        Map<Long, BigDecimal> settlementMap = getSettlementMap(startDate, endDate);

        long beforeTime1 = System.currentTimeMillis();
        processSettlements(settlementMap, yesterday);
        long afterTime1 = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
        long diffTime1 = afterTime1 - beforeTime1; // 두 개의 실행 시간
        log.info("실행 시간(ms): " + diffTime1); // 세컨드(초 단위 변환)

    }

    private Map<Long, BigDecimal> getSettlementMap(LocalDateTime startDate, LocalDateTime endDate) {
        List<Payment> paymentList = paymentRepository.findByPaymentDateBetweenAndStatus(startDate, endDate, PAYMENT_COMPLETED);
        // partner_id를 기준으로 group by
        return paymentList.stream()
                .collect(Collectors.groupingBy(
                        Payment::getPartnerId,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Payment::getPaymentAmount,
                                BigDecimal::add
                        )
                ));
    }

    private void bulkProcessSettlements(Map<Long, BigDecimal> settlementMap, LocalDate paymentDate) {
        String sql = "INSERT INTO settlements (partner_id, total_amount, payment_date) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Long partnerId = (Long) settlementMap.keySet().toArray()[i];
                BigDecimal amount = settlementMap.get(partnerId);

                ps.setLong(1, partnerId);
                ps.setBigDecimal(2, amount);
                ps.setObject(3, paymentDate);
            }

            @Override
            public int getBatchSize() {
                return settlementMap.size();
            }
        });
    }

    private void processSettlements(Map<Long, BigDecimal> settlementMap, LocalDate paymentDate) {
        ForkJoinPool customForkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

        try {
            customForkJoinPool.submit(() ->
                    settlementMap.entrySet().parallelStream()
                            .forEach(entry -> {
                                Settlement settlement = Settlement.create(entry.getKey(), entry.getValue(), paymentDate);
                                settlementRepository.save(settlement);
                            })
            ).get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            customForkJoinPool.shutdown();
        }

    }
}
