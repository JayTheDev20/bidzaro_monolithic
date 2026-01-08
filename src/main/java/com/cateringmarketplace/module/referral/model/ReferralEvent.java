package com.cateringmarketplace.module.referral.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * ReferralEvent entity for tracking referral actions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "referral_events")
@CompoundIndex(name = "referrer_referred_idx", def = "{'referrer_user_id': 1, 'referred_user_id': 1}", unique = true)
public class ReferralEvent {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("event_id")
    private String eventId;

    @Indexed
    @Field("referrer_user_id")
    private String referrerUserId;

    @Indexed
    @Field("referred_user_id")
    private String referredUserId;

    @Field("referral_code")
    private String referralCode;

    @Field("event_type")
    private EventType eventType;

    @Field("reward_status")
    private RewardStatus rewardStatus;

    @Field("referrer_reward_points")
    private Integer referrerRewardPoints;

    @Field("referred_reward_points")
    private Integer referredRewardPoints;

    @Field("order_id")
    private String orderId;

    @Field("rewarded_at")
    private Instant rewardedAt;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    public enum EventType {
        SIGNUP,                  // Referred user signed up
        FIRST_ORDER_COMPLETED    // Referred user completed first order
    }

    public enum RewardStatus {
        PENDING,    // Awaiting qualification (e.g., first order)
        GRANTED,    // Reward has been given
        CANCELLED   // Referral was cancelled (fraud, etc.)
    }
}

