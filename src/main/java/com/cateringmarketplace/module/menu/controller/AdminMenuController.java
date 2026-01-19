package com.cateringmarketplace.module.menu.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.module.menu.dto.request.CategoryRequest;
import com.cateringmarketplace.module.menu.dto.request.MasterMenuItemRequest;
import com.cateringmarketplace.module.menu.dto.response.CategoryResponse;
import com.cateringmarketplace.module.menu.dto.response.MenuItemResponse;
import com.cateringmarketplace.module.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for admin menu operations.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Menu", description = "Admin menu management APIs")
public class AdminMenuController {

    private final MenuService menuService;

    @PostMapping("/categories")
    @Operation(summary = "Create category", description = "Creates a new menu category")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        log.info("Received request to create category: {}", request.getCategoryName());
        CategoryResponse category = menuService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(category, "Category created successfully"));
    }

    @PutMapping("/categories/{categoryId}")
    @Operation(summary = "Update category", description = "Updates an existing category")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable String categoryId,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = menuService.updateCategory(categoryId, request);
        return ResponseEntity.ok(ApiResponse.success(category, "Category updated successfully"));
    }

    @PostMapping("/menu-items")
    @Operation(summary = "Create master menu item", description = "Creates a new master menu item")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> createMasterMenuItem(
            @Valid @RequestBody MasterMenuItemRequest request) {
        MenuItemResponse item = menuService.createMasterMenuItem(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(item, "Menu item created successfully"));
    }

    @PutMapping("/menu-items/{itemId}")
    @Operation(summary = "Update master menu item", description = "Updates an existing master menu item")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateMasterMenuItem(
            @PathVariable String itemId,
            @Valid @RequestBody MasterMenuItemRequest request) {
        MenuItemResponse item = menuService.updateMasterMenuItem(itemId, request);
        return ResponseEntity.ok(ApiResponse.success(item, "Menu item updated successfully"));
    }
}