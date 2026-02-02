package com.cateringmarketplace.module.cart.repository;

import com.cateringmarketplace.module.cart.model.MasterCartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterCartRepository extends MongoRepository<MasterCartItem, String> {
    
    List<MasterCartItem> findByUserId(String userId);
    
    Optional<MasterCartItem> findByUserIdAndMasterItemId(String userId, String masterItemId);
    
    void deleteByUserId(String userId);

    void deleteByUserIdAndMasterItemId(String userId, String masterItemId);
}
