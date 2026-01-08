package com.cateringmarketplace.module.menu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Category entity for menu item categories.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "categories")
public class Category {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("category_id")
    private String categoryId;

    @Field("category_name")
    private String categoryName;

    @Field("category_name_hindi")
    private String categoryNameHindi;

    private String description;

    @Field("display_order")
    private Integer displayOrder;

    @Field("icon_url")
    private String iconUrl;

    private CategoryStatus status;

    public enum CategoryStatus {
        ACTIVE,
        INACTIVE
    }

    /**
     * Checks if category is active.
     */
    public boolean isActive() {
        return status == CategoryStatus.ACTIVE;
    }
}

