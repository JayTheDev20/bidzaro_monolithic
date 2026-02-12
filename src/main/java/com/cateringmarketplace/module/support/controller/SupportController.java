package com.cateringmarketplace.module.support.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.support.dto.request.CreateTicketRequest;
import com.cateringmarketplace.module.support.dto.response.TicketResponse;
import com.cateringmarketplace.module.support.model.Ticket.TicketStatus;
import com.cateringmarketplace.module.support.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for support ticket operations.
 */
@RestController
@RequestMapping("/support")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Support", description = "Support ticket management APIs")
@SecurityRequirement(name = "bearerAuth")
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/tickets")
    @Operation(summary = "Create ticket", description = "Creates a new support ticket")
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Creating support ticket by user: {}", userDetails.getUserId());
        TicketResponse response = supportService.createTicket(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Ticket created successfully"));
    }

    @GetMapping("/tickets")
    @Operation(summary = "Get my tickets", description = "Returns user's support tickets")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getMyTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TicketResponse> tickets = supportService.getUserTickets(userDetails.getUserId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(
                tickets.getContent(),
                "Tickets retrieved",
                PageInfo.from(tickets)
        ));
    }

    @GetMapping("/tickets/{ticketId}")
    @Operation(summary = "Get ticket", description = "Returns ticket details")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicket(
            @PathVariable String ticketId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        TicketResponse response = supportService.getTicket(ticketId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== SUPPORT AGENT ENDPOINTS ====================

    @GetMapping("/admin/tickets")
    @Operation(summary = "Get all open tickets", description = "Returns all open tickets (Support only)")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getOpenTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("priority").descending().and(Sort.by("createdAt").ascending()));
        Page<TicketResponse> tickets = supportService.getOpenTickets(pageable);

        return ResponseEntity.ok(ApiResponse.success(
                tickets.getContent(),
                "Open tickets retrieved",
                PageInfo.from(tickets)
        ));
    }

    @GetMapping("/admin/my-tickets")
    @Operation(summary = "Get assigned tickets", description = "Returns tickets assigned to current agent")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getMyAssignedTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("priority").descending());
        Page<TicketResponse> tickets = supportService.getAgentTickets(userDetails.getUserId(), status, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                tickets.getContent(),
                "Assigned tickets retrieved",
                PageInfo.from(tickets)
        ));
    }

    @PostMapping("/admin/tickets/{ticketId}/assign")
    @Operation(summary = "Assign ticket", description = "Assigns a ticket to an agent")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<ApiResponse<TicketResponse>> assignTicket(
            @PathVariable String ticketId,
            @RequestParam String agentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Assigning ticket {} to agent {}", ticketId, agentId);
        TicketResponse response = supportService.assignTicket(ticketId, agentId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Ticket assigned"));
    }

    @PatchMapping("/admin/tickets/{ticketId}/status")
    @Operation(summary = "Update status", description = "Updates ticket status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<ApiResponse<TicketResponse>> updateStatus(
            @PathVariable String ticketId,
            @RequestParam String status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        TicketStatus newStatus = TicketStatus.valueOf(status.toUpperCase());
        TicketResponse response = supportService.updateStatus(ticketId, newStatus, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Status updated"));
    }

    @PostMapping("/admin/tickets/{ticketId}/resolve")
    @Operation(summary = "Resolve ticket", description = "Resolves a ticket")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<ApiResponse<TicketResponse>> resolveTicket(
            @PathVariable String ticketId,
            @RequestParam String resolutionNotes,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        TicketResponse response = supportService.resolveTicket(ticketId, resolutionNotes, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Ticket resolved"));
    }

    @PostMapping("/tickets/{ticketId}/rate")
    @Operation(summary = "Rate ticket", description = "Rates the support experience")
    public ResponseEntity<ApiResponse<TicketResponse>> rateTicket(
            @PathVariable String ticketId,
            @RequestParam int rating,
            @RequestParam(required = false) String feedback,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        TicketResponse response = supportService.rateTicket(ticketId, rating, feedback, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Thank you for your feedback"));
    }
}
