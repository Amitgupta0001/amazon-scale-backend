package com.amazonscale.payment.repository;

import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.payment.entity.Payment;
import com.amazonscale.payment.enums.PaymentGateway;
import com.amazonscale.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByOrder_Id(Long orderId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByGateway(PaymentGateway gateway);

    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

    boolean existsByTransactionId(String transactionId);

    long countByStatus(PaymentStatus status);

    List<Payment> findByOrder_User_Id(Long userId);

    Optional<Payment> findByIdAndOrder_User_Id(Long paymentId, Long userId);
}
