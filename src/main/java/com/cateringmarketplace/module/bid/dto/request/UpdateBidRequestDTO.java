package com.cateringmarketplace.module.bid.dto.request;

import com.cateringmarketplace.module.bid.dto.request.CreateBidRequestDTO.AdditionalRequirementsDTO;
import com.cateringmarketplace.module.bid.dto.request.CreateBidRequestDTO.BudgetDTO;
import com.cateringmarketplace.module.bid.dto.request.CreateBidRequestDTO.EventDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a bid request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBidRequestDTO {

    private EventDetailsDTO eventDetails;
    private BudgetDTO budget;
    private AdditionalRequirementsDTO additionalRequirements;
}
