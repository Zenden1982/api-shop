package com.teamwork.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.teamwork.api.model.DTO.ImageReadDTO;
import com.teamwork.api.model.Image;
import com.teamwork.api.repository.ProductRepository;
import com.teamwork.api.service.ImageService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/images")
@SecurityRequirement(name = "BearerAuth")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/{productId}")
    public ResponseEntity<Image> create(@PathVariable Long productId, @RequestPart("image") MultipartFile image) {
        Image image2 = new Image();
        imageService.uploadImage(image);
        image2.setImage(image.getOriginalFilename());
        image2.setProduct(productRepository.findById(productId).get());
        return ResponseEntity.status(201).body(imageService.create(image2));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageReadDTO> read(@PathVariable Long id) {
        return ResponseEntity.status(200).body(imageService.read(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ImageReadDTO>> readAll(@RequestParam(defaultValue = "") Long productId) {

        return ResponseEntity.status(200).body(imageService.readAll(productId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<byte[]> delete(@PathVariable Long id) {
        imageService.delete(id);
        return ResponseEntity.status(200).build();
    }

}