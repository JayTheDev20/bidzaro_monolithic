package com.cateringmarketplace.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date and time operations.
 */
public final class DateUtil {

    private DateUtil() {
        // Private constructor to prevent instantiation
    }

    public static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");
    public static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    public static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    /**
     * Gets current instant.
     */
    public static Instant now() {
        return Instant.now();
    }

    /**
     * Gets current date in UTC.
     */
    public static LocalDate today() {
        return LocalDate.now(DEFAULT_ZONE);
    }

    /**
     * Gets current date-time in UTC.
     */
    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now(DEFAULT_ZONE);
    }

    /**
     * Converts instant to local date in UTC.
     */
    public static LocalDate toLocalDate(Instant instant) {
        return instant != null ? instant.atZone(DEFAULT_ZONE).toLocalDate() : null;
    }

    /**
     * Converts instant to local date-time in UTC.
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? instant.atZone(DEFAULT_ZONE).toLocalDateTime() : null;
    }

    /**
     * Converts local date to instant (start of day in UTC).
     */
    public static Instant toInstant(LocalDate date) {
        return date != null ? date.atStartOfDay(DEFAULT_ZONE).toInstant() : null;
    }

    /**
     * Converts local date-time to instant in UTC.
     */
    public static Instant toInstant(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.atZone(DEFAULT_ZONE).toInstant() : null;
    }

    /**
     * Parses date string to LocalDate.
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses date-time string to Instant.
     */
    public static Instant parseDateTime(String dateTimeStr) {
        try {
            return Instant.parse(dateTimeStr);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Formats instant to ISO date-time string.
     */
    public static String formatDateTime(Instant instant) {
        return instant != null ? DATETIME_FORMATTER.format(instant.atZone(DEFAULT_ZONE)) : null;
    }

    /**
     * Formats local date to string.
     */
    public static String formatDate(LocalDate date) {
        return date != null ? DATE_FORMATTER.format(date) : null;
    }

    /**
     * Formats instant to display format.
     */
    public static String formatForDisplay(Instant instant) {
        return instant != null ? DISPLAY_DATETIME_FORMATTER.format(instant.atZone(IST_ZONE)) : null;
    }

    /**
     * Gets days between two instants.
     */
    public static long daysBetween(Instant start, Instant end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Gets hours between two instants.
     */
    public static long hoursBetween(Instant start, Instant end) {
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Gets minutes between two instants.
     */
    public static long minutesBetween(Instant start, Instant end) {
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Adds days to instant.
     */
    public static Instant plusDays(Instant instant, long days) {
        return instant.plus(days, ChronoUnit.DAYS);
    }

    /**
     * Adds hours to instant.
     */
    public static Instant plusHours(Instant instant, long hours) {
        return instant.plus(hours, ChronoUnit.HOURS);
    }

    /**
     * Subtracts days from instant.
     */
    public static Instant minusDays(Instant instant, long days) {
        return instant.minus(days, ChronoUnit.DAYS);
    }

    /**
     * Checks if date is in the future.
     */
    public static boolean isFuture(Instant instant) {
        return instant != null && instant.isAfter(Instant.now());
    }

    /**
     * Checks if date is in the past.
     */
    public static boolean isPast(Instant instant) {
        return instant != null && instant.isBefore(Instant.now());
    }

    /**
     * Checks if local date is in the future.
     */
    public static boolean isFuture(LocalDate date) {
        return date != null && date.isAfter(today());
    }

    /**
     * Gets start of day for instant.
     */
    public static Instant startOfDay(Instant instant) {
        return instant.atZone(DEFAULT_ZONE).toLocalDate().atStartOfDay(DEFAULT_ZONE).toInstant();
    }

    /**
     * Gets end of day for instant.
     */
    public static Instant endOfDay(Instant instant) {
        return instant.atZone(DEFAULT_ZONE).toLocalDate().atTime(23, 59, 59, 999999999)
                .atZone(DEFAULT_ZONE).toInstant();
    }

    /**
     * Gets days remaining until target date.
     */
    public static long daysUntil(Instant target) {
        return daysBetween(Instant.now(), target);
    }

    /**
     * Gets days since past date.
     */
    public static long daysSince(Instant past) {
        return daysBetween(past, Instant.now());
    }

    /**
     * Checks if two instants are on the same day.
     */
    public static boolean isSameDay(Instant instant1, Instant instant2) {
        return toLocalDate(instant1).equals(toLocalDate(instant2));
    }
}

