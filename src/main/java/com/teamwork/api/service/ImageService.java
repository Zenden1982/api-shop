package com.teamwork.api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.teamwork.api.exception.ResourceNotFoundException;
import com.teamwork.api.model.DTO.ImageReadDTO;
import com.teamwork.api.model.Image;
import com.teamwork.api.model.Product;
import com.teamwork.api.repository.ImageRepository;
import com.teamwork.api.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductRepository productRepository;

    public String BUCKET = "src/main/resources/images";

    @Transactional
    public Image uploadImageForProduct(Long productId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Файл пустой");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт не найден: " + productId));

        String storedFileName = saveFileToDisk(file);

        Optional<Image> existingImageOpt = imageRepository.findByProductId(productId);

        if (existingImageOpt.isPresent()) {
            Image existingImage = existingImageOpt.get();

            deletePhysicalFile(existingImage.getImage());

            existingImage.setImage(storedFileName);

            return imageRepository.save(existingImage);
        } else {
            Image newImage = new Image();
            newImage.setImage(storedFileName);
            newImage.setProduct(product);
            return imageRepository.save(newImage);
        }
    }


    private String saveFileToDisk(MultipartFile image) {
        String filename = image.getOriginalFilename();
        if (filename == null) throw new RuntimeException("Имя файла null");

        filename = Path.of(filename).getFileName().toString();
        Path fullPath = Path.of(BUCKET, filename);

        try {
            Files.createDirectories(fullPath.getParent());
            Files.copy(image.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения файла на диск", e);
        }
    }

    private void deletePhysicalFile(String filename) {
        if (filename == null) return;
        try {
            Path fullPath = Path.of(BUCKET, filename);
            Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            System.err.println("Не удалось удалить старый файл: " + filename);
        }
    }


    public Optional<byte[]> getImage(String name) {
        if (name == null) return Optional.empty();
        Path fullPath = Path.of(BUCKET, name);
        if (Files.exists(fullPath)) {
            try {
                return Optional.of(Files.readAllBytes(fullPath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public Image create(Image entity) {
        try {
            return imageRepository.save(entity);
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException("Ошибка создания записи изображения");
        }
    }

    @Transactional
    public ImageReadDTO read(Long id) {
        return imageRepository.findById(id).map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Ошибка чтения изображения " + id));
    }

    @Transactional
    public ImageReadDTO readImageByProductId(Long productId) {
        try {
            return map(imageRepository.findByProductId(productId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Не найдено изображение для продукта с ID " + productId)));
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while reading images", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while reading images", e);
        }
    }

    public ImageReadDTO map(Image image) {
        ImageReadDTO imageReadDTO = new ImageReadDTO();
        imageReadDTO.setId(image.getId());
        Optional<byte[]> bytes = getImage(image.getImage());
        if (bytes.isPresent()) {
            imageReadDTO.setImage(bytes.get());
        } else {
            imageReadDTO.setImage(new byte[0]);
        }
        return imageReadDTO;
    }

    public Image update(Long id, Image entity) {
        return imageRepository.findById(id).map(image -> {
            image.setImage(entity.getImage());
            image.setProduct(entity.getProduct());
            return imageRepository.save(image);
        }).orElseThrow(() -> new RuntimeException("Error updating image " + id));
    }

    public void delete(Long id) {
        try {
            imageRepository.findById(id).ifPresent(img -> deletePhysicalFile(img.getImage()));
            imageRepository.deleteById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error deleting image " + id);
        }
    }
}
