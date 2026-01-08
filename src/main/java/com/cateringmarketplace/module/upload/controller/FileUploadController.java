package com.cateringmarketplace.module.upload.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.upload.dto.response.FileUploadResponse;
import com.cateringmarketplace.module.upload.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for file upload operations.
 */
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Upload", description = "File upload and management APIs")
@SecurityRequirement(name = "bearerAuth")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image", description = "Uploads an image file (JPEG, PNG, GIF, WebP - max 5MB)")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Image upload request from user: {}", userDetails.getUserId());
        FileUploadResponse response = fileUploadService.uploadImage(
                file, userDetails.getUserId(), entityType, entityId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Image uploaded successfully"));
    }

    @PostMapping(value = "/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload document", description = "Uploads a document file (PDF, DOC, DOCX - max 10MB)")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Document upload request from user: {}", userDetails.getUserId());
        FileUploadResponse response = fileUploadService.uploadDocument(
                file, userDetails.getUserId(), entityType, entityId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Document uploaded successfully"));
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "Get file info", description = "Returns file information by ID")
    public ResponseEntity<ApiResponse<FileUploadResponse>> getFile(
            @PathVariable String fileId) {
        FileUploadResponse response = fileUploadService.getFile(fileId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Get files for entity", description = "Returns all files for a specific entity")
    public ResponseEntity<ApiResponse<List<FileUploadResponse>>> getFilesForEntity(
            @PathVariable String entityType,
            @PathVariable String entityId) {
        List<FileUploadResponse> files = fileUploadService.getFilesForEntity(entityType, entityId);
        return ResponseEntity.ok(ApiResponse.success(files));
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete file", description = "Deletes an uploaded file")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @PathVariable String fileId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("File delete request: {} by user: {}", fileId, userDetails.getUserId());
        fileUploadService.deleteFile(fileId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "File deleted successfully"));
    }
}

