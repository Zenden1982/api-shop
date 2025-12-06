package com.teamwork.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.model.DTO.ReviewCreateUpdateDTO;
import com.teamwork.api.model.DTO.ReviewReadDTO;
import com.teamwork.api.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Управление отзывами к товарам")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Оставить отзыв", description = "Создает новый отзыв от имени текущего пользователя.")
    public ResponseEntity<ReviewReadDTO> createReview(
            @RequestBody @Valid ReviewCreateUpdateDTO dto,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        ReviewReadDTO created = reviewService.createReview(dto, currentUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Получить отзывы товара (с пагинацией)", description = "Возвращает страницу отзывов для конкретного товара. Параметры: page, size, sort.")
    public ResponseEntity<Page<ReviewReadDTO>> getProductReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReviewReadDTO> page = reviewService.getReviewsByProductId(productId, pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Удалить отзыв", description = "Удаляет отзыв. Доступно автору отзыва или администратору.")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        reviewService.deleteReview(id, currentUsername, isAdmin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить отзыв по ID")
    public ResponseEntity<ReviewReadDTO> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Обновить отзыв", description = "Редактировать отзыв. Доступно только автору или админу.")
    public ResponseEntity<ReviewReadDTO> updateReview(
            @PathVariable Long id,
            @RequestBody @Valid ReviewCreateUpdateDTO dto,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        ReviewReadDTO updatedReview = reviewService.updateReview(id, dto, currentUsername, isAdmin);
        return ResponseEntity.ok(updatedReview);
    }
}
