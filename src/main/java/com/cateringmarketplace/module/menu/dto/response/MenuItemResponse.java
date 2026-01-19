package com.cateringmarketplace.module.menu.dto.response;

import com.cateringmarketplace.module.menu.model.MenuItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for menu item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuItemResponse {

    private String itemId;
    private String itemName;
    private String itemNameHindi;
    private String description;
    private String categoryId;
    private String categoryName;
    private String cuisineType;
    private String foodType;
    private String spiceLevel;
    private List<String> dietaryTags;
    private List<String> allergens;
    private NutritionalInfoDTO nutritionalInfo;
    private List<String> imageUrls;
    private Boolean isPopular;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionalInfoDTO {
        private Integer calories;
        private Integer proteinGrams;
        private Integer carbsGrams;
        private Integer fatGrams;
        private Integer servingSizeGrams;
    }

    public static MenuItemResponse fromEntity(MenuItem item) {
        if (item == null) return null;

        MenuItemResponseBuilder builder = MenuItemResponse.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .itemNameHindi(item.getItemNameHindi())
                .description(item.getDescription())
                .categoryId(item.getCategoryId())
                .cuisineType(item.getCuisineType())
                .foodType(item.getFoodType() != null ? item.getFoodType().name() : null)
                .spiceLevel(item.getSpiceLevel() != null ? item.getSpiceLevel().name() : null)
                .dietaryTags(item.getDietaryTags())
                .allergens(item.getAllergens())
                .imageUrls(item.getImageUrls())
                .isPopular(item.getIsPopular())
                .status(item.getStatus() != null ? item.getStatus().name() : null)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt());

        if (item.getNutritionalInfo() != null) {
            builder.nutritionalInfo(NutritionalInfoDTO.builder()
                    .calories(item.getNutritionalInfo().getCalories())
                    .proteinGrams(item.getNutritionalInfo().getProteinGrams())
                    .carbsGrams(item.getNutritionalInfo().getCarbsGrams())
                    .fatGrams(item.getNutritionalInfo().getFatGrams())
                    .servingSizeGrams(item.getNutritionalInfo().getServingSizeGrams())
                    .build());
        }

        return builder.build();
    }
}
