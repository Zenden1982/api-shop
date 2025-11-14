package com.teamwork.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teamwork.api.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}
