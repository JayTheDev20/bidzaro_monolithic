package com.cateringmarketplace.module.cart.service;

import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.cart.model.MasterCartItem;
import com.cateringmarketplace.module.cart.repository.MasterCartRepository;
import com.cateringmarketplace.module.menu.model.MenuItem;
import com.cateringmarketplace.module.menu.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MasterCartService {

    private final MasterCartRepository masterCartRepository;
    private final MenuItemRepository menuItemRepository;

    public List<MasterCartItem> getMasterCart(String userId) {
        return masterCartRepository.findByUserId(userId);
    }

    @Transactional
    public MasterCartItem addToMasterCart(String userId, String masterItemId, int quantity) {
        MenuItem masterItem = menuItemRepository.findByMasterItemId(masterItemId) // FIX
                .orElseThrow(() -> new ResourceNotFoundException("Master menu item not found"));

        MasterCartItem cartItem = masterCartRepository.findByUserIdAndMasterItemId(userId, masterItemId)
                .orElse(MasterCartItem.builder()
                        .userId(userId)
                        .masterItemId(masterItemId)
                        .itemName(masterItem.getItemName())
                        .imageUrl(masterItem.getPrimaryImageUrl())
                        .quantity(0)
                        .addedAt(Instant.now())
                        .build());

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        return masterCartRepository.save(cartItem);
    }

    @Transactional
    public void removeFromMasterCart(String userId, String masterItemId) {
        masterCartRepository.deleteByUserIdAndMasterItemId(userId, masterItemId);
    }

    @Transactional
    public void clearMasterCart(String userId) {
        masterCartRepository.deleteByUserId(userId);
    }
}
