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
        if (country == null || country.isEmpty()) {
            country = "USA"; // Default to USA
        }

        String finalCountry = country;
        PaymentGatewayStrategy gateway = paymentGateways.stream()
                .filter(g -> g.supportsCountry(finalCountry))
                .findFirst()
                .orElse(null);

        if (gateway == null) {
            // Default to Stripe for USA
            gateway = paymentGateways.stream()
                    .filter(g -> "STRIPE".equals(g.getGatewayName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No payment gateway configured"));
        }

        log.info("Selected payment gateway: {} for country: {}", gateway.getGatewayName(), country);
        return gateway;
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

