package com.cateringmarketplace.module.menu.repository;

import com.cateringmarketplace.module.menu.model.Category;
import com.cateringmarketplace.module.menu.model.Category.CategoryStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Category entity operations.
 */
@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findByCategoryId(String categoryId);

    List<Category> findByStatus(CategoryStatus status, Sort sort);

    List<Category> findByStatusOrderByDisplayOrderAsc(CategoryStatus status);

    boolean existsByCategoryId(String categoryId);

    boolean existsByCategoryName(String categoryName);
}

