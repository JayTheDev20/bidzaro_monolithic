package com.cateringmarketplace.module.upload.repository;

import com.cateringmarketplace.module.upload.model.UploadedFile;
import com.cateringmarketplace.module.upload.model.UploadedFile.FileType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UploadedFile entity.
 */
@Repository
public interface UploadedFileRepository extends MongoRepository<UploadedFile, String> {

    Optional<UploadedFile> findByFileId(String fileId);

    Optional<UploadedFile> findByFileIdAndDeletedFalse(String fileId);

    Page<UploadedFile> findByUploadedByAndDeletedFalse(String uploadedBy, Pageable pageable);

    List<UploadedFile> findByEntityTypeAndEntityIdAndDeletedFalse(String entityType, String entityId);

    List<UploadedFile> findByUploadedByAndFileTypeAndDeletedFalse(String uploadedBy, FileType fileType);

    long countByUploadedByAndDeletedFalse(String uploadedBy);

    boolean existsByFileId(String fileId);
}

