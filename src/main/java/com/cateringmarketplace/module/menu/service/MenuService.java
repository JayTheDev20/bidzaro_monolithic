package com.cateringmarketplace.module.menu.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ConflictException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.menu.dto.request.VendorMenuItemRequest;
import com.cateringmarketplace.module.menu.dto.response.CategoryResponse;
import com.cateringmarketplace.module.menu.dto.response.MenuItemResponse;
import com.cateringmarketplace.module.menu.dto.response.VendorMenuItemResponse;
import com.cateringmarketplace.module.menu.model.Category;
import com.cateringmarketplace.module.menu.model.Category.CategoryStatus;
import com.cateringmarketplace.module.menu.model.MenuItem;
import com.cateringmarketplace.module.menu.model.MenuItem.ItemStatus;
import com.cateringmarketplace.module.menu.model.VendorMenuItem;
import com.cateringmarketplace.module.menu.model.VendorMenuItem.VendorItemStatus;
import com.cateringmarketplace.module.menu.repository.CategoryRepository;
import com.cateringmarketplace.module.menu.repository.MenuItemRepository;
import com.cateringmarketplace.module.menu.repository.VendorMenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for menu operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final VendorMenuItemRepository vendorMenuItemRepository;

    // ==================== CATEGORY OPERATIONS ====================

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByStatusOrderByDisplayOrderAsc(CategoryStatus.ACTIVE)
                .stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(String categoryId) {
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return CategoryResponse.fromEntity(category);
    }

    // ==================== MASTER MENU ITEM OPERATIONS ====================

    public Page<MenuItemResponse> getAllMenuItems(Pageable pageable) {
        return menuItemRepository.findByStatus(ItemStatus.ACTIVE, pageable)
                .map(MenuItemResponse::fromEntity);
    }

    public Page<MenuItemResponse> getMenuItemsByCategory(String categoryId, Pageable pageable) {
        return menuItemRepository.findByCategoryIdAndStatus(categoryId, ItemStatus.ACTIVE, pageable)
                .map(MenuItemResponse::fromEntity);
    }

    public MenuItemResponse getMenuItemById(String itemId) {
        MenuItem item = menuItemRepository.findByItemId(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        return MenuItemResponse.fromEntity(item);
    }

    public Page<MenuItemResponse> searchMenuItems(String query, Pageable pageable) {
        return menuItemRepository.searchByName(query, pageable)
                .map(MenuItemResponse::fromEntity);
    }

    public List<MenuItemResponse> getPopularItems() {
        return menuItemRepository.findPopularItems()
                .stream()
                .map(MenuItemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ==================== VENDOR MENU ITEM OPERATIONS ====================

    @Transactional
    public VendorMenuItemResponse addVendorMenuItem(VendorMenuItemRequest request, String vendorId) {
        log.info("Adding menu item for vendor: {}", vendorId);

        // Check if master item exists
        MenuItem masterItem = menuItemRepository.findByItemId(request.getMasterItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Master menu item not found"));

        // Check if vendor already has this item
        if (vendorMenuItemRepository.existsByVendorIdAndMasterItemId(vendorId, request.getMasterItemId())) {
            throw new ConflictException("ITEM_EXISTS", "This item is already in your menu");
        }

        // Calculate discounted price
        BigDecimal discountedPrice = request.getPricePerPlate();
        if (request.getDiscountPercentage() != null && request.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = request.getPricePerPlate()
                    .multiply(request.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            discountedPrice = request.getPricePerPlate().subtract(discount);
        }

        VendorMenuItem vendorItem = VendorMenuItem.builder()
                .vendorItemId(UUID.randomUUID().toString())
                .vendorId(vendorId)
                .masterItemId(request.getMasterItemId())
                .customName(request.getCustomName() != null ? request.getCustomName() : masterItem.getItemName())
                .customDescription(request.getCustomDescription())
                .pricing(VendorMenuItem.VendorItemPricing.builder()
                        .currency("INR")
                        .pricePerPlate(request.getPricePerPlate())
                        .minimumOrderQuantity(request.getMinimumOrderQuantity() != null ? request.getMinimumOrderQuantity() : 1)
                        .discountPercentage(request.getDiscountPercentage())
                        .discountedPrice(discountedPrice)
                        .build())
                .availability(VendorMenuItem.VendorItemAvailability.builder()
                        .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                        .unavailableReason(request.getUnavailableReason())
                        .advanceNoticeHours(request.getAdvanceNoticeHours())
                        .maxDailyCapacity(request.getMaxDailyCapacity())
                        .build())
                .preparationTimeMinutes(request.getPreparationTimeMinutes())
                .status(VendorItemStatus.ACTIVE)
                .build();

        // Map customization options
        if (request.getCustomizationOptions() != null) {
            vendorItem.setCustomizationOptions(request.getCustomizationOptions().stream()
                    .map(opt -> VendorMenuItem.CustomizationOption.builder()
                            .optionName(opt.getOptionName())
                            .choices(opt.getChoices())
                            .additionalCost(opt.getAdditionalCost())
                            .isRequired(opt.getIsRequired())
                            .build())
                    .collect(Collectors.toList()));
        }

        vendorItem = vendorMenuItemRepository.save(vendorItem);
        log.info("Vendor menu item created: {}", vendorItem.getVendorItemId());

        return VendorMenuItemResponse.fromEntity(vendorItem);
    }

    public Page<VendorMenuItemResponse> getVendorMenuItems(String vendorId, Pageable pageable) {
        return vendorMenuItemRepository.findActiveItemsByVendor(vendorId, pageable)
                .map(VendorMenuItemResponse::fromEntity);
    }

    public VendorMenuItemResponse getVendorMenuItemById(String vendorItemId) {
        VendorMenuItem item = vendorMenuItemRepository.findByVendorItemId(vendorItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor menu item not found"));
        return VendorMenuItemResponse.fromEntity(item);
    }

    @Transactional
    public VendorMenuItemResponse updateVendorMenuItem(String vendorItemId, VendorMenuItemRequest request, String vendorId) {
        log.info("Updating vendor menu item: {}", vendorItemId);

        VendorMenuItem item = vendorMenuItemRepository.findByVendorItemId(vendorItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor menu item not found"));

        if (!item.getVendorId().equals(vendorId)) {
            throw new BadRequestException("FORBIDDEN", "You cannot update this item");
        }

        // Update fields
        if (request.getCustomName() != null) {
            item.setCustomName(request.getCustomName());
        }
        if (request.getCustomDescription() != null) {
            item.setCustomDescription(request.getCustomDescription());
        }
        if (request.getPricePerPlate() != null) {
            item.getPricing().setPricePerPlate(request.getPricePerPlate());
            // Recalculate discounted price
            if (request.getDiscountPercentage() != null) {
                item.getPricing().setDiscountPercentage(request.getDiscountPercentage());
                BigDecimal discount = request.getPricePerPlate()
                        .multiply(request.getDiscountPercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                item.getPricing().setDiscountedPrice(request.getPricePerPlate().subtract(discount));
            }
        }
        if (request.getPreparationTimeMinutes() != null) {
            item.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        }

        item = vendorMenuItemRepository.save(item);
        return VendorMenuItemResponse.fromEntity(item);
    }

    @Transactional
    public VendorMenuItemResponse updateItemAvailability(String vendorItemId, boolean isAvailable,
                                                          String reason, String vendorId) {
        log.info("Updating availability for item: {} to {}", vendorItemId, isAvailable);

        VendorMenuItem item = vendorMenuItemRepository.findByVendorItemId(vendorItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor menu item not found"));

        if (!item.getVendorId().equals(vendorId)) {
            throw new BadRequestException("FORBIDDEN", "You cannot update this item");
        }

        item.getAvailability().setIsAvailable(isAvailable);
        if (!isAvailable && reason != null) {
            item.getAvailability().setUnavailableReason(reason);
        }

        item = vendorMenuItemRepository.save(item);
        return VendorMenuItemResponse.fromEntity(item);
    }

    @Transactional
    public void deleteVendorMenuItem(String vendorItemId, String vendorId) {
        log.info("Deleting vendor menu item: {}", vendorItemId);

        VendorMenuItem item = vendorMenuItemRepository.findByVendorItemId(vendorItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor menu item not found"));

        if (!item.getVendorId().equals(vendorId)) {
            throw new BadRequestException("FORBIDDEN", "You cannot delete this item");
        }

        // Soft delete by changing status
        item.setStatus(VendorItemStatus.INACTIVE);
        vendorMenuItemRepository.save(item);
    }
}

