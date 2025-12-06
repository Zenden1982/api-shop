package com.teamwork.api.model.DTO;

import java.time.LocalDateTime;

import com.teamwork.api.model.Review;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewReadDTO {

    private Long id;
    private Long productId;
    private String productName;
    private String username;
    private String text;
    private Integer rating;
    private LocalDateTime createdAt;

    public static ReviewReadDTO fromEntity(Review review) {
        return ReviewReadDTO.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .username(review.getUser().getUsername())
                .text(review.getText())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}