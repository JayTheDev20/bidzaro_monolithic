package com.cateringmarketplace.module.menu.dto.request;

import com.cateringmarketplace.module.menu.model.Category.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating/updating a category.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    private String categoryNameHindi;
    private String description;
    private Integer displayOrder;
    private String iconUrl;
    private CategoryStatus status;
}