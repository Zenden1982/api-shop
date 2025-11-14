package com.teamwork.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teamwork.api.model.Cart;
import com.teamwork.api.model.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
