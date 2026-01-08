package com.cateringmarketplace.module.upload.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * File upload entity for tracking uploaded files.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "uploaded_files")
public class UploadedFile {

    // =========================================================
    // IDS
    // =========================================================

    @Id
    private String id;

    @Field("file_id")
    @Indexed(unique = true)
    private String fileId;

    @Field("uploaded_by")
    @Indexed
    private String uploadedBy;

    // =========================================================
    // FILE INFO
    // =========================================================

    @Field("file_name")
    private String fileName;

    @Field("original_name")
    private String originalName;

    @Field("file_type")
    private FileType fileType;

    @Field("content_type")
    private String contentType;

    @Field("file_size")
    private Long fileSize;

    @Field("file_url")
    private String fileUrl;

    @Field("storage_path")
    private String storagePath;

    // =========================================================
    // RELATION
    // =========================================================

    @Field("entity_type")
    private String entityType;

    @Field("entity_id")
    private String entityId;

    // =========================================================
    // FLAGS
    // =========================================================

    @Builder.Default
    @Field("is_public")
    private Boolean isPublic = true;

    @Builder.Default
    private Boolean deleted = false;

    @Field("deleted_at")
    private Instant deletedAt;

    // =========================================================
    // AUDIT
    // =========================================================

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    // =========================================================
    // ENUM
    // =========================================================

    public enum FileType {
        IMAGE,
        DOCUMENT,
        VIDEO
    }

    // =========================================================
    // UTILITY
    // =========================================================

    /**
     * Gets file extension from original name.
     */
    public String getFileExtension() {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName
                .substring(originalName.lastIndexOf(".") + 1)
                .toLowerCase();
    }
}
