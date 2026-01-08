package com.cateringmarketplace.module.menu.dto.response;

import com.cateringmarketplace.module.menu.model.Category;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for category.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {

    private String categoryId;
    private String categoryName;
    private String categoryNameHindi;
    private String description;
    private Integer displayOrder;
    private String iconUrl;
    private String status;

    // =========================================================
    // Mapper
    // =========================================================
    public static CategoryResponse fromEntity(Category category) {
        if (category == null) return null;

        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .categoryNameHindi(category.getCategoryNameHindi())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .iconUrl(category.getIconUrl())
                .status(category.getStatus() != null
                        ? category.getStatus().name()
                        : null)
                .build();
    }
}
