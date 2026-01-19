# Customer API Documentation

This document lists all API endpoints available for **Customers** (Users), including authentication, browsing, ordering, and account management.

## Authentication (`/auth`)

### Register a new user
**POST** `/auth/register`
```json
{
  "email": "user@example.com",
  "phone": "+1234567890",
  "password": "Password@123",
  "firstName": "John",
  "lastName": "Doe",
  "country": "India",
  "userType": "USER",
  "fcmToken": "device_fcm_token",
  "deviceInfo": "Android 13, Pixel 7"
}
```
*User Types: USER, VENDOR, ADMIN, SUPPORT_AGENT*

**Response:**
```json
{
  "success": true,
  "message": "Registration successful. Please verify your email.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "userId": "user_12345",
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "USER"
    }
  }
}
```

### User login
**POST** `/auth/login`
```json
{
  "identifier": "user@example.com",
  "password": "Password@123",
  "fcmToken": "device_fcm_token",
  "deviceInfo": "Android 13, Pixel 7"
}
```
**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "userId": "user_12345",
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "USER"
    }
  }
}
```

### Forgot password
**POST** `/auth/forgot-password`
```json
{ "email": "user@example.com" }
```
**Response:**
```json
{
  "success": true,
  "message": "Password reset OTP sent to your email",
  "data": {
    "otpId": "otp_12345",
    "expirySeconds": 300
  }
}
```

### Reset password
**POST** `/auth/reset-password`
```json
{
  "email": "user@example.com",
  "otp": "123456",
  "newPassword": "NewPassword@123"
}
```
**Response:**
```json
{
  "success": true,
  "message": "Password reset successfully",
  "data": null
}
```

## User Profile (`/users`)

### Get profile
**GET** `/users/profile`
*Headers: Authorization: Bearer <token>*
**Response:**
```json
{
  "success": true,
  "data": {
    "userId": "user_123",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phone": "+1234567890",
    "profilePictureUrl": "http://...",
    "dateOfBirth": "1990-01-01",
    "gender": "MALE",
    "preferredLanguage": "en",
    "preferredCurrency": "USD",
    "country": "USA",
    "loyalty": {
      "pointsBalance": 100,
      "lifetimePoints": 500,
      "tier": "BRONZE"
    }
  }
}
```
*Genders: MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY*

### Update profile
**PUT** `/users/profile`
```json
{
  "firstName": "Johnny",
  "lastName": "Doe",
  "dateOfBirth": "1990-01-01",
  "gender": "MALE",
  "preferredLanguage": "en",
  "preferredCurrency": "USD",
  "country": "USA"
}
```
**Response:**
```json
{
  "success": true,
  "message": "Profile updated",
  "data": {
    "userId": "user_123",
    "firstName": "Johnny",
    "phone": "+1987654321"
  }
}
```

### Manage Addresses
*   **GET** `/users/addresses` - List addresses
*   **POST** `/users/addresses` - Add address
    ```json
    {
      "addressType": "HOME",
      "label": "Home",
      "fullName": "John Doe",
      "phone": "+1234567890",
      "streetAddress": "123 Main St",
      "apartment": "Apt 4B",
      "city": "New York",
      "state": "NY",
      "postalCode": "10001",
      "country": "USA",
      "landmark": "Near Central Park",
      "latitude": 40.7128,
      "longitude": -74.0060,
      "isDefault": true
    }
    ```
    **Response:**
    ```json
    {
      "success": true,
      "message": "Address added",
      "data": {
        "addressId": "addr_2",
        "label": "Home"
      }
    }
    ```
*   **PUT** `/users/addresses/{id}` - Update address
*   **DELETE** `/users/addresses/{id}` - Delete address

## Vendors & Menu (`/vendors`, `/menu`)

### Browse Vendors
*   **GET** `/vendors` - List all vendors
*   **GET** `/vendors/search?query=pizza` - Search vendors
*   **GET** `/vendors/{vendorId}` - Get vendor details

### Browse Menu
*   **GET** `/menu/categories` - List categories
*   **GET** `/menu/items` - List all items
*   **GET** `/menu/items/search?query=burger` - Search items
*   **GET** `/menu/vendor-items?vendorId={id}` - Get specific vendor's menu

## Bids (`/bids`)

### Create bid request
**POST** `/bids/requests`
```json
{
  "targetedVendors": ["vendor_1", "vendor_2"],
  "budget": {
    "currency": "USD",
    "estimatedBudget": 5000.00,
    "budgetRange": "4000-6000"
  },
  "additionalRequirements": {
    "serviceStaffNeeded": true,
    "numberOfStaff": 5,
    "decorationNeeded": false,
    "liveCounters": ["Pasta", "Tacos"],
    "specialInstructions": "No peanuts"
  },
  "menuItems": [
    {
      "vendorItemId": "item_1",
      "itemName": "Spring Rolls",
      "quantity": 50,
      "customizations": [
        { "optionName": "Sauce", "selectedChoice": "Spicy" }
      ]
    }
  ],
  "eventDetails": {
    "eventType": "Birthday Party",
    "eventName": "John's 30th",
    "eventDate": "2023-12-25T18:00:00",
    "eventStartTime": "18:00",
    "eventEndTime": "22:00",
    "numberOfGuests": 50,
    "venueAddress": {
      "streetAddress": "123 Main St",
      "city": "New York",
      "state": "NY",
      "postalCode": "10001",
      "country": "USA",
      "latitude": 40.7128,
      "longitude": -74.0060
    }
  }
}
```
**Response:**
```json
{
  "success": true,
  "message": "Bid request created successfully",
  "data": {
    "bidRequestId": "bid_req_123",
    "eventName": "John's 30th",
    "status": "OPEN",
    "createdAt": "2023-10-01T10:00:00"
  }
}
```
*Bid Request Statuses: DRAFT, ACTIVE, COMPETITIVE, COOLING, ACCEPTED, EXPIRED, CANCELLED*

### Manage Bids
*   **GET** `/bids/requests` - View my requests
*   **GET** `/bids/requests/{id}/bids` - View offers from vendors
*   **POST** `/bids/{bidId}/accept` - Accept a vendor's offer

## Cart & Orders (`/cart`, `/orders`)

### Cart Operations
*   **GET** `/cart` - View cart
*   **POST** `/cart/items` - Add item
    ```json
    { "vendorItemId": "item_123", "quantity": 2 }
    ```
*   **PUT** `/cart/items/{id}?quantity=3` - Update quantity
*   **DELETE** `/cart/items/{id}` - Remove item

### Order Operations
*   **POST** `/orders?bidRequestId={id}` - Create order from accepted bid
*   **GET** `/orders` - View order history
*   **GET** `/orders/{id}` - View order details
*   **POST** `/orders/{id}/cancel` - Cancel order

*Order Statuses: PENDING_TOKEN_PAYMENT, CONFIRMED, IN_PREPARATION, READY_FOR_DELIVERY, DELIVERING, DELIVERED, COMPLETED, CANCELLED*

## Payments (`/payments`)

### Initiate Payment
**POST** `/payments/initiate`
*Query Params: orderId, paymentType (CARD/UPI), amount*
**Response:**
```json
{
  "success": true,
  "message": "Payment initiated",
  "data": {
    "paymentId": "pay_123",
    "gatewayOrderId": "order_razorpay_123",
    "amount": 100.00,
    "currency": "INR"
  }
}
```

### View Transactions
**GET** `/payments/transactions`

## Wishlist (`/wishlist`)

*   **GET** `/wishlist` - View wishlist
*   **POST** `/wishlist/items?vendorItemId={id}` - Add to wishlist
*   **DELETE** `/wishlist/items/{id}` - Remove from wishlist

## Reviews (`/reviews`)

### Submit Review
**POST** `/reviews`
```json
{
  "orderId": "order_123",
  "rating": 5,
  "foodQualityRating": 5,
  "serviceQualityRating": 4,
  "hygieneRating": 5,
  "valueForMoneyRating": 4,
  "punctualityRating": 5,
  "reviewText": "Great food and service!",
  "images": ["http://image1.jpg", "http://image2.jpg"]
}
```
**Response:**
```json
{
  "success": true,
  "message": "Review submitted successfully",
  "data": {
    "reviewId": "rev_1",
    "rating": 5
  }
}
```

*   **GET** `/reviews/my` - View my reviews

## Chat (`/chat`)

*   **GET** `/chat/conversations` - List conversations
*   **POST** `/chat/conversations?otherUserId={vendorId}` - Start chat
*   **POST** `/chat/messages?conversationId={id}&content=Hello` - Send message
*   **GET** `/chat/conversations/{id}/messages` - Get messages

## Support (`/support`)

### Create Ticket
**POST** `/support/tickets`
```json
{
  "category": "ORDER_ISSUE",
  "subcategory": "LATE_DELIVERY",
  "priority": "HIGH",
  "subject": "Order Issue",
  "description": "Late delivery for order #123",
  "orderId": "order_123",
  "attachmentUrls": ["http://image.jpg"]
}
```
**Response:**
```json
{
  "success": true,
  "message": "Ticket created successfully",
  "data": {
    "ticketId": "ticket_1",
    "status": "OPEN"
  }
}
```
*Priorities: LOW, MEDIUM, HIGH, URGENT*

*   **GET** `/support/tickets` - View my tickets

## Loyalty & Referral (`/loyalty`, `/referral`)

*   **GET** `/loyalty/balance` - Check points
*   **GET** `/referral/code` - Get my referral code
