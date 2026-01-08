package com.cateringmarketplace.module.admin.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating announcements.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnnouncementRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200)
    private String title;

    @NotBlank(message = "Message is required")
    @Size(min = 10, max = 2000)
    private String message;

    // ALL, USERS, VENDORS, ADMINS
    private String targetAudience;

    // LOW, NORMAL, HIGH, URGENT
    private String priority;

    private Instant startDate;

    private Instant endDate;
}
