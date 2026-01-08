package com.cateringmarketplace.common.constant;

/**
 * Centralized error messages for the application.
 */
public final class ErrorMessages {

    private ErrorMessages() {
        // Private constructor to prevent instantiation
    }

    // Authentication Errors
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    public static final String ACCOUNT_LOCKED = "Account is locked. Please try again later";
    public static final String ACCOUNT_SUSPENDED = "Account has been suspended";
    public static final String ACCOUNT_NOT_VERIFIED = "Please verify your email before logging in";
    public static final String TOKEN_EXPIRED = "Token has expired";
    public static final String TOKEN_INVALID = "Invalid token";
    public static final String REFRESH_TOKEN_EXPIRED = "Refresh token has expired. Please login again";
    public static final String UNAUTHORIZED_ACCESS = "You are not authorized to access this resource";

    // User Errors
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ALREADY_EXISTS = "User with this email already exists";
    public static final String PHONE_ALREADY_EXISTS = "User with this phone number already exists";
    public static final String EMAIL_ALREADY_VERIFIED = "Email is already verified";
    public static final String PHONE_ALREADY_VERIFIED = "Phone is already verified";
    public static final String INVALID_OLD_PASSWORD = "Current password is incorrect";
    public static final String PASSWORD_MISMATCH = "Passwords do not match";

    // Vendor Errors
    public static final String VENDOR_NOT_FOUND = "Vendor not found";
    public static final String VENDOR_NOT_APPROVED = "Vendor account is not yet approved";
    public static final String VENDOR_SUSPENDED = "Vendor account has been suspended";
    public static final String VENDOR_ALREADY_EXISTS = "Vendor with this business email already exists";
    public static final String VENDOR_REGISTRATION_PENDING = "Vendor registration is pending approval";

    // Menu Errors
    public static final String MENU_ITEM_NOT_FOUND = "Menu item not found";
    public static final String CATEGORY_NOT_FOUND = "Category not found";
    public static final String VENDOR_MENU_ITEM_NOT_FOUND = "Vendor menu item not found";
    public static final String ITEM_NOT_AVAILABLE = "This item is currently not available";
    public static final String ITEM_OUT_OF_STOCK = "This item is out of stock";

    // Cart Errors
    public static final String CART_ITEM_NOT_FOUND = "Cart item not found";
    public static final String CART_EMPTY = "Cart is empty";
    public static final String INVALID_QUANTITY = "Quantity must be at least 1";
    public static final String MAX_CART_ITEMS_EXCEEDED = "Maximum cart items limit exceeded";

    // Wishlist Errors
    public static final String WISHLIST_ITEM_NOT_FOUND = "Wishlist item not found";
    public static final String ITEM_ALREADY_IN_WISHLIST = "Item is already in wishlist";

    // Bid Errors
    public static final String BID_REQUEST_NOT_FOUND = "Bid request not found";
    public static final String BID_NOT_FOUND = "Bid not found";
    public static final String BID_ALREADY_SUBMITTED = "You have already submitted a bid for this request";
    public static final String BID_REVISION_LIMIT_EXCEEDED = "Maximum bid revision limit exceeded";
    public static final String BID_EXPIRED = "This bid has expired";
    public static final String BID_REQUEST_EXPIRED = "This bid request has expired";
    public static final String BID_REQUEST_CLOSED = "This bid request is no longer accepting bids";
    public static final String CANNOT_BID_OWN_REQUEST = "You cannot bid on your own request";
    public static final String EVENT_DATE_PAST = "Event date must be in the future";
    public static final String INVALID_GUEST_COUNT = "Guest count must be between 50 and 10000";

    // Order Errors
    public static final String ORDER_NOT_FOUND = "Order not found";
    public static final String ORDER_ALREADY_CANCELLED = "Order has already been cancelled";
    public static final String ORDER_CANNOT_BE_CANCELLED = "Order cannot be cancelled at this stage";
    public static final String ORDER_STATUS_UPDATE_NOT_ALLOWED = "Order status cannot be updated to this state";
    public static final String PAYMENT_REQUIRED = "Payment is required to proceed";
    public static final String ORDER_NOT_CONFIRMED = "Order is not yet confirmed";

    // Payment Errors
    public static final String PAYMENT_NOT_FOUND = "Payment not found";
    public static final String PAYMENT_FAILED = "Payment processing failed";
    public static final String PAYMENT_ALREADY_COMPLETED = "Payment has already been completed";
    public static final String INVALID_PAYMENT_AMOUNT = "Invalid payment amount";
    public static final String REFUND_NOT_ALLOWED = "Refund is not allowed for this order";
    public static final String REFUND_FAILED = "Refund processing failed";
    public static final String PAYMENT_VERIFICATION_FAILED = "Payment verification failed";

    // Chat Errors
    public static final String CONVERSATION_NOT_FOUND = "Conversation not found";
    public static final String MESSAGE_NOT_FOUND = "Message not found";
    public static final String NOT_CONVERSATION_PARTICIPANT = "You are not a participant in this conversation";

    // Review Errors
    public static final String REVIEW_NOT_FOUND = "Review not found";
    public static final String REVIEW_ALREADY_EXISTS = "You have already reviewed this order";
    public static final String CANNOT_REVIEW_OWN_VENDOR = "You cannot review your own vendor";
    public static final String ORDER_NOT_COMPLETED = "You can only review completed orders";
    public static final String REVIEW_EDIT_WINDOW_EXPIRED = "Review can only be edited within 24 hours";

    // Notification Errors
    public static final String NOTIFICATION_NOT_FOUND = "Notification not found";
    public static final String NOTIFICATION_SEND_FAILED = "Failed to send notification";

    // Support Errors
    public static final String TICKET_NOT_FOUND = "Support ticket not found";
    public static final String TICKET_ALREADY_CLOSED = "This ticket has already been closed";
    public static final String TICKET_ALREADY_ASSIGNED = "This ticket is already assigned to an agent";

    // File Upload Errors
    public static final String FILE_NOT_FOUND = "File not found";
    public static final String FILE_TOO_LARGE = "File size exceeds maximum allowed size";
    public static final String INVALID_FILE_TYPE = "Invalid file type";
    public static final String FILE_UPLOAD_FAILED = "File upload failed";
    public static final String FILE_DELETE_FAILED = "File deletion failed";

    // Validation Errors
    public static final String VALIDATION_ERROR = "Validation failed";
    public static final String INVALID_EMAIL_FORMAT = "Invalid email format";
    public static final String INVALID_PHONE_FORMAT = "Invalid phone number format";
    public static final String INVALID_PASSWORD_FORMAT = "Password must be at least 8 characters with uppercase, lowercase, number, and special character";
    public static final String FIELD_REQUIRED = "This field is required";
    public static final String INVALID_DATE_FORMAT = "Invalid date format";
    public static final String INVALID_UUID_FORMAT = "Invalid UUID format";

    // OTP Errors
    public static final String OTP_EXPIRED = "OTP has expired";
    public static final String OTP_INVALID = "Invalid OTP";
    public static final String OTP_MAX_ATTEMPTS_EXCEEDED = "Maximum OTP attempts exceeded. Please request a new OTP";
    public static final String OTP_RESEND_TOO_SOON = "Please wait before requesting a new OTP";

    // Rate Limit Errors
    public static final String RATE_LIMIT_EXCEEDED = "Too many requests. Please try again later";

    // General Errors
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later";
    public static final String BAD_REQUEST = "Bad request";
    public static final String RESOURCE_NOT_FOUND = "Requested resource not found";
    public static final String CONFLICT = "Resource conflict";
    public static final String SERVICE_UNAVAILABLE = "Service is temporarily unavailable";

    // Address Errors
    public static final String ADDRESS_NOT_FOUND = "Address not found";
    public static final String ADDRESS_LIMIT_EXCEEDED = "Maximum address limit exceeded";
    public static final String CANNOT_DELETE_DEFAULT_ADDRESS = "Cannot delete default address. Set another address as default first";

    // Document Errors
    public static final String DOCUMENT_NOT_FOUND = "Document not found";
    public static final String DOCUMENT_ALREADY_VERIFIED = "Document is already verified";
    public static final String DOCUMENT_VERIFICATION_PENDING = "Document verification is pending";
}

