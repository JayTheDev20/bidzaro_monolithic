package com.cateringmarketplace.module.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {

    private String otherUserId;
    
    private String vendorId; // Added vendorId

    @Builder.Default
    private String type = "USER_VENDOR";
}
