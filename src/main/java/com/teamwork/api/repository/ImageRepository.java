package com.teamwork.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teamwork.api.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
    // List<Image> findByConfigOption(Long configOptionId);
}
