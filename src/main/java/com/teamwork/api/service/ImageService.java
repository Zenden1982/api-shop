package com.teamwork.api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.teamwork.api.model.DTO.ImageReadDTO;
import com.teamwork.api.model.Image;
import com.teamwork.api.repository.ImageRepository;

@Service
@Component
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public String BUCKET = "src\\main\\resources\\images";

    public void uploadImage(MultipartFile image) {
        Optional<String> originalFilename = Optional.ofNullable(image.getOriginalFilename());
        if (originalFilename.isPresent()) {
            @SuppressWarnings("null")
            Path fullPath = Path.of(BUCKET, image.getOriginalFilename().replace("/", "\\"));

            try {
                Files.createDirectories(fullPath.getParent());
                Files.write(fullPath, image.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Optional<byte[]> getImage(String name) {
        Optional<Path> fullPath = Optional.ofNullable(Path.of(BUCKET, name.replace("/", "\\")));
        if (fullPath.isPresent() && Files.exists(fullPath.get())) {
            try {
                return Optional.of(Files.readAllBytes(fullPath.get()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    public Image create(Image entity) {
        try {
            return imageRepository.save(entity);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error creating image" + entity.getId());
        }
    }

    public ImageReadDTO read(Long id) {
        return imageRepository.findById(id).map(this::map)
                .orElseThrow(() -> new RuntimeException("Error reading image" + id));
    }

    public List<ImageReadDTO> readAll(Long productId) {
        try {

            return imageRepository.findByProductId(productId).stream()
                    .map(this::map).toList();

        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while reading images", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while reading images", e);
        }
    }

    public ImageReadDTO map(Image image) {
        ImageReadDTO imageReadDTO = new ImageReadDTO();
        imageReadDTO.setId(image.getId());
        imageReadDTO.setImage(getImage(image.getImage()).get());
        return imageReadDTO;
    }

    public Image update(Long id, Image entity) {
        return imageRepository.findById(id).map(image -> {
            image.setImage(entity.getImage());
            image.setConfig(entity.getConfig());
            return imageRepository.save(image);
        }).orElseThrow(() -> {
            throw new RuntimeException("Error updating image" + id);
        });
    }

    public void delete(Long id) {
        try {
            imageRepository.deleteById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error deleting image" + id);
        }
    }
}