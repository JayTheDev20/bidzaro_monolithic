package com.cateringmarketplace.module.admin.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating announcements.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAnnouncementRequest {

    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @Size(min = 10, max = 2000, message = "Message must be between 10 and 2000 characters")
    private String message;

    // ALL, USERS, VENDORS, ADMINS
    private String targetAudience;

    // LOW, NORMAL, HIGH, URGENT
    private String priority;

    private Instant startDate;

    private Instant endDate;

    private Boolean isActive;
}

