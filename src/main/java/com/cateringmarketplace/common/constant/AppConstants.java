package com.cateringmarketplace.common.constant;

/**
 * Application-wide constants.
 */
public final class AppConstants {

    private AppConstants() {
        // Private constructor to prevent instantiation
    }

    // API Versioning
    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api/" + API_VERSION;

    // Pagination Defaults
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // User Types
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_VENDOR = "ROLE_VENDOR";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_SUPPORT_AGENT = "ROLE_SUPPORT_AGENT";

    // User Status
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_SUSPENDED = "SUSPENDED";
    public static final String STATUS_DELETED = "DELETED";
    public static final String STATUS_PENDING = "PENDING";

    // Order Status
    public static final String ORDER_PENDING_TOKEN = "PENDING_TOKEN_PAYMENT";
    public static final String ORDER_CONFIRMED = "CONFIRMED";
    public static final String ORDER_IN_PREPARATION = "IN_PREPARATION";
    public static final String ORDER_READY = "READY_FOR_DELIVERY";
    public static final String ORDER_DELIVERING = "DELIVERING";
    public static final String ORDER_DELIVERED = "DELIVERED";
    public static final String ORDER_COMPLETED = "COMPLETED";
    public static final String ORDER_CANCELLED = "CANCELLED";

    // Bid Status
    public static final String BID_DRAFT = "DRAFT";
    public static final String BID_ACTIVE = "ACTIVE";
    public static final String BID_COMPETITIVE = "COMPETITIVE";
    public static final String BID_COOLING = "COOLING";
    public static final String BID_ACCEPTED = "ACCEPTED";
    public static final String BID_EXPIRED = "EXPIRED";
    public static final String BID_CANCELLED = "CANCELLED";

    // Payment Status
    public static final String PAYMENT_PENDING = "PENDING";
    public static final String PAYMENT_PROCESSING = "PROCESSING";
    public static final String PAYMENT_SUCCESS = "SUCCESS";
    public static final String PAYMENT_FAILED = "FAILED";
    public static final String PAYMENT_REFUNDED = "REFUNDED";

    // Notification Channels
    public static final String CHANNEL_EMAIL = "EMAIL";
    public static final String CHANNEL_SMS = "SMS";
    public static final String CHANNEL_PUSH = "PUSH";
    public static final String CHANNEL_WHATSAPP = "WHATSAPP";
    public static final String CHANNEL_IN_APP = "IN_APP";

    // Food Types
    public static final String FOOD_VEG = "VEG";
    public static final String FOOD_NON_VEG = "NON_VEG";
    public static final String FOOD_VEGAN = "VEGAN";
    public static final String FOOD_EGG = "EGG";

    // Cache Names
    public static final String CACHE_VENDORS = "vendors";
    public static final String CACHE_MENU_ITEMS = "menuItems";
    public static final String CACHE_CATEGORIES = "categories";
    public static final String CACHE_PLATFORM_CONFIG = "platformConfig";
    public static final String CACHE_USER_SESSIONS = "userSessions";
    public static final String CACHE_OTP = "otpCache";
    public static final String CACHE_RATE_LIMITS = "rateLimits";

    // File Upload
    public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final long MAX_DOCUMENT_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "webp"};
    public static final String[] ALLOWED_DOCUMENT_TYPES = {"pdf", "doc", "docx", "jpg", "jpeg", "png"};

    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String TIME_FORMAT = "HH:mm:ss";

    // JWT Claims
    public static final String JWT_CLAIM_USER_ID = "userId";
    public static final String JWT_CLAIM_EMAIL = "email";
    public static final String JWT_CLAIM_USER_TYPE = "userType";
    public static final String JWT_CLAIM_ROLES = "roles";

    // Headers
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_BEARER_PREFIX = "Bearer ";
    public static final String HEADER_X_TOTAL_COUNT = "X-Total-Count";
    public static final String HEADER_X_PAGE_NUMBER = "X-Page-Number";
    public static final String HEADER_X_PAGE_SIZE = "X-Page-Size";

    // Regex Patterns
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final String PHONE_REGEX = "^[+]?[0-9]{10,15}$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    // Bidding Constants
    public static final int DEFAULT_COMPETITIVE_PERIOD_HOURS = 72;
    public static final int DEFAULT_COOLING_PERIOD_HOURS = 24;
    public static final int MAX_BID_REVISIONS = 5;
    public static final int MIN_GUESTS = 50;
    public static final int MAX_GUESTS = 10000;

    // Payment Constants
    public static final double DEFAULT_TOKEN_PERCENTAGE = 25.0;
    public static final double DEFAULT_PLATFORM_FEE_PERCENTAGE = 2.0;
    public static final String DEFAULT_CURRENCY = "INR";

    // OTP Constants
    public static final int OTP_LENGTH = 6;
    public static final int OTP_EXPIRY_MINUTES = 10;
    public static final int MAX_OTP_ATTEMPTS = 5;
    public static final int OTP_RESEND_INTERVAL_SECONDS = 60;

    // Geolocation
    public static final double DEFAULT_SEARCH_RADIUS_KM = 50.0;
    public static final double MAX_SEARCH_RADIUS_KM = 200.0;
}

