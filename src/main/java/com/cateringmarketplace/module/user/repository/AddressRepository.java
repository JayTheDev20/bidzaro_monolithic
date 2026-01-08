package com.cateringmarketplace.module.user.repository;

import com.cateringmarketplace.module.user.model.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Address entity operations.
 */
@Repository
public interface AddressRepository extends MongoRepository<Address, String> {

    /**
     * Finds address by address ID.
     */
    Optional<Address> findByAddressId(String addressId);

    /**
     * Finds all addresses for a user.
     */
    List<Address> findByUserIdOrderByIsDefaultDescCreatedAtDesc(String userId);

    /**
     * Finds all addresses for a user (ordered by default first).
     */
    List<Address> findByUserIdOrderByIsDefaultDesc(String userId);

    /**
     * Finds all addresses for a user.
     */
    List<Address> findByUserId(String userId);

    /**
     * Finds default address for a user.
     */
    Optional<Address> findByUserIdAndIsDefaultTrue(String userId);

    /**
     * Finds address by user ID and address ID.
     */
    Optional<Address> findByUserIdAndAddressId(String userId, String addressId);

    /**
     * Finds address by address ID and user ID (alias).
     */
    Optional<Address> findByAddressIdAndUserId(String addressId, String userId);

    /**
     * Counts addresses for a user.
     */
    long countByUserId(String userId);

    /**
     * Deletes address by address ID.
     */
    void deleteByAddressId(String addressId);

    /**
     * Deletes all addresses for a user.
     */
    void deleteByUserId(String userId);

    /**
     * Checks if address exists for user.
     */
    boolean existsByUserIdAndAddressId(String userId, String addressId);

    /**
     * Finds addresses by type.
     */
    List<Address> findByUserIdAndAddressType(String userId, Address.AddressType addressType);

    /**
     * Unsets default for all user addresses.
     */
    @Query("{'user_id': ?0, 'is_default': true}")
    List<Address> findDefaultAddresses(String userId);
}

