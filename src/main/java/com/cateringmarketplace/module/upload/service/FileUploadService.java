package com.cateringmarketplace.module.upload.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.upload.dto.response.FileUploadResponse;
import com.cateringmarketplace.module.upload.model.UploadedFile;
import com.cateringmarketplace.module.upload.model.UploadedFile.FileType;
import com.cateringmarketplace.module.upload.repository.UploadedFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service class for file upload operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final UploadedFileRepository uploadedFileRepository;

    @Value("${app.upload.path:./uploads}")
    private String uploadPath;

    @Value("${app.upload.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final Set<String> ALLOWED_DOCUMENT_TYPES = Set.of(
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "image/jpeg", "image/png"
    );

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_DOCUMENT_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Uploads an image file.
     */
    @Transactional
    public FileUploadResponse uploadImage(MultipartFile file, String userId, String entityType, String entityId) {
        log.info("Uploading image for user: {}", userId);

        validateImageFile(file);

        return uploadFile(file, userId, FileType.IMAGE, entityType, entityId);
    }

    /**
     * Uploads a document file.
     */
    @Transactional
    public FileUploadResponse uploadDocument(MultipartFile file, String userId, String entityType, String entityId) {
        log.info("Uploading document for user: {}", userId);

        validateDocumentFile(file);

        return uploadFile(file, userId, FileType.DOCUMENT, entityType, entityId);
    }

    /**
     * Gets file by ID.
     */
    public FileUploadResponse getFile(String fileId) {
        UploadedFile file = uploadedFileRepository.findByFileIdAndDeletedFalse(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));
        return FileUploadResponse.fromEntity(file);
    }

    /**
     * Gets files for an entity.
     */
    public List<FileUploadResponse> getFilesForEntity(String entityType, String entityId) {
        return uploadedFileRepository.findByEntityTypeAndEntityIdAndDeletedFalse(entityType, entityId)
                .stream()
                .map(FileUploadResponse::fromEntity)
                .toList();
    }

    /**
     * Deletes a file.
     */
    @Transactional
    public void deleteFile(String fileId, String userId) {
        log.info("Deleting file: {} by user: {}", fileId, userId);

        UploadedFile file = uploadedFileRepository.findByFileIdAndDeletedFalse(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        if (!file.getUploadedBy().equals(userId)) {
            throw new BadRequestException("FORBIDDEN", "You cannot delete this file");
        }

        // Soft delete
        file.setDeleted(true);
        file.setDeletedAt(Instant.now());
        uploadedFileRepository.save(file);

        // Optionally delete physical file
        deletePhysicalFile(file.getStoragePath());
    }

    // ==================== HELPER METHODS ====================

    private FileUploadResponse uploadFile(MultipartFile file, String userId, FileType fileType,
                                           String entityType, String entityId) {
        try {
            String fileId = UUID.randomUUID().toString();
            String originalName = file.getOriginalFilename();
            String extension = getFileExtension(originalName);
            String fileName = fileId + "." + extension;

            // Create directory structure
            String subDir = fileType.name().toLowerCase() + "s";
            Path dirPath = Paths.get(uploadPath, subDir);
            Files.createDirectories(dirPath);

            // Save file
            Path filePath = dirPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String storagePath = subDir + "/" + fileName;
            String fileUrl = baseUrl + "/" + storagePath;

            UploadedFile uploadedFile = UploadedFile.builder()
                    .fileId(fileId)
                    .uploadedBy(userId)
                    .fileName(fileName)
                    .originalName(originalName)
                    .fileType(fileType)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .fileUrl(fileUrl)
                    .storagePath(storagePath)
                    .entityType(entityType)
                    .entityId(entityId)
                    .build();

            uploadedFile = uploadedFileRepository.save(uploadedFile);
            log.info("File uploaded: {} -> {}", originalName, fileUrl);

            return FileUploadResponse.fromEntity(uploadedFile);

        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new BadRequestException("UPLOAD_FAILED", "Failed to upload file: " + e.getMessage());
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("EMPTY_FILE", "File is empty");
        }

        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("INVALID_TYPE", "Invalid image type. Allowed: JPEG, PNG, GIF, WebP");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BadRequestException("FILE_TOO_LARGE", "Image size must be less than 5MB");
        }
    }

    private void validateDocumentFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("EMPTY_FILE", "File is empty");
        }

        if (!ALLOWED_DOCUMENT_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("INVALID_TYPE", "Invalid document type. Allowed: PDF, DOC, DOCX, JPEG, PNG");
        }

        if (file.getSize() > MAX_DOCUMENT_SIZE) {
            throw new BadRequestException("FILE_TOO_LARGE", "Document size must be less than 10MB");
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "bin";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private void deletePhysicalFile(String storagePath) {
        try {
            Path path = Paths.get(uploadPath, storagePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete physical file: {}", storagePath, e);
        }
    }
}

