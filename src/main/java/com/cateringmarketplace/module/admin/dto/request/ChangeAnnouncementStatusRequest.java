package com.cateringmarketplace.module.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for changing announcement status (active/inactive).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeAnnouncementStatusRequest {

    @NotNull(message = "Status is required")
    private Boolean isActive;
}

