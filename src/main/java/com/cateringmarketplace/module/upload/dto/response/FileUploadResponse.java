package com.cateringmarketplace.module.upload.dto.response;

import com.cateringmarketplace.module.upload.model.UploadedFile;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for file upload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileUploadResponse {

    private String fileId;
    private String fileName;
    private String originalName;
    private String fileType;
    private String contentType;
    private Long fileSize;
    private String fileUrl;
    private Instant createdAt;

    public static FileUploadResponse fromEntity(UploadedFile file) {
        if (file == null) return null;

        return FileUploadResponse.builder()
                .fileId(file.getFileId())
                .fileName(file.getFileName())
                .originalName(file.getOriginalName())
                .fileType(file.getFileType() != null ? file.getFileType().name() : null)
                .contentType(file.getContentType())
                .fileSize(file.getFileSize())
                .fileUrl(file.getFileUrl())
                .createdAt(file.getCreatedAt())
                .build();
    }
}

