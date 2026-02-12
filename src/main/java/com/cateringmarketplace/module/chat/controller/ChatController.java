package com.cateringmarketplace.module.chat.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.common.util.FileUtil;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.chat.dto.ConversationResponse;
import com.cateringmarketplace.module.chat.dto.CreateConversationRequest;
import com.cateringmarketplace.module.chat.model.Conversation;
import com.cateringmarketplace.module.chat.model.Conversation.ConversationType;
import com.cateringmarketplace.module.chat.model.Message;
import com.cateringmarketplace.module.chat.model.Message.MessageType;
import com.cateringmarketplace.module.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for chat operations.
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat", description = "Chat and messaging APIs")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatService chatService;
    private final FileUtil fileUtil;

    @Value("${app-upload.path:./uploads}")
    private String uploadPath;

    @Value("${app-upload.base-url:http://localhost:8080/uploads}")
    private String uploadBaseUrl;

    @GetMapping("/conversations")
    @Operation(summary = "Get conversations", description = "Returns user's conversations")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<Conversation> conversationsPage = chatService.getUserConversations(userDetails.getUserId(), pageable);

        List<ConversationResponse> responseList = conversationsPage.getContent().stream()
                .map(c -> ConversationResponse.fromEntity(c, userDetails.getUserId()))
                .collect(Collectors.toList());

        Page<ConversationResponse> responsePage = new PageImpl<>(responseList, pageable, conversationsPage.getTotalElements());

        return ResponseEntity.ok(ApiResponse.success(
                responsePage.getContent(),
                "Conversations retrieved",
                PageInfo.from(responsePage)
        ));
    }

    @GetMapping("/conversations/{conversationId}")
    @Operation(summary = "Get conversation", description = "Returns conversation details")
    public ResponseEntity<ApiResponse<ConversationResponse>> getConversation(
            @PathVariable String conversationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Conversation conversation = chatService.getConversation(conversationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(ConversationResponse.fromEntity(conversation, userDetails.getUserId())));
    }

    @PostMapping("/conversations")
    @Operation(summary = "Create conversation", description = "Creates or gets existing conversation with another user")
    public ResponseEntity<ApiResponse<ConversationResponse>> createConversation(
            @Valid @RequestBody CreateConversationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Conversation conversation = chatService.getOrCreateConversation(
                userDetails.getUserId(), request);

        return ResponseEntity.ok(ApiResponse.success(
                ConversationResponse.fromEntity(conversation, userDetails.getUserId()),
                "Conversation ready"
        ));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "Get messages", description = "Returns messages in a conversation")
    public ResponseEntity<ApiResponse<List<Message>>> getMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<Message> messages = chatService.getMessages(conversationId, userDetails.getUserId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(
                messages.getContent(),
                "Messages retrieved",
                PageInfo.from(messages)
        ));
    }

    @PostMapping("/messages")
    @Operation(summary = "Send message", description = "Sends a message in a conversation")
    public ResponseEntity<ApiResponse<Message>> sendMessage(
            @RequestParam String conversationId,
            @RequestParam String content,
            @RequestParam(defaultValue = "TEXT") String messageType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        MessageType type = MessageType.valueOf(messageType.toUpperCase());
        Message message = chatService.sendMessage(conversationId, userDetails.getUserId(), content, type);

        return ResponseEntity.ok(ApiResponse.success(message, "Message sent"));
    }

    @PatchMapping("/conversations/{conversationId}/read")
    @Operation(summary = "Mark as read", description = "Marks all messages in conversation as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable String conversationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatService.markAsRead(conversationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Marked as read"));
    }

    @DeleteMapping("/messages/{messageId}")
    @Operation(summary = "Delete message", description = "Deletes a message (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @PathVariable String messageId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatService.deleteMessage(messageId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Message deleted"));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file", description = "Uploads a file for chat")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {

        // Validate file (image or document)
        try {
            fileUtil.validateImageFile(file);
        } catch (Exception e) {
            // If not image, try document
            fileUtil.validateDocumentFile(file);
        }

        // Save file
        Path targetPath = Paths.get(uploadPath, "chat");
        Path savedPath = fileUtil.saveToLocal(file, targetPath);
        
        String fileName = savedPath.getFileName().toString();
        String fileUrl = uploadBaseUrl + "/chat/" + fileName;

        return ResponseEntity.ok(ApiResponse.success(
                Map.of(
                        "fileName", file.getOriginalFilename(),
                        "fileUrl", fileUrl,
                        "fileType", fileUtil.getFileExtension(file.getOriginalFilename()),
                        "fileSize", file.getSize()
                ),
                "File uploaded successfully"
        ));
    }
}
