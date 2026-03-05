package com.cateringmarketplace.module.menu.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for admin menu operations.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Menu", description = "Admin menu management APIs")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminMenuController {

    private final MenuService menuService;

    // ==================== CATEGORY ENDPOINTS ====================

    @PostMapping({"/categories", "/menu/categories"})
    @Operation(summary = "Create category", description = "Creates a new menu category")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        log.info("Creating category: {}", request.getCategoryName());
        CategoryResponse category = menuService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(category, "Category created successfully"));
    }

    @PutMapping("/categories/{categoryId}")
    @Operation(summary = "Update category", description = "Updates an existing category")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable String categoryId,
            @Valid @RequestBody CategoryRequest request) {
        log.info("Updating category: {}", categoryId);
        CategoryResponse category = menuService.updateCategory(categoryId, request);
        return ResponseEntity.ok(ApiResponse.success(category, "Category updated successfully"));
    }

    @GetMapping({"/categories", "/menu/categories"})
    @Operation(summary = "Get all categories", description = "Returns all categories with optional status filter (ACTIVE/INACTIVE). Returns all if no filter.")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(
            @RequestParam(required = false) String status) {
        log.info("Fetching all categories for admin, status filter: {}", status);
        List<CategoryResponse> categories = menuService.getAllCategoriesAdmin(status);
        return ResponseEntity.ok(ApiResponse.success(categories, "Categories retrieved"));
    }

    @GetMapping({"/categories/{categoryId}", "/menu/categories/{categoryId}"})
    @Operation(summary = "Get category by ID", description = "Returns a single category by ID")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable String categoryId) {
        log.info("Fetching category: {}", categoryId);
        CategoryResponse category = menuService.getCategoryById(categoryId);
        return ResponseEntity.ok(ApiResponse.success(category, "Category retrieved"));
    }

    @PatchMapping("/categories/{categoryId}/activate")
    @Operation(summary = "Activate category", description = "Activates an inactive category")
    public ResponseEntity<ApiResponse<CategoryResponse>> activateCategory(
            @PathVariable String categoryId) {
        log.info("Activating category: {}", categoryId);
        CategoryResponse category = menuService.activateCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(category, "Category activated successfully"));
    }

    @PatchMapping("/categories/{categoryId}/inactivate")
    @Operation(summary = "Inactivate category", description = "Inactivates an active category")
    public ResponseEntity<ApiResponse<CategoryResponse>> inactivateCategory(
            @PathVariable String categoryId) {
        log.info("Inactivating category: {}", categoryId);
        CategoryResponse category = menuService.inactivateCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(category, "Category inactivated successfully"));
    }

    @DeleteMapping("/categories/{categoryId}")
    @Operation(summary = "Delete category", description = "Permanently deletes a category. Fails if category has menu items.")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable String categoryId) {
        log.info("Deleting category: {}", categoryId);
        menuService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted successfully"));
    }

    // ==================== MENU ITEM ENDPOINTS ====================

    @GetMapping({"/menu-items", "/menu/items"})
    @Operation(summary = "Get all menu items", description = "Returns all menu items with optional status filter (ACTIVE/INACTIVE). Returns all if no filter.")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getAllMenuItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        log.info("Fetching all menu items for admin, status filter: {}", status);
        Pageable pageable = PageRequest.of(page, size);
        Page<MenuItemResponse> items = menuService.getAllMenuItemsAdmin(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(
                items.getContent(),
                "Menu items retrieved",
                PageInfo.from(items)
        ));
    }

    @GetMapping({"/menu-items/{itemId}", "/menu/items/{itemId}"})
    @Operation(summary = "Get menu item by ID", description = "Returns a single menu item with category name")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getMenuItemById(
            @PathVariable String itemId) {
        log.info("Fetching menu item: {}", itemId);
        MenuItemResponse item = menuService.getMenuItemById(itemId);
        return ResponseEntity.ok(ApiResponse.success(item, "Menu item retrieved"));
    }

    @PostMapping({"/menu-items", "/menu/items"})
    @Operation(summary = "Create menu item", description = "Creates a new master menu item")
    public ResponseEntity<ApiResponse<MenuItemResponse>> createMasterMenuItem(
            @Valid @RequestBody MasterMenuItemRequest request) {
        log.info("Creating menu item: {}", request.getItemName());
        MenuItemResponse item = menuService.createMasterMenuItem(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(item, "Menu item created successfully"));
    }

    @PutMapping("/menu-items/{itemId}")
    @Operation(summary = "Update menu item", description = "Updates an existing master menu item")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateMasterMenuItem(
            @PathVariable String itemId,
            @Valid @RequestBody MasterMenuItemRequest request) {
        log.info("Updating menu item: {}", itemId);
        MenuItemResponse item = menuService.updateMasterMenuItem(itemId, request);
        return ResponseEntity.ok(ApiResponse.success(item, "Menu item updated successfully"));
    }

    @PatchMapping("/menu-items/{itemId}/activate")
    @Operation(summary = "Activate menu item", description = "Activates an inactive menu item")
    public ResponseEntity<ApiResponse<MenuItemResponse>> activateMenuItem(
            @PathVariable String itemId) {
        log.info("Activating menu item: {}", itemId);
        MenuItemResponse item = menuService.activateMenuItem(itemId);
        return ResponseEntity.ok(ApiResponse.success(item, "Menu item activated successfully"));
    }

    @PatchMapping("/menu-items/{itemId}/inactivate")
    @Operation(summary = "Inactivate menu item", description = "Inactivates an active menu item")
    public ResponseEntity<ApiResponse<MenuItemResponse>> inactivateMenuItem(
            @PathVariable String itemId) {
        log.info("Inactivating menu item: {}", itemId);
        MenuItemResponse item = menuService.inactivateMenuItem(itemId);
        return ResponseEntity.ok(ApiResponse.success(item, "Menu item inactivated successfully"));
    }

    @DeleteMapping("/menu-items/{itemId}")
    @Operation(summary = "Delete menu item", description = "Permanently deletes a menu item")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(
            @PathVariable String itemId) {
        log.info("Deleting menu item: {}", itemId);
        menuService.deleteMenuItem(itemId);
        return ResponseEntity.ok(ApiResponse.success(null, "Menu item deleted successfully"));
    }
}
