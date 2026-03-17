package com.cateringmarketplace.module.order.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.order.dto.response.OrderResponse;
import com.cateringmarketplace.module.order.model.Order.OrderStatus;
import com.cateringmarketplace.module.order.service.OrderService;
import com.cateringmarketplace.module.vendor.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for order operations.
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Order management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;
    private final VendorService vendorService;

    // Removed manual createOrder endpoint as order creation is now handled automatically after payment

    @GetMapping
    @Operation(summary = "Get user's orders", description = "Returns paginated list of user's orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponse> orders = orderService.getUserOrders(userDetails.getUserId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(
                orders.getContent(),
                "Orders retrieved",
                PageInfo.from(orders)
        ));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Returns order details by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        OrderResponse response = orderService.getOrder(orderId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an order")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Updating order {} status to: {}", orderId, status);
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        OrderResponse response = orderService.updateOrderStatus(orderId, orderStatus, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Order status updated"));
    }

    // Backward compatibility for clients using PUT instead of PATCH.
    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status (PUT compatibility)", description = "Updates the status of an order")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatusPut(
            @PathVariable String orderId,
            @RequestParam String status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Updating order {} status to: {} via PUT", orderId, status);
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        OrderResponse response = orderService.updateOrderStatus(orderId, orderStatus, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Order status updated"));
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancels an order")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable String orderId,
            @RequestParam(required = false, defaultValue = "User requested cancellation") String reason,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Cancelling order: {} by user: {}", orderId, userDetails.getUserId());
        OrderResponse response = orderService.cancelOrder(orderId, reason, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Order cancelled"));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming orders", description = "Returns user's upcoming orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUpcomingOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("eventDetails.eventDate").ascending());
        Page<OrderResponse> orders = orderService.getUpcomingOrders(userDetails.getUserId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(
                orders.getContent(),
                "Upcoming orders retrieved",
                PageInfo.from(orders)
        ));
    }

    @GetMapping("/history")
    @Operation(summary = "Get order history", description = "Returns user's completed/cancelled orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrderHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponse> orders = orderService.getUserOrders(userDetails.getUserId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(
                orders.getContent(),
                "Order history retrieved",
                PageInfo.from(orders)
        ));
    }

    // ==================== VENDOR ENDPOINTS ====================

    @GetMapping("/vendor")
    @Operation(summary = "Get vendor's orders", description = "Returns orders assigned to vendor")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getVendorOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String vendorId = vendorService.getVendorByUserId(userDetails.getUserId()).getVendorId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponse> orders = orderService.getVendorOrders(vendorId, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                orders.getContent(),
                "Vendor orders retrieved",
                PageInfo.from(orders)
        ));
    }

    @GetMapping("/vendor/my")
    @Operation(summary = "Get my vendor orders (shorthand)", description = "Returns orders assigned to vendor - shorthand for /vendor")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyVendorOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String vendorId = vendorService.getVendorByUserId(userDetails.getUserId()).getVendorId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponse> orders = orderService.getVendorOrders(vendorId, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                orders.getContent(),
                "Vendor orders retrieved",
                PageInfo.from(orders)
        ));
    }

    @PutMapping("/vendor/{orderId}/status")
    @Operation(summary = "Update vendor order status", description = "Allows assigned vendor to update order workflow status")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateVendorOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        OrderResponse response = orderService.updateOrderStatusByVendor(orderId, orderStatus, userDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.success(response, "Vendor order status updated"));
    }

    @PatchMapping("/vendor/{orderId}/status")
    @Operation(summary = "Update vendor order status (PATCH)", description = "Allows assigned vendor to update order workflow status")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateVendorOrderStatusPatch(
            @PathVariable String orderId,
            @RequestParam String status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        OrderResponse response = orderService.updateOrderStatusByVendor(orderId, orderStatus, userDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.success(response, "Vendor order status updated"));
    }
}
