package com.teamwork.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.teamwork.api.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select avg(r.rating) from Review r where r.product.id = :productId")
    Double findAverageRatingByProductId(Long productId);

    Page<Review> findByProductId(Long productId, Pageable pageable);


    Page<Review> findByUserUsername(String username, Pageable pageable);

    Page<Review> findByUserUsernameAndProductId(String username, Long productId, Pageable pageable);
}
