package com.cateringmarketplace.module.admin.repository;

import com.cateringmarketplace.module.admin.model.Announcement;
import com.cateringmarketplace.module.admin.model.Announcement.TargetAudience;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Announcement entity.
 */
@Repository
public interface AnnouncementRepository extends MongoRepository<Announcement, String> {

    Optional<Announcement> findByAnnouncementId(String announcementId);

    Page<Announcement> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    @Query("{'is_active': true, 'target_audience': {'$in': [?0, 'ALL']}, " +
           "'$or': [{'start_date': null}, {'start_date': {'$lte': ?1}}], " +
           "'$or': [{'end_date': null}, {'end_date': {'$gte': ?1}}]}")
    List<Announcement> findActiveAnnouncementsForAudience(TargetAudience audience, Instant now);

    Page<Announcement> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

