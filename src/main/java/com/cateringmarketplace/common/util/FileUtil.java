package com.cateringmarketplace.common.util;

import com.cateringmarketplace.common.constant.AppConstants;
import com.cateringmarketplace.common.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

/**
 * Utility class for file operations.
 */
@Component
@Slf4j
public class FileUtil {

    @Value("${app.upload.allowed-image-types:jpg,jpeg,png,gif,webp}")
    private String allowedImageTypes;

    @Value("${app.upload.allowed-document-types:pdf,doc,docx,jpg,jpeg,png}")
    private String allowedDocumentTypes;

    @Value("${app.upload.max-image-size-mb:5}")
    private long maxImageSizeMb;

    @Value("${app.upload.max-document-size-mb:10}")
    private long maxDocumentSizeMb;

    /**
     * Validates and processes an image file.
     */
    public void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!isAllowedImageType(extension)) {
            throw new BadRequestException("Invalid image type. Allowed types: " + allowedImageTypes);
        }

        if (file.getSize() > maxImageSizeMb * 1024 * 1024) {
            throw new BadRequestException("File size exceeds maximum allowed size of " + maxImageSizeMb + "MB");
        }
    }

    /**
     * Validates and processes a document file.
     */
    public void validateDocumentFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!isAllowedDocumentType(extension)) {
            throw new BadRequestException("Invalid document type. Allowed types: " + allowedDocumentTypes);
        }

        if (file.getSize() > maxDocumentSizeMb * 1024 * 1024) {
            throw new BadRequestException("File size exceeds maximum allowed size of " + maxDocumentSizeMb + "MB");
        }
    }

    /**
     * Gets the file extension.
     */
    public String getFileExtension(String filename) {
        return FilenameUtils.getExtension(filename).toLowerCase();
    }

    /**
     * Checks if file is an allowed image type.
     */
    public boolean isAllowedImageType(String extension) {
        return Arrays.asList(allowedImageTypes.split(",")).contains(extension.toLowerCase());
    }

    /**
     * Checks if file is an allowed document type.
     */
    public boolean isAllowedDocumentType(String extension) {
        return Arrays.asList(allowedDocumentTypes.split(",")).contains(extension.toLowerCase());
    }

    /**
     * Generates a unique filename.
     */
    public String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + "." + extension;
    }

    /**
     * Generates a unique filename with prefix.
     */
    public String generateUniqueFileName(String prefix, String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return prefix + "_" + UUID.randomUUID().toString() + "." + extension;
    }

    /**
     * Gets the content type from file extension.
     */
    public String getContentType(String extension) {
        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> "application/octet-stream";
        };
    }

    /**
     * Saves file to local path (for testing/development).
     */
    public Path saveToLocal(MultipartFile file, Path targetPath) throws IOException {
        String filename = generateUniqueFileName(file.getOriginalFilename());
        Path filePath = targetPath.resolve(filename);

        Files.createDirectories(targetPath);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("File saved locally: {}", filePath);
        return filePath;
    }

    /**
     * Gets file size in human-readable format.
     */
    public String getReadableFileSize(long size) {
        if (size <= 0) return "0 B";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return String.format("%.2f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    /**
     * Checks if file is an image.
     */
    public boolean isImage(String filename) {
        String extension = getFileExtension(filename);
        return Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg").contains(extension);
    }

    /**
     * Sanitizes filename to remove special characters.
     */
    public String sanitizeFileName(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}

