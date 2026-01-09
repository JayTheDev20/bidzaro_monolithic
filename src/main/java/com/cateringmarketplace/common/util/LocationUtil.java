package com.cateringmarketplace.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Utility for location-based configuration.
 * Platform is USA-focused with USD currency.
 */
@Component
@Slf4j
public class LocationUtil {

    @Value("${app.default-country:USA}")
    private String defaultCountry;

    @Value("${app.default-currency:USD}")
    private String defaultCurrency;

    // US States
    private static final Set<String> US_STATES = Set.of(
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
            "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
            "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
    );

    // US Time Zones by State (simplified)
    private static final Map<String, String> STATE_TIMEZONES = Map.ofEntries(
            Map.entry("CA", "America/Los_Angeles"),
            Map.entry("NY", "America/New_York"),
            Map.entry("TX", "America/Chicago"),
            Map.entry("FL", "America/New_York"),
            Map.entry("IL", "America/Chicago"),
            Map.entry("WA", "America/Los_Angeles"),
            Map.entry("AZ", "America/Phoenix"),
            Map.entry("MA", "America/New_York"),
            Map.entry("CO", "America/Denver"),
            Map.entry("GA", "America/New_York")
    );

    // Major US Cities
    private static final Map<String, String> MAJOR_CITIES = Map.ofEntries(
            Map.entry("New York", "NY"),
            Map.entry("Los Angeles", "CA"),
            Map.entry("Chicago", "IL"),
            Map.entry("Houston", "TX"),
            Map.entry("Phoenix", "AZ"),
            Map.entry("Philadelphia", "PA"),
            Map.entry("San Antonio", "TX"),
            Map.entry("San Diego", "CA"),
            Map.entry("Dallas", "TX"),
            Map.entry("San Jose", "CA"),
            Map.entry("Austin", "TX"),
            Map.entry("Jacksonville", "FL"),
            Map.entry("San Francisco", "CA"),
            Map.entry("Columbus", "OH"),
            Map.entry("Charlotte", "NC"),
            Map.entry("Indianapolis", "IN"),
            Map.entry("Seattle", "WA"),
            Map.entry("Denver", "CO"),
            Map.entry("Boston", "MA"),
            Map.entry("Portland", "OR"),
            Map.entry("Las Vegas", "NV"),
            Map.entry("Miami", "FL"),
            Map.entry("Atlanta", "GA")
    );

    /**
     * Gets the default country for the platform.
     */
    public String getDefaultCountry() {
        return defaultCountry;
    }

    /**
     * Gets the default currency for the platform.
     */
    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    /**
     * Gets currency symbol for USD.
     */
    public String getCurrencySymbol() {
        return "$";
    }

    /**
     * Formats currency amount in USD.
     */
    public String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }

    /**
     * Validates if state code is valid US state.
     */
    public boolean isValidState(String stateCode) {
        if (stateCode == null) return false;
        return US_STATES.contains(stateCode.toUpperCase());
    }

    /**
     * Gets timezone for a state.
     */
    public String getTimezoneForState(String stateCode) {
        if (stateCode == null) return "America/New_York"; // Default EST
        return STATE_TIMEZONES.getOrDefault(stateCode.toUpperCase(), "America/New_York");
    }

    /**
     * Gets state code for a city.
     */
    public String getStateForCity(String city) {
        if (city == null) return null;
        return MAJOR_CITIES.get(city);
    }

    /**
     * Validates US phone number format.
     */
    public boolean isValidUSPhoneNumber(String phone) {
        if (phone == null) return false;
        // Remove all non-digits
        String digits = phone.replaceAll("[^0-9]", "");
        // US phone should have 10 digits (or 11 with country code 1)
        return digits.length() == 10 || (digits.length() == 11 && digits.startsWith("1"));
    }

    /**
     * Formats phone number to US format: (XXX) XXX-XXXX.
     */
    public String formatUSPhoneNumber(String phone) {
        if (phone == null) return null;
        String digits = phone.replaceAll("[^0-9]", "");

        // Remove country code if present
        if (digits.length() == 11 && digits.startsWith("1")) {
            digits = digits.substring(1);
        }

        if (digits.length() != 10) return phone; // Return as-is if invalid

        return String.format("(%s) %s-%s",
                digits.substring(0, 3),
                digits.substring(3, 6),
                digits.substring(6, 10));
    }

    /**
     * Validates US ZIP code format.
     */
    public boolean isValidZipCode(String zipCode) {
        if (zipCode == null) return false;
        // US ZIP: 5 digits or ZIP+4 format (12345-6789)
        return zipCode.matches("^\\d{5}(-\\d{4})?$");
    }

    /**
     * Gets location-based configuration.
     */
    public Map<String, Object> getLocationConfig(String state, String city) {
        Map<String, Object> config = new HashMap<>();

        config.put("country", defaultCountry);
        config.put("currency", defaultCurrency);
        config.put("currencySymbol", getCurrencySymbol());

        if (state != null && isValidState(state)) {
            config.put("state", state.toUpperCase());
            config.put("timezone", getTimezoneForState(state));
        }

        if (city != null) {
            config.put("city", city);
            String stateCode = getStateForCity(city);
            if (stateCode != null && !config.containsKey("state")) {
                config.put("state", stateCode);
                config.put("timezone", getTimezoneForState(stateCode));
            }
        }

        // Add tax rate based on state (simplified - you'd want a real tax service)
        if (config.containsKey("state")) {
            config.put("taxRate", getStateTaxRate((String) config.get("state")));
        }

        return config;
    }

    /**
     * Gets simplified state tax rate (for demonstration - use real tax service in production).
     */
    private double getStateTaxRate(String state) {
        return switch (state.toUpperCase()) {
            case "CA" -> 7.25;
            case "NY" -> 4.0;
            case "TX" -> 6.25;
            case "FL" -> 6.0;
            case "IL" -> 6.25;
            case "WA" -> 6.5;
            case "AZ" -> 5.6;
            case "MA" -> 6.25;
            case "CO" -> 2.9;
            case "GA" -> 4.0;
            default -> 5.0; // Average US sales tax
        };
    }

    /**
     * Gets all US states.
     */
    public Set<String> getAllStates() {
        return Collections.unmodifiableSet(US_STATES);
    }

    /**
     * Gets all major cities.
     */
    public Set<String> getMajorCities() {
        return Collections.unmodifiableSet(MAJOR_CITIES.keySet());
    }
}

