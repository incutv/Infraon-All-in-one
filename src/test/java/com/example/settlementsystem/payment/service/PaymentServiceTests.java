package com.example.settlementsystem.payment.service;

import com.example.settlementsystem.payment.entity.Payment;
import com.example.settlementsystem.payment.repository.PaymentRepository;
import com.example.settlementsystem.payment.util.PaymentClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testCancelPayment_success() {
        // Arrange
        String uid = "testImpUid123";
        String mockApiResponse = "{\"status\":\"success\"}";

        Payment mockPayment = new Payment();
        mockPayment.setId(1L);
        mockPayment.setImpUid(uid);
        mockPayment.setStatus("paid");

        when(paymentClient.cancelPayment(uid)).thenReturn(mockApiResponse);
        when(paymentRepository.findByImpUid(uid)).thenReturn(Optional.of(mockPayment));

        // Act
        paymentService.canclePayment(uid);

        // Assert
        assertEquals("cancel", mockPayment.getStatus(), "Payment status should be updated to 'cancel'");

        // Verify interactions
        verify(paymentClient).cancelPayment(uid);
        verify(paymentRepository).findByImpUid(uid);
        verifyNoMoreInteractions(paymentRepository, paymentClient);
    }

    @Test
    void testCancelPayment_apiErrorResponse() {
        // Arrange
        String uid = "testImpUid123";
        String mockApiResponse = "{\"status\":\"ERROR\"}";

        when(paymentClient.cancelPayment(uid)).thenReturn(mockApiResponse);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.canclePayment(uid));
        assertEquals("Payment cancellation failed during recovery process.", exception.getMessage());

        // Verify interactions
        verify(paymentClient).cancelPayment(uid);
        verifyNoInteractions(paymentRepository); // Repository 호출은 발생하지 않음
    }
}

