package com.cateringmarketplace.module.menu.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.menu.dto.request.VendorMenuItemRequest;
import com.cateringmarketplace.module.menu.dto.response.CategoryResponse;
import com.cateringmarketplace.module.menu.dto.response.MenuItemResponse;
import com.cateringmarketplace.module.menu.dto.response.VendorMenuItemResponse;
import com.cateringmarketplace.module.menu.service.MenuService;
import com.cateringmarketplace.module.vendor.service.VendorService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for menu operations.
 */
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Menu", description = "Menu management APIs")
public class MenuController {

    private final MenuService menuService;
    private final VendorService vendorService;

    // ==================== CATEGORY ENDPOINTS ====================

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Returns list of all active categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = menuService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "Get category by ID", description = "Returns category details")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable String categoryId) {
        CategoryResponse category = menuService.getCategoryById(categoryId);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    // ==================== MASTER MENU ITEM ENDPOINTS ====================

    @GetMapping("/items")
    @Operation(summary = "Get all menu items", description = "Returns paginated list of menu items")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getAllMenuItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MenuItemResponse> items = menuService.getAllMenuItems(pageable);

        return ResponseEntity.ok(ApiResponse.success(
                items.getContent(),
                "Menu items retrieved",
                PageInfo.from(items)
        ));
    }

    @GetMapping("/items/{itemId}")
    @Operation(summary = "Get menu item by ID", description = "Returns menu item details")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getMenuItemById(
            @PathVariable String itemId) {
        MenuItemResponse item = menuService.getMenuItemById(itemId);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    @GetMapping("/items/category/{categoryId}")
    @Operation(summary = "Get menu items by category", description = "Returns menu items for a category")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getMenuItemsByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MenuItemResponse> items = menuService.getMenuItemsByCategory(categoryId, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                items.getContent(),
                "Menu items retrieved",
                PageInfo.from(items)
        ));
    }

    @GetMapping("/items/search")
    @Operation(summary = "Search menu items", description = "Search menu items by name")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> searchMenuItems(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MenuItemResponse> items = menuService.searchMenuItems(query, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                items.getContent(),
                "Search results",
                PageInfo.from(items)
        ));
    }

    @GetMapping("/items/popular")
    @Operation(summary = "Get popular items", description = "Returns popular menu items")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getPopularItems() {
        List<MenuItemResponse> items = menuService.getPopularItems();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    // ==================== VENDOR MENU ITEM ENDPOINTS ====================

    @GetMapping("/vendor-items")
    @Operation(summary = "Get vendor menu items", description = "Returns menu items for a vendor")
    public ResponseEntity<ApiResponse<List<VendorMenuItemResponse>>> getVendorMenuItems(
            @RequestParam String vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VendorMenuItemResponse> items = menuService.getVendorMenuItems(vendorId, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                items.getContent(),
                "Vendor menu items retrieved",
                PageInfo.from(items)
        ));
    }

    @GetMapping("/vendor-items/{vendorItemId}")
    @Operation(summary = "Get vendor menu item by ID", description = "Returns vendor menu item details")
    public ResponseEntity<ApiResponse<VendorMenuItemResponse>> getVendorMenuItemById(
            @PathVariable String vendorItemId) {
        VendorMenuItemResponse item = menuService.getVendorMenuItemById(vendorItemId);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    @PostMapping("/vendor-items")
    @Operation(summary = "Add item to vendor menu", description = "Adds a master menu item to vendor's menu")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<VendorMenuItemResponse>> addVendorMenuItem(
            @Valid @RequestBody VendorMenuItemRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String vendorId = vendorService.getVendorByUserId(userDetails.getUserId()).getVendorId();
        VendorMenuItemResponse item = menuService.addVendorMenuItem(request, vendorId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(item, "Item added to menu"));
    }

    @PutMapping("/vendor-items/{vendorItemId}")
    @Operation(summary = "Update vendor menu item", description = "Updates a vendor menu item")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<VendorMenuItemResponse>> updateVendorMenuItem(
            @PathVariable String vendorItemId,
            @Valid @RequestBody VendorMenuItemRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String vendorId = vendorService.getVendorByUserId(userDetails.getUserId()).getVendorId();
        VendorMenuItemResponse item = menuService.updateVendorMenuItem(vendorItemId, request, vendorId);

        return ResponseEntity.ok(ApiResponse.success(item, "Item updated"));
    }

    @PatchMapping("/vendor-items/{vendorItemId}/availability")
    @Operation(summary = "Update item availability", description = "Updates availability of a vendor menu item")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<VendorMenuItemResponse>> updateItemAvailability(
            @PathVariable String vendorItemId,
            @RequestParam boolean isAvailable,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String vendorId = vendorService.getVendorByUserId(userDetails.getUserId()).getVendorId();
        VendorMenuItemResponse item = menuService.updateItemAvailability(vendorItemId, isAvailable, reason, vendorId);

        return ResponseEntity.ok(ApiResponse.success(item, "Availability updated"));
    }


    @DeleteMapping("/vendor-items/{vendorItemId}")
    @Operation(summary = "Delete vendor menu item", description = "Removes an item from vendor's menu")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<Void>> deleteVendorMenuItem(
            @PathVariable String vendorItemId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String vendorId = vendorService.getVendorByUserId(userDetails.getUserId()).getVendorId();
        menuService.deleteVendorMenuItem(vendorItemId, vendorId);

        return ResponseEntity.ok(ApiResponse.success(null, "Item removed from menu"));
    }
}
