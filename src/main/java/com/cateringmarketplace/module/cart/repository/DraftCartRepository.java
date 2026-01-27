package com.cateringmarketplace.module.cart.repository;

import com.cateringmarketplace.module.cart.model.DraftCartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DraftCartRepository extends MongoRepository<DraftCartItem, String> {
    
    List<DraftCartItem> findByUserId(String userId);
    
    Optional<DraftCartItem> findByUserIdAndMasterItemId(String userId, String masterItemId);
    
    void deleteByUserId(String userId);
    
    void deleteByUserIdAndMasterItemId(String userId, String masterItemId);
}
