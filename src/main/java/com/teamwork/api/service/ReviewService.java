package com.teamwork.api.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.teamwork.api.exception.ResourceNotFoundException;
import com.teamwork.api.model.DTO.ReviewCreateUpdateDTO;
import com.teamwork.api.model.DTO.ReviewReadDTO;
import com.teamwork.api.model.Product;
import com.teamwork.api.model.Review;
import com.teamwork.api.model.User;
import com.teamwork.api.repository.ProductRepository;
import com.teamwork.api.repository.ReviewRepository;
import com.teamwork.api.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ReviewReadDTO createReview(ReviewCreateUpdateDTO dto, String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Продукт с ID " + dto.getProductId() + " не найден"));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setText(dto.getText());
        review.setRating(dto.getRating());
        review.setCreatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        return ReviewReadDTO.fromEntity(saved);
    }

    @Transactional
    public Page<ReviewReadDTO> getUserReviews(String username, Pageable pageable) {
        return reviewRepository.findByUserUsername(username, pageable)
                .map(ReviewReadDTO::fromEntity);
    }

    @Transactional
    public Page<ReviewReadDTO> getUserReviewsByProduct(String username, Long productId, Pageable pageable) {
        return reviewRepository.findByUserUsernameAndProductId(username, productId, pageable)
                .map(ReviewReadDTO::fromEntity);
    }


    @Transactional
    public Page<ReviewReadDTO> getReviewsByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(ReviewReadDTO::fromEntity);
    }

    @Transactional
    public ReviewReadDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Отзыв с ID " + id + " не найден"));
        return ReviewReadDTO.fromEntity(review);
    }

    @Transactional
    public ReviewReadDTO updateReview(Long id, ReviewCreateUpdateDTO dto, String currentUsername, boolean isAdmin) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Отзыв с ID " + id + " не найден"));


        if (!review.getUser().getUsername().equals(currentUsername) && !isAdmin) {
            throw new AccessDeniedException("Вы не можете редактировать чужой отзыв");
        }

        review.setText(dto.getText());
        review.setRating(dto.getRating());


        Review updated = reviewRepository.save(review);
        return ReviewReadDTO.fromEntity(updated);
    }

    @Transactional
    public void deleteReview(Long id, String currentUsername, boolean isAdmin) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Отзыв с ID " + id + " не найден"));

        if (!review.getUser().getUsername().equals(currentUsername) && !isAdmin) {
            throw new AccessDeniedException("Вы не можете удалить чужой отзыв");
        }

        reviewRepository.delete(review);
    }
}