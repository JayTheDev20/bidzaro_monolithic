package com.cateringmarketplace.module.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * Announcement entity for platform-wide announcements.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "announcements")
public class Announcement {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("announcement_id")
    private String announcementId;

    private String title;

    private String message;

    @Field("target_audience")
    private TargetAudience targetAudience;

    private AnnouncementPriority priority;

    @Field("start_date")
    private Instant startDate;

    @Field("end_date")
    private Instant endDate;

    @Field("is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Field("created_by")
    private String createdBy;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    public enum TargetAudience {
        ALL,
        USERS,
        VENDORS,
        ADMINS
    }

    public enum AnnouncementPriority {
        LOW,
        NORMAL,
        HIGH,
        URGENT
    }

    /**
     * Checks if announcement is currently active.
     */
    public boolean isCurrentlyActive() {
        if (!Boolean.TRUE.equals(isActive)) return false;
        Instant now = Instant.now();
        if (startDate != null && now.isBefore(startDate)) return false;
        if (endDate != null && now.isAfter(endDate)) return false;
        return true;
    }
}

