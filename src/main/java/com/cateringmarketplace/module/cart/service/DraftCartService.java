package com.cateringmarketplace.module.cart.service;

import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.cart.dto.BatchAddDraftRequest;
import com.cateringmarketplace.module.cart.model.DraftCartItem;
import com.cateringmarketplace.module.cart.repository.DraftCartRepository;
import com.cateringmarketplace.module.menu.model.MenuItem;
import com.cateringmarketplace.module.menu.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DraftCartService {

    private final DraftCartRepository draftCartRepository;
    private final MenuItemRepository menuItemRepository;

    public List<DraftCartItem> getDraftCart(String userId) {
        return draftCartRepository.findByUserId(userId);
    }

    @Transactional
    public DraftCartItem addToDraftCart(String userId, String masterItemId, int quantity) {
        log.info("Adding item {} to draft cart for user {}", masterItemId, userId);

        MenuItem masterItem = menuItemRepository.findByMasterItemId(masterItemId) // FIX
                .orElseThrow(() -> new ResourceNotFoundException("Master menu item not found"));

        DraftCartItem existingItem = draftCartRepository.findByUserIdAndMasterItemId(userId, masterItemId)
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setUpdatedAt(Instant.now());
            return draftCartRepository.save(existingItem);
        }

        DraftCartItem newItem = DraftCartItem.builder()
                .userId(userId)
                .masterItemId(masterItemId)
                .itemName(masterItem.getItemName())
                .quantity(quantity)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        return draftCartRepository.save(newItem);
    }

    @Transactional
    public List<DraftCartItem> batchAddToDraftCart(String userId, BatchAddDraftRequest request) {
        log.info("Batch adding {} items to draft cart for user {}", request.getItems().size(), userId);
        
        List<DraftCartItem> savedItems = new ArrayList<>();
        
        // Optimize: Fetch all master items in one query if possible, or loop (caching helps)
        // For now, simple loop is fine for reasonable batch sizes
        
        for (BatchAddDraftRequest.DraftItemRequest itemRequest : request.getItems()) {
            try {
                savedItems.add(addToDraftCart(userId, itemRequest.getMasterItemId(), itemRequest.getQuantity()));
            } catch (ResourceNotFoundException e) {
                log.warn("Skipping item {}: {}", itemRequest.getMasterItemId(), e.getMessage());
            }
        }
        
        return savedItems;
    }

    @Transactional
    public void removeFromDraftCart(String userId, String masterItemId) {
        draftCartRepository.deleteByUserIdAndMasterItemId(userId, masterItemId);
    }

    @Transactional
    public void clearDraftCart(String userId) {
        draftCartRepository.deleteByUserId(userId);
    }
}
