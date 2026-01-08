package com.cateringmarketplace.module.notification.repository;

import com.cateringmarketplace.module.notification.model.Notification;
import com.cateringmarketplace.module.notification.model.Notification.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Notification entity operations.
 */
@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    /**
     * Find by notification ID.
     */
    Optional<Notification> findByNotificationId(String notificationId);

    /**
     * Find notifications by user ID.
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * Find unread notifications by user ID.
     */
    @Query("{'user_id': ?0, 'status': {'$nin': ['READ']}}")
    Page<Notification> findUnreadByUserId(String userId, Pageable pageable);

    /**
     * Count unread notifications for user.
     */
    @Query(value = "{'user_id': ?0, 'status': {'$nin': ['READ']}}", count = true)
    long countUnreadByUserId(String userId);

    /**
     * Find pending notifications for sending.
     */
    List<Notification> findByStatus(NotificationStatus status);

    /**
     * Find failed notifications for retry.
     */
    @Query("{'status': 'FAILED', 'retry_count': {'$lt': ?0}}")
    List<Notification> findFailedNotificationsForRetry(int maxRetries);
}
