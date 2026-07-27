package com.amazonscale.payment.repository;

import com.amazonscale.payment.entity.Payment;
import com.amazonscale.payment.enums.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentRepositoryTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("Should find Payment by transaction ID and by order ID")
    void shouldFindPaymentByTransactionIdAndOrderId() {
        // Arrange
        Payment payment = Payment.builder().id(100L).transactionId("TXN-TEST-001").status(PaymentStatus.PENDING).build();

        when(paymentRepository.findByTransactionId("TXN-TEST-001")).thenReturn(Optional.of(payment));
        when(paymentRepository.findByOrder_Id(1L)).thenReturn(List.of(payment));
        when(paymentRepository.findByOrder_User_Id(10L)).thenReturn(List.of(payment));
        when(paymentRepository.existsByTransactionId("TXN-TEST-001")).thenReturn(true);
        when(paymentRepository.countByStatus(PaymentStatus.PENDING)).thenReturn(1L);

        // Act
        Optional<Payment> foundByTxn = paymentRepository.findByTransactionId("TXN-TEST-001");
        List<Payment> paymentsByOrder = paymentRepository.findByOrder_Id(1L);
        List<Payment> paymentsByUser = paymentRepository.findByOrder_User_Id(10L);
        boolean exists = paymentRepository.existsByTransactionId("TXN-TEST-001");
        long count = paymentRepository.countByStatus(PaymentStatus.PENDING);

        // Assert
        assertThat(foundByTxn).isPresent();
        assertThat(paymentsByOrder).hasSize(1);
        assertThat(paymentsByUser).hasSize(1);
        assertThat(exists).isTrue();
        assertThat(count).isEqualTo(1L);
    }
}
