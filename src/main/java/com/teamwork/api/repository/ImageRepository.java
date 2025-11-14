package com.teamwork.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teamwork.api.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByProductId(Long productId);
}
