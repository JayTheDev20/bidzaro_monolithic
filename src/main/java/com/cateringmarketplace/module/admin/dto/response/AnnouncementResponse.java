package com.cateringmarketplace.module.admin.dto.response;

import com.cateringmarketplace.module.admin.model.Announcement.AnnouncementPriority;
import com.cateringmarketplace.module.admin.model.Announcement.TargetAudience;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for Announcement with creator details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {

    private String announcementId;

    private String title;

    private String message;

    private TargetAudience targetAudience;

    private AnnouncementPriority priority;

    private Instant startDate;

    private Instant endDate;

    private Boolean isActive;

    private String createdBy;

    private String createdByName;

    private Instant createdAt;
}

