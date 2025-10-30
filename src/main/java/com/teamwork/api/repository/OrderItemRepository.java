package com.teamwork.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teamwork.api.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
