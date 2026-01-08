package com.cateringmarketplace.module.menu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.cateringmarketplace.module.menu.model.MenuItem;
import com.cateringmarketplace.module.menu.model.MenuItem.FoodType;
import com.cateringmarketplace.module.menu.model.MenuItem.ItemStatus;

/**
 * Repository for MenuItem (master menu) entity operations.
 */
@Repository
public interface MenuItemRepository extends MongoRepository<MenuItem, String> {

    // ---------- COUNT METHODS ----------
    long countByCategoryId(String categoryId);

    long countByStatus(ItemStatus status);

    // ---------- EXISTS ----------
    boolean existsByItemId(String itemId);

    // ---------- FIND BY TAGS ----------
    @Query("{'status': 'ACTIVE', 'dietary_tags': {'$in': ?0}}")
    Page<MenuItem> findByDietaryTags(List<String> tags, Pageable pageable);

    // ---------- POPULAR ITEMS ----------
    @Query("{'status': 'ACTIVE', 'is_popular': true}")
    List<MenuItem> findPopularItems();

    // ---------- CUISINE ----------
    @Query("{'status': 'ACTIVE', 'cuisine_type': ?0}")
    Page<MenuItem> findByCuisineType(String cuisineType, Pageable pageable);

    // ---------- FOOD TYPE & STATUS ----------
    Page<MenuItem> findByFoodTypeAndStatus(FoodType foodType, ItemStatus status, Pageable pageable);

    // ---------- SEARCH ----------
    @Query("{'status': 'ACTIVE', 'item_name': {'$regex': ?0, '$options': 'i'}}")
    Page<MenuItem> searchByName(String name, Pageable pageable);

    // ---------- CATEGORY ----------
    Page<MenuItem> findByCategoryIdAndStatus(String categoryId, ItemStatus status, Pageable pageable);

    // ---------- STATUS ----------
    Page<MenuItem> findByStatus(ItemStatus status, Pageable pageable);

    // ---------- SINGLE ITEM ----------
    Optional<MenuItem> findByItemId(String itemId);
}
