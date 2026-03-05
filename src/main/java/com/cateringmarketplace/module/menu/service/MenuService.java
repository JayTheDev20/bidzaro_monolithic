package com.cateringmarketplace.module.menu.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ConflictException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.menu.dto.request.CategoryRequest;
import com.cateringmarketplace.module.menu.dto.request.MasterMenuItemRequest;
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
import org.springframework.data.domain.Sort;
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

    public List<CategoryResponse> getAllCategoriesAdmin(String status) {
        if (status != null && !status.isBlank()) {
            try {
                CategoryStatus categoryStatus = CategoryStatus.valueOf(status.toUpperCase());
                return categoryRepository.findByStatus(categoryStatus, Sort.by(Sort.Direction.ASC, "display_order"))
                        .stream()
                        .map(CategoryResponse::fromEntity)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("INVALID_STATUS", "Invalid status: " + status + ". Valid values: ACTIVE, INACTIVE");
            }
        }
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "display_order"))
                .stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(String categoryId) {
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public CategoryResponse activateCategory(String categoryId) {
        log.info("Activating category: {}", categoryId);
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (category.getStatus() == CategoryStatus.ACTIVE) {
            throw new ConflictException("ALREADY_ACTIVE", "Category is already active");
        }
        category.setStatus(CategoryStatus.ACTIVE);
        category = categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public CategoryResponse inactivateCategory(String categoryId) {
        log.info("Inactivating category: {}", categoryId);
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (category.getStatus() == CategoryStatus.INACTIVE) {
            throw new ConflictException("ALREADY_INACTIVE", "Category is already inactive");
        }
        category.setStatus(CategoryStatus.INACTIVE);
        category = categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public void deleteCategory(String categoryId) {
        log.info("Deleting category: {}", categoryId);
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        long itemCount = menuItemRepository.countByCategoryId(categoryId);
        if (itemCount > 0) {
            throw new ConflictException("CATEGORY_HAS_ITEMS",
                    "Cannot delete category with " + itemCount + " menu item(s). Inactivate or reassign items first.");
        }
        categoryRepository.delete(category);
        log.info("Category deleted successfully: {}", categoryId);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating new category: {}", request.getCategoryName());

        if (categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new ConflictException("CATEGORY_EXISTS", "Category with this name already exists");
        }

        Category category = Category.builder()
                .categoryId(UUID.randomUUID().toString())
                .categoryName(request.getCategoryName())
                .categoryNameHindi(request.getCategoryNameHindi())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .status(CategoryStatus.ACTIVE)
                .build();

        category = categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public CategoryResponse updateCategory(String categoryId, CategoryRequest request) {
        log.info("Updating category: {}", categoryId);

        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (request.getCategoryName() != null) {
            category.setCategoryName(request.getCategoryName());
        }
        if (request.getCategoryNameHindi() != null) {
            category.setCategoryNameHindi(request.getCategoryNameHindi());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getIconUrl() != null) {
            category.setIconUrl(request.getIconUrl());
        }
        if (request.getDisplayOrder() != null) {
            category.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }

        category = categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    // ==================== MASTER MENU ITEM OPERATIONS ====================

    public Page<MenuItemResponse> getAllMenuItems(Pageable pageable) {
        return menuItemRepository.findByStatus(ItemStatus.ACTIVE, pageable)
                .map(this::toResponseWithCategoryName);
    }

    public Page<MenuItemResponse> getAllMenuItemsAdmin(String status, Pageable pageable) {
        Page<MenuItem> items;
        if (status != null && !status.isBlank()) {
            try {
                ItemStatus itemStatus = ItemStatus.valueOf(status.toUpperCase());
                items = menuItemRepository.findByStatus(itemStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("INVALID_STATUS", "Invalid status: " + status + ". Valid values: ACTIVE, INACTIVE");
            }
        } else {
            items = menuItemRepository.findAll(pageable);
        }
        return items.map(this::toResponseWithCategoryName);
    }

    public Page<MenuItemResponse> getMenuItemsByCategory(String categoryId, Pageable pageable) {
        return menuItemRepository.findByCategoryIdAndStatus(categoryId, ItemStatus.ACTIVE, pageable)
                .map(this::toResponseWithCategoryName);
    }

    public MenuItemResponse getMenuItemById(String masterItemId) {
        MenuItem item = menuItemRepository.findByMasterItemId(masterItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        return toResponseWithCategoryName(item);
    }

    public Page<MenuItemResponse> searchMenuItems(String query, Pageable pageable) {
        return menuItemRepository.searchByName(query, pageable)
                .map(this::toResponseWithCategoryName);
    }

    public List<MenuItemResponse> getPopularItems() {
        return menuItemRepository.findPopularItems()
                .stream()
                .map(this::toResponseWithCategoryName)
                .collect(Collectors.toList());
    }

    /**
     * Builds MenuItemResponse and enriches it with category name from DB.
     */
    private MenuItemResponse toResponseWithCategoryName(MenuItem item) {
        MenuItemResponse response = MenuItemResponse.fromEntity(item);
        if (item.getCategoryId() != null) {
            categoryRepository.findByCategoryId(item.getCategoryId())
                    .ifPresent(category -> response.setCategoryName(category.getCategoryName()));
        }
        return response;
    }

    @Transactional
    public MenuItemResponse createMasterMenuItem(MasterMenuItemRequest request) {
        log.info("Creating master menu item: {}", request.getItemName());

        // Verify category exists and fetch it
        Category category = categoryRepository.findByCategoryId(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Map nutritional info
        MenuItem.NutritionalInfo nutritionalInfo = null;
        if (request.getNutritionalInfo() != null) {
            nutritionalInfo = MenuItem.NutritionalInfo.builder()
                    .calories(request.getNutritionalInfo().getCalories())
                    .proteinGrams(request.getNutritionalInfo().getProteinGrams())
                    .carbsGrams(request.getNutritionalInfo().getCarbsGrams())
                    .fatGrams(request.getNutritionalInfo().getFatGrams())
                    .servingSizeGrams(request.getNutritionalInfo().getServingSizeGrams())
                    .build();
        }

        MenuItem item = MenuItem.builder()
                .masterItemId(UUID.randomUUID().toString())
                .itemName(request.getItemName())
                .itemNameHindi(request.getItemNameHindi())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .cuisineType(request.getCuisineType())
                .foodType(request.getFoodType())
                .spiceLevel(request.getSpiceLevel())
                .dietaryTags(request.getDietaryTags())
                .allergens(request.getAllergens())
                .nutritionalInfo(nutritionalInfo)
                .imageUrls(request.getImageUrls())
                .isPopular(request.getIsPopular() != null ? request.getIsPopular() : false)
                .status(ItemStatus.ACTIVE)
                .build();

        item = menuItemRepository.save(item);

        // Build response with category name
        MenuItemResponse response = MenuItemResponse.fromEntity(item);
        response.setCategoryName(category.getCategoryName());
        return response;
    }

    @Transactional
    public MenuItemResponse updateMasterMenuItem(String masterItemId, MasterMenuItemRequest request) {
        log.info("Updating master menu item: {}", masterItemId);

        MenuItem item = menuItemRepository.findByMasterItemId(masterItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        if (request.getItemName() != null) {
            item.setItemName(request.getItemName());
        }
        if (request.getItemNameHindi() != null) {
            item.setItemNameHindi(request.getItemNameHindi());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null) {
            // Verify new category exists
            if (!categoryRepository.existsByCategoryId(request.getCategoryId())) {
                throw new ResourceNotFoundException("Category not found");
            }
            item.setCategoryId(request.getCategoryId());
        }
        if (request.getCuisineType() != null) {
            item.setCuisineType(request.getCuisineType());
        }
        if (request.getFoodType() != null) {
            item.setFoodType(request.getFoodType());
        }
        if (request.getSpiceLevel() != null) {
            item.setSpiceLevel(request.getSpiceLevel());
        }
        if (request.getDietaryTags() != null) {
            item.setDietaryTags(request.getDietaryTags());
        }
        if (request.getAllergens() != null) {
            item.setAllergens(request.getAllergens());
        }
        if (request.getNutritionalInfo() != null) {
            item.setNutritionalInfo(MenuItem.NutritionalInfo.builder()
                    .calories(request.getNutritionalInfo().getCalories())
                    .proteinGrams(request.getNutritionalInfo().getProteinGrams())
                    .carbsGrams(request.getNutritionalInfo().getCarbsGrams())
                    .fatGrams(request.getNutritionalInfo().getFatGrams())
                    .servingSizeGrams(request.getNutritionalInfo().getServingSizeGrams())
                    .build());
        }
        if (request.getImageUrls() != null) {
            item.setImageUrls(request.getImageUrls());
        }
        if (request.getIsPopular() != null) {
            item.setIsPopular(request.getIsPopular());
        }
        if (request.getStatus() != null) {
            item.setStatus(request.getStatus());
        }

        item = menuItemRepository.save(item);

        // Build response with category name
        MenuItemResponse response = MenuItemResponse.fromEntity(item);
        categoryRepository.findByCategoryId(item.getCategoryId())
                .ifPresent(category -> response.setCategoryName(category.getCategoryName()));
        return response;
    }

    @Transactional
    public MenuItemResponse activateMenuItem(String masterItemId) {
        log.info("Activating menu item: {}", masterItemId);

        MenuItem item = menuItemRepository.findByMasterItemId(masterItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        if (item.getStatus() == ItemStatus.ACTIVE) {
            throw new ConflictException("ALREADY_ACTIVE", "Menu item is already active");
        }

        item.setStatus(ItemStatus.ACTIVE);
        item = menuItemRepository.save(item);
        return toResponseWithCategoryName(item);
    }

    @Transactional
    public MenuItemResponse inactivateMenuItem(String masterItemId) {
        log.info("Inactivating menu item: {}", masterItemId);

        MenuItem item = menuItemRepository.findByMasterItemId(masterItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        if (item.getStatus() == ItemStatus.INACTIVE) {
            throw new ConflictException("ALREADY_INACTIVE", "Menu item is already inactive");
        }

        item.setStatus(ItemStatus.INACTIVE);
        item = menuItemRepository.save(item);
        return toResponseWithCategoryName(item);
    }

    @Transactional
    public void deleteMenuItem(String masterItemId) {
        log.info("Deleting menu item: {}", masterItemId);

        MenuItem item = menuItemRepository.findByMasterItemId(masterItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        menuItemRepository.delete(item);
        log.info("Menu item deleted successfully: {}", masterItemId);
    }

    // ==================== VENDOR MENU ITEM OPERATIONS ====================

    @Transactional
    public VendorMenuItemResponse addVendorMenuItem(VendorMenuItemRequest request, String vendorId) {
        log.info("Adding menu item for vendor: {}", vendorId);

        // Check if master item exists
        MenuItem masterItem = menuItemRepository.findByMasterItemId(request.getMasterItemId())
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
                        .currency("USD")
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
        if (request.getMinimumOrderQuantity() != null) {
            item.getPricing().setMinimumOrderQuantity(request.getMinimumOrderQuantity());
        }
        if (request.getDiscountPercentage() != null) {
            item.getPricing().setDiscountPercentage(request.getDiscountPercentage());
            // Recalculate discounted price if price is also updated, otherwise use existing price
            BigDecimal price = request.getPricePerPlate() != null ? request.getPricePerPlate() : item.getPricing().getPricePerPlate();
            BigDecimal discount = price
                    .multiply(request.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            item.getPricing().setDiscountedPrice(price.subtract(discount));
        }
        if (request.getIsAvailable() != null) {
            item.getAvailability().setIsAvailable(request.getIsAvailable());
        }
        if (request.getUnavailableReason() != null) {
            item.getAvailability().setUnavailableReason(request.getUnavailableReason());
        }
        if (request.getAdvanceNoticeHours() != null) {
            item.getAvailability().setAdvanceNoticeHours(request.getAdvanceNoticeHours());
        }
        if (request.getMaxDailyCapacity() != null) {
            item.getAvailability().setMaxDailyCapacity(request.getMaxDailyCapacity());
        }
        if (request.getPreparationTimeMinutes() != null) {
            item.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        }
        if (request.getCustomizationOptions() != null) {
            item.setCustomizationOptions(request.getCustomizationOptions().stream()
                    .map(opt -> VendorMenuItem.CustomizationOption.builder()
                            .optionName(opt.getOptionName())
                            .choices(opt.getChoices())
                            .additionalCost(opt.getAdditionalCost())
                            .isRequired(opt.getIsRequired())
                            .build())
                    .collect(Collectors.toList()));
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

        // Update status to reflect availability: ACTIVE when available, INACTIVE when not
        if (isAvailable) {
            item.setStatus(VendorMenuItem.VendorItemStatus.ACTIVE);
        } else {
            item.setStatus(VendorMenuItem.VendorItemStatus.INACTIVE);
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
