package com.cateringmarketplace.module.payment.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory for selecting the appropriate payment gateway based on country.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentGatewayFactory {

    private final List<PaymentGatewayStrategy> paymentGateways;

    /**
     * Gets the appropriate payment gateway for a country.
     */
    public PaymentGatewayStrategy getGatewayForCountry(String country) {
        String normalizedCountry = normalizeCountry(country);

        PaymentGatewayStrategy gateway = paymentGateways.stream()
                .filter(g -> g.supportsCountry(normalizedCountry))
                .findFirst()
                .orElse(null);

        if (gateway == null) {
            // Keep existing fallback behavior but log clearly when country is unmapped.
            gateway = paymentGateways.stream()
                    .filter(g -> "STRIPE".equals(g.getGatewayName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No payment gateway configured"));
            log.warn("No direct gateway mapping for country '{}', falling back to {}", normalizedCountry, gateway.getGatewayName());
        }

        log.info("Selected payment gateway: {} for country: {}", gateway.getGatewayName(), normalizedCountry);
        return gateway;
    }

    private String normalizeCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            return "USA";
        }

        String normalized = country.trim();
        if ("IN".equalsIgnoreCase(normalized) || "INDIA".equalsIgnoreCase(normalized)) {
            return "INDIA";
        }
        if ("US".equalsIgnoreCase(normalized) || "USA".equalsIgnoreCase(normalized) ||
                "UNITED STATES".equalsIgnoreCase(normalized) || "UNITED STATES OF AMERICA".equalsIgnoreCase(normalized)) {
            return "USA";
        }
        return normalized.toUpperCase();
    }

    /**
     * Gets a specific gateway by name.
     */
    public PaymentGatewayStrategy getGatewayByName(String gatewayName) {
        return paymentGateways.stream()
                .filter(g -> g.getGatewayName().equalsIgnoreCase(gatewayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Payment gateway not found: " + gatewayName));
    }
}
