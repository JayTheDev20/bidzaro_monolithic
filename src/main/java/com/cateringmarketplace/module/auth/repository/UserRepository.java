package com.cateringmarketplace.module.auth.repository;

import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.model.enums.UserStatus;
import com.cateringmarketplace.module.auth.model.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find user by userId (UUID).
     */
    Optional<User> findByUserId(String userId);

    /**
     * Find user by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by phone number.
     */
    Optional<User> findByPhone(String phone);

    /**
     * Find user by email or phone.
     */
    Optional<User> findByEmailOrPhone(String email, String phone);

    /**
     * Check if email exists.
     */
    boolean existsByEmail(String email);

    /**
     * Check if phone exists.
     */
    boolean existsByPhone(String phone);

    /**
     * Check if email or phone exists.
     */
    boolean existsByEmailOrPhone(String email, String phone);

    /**
     * Find users by user type.
     */
    Page<User> findByUserType(UserType userType, Pageable pageable);

    /**
     * Find users by status.
     */
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    /**
     * Find users by user type and status.
     */
    Page<User> findByUserTypeAndStatus(UserType userType, UserStatus status, Pageable pageable);

    /**
     * Find users by status and user type.
     */
    Page<User> findByStatusAndUserType(UserStatus status, UserType userType, Pageable pageable);

    /**
     * Find users who haven't logged in since a given date.
     */
    @Query("{ 'last_login_at': { $lt: ?0 }, 'status': 'ACTIVE' }")
    List<User> findInactiveUsers(Instant since);

    /**
     * Find users with locked accounts.
     */
    @Query("{ 'locked_until': { $gt: ?0 } }")
    List<User> findLockedUsers(Instant now);

    /**
     * Find users by email containing (for search).
     */
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    /**
     * Find users by name containing.
     */
    @Query("{ $or: [ { 'first_name': { $regex: ?0, $options: 'i' } }, { 'last_name': { $regex: ?0, $options: 'i' } } ] }")
    Page<User> findByNameContaining(String name, Pageable pageable);

    /**
     * Count users by user type.
     */
    long countByUserType(UserType userType);

    /**
     * Count users by status.
     */
    long countByStatus(UserStatus status);

    /**
     * Count new users registered after a date.
     */
    long countByCreatedAtAfter(Instant date);

    /**
     * Find users registered between dates.
     */
    List<User> findByCreatedAtBetween(Instant start, Instant end);

    /**
     * Delete user by userId.
     */
    void deleteByUserId(String userId);
}
