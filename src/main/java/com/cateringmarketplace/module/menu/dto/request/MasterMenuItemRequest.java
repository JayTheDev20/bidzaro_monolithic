package com.cateringmarketplace.module.menu.dto.request;

import com.cateringmarketplace.module.menu.model.MenuItem.FoodType;
import com.cateringmarketplace.module.menu.model.MenuItem.ItemStatus;
import com.cateringmarketplace.module.menu.model.MenuItem.SpiceLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating/updating a master menu item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterMenuItemRequest {

    @NotBlank(message = "Item ID is required")
    private String itemId;

    @NotBlank(message = "Item name is required")
    private String itemName;

    private String itemNameHindi;
    private String description;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    private String cuisineType;

    @NotNull(message = "Food type is required")
    private FoodType foodType;

    private SpiceLevel spiceLevel;
    private List<String> dietaryTags;
    private List<String> allergens;

    @Valid
    private com.cateringmarketplace.module.menu.dto.response.MenuItemResponse.NutritionalInfoDTO nutritionalInfo;

    private List<String> imageUrls;
    private Boolean isPopular;
    private ItemStatus status;
}