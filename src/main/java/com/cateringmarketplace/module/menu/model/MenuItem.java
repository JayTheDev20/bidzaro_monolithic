package com.cateringmarketplace.module.menu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MenuItem entity representing master menu items.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "menu_items")
public class MenuItem {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Indexed(unique = true)
    @Field("master_item_id") // Renamed from item_id
    private String masterItemId; // Renamed from itemId

    @TextIndexed
    @Field("item_name")
    private String itemName;

    @Field("item_name_hindi")
    private String itemNameHindi;

    private String description;

    @Indexed
    @Field("category_id")
    private String categoryId;

    @Indexed
    @Field("cuisine_type")
    private String cuisineType;

    @Indexed
    @Field("food_type")
    private FoodType foodType;

    @Field("spice_level")
    private SpiceLevel spiceLevel;

    @Field("dietary_tags")
    @Builder.Default
    private List<String> dietaryTags = new ArrayList<>();

    @Builder.Default
    private List<String> allergens = new ArrayList<>();

    @Field("nutritional_info")
    private NutritionalInfo nutritionalInfo;

    @Field("image_urls")
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @Field("is_popular")
    @Builder.Default
    private Boolean isPopular = false;

    private ItemStatus status;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    // Enums
    public enum FoodType {
        VEG,
        NON_VEG,
        VEGAN,
        EGG,
        BEVERAGES,
        DESSERTS,
        SNACKS,
        SWEETS,
        OTHER,

    }

    public enum SpiceLevel {
        MILD,
        MEDIUM,
        SPICY,
        EXTRA_SPICY,
        HOT,
        NONE,
        COLD,
        EXTRA_HOT
    }

    public enum ItemStatus {
        ACTIVE,
        INACTIVE
    }

    // Embedded classes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionalInfo {
        private Integer calories;
        private Integer proteinGrams;
        private Integer carbsGrams;
        private Integer fatGrams;
        private Integer servingSizeGrams;
    }

    /**
     * Checks if item is active.
     */
    public boolean isActive() {
        return status == ItemStatus.ACTIVE;
    }

    /**
     * Gets primary image URL.
     */
    public String getPrimaryImageUrl() {
        return imageUrls != null && !imageUrls.isEmpty() ? imageUrls.get(0) : null;
    }
}
