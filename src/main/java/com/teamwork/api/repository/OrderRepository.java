package com.teamwork.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teamwork.api.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
