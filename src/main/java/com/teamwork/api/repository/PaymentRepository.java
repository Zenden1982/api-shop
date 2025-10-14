package com.teamwork.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamwork.api.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    public Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByTransactionId(String transactionId);

}
