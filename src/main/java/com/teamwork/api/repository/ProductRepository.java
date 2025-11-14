package com.teamwork.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teamwork.api.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
