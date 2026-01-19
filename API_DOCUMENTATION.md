# API Documentation

This document lists all API endpoints available in the project, including their HTTP methods, URLs, descriptions, and example payloads/responses.

## Authentication (`/auth`)

### Register a new user
**POST** `/auth/register`

**Description:** Creates a new user account.

**Request Body:**
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

**Description:** Authenticates user and returns access and refresh tokens.

**Request Body:**
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

### Refresh access token
**POST** `/auth/refresh-token`

**Description:** Generates new access and refresh tokens.

**Request Body:**
```json
{
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4..."
}
```

**Response:**
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "new_refresh_token...",
    "expiresIn": 3600
  }
}
```

### User logout
**POST** `/auth/logout`

**Description:** Invalidates the current refresh token.

**Headers:**
`Authorization: Bearer <access_token>`
`X-Refresh-Token: <refresh_token>`

**Response:**
```json
{
  "success": true,
  "message": "Logged out successfully",
  "data": null
}
```

### Logout from all devices
**POST** `/auth/logout-all`

**Description:** Invalidates all refresh tokens for the user.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Logged out from all devices successfully",
  "data": null
}
```

### Send OTP
**POST** `/auth/send-otp`

**Description:** Sends OTP to email or phone for verification.

**Request Body:**
```json
{
  "identifier": "user@example.com",
  "type": "EMAIL",
  "purpose": "VERIFICATION"
}
```

**Response:**
```json
{
  "success": true,
  "message": "OTP sent successfully",
  "data": {
    "otpId": "otp_12345",
    "expirySeconds": 300
  }
}
```

### Verify OTP
**POST** `/auth/verify-otp`

**Description:** Verifies the OTP sent to email or phone.

**Request Body:**
```json
{
  "identifier": "user@example.com",
  "otp": "123456",
  "otpId": "otp_12345"
}
```

**Response:**
```json
{
  "success": true,
  "message": "OTP verified successfully",
  "data": {
    "verified": true,
    "verificationToken": "token_xyz"
  }
}
```

### Forgot password
**POST** `/auth/forgot-password`

**Description:** Initiates password reset by sending OTP to email.

**Request Body:**
```json
{
  "email": "user@example.com"
}
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

**Description:** Resets password using OTP verification.

**Request Body:**
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

### Change password
**POST** `/auth/change-password`

**Description:** Changes password for authenticated user.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "currentPassword": "OldPassword@123",
  "newPassword": "NewPassword@123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Password changed successfully",
  "data": null
}
```

### Get current user
**GET** `/auth/me`

**Description:** Returns the current authenticated user's information.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": "user_12345",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "USER",
    "phone": "+1234567890",
    "profilePicture": "https://example.com/pic.jpg"
  }
}
```

## Credential Recovery (`/auth/recover`)

### Forgot email - Recover using phone
**POST** `/auth/recover/forgot-email`

**Description:** If user forgot email, they can enter phone number to receive email via WhatsApp.

**Query Parameters:**
`phone`: The user's phone number (e.g., `+1234567890`)

**Response:**
```json
{
  "success": true,
  "message": "Email sent to your WhatsApp",
  "data": {
    "status": "SENT"
  }
}
```

### Forgot phone - Recover using email
**POST** `/auth/recover/forgot-phone`

**Description:** If user forgot phone number, they can enter email to receive phone via email.

**Query Parameters:**
`email`: The user's email address

**Response:**
```json
{
  "success": true,
  "message": "Phone number sent to your email",
  "data": {
    "status": "SENT"
  }
}
```

## Bids (`/bids`)

### Create bid request
**POST** `/bids/requests`

**Description:** Creates a new bid request for catering services.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "eventName": "Birthday Party",
  "eventDate": "2023-12-25T18:00:00",
  "guestCount": 50,
  "budget": 5000.00,
  "location": {
    "address": "123 Main St",
    "city": "New York",
    "latitude": 40.7128,
    "longitude": -74.0060
  },
  "requirements": "Vegetarian options required",
  "cuisines": ["Italian", "Mexican"]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Bid request created successfully",
  "data": {
    "bidRequestId": "bid_req_123",
    "eventName": "Birthday Party",
    "status": "OPEN",
    "createdAt": "2023-10-01T10:00:00"
  }
}
```

### Get user's bid requests
**GET** `/bids/requests`

**Description:** Returns paginated list of user's bid requests.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`page`: Page number (default 0)
`size`: Page size (default 20)

**Response:**
```json
{
  "success": true,
  "message": "Bid requests retrieved",
  "data": [
    {
      "bidRequestId": "bid_req_123",
      "eventName": "Birthday Party",
      "status": "OPEN",
      "bidCount": 3
    }
  ],
  "pageInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### Get bid request
**GET** `/bids/requests/{bidRequestId}`

**Description:** Returns bid request details by ID.

**Response:**
```json
{
  "success": true,
  "data": {
    "bidRequestId": "bid_req_123",
    "eventName": "Birthday Party",
    "eventDate": "2023-12-25T18:00:00",
    "guestCount": 50,
    "budget": 5000.00,
    "status": "OPEN",
    "requirements": "Vegetarian options required"
  }
}
```

### Cancel bid request
**DELETE** `/bids/requests/{bidRequestId}`

**Description:** Cancels a bid request.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Bid request cancelled",
  "data": null
}
```

### Get bids for request
**GET** `/bids/requests/{bidRequestId}/bids`

**Description:** Returns all bids for a bid request (owner only).

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "bidId": "bid_456",
      "vendorName": "Tasty Catering",
      "amount": 4500.00,
      "description": "Full service catering",
      "status": "SUBMITTED"
    }
  ]
}
```

### Accept bid
**POST** `/bids/{bidId}/accept`

**Description:** Accepts a vendor's bid.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Bid accepted. Cooling period started.",
  "data": {
    "bidRequestId": "bid_req_123",
    "status": "ACCEPTED",
    "acceptedBidId": "bid_456"
  }
}
```

### Submit bid (Vendor only)
**POST** `/bids/requests/{bidRequestId}/submit-bid`

**Description:** Submits a bid for a bid request.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "amount": 4800.00,
  "description": "Premium package with dessert included",
  "validUntil": "2023-10-15T23:59:59"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Bid submitted successfully",
  "data": {
    "bidId": "bid_789",
    "amount": 4800.00,
    "status": "SUBMITTED"
  }
}
```

### Revise bid (Vendor only)
**PUT** `/bids/{bidId}`

**Description:** Revises an existing bid.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "amount": 4600.00,
  "description": "Revised price",
  "validUntil": "2023-10-15T23:59:59"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Bid revised successfully",
  "data": {
    "bidId": "bid_789",
    "amount": 4600.00,
    "status": "SUBMITTED"
  }
}
```

### Withdraw bid (Vendor only)
**DELETE** `/bids/{bidId}`

**Description:** Withdraws a submitted bid.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Bid withdrawn",
  "data": null
}
```

### Get vendor's bids (Vendor only)
**GET** `/bids/vendor/submitted`

**Description:** Returns all bids submitted by vendor.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Vendor bids retrieved",
  "data": [
    {
      "bidId": "bid_789",
      "eventName": "Birthday Party",
      "amount": 4600.00,
      "status": "SUBMITTED"
    }
  ]
}
```

### Get bid requests for vendor (Vendor only)
**GET** `/bids/vendor/received`

**Description:** Returns active bid requests vendor can bid on.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Bid requests retrieved",
  "data": [
    {
      "bidRequestId": "bid_req_123",
      "eventName": "Corporate Lunch",
      "budget": 2000.00
    }
  ]
}
```

### Get active bid requests (Vendor only)
**GET** `/bids/active`

**Description:** Returns all active bid requests.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Active bid requests retrieved",
  "data": [
    {
      "bidRequestId": "bid_req_123",
      "eventName": "Wedding Reception",
      "budget": 15000.00
    }
  ]
}
```

## Cart (`/cart`)

### Get cart
**GET** `/cart`

**Description:** Returns user's cart items.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "cartItemId": "cart_item_1",
      "vendorItemId": "item_123",
      "itemName": "Chicken Biryani",
      "price": 12.50,
      "quantity": 2,
      "totalPrice": 25.00
    }
  ]
}
```

### Get cart grouped by vendor
**GET** `/cart/grouped`

**Description:** Returns cart items grouped by vendor.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "vendor_1": [
      {
        "cartItemId": "cart_item_1",
        "itemName": "Chicken Biryani",
        "quantity": 2
      }
    ]
  }
}
```

### Add to cart
**POST** `/cart/items`

**Description:** Adds an item to the cart.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`vendorItemId`: ID of the item to add
`quantity`: Quantity (default 1)

**Response:**
```json
{
  "success": true,
  "message": "Item added to cart",
  "data": {
    "cartItemId": "cart_item_2",
    "vendorItemId": "item_456",
    "quantity": 1
  }
}
```

### Update cart item
**PUT** `/cart/items/{cartItemId}`

**Description:** Updates quantity of a cart item.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`quantity`: New quantity

**Response:**
```json
{
  "success": true,
  "message": "Cart item updated",
  "data": {
    "cartItemId": "cart_item_2",
    "quantity": 3
  }
}
```

### Remove from cart
**DELETE** `/cart/items/{cartItemId}`

**Description:** Removes an item from the cart.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Item removed from cart",
  "data": null
}
```

### Clear cart
**DELETE** `/cart`

**Description:** Clears the entire cart.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Cart cleared",
  "data": null
}
```

### Get cart count
**GET** `/cart/count`

**Description:** Returns number of items in cart.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "count": 5
  }
}
```

### Get cart total
**GET** `/cart/total`

**Description:** Returns total price of items in cart.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "total": 150.00
  }
}
```

## Chat (`/chat`)

### Get conversations
**GET** `/chat/conversations`

**Description:** Returns user's conversations.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Conversations retrieved",
  "data": [
    {
      "conversationId": "conv_123",
      "otherUser": {
        "userId": "vendor_1",
        "name": "Tasty Catering"
      },
      "lastMessage": "Hello, is this available?",
      "unreadCount": 2,
      "updatedAt": "2023-10-01T12:00:00"
    }
  ]
}
```

### Get conversation
**GET** `/chat/conversations/{conversationId}`

**Description:** Returns conversation details.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "conversationId": "conv_123",
    "participants": ["user_1", "vendor_1"],
    "createdAt": "2023-10-01T10:00:00"
  }
}
```

### Create conversation
**POST** `/chat/conversations`

**Description:** Creates or gets existing conversation with another user.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`otherUserId`: ID of the other user
`type`: Conversation type (default USER_VENDOR)

**Response:**
```json
{
  "success": true,
  "message": "Conversation ready",
  "data": {
    "conversationId": "conv_123",
    "type": "USER_VENDOR"
  }
}
```

### Get messages
**GET** `/chat/conversations/{conversationId}/messages`

**Description:** Returns messages in a conversation.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Messages retrieved",
  "data": [
    {
      "messageId": "msg_1",
      "senderId": "vendor_1",
      "content": "Yes, we are available.",
      "timestamp": "2023-10-01T12:05:00"
    }
  ]
}
```

### Send message
**POST** `/chat/messages`

**Description:** Sends a message in a conversation.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`conversationId`: ID of the conversation
`content`: Message content
`messageType`: Type (TEXT, IMAGE, etc.)

**Response:**
```json
{
  "success": true,
  "message": "Message sent",
  "data": {
    "messageId": "msg_2",
    "content": "Great, thanks!",
    "timestamp": "2023-10-01T12:06:00"
  }
}
```

### Mark as read
**PATCH** `/chat/conversations/{conversationId}/read`

**Description:** Marks all messages in conversation as read.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Marked as read",
  "data": null
}
```

### Delete message
**DELETE** `/chat/messages/{messageId}`

**Description:** Deletes a message (soft delete).

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Message deleted",
  "data": null
}
```

## Menu (`/menu`)

### Get all categories
**GET** `/menu/categories`

**Description:** Returns list of all active categories.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "categoryId": "cat_1",
      "name": "Appetizers",
      "imageUrl": "http://..."
    },
    {
      "categoryId": "cat_2",
      "name": "Main Course",
      "imageUrl": "http://..."
    }
  ]
}
```

### Get category by ID
**GET** `/menu/categories/{categoryId}`

**Description:** Returns category details.

**Response:**
```json
{
  "success": true,
  "data": {
    "categoryId": "cat_1",
    "name": "Appetizers",
    "description": "Starters and snacks"
  }
}
```

### Get all menu items
**GET** `/menu/items`

**Description:** Returns paginated list of menu items.

**Response:**
```json
{
  "success": true,
  "message": "Menu items retrieved",
  "data": [
    {
      "itemId": "item_1",
      "name": "Spring Rolls",
      "category": "Appetizers"
    }
  ]
}
```

### Get menu item by ID
**GET** `/menu/items/{itemId}`

**Description:** Returns menu item details.

**Response:**
```json
{
  "success": true,
  "data": {
    "itemId": "item_1",
    "name": "Spring Rolls",
    "description": "Crispy vegetable rolls",
    "imageUrl": "http://..."
  }
}
```

### Get menu items by category
**GET** `/menu/items/category/{categoryId}`

**Description:** Returns menu items for a category.

**Response:**
```json
{
  "success": true,
  "message": "Menu items retrieved",
  "data": [
    {
      "itemId": "item_1",
      "name": "Spring Rolls"
    }
  ]
}
```

### Search menu items
**GET** `/menu/items/search`

**Description:** Search menu items by name.

**Query Parameters:**
`query`: Search term

**Response:**
```json
{
  "success": true,
  "message": "Search results",
  "data": [
    {
      "itemId": "item_1",
      "name": "Spring Rolls"
    }
  ]
}
```

### Get popular items
**GET** `/menu/items/popular`

**Description:** Returns popular menu items.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "itemId": "item_1",
      "name": "Spring Rolls",
      "popularityScore": 95
    }
  ]
}
```

### Get vendor menu items
**GET** `/menu/vendor-items`

**Description:** Returns menu items for a vendor.

**Query Parameters:**
`vendorId`: ID of the vendor

**Response:**
```json
{
  "success": true,
  "message": "Vendor menu items retrieved",
  "data": [
    {
      "vendorItemId": "v_item_1",
      "name": "Spring Rolls",
      "price": 5.00,
      "isAvailable": true
    }
  ]
}
```

### Get vendor menu item by ID
**GET** `/menu/vendor-items/{vendorItemId}`

**Description:** Returns vendor menu item details.

**Response:**
```json
{
  "success": true,
  "data": {
    "vendorItemId": "v_item_1",
    "name": "Spring Rolls",
    "price": 5.00,
    "description": "Our special recipe"
  }
}
```

### Add item to vendor menu (Vendor only)
**POST** `/menu/vendor-items`

**Description:** Adds a master menu item to vendor's menu.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "masterItemId": "item_1",
  "price": 5.50,
  "description": "With spicy sauce",
  "isAvailable": true
}
```

**Response:**
```json
{
  "success": true,
  "message": "Item added to menu",
  "data": {
    "vendorItemId": "v_item_2",
    "name": "Spring Rolls",
    "price": 5.50
  }
}
```

### Update vendor menu item (Vendor only)
**PUT** `/menu/vendor-items/{vendorItemId}`

**Description:** Updates a vendor menu item.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "price": 6.00,
  "description": "New recipe",
  "isAvailable": true
}
```

**Response:**
```json
{
  "success": true,
  "message": "Item updated",
  "data": {
    "vendorItemId": "v_item_2",
    "price": 6.00
  }
}
```

### Update item availability (Vendor only)
**PATCH** `/menu/vendor-items/{vendorItemId}/availability`

**Description:** Updates availability of a vendor menu item.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`isAvailable`: true/false
`reason`: Optional reason

**Response:**
```json
{
  "success": true,
  "message": "Availability updated",
  "data": {
    "vendorItemId": "v_item_2",
    "isAvailable": false
  }
}
```

### Delete vendor menu item (Vendor only)
**DELETE** `/menu/vendor-items/{vendorItemId}`

**Description:** Removes an item from vendor's menu.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Item removed from menu",
  "data": null
}
```

## Users (`/users`)

### Get profile
**GET** `/users/profile`

**Description:** Returns current user's profile.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": "user_123",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phone": "+1234567890"
  }
}
```

### Update profile
**PUT** `/users/profile`

**Description:** Updates user profile.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "firstName": "Johnny",
  "lastName": "Doe",
  "phone": "+1987654321"
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

### Update profile picture
**PATCH** `/users/profile-picture`

**Description:** Updates user's profile picture.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`imageUrl`: URL of the new image

**Response:**
```json
{
  "success": true,
  "message": "Profile picture updated",
  "data": {
    "profilePicture": "http://new-image-url.com"
  }
}
```

### Get notification preferences
**GET** `/users/notification-preferences`

**Description:** Returns user's notification preferences.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "emailNotifications": true,
    "pushNotifications": true,
    "smsNotifications": false
  }
}
```

### Update notification preferences
**PUT** `/users/notification-preferences`

**Description:** Updates notification preferences.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "emailNotifications": false,
  "pushNotifications": true,
  "smsNotifications": true
}
```

**Response:**
```json
{
  "success": true,
  "message": "Preferences updated",
  "data": {
    "emailNotifications": false,
    "smsNotifications": true
  }
}
```

### Get addresses
**GET** `/users/addresses`

**Description:** Returns all addresses for current user.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "addressId": "addr_1",
      "label": "Home",
      "street": "123 Main St",
      "city": "New York",
      "isDefault": true
    }
  ]
}
```

### Get address
**GET** `/users/addresses/{addressId}`

**Description:** Returns address by ID.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "addressId": "addr_1",
    "label": "Home",
    "street": "123 Main St",
    "city": "New York",
    "zipCode": "10001"
  }
}
```

### Add address
**POST** `/users/addresses`

**Description:** Adds a new address.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "label": "Work",
  "street": "456 Office Blvd",
  "city": "New York",
  "zipCode": "10002",
  "country": "USA"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Address added",
  "data": {
    "addressId": "addr_2",
    "label": "Work"
  }
}
```

### Update address
**PUT** `/users/addresses/{addressId}`

**Description:** Updates an address.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "label": "Office",
  "street": "456 Office Blvd Suite 200"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Address updated",
  "data": {
    "addressId": "addr_2",
    "label": "Office"
  }
}
```

### Delete address
**DELETE** `/users/addresses/{addressId}`

**Description:** Deletes an address.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Address deleted",
  "data": null
}
```

### Set default address
**PATCH** `/users/addresses/{addressId}/default`

**Description:** Sets an address as default.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Default address set",
  "data": {
    "addressId": "addr_2",
    "isDefault": true
  }
}
```

## Admin (`/admin`)

### Get dashboard stats
**GET** `/admin/dashboard`

**Description:** Returns admin dashboard statistics.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "totalUsers": 1000,
    "totalVendors": 50,
    "totalOrders": 500,
    "totalRevenue": 25000.00
  }
}
```

### Get all users
**GET** `/admin/users`

**Description:** Returns paginated list of all users.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Users retrieved",
  "data": [
    {
      "userId": "user_1",
      "email": "user1@example.com",
      "status": "ACTIVE"
    }
  ]
}
```

### Update user status
**PATCH** `/admin/users/{userId}/status`

**Description:** Updates user status (activate/suspend).

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`status`: ACTIVE/SUSPENDED
`reason`: Optional reason

**Response:**
```json
{
  "success": true,
  "message": "User status updated",
  "data": {
    "userId": "user_1",
    "status": "SUSPENDED"
  }
}
```

### Get pending vendors
**GET** `/admin/vendors/pending`

**Description:** Returns vendors pending approval.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Pending vendors retrieved",
  "data": [
    {
      "vendorId": "vendor_new",
      "businessName": "New Catering Co",
      "status": "PENDING"
    }
  ]
}
```

### Approve vendor
**POST** `/admin/vendors/{vendorId}/approve`

**Description:** Approves a vendor registration.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Vendor approved successfully",
  "data": {
    "vendorId": "vendor_new",
    "status": "APPROVED"
  }
}
```

### Reject vendor
**POST** `/admin/vendors/{vendorId}/reject`

**Description:** Rejects a vendor registration with reason.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`reason`: Rejection reason

**Response:**
```json
{
  "success": true,
  "message": "Vendor rejected",
  "data": {
    "vendorId": "vendor_new",
    "status": "REJECTED"
  }
}
```

### Get all orders
**GET** `/admin/orders`

**Description:** Returns all platform orders.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Orders retrieved",
  "data": [
    {
      "orderId": "order_1",
      "amount": 100.00,
      "status": "COMPLETED"
    }
  ]
}
```

### Get platform config
**GET** `/admin/platform-config`

**Description:** Returns platform configuration.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`country`: Country code (default India)

**Response:**
```json
{
  "success": true,
  "data": {
    "commissionRate": 10.0,
    "taxRate": 5.0,
    "currency": "INR"
  }
}
```

### Update platform config
**PUT** `/admin/platform-config`

**Description:** Updates platform configuration.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "commissionRate": 12.0,
  "taxRate": 5.0
}
```

**Response:**
```json
{
  "success": true,
  "message": "Configuration updated",
  "data": {
    "commissionRate": 12.0
  }
}
```

### Get audit logs
**GET** `/admin/audit-logs`

**Description:** Returns audit logs.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Audit logs retrieved",
  "data": [
    {
      "logId": "log_1",
      "action": "USER_LOGIN",
      "timestamp": "2023-10-01T10:00:00"
    }
  ]
}
```

### Get announcements
**GET** `/admin/announcements`

**Description:** Returns all announcements.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Announcements retrieved",
  "data": [
    {
      "announcementId": "ann_1",
      "title": "Maintenance",
      "message": "System maintenance on Sunday"
    }
  ]
}
```

### Create announcement
**POST** `/admin/announcements`

**Description:** Creates a new platform announcement.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "title": "New Feature",
  "message": "Check out our new feature!",
  "targetAudience": "ALL"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Announcement created",
  "data": {
    "announcementId": "ann_2",
    "title": "New Feature"
  }
}
```

### Delete announcement
**DELETE** `/admin/announcements/{announcementId}`

**Description:** Deletes an announcement.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Announcement deleted",
  "data": null
}
```

## Orders (`/orders`)

### Create order from bid
**POST** `/orders`

**Description:** Creates an order from an accepted bid request.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`bidRequestId`: ID of the accepted bid request

**Response:**
```json
{
  "success": true,
  "message": "Order created. Please complete token payment.",
  "data": {
    "orderId": "order_123",
    "amount": 5000.00,
    "status": "PENDING_PAYMENT"
  }
}
```

### Get user's orders
**GET** `/orders`

**Description:** Returns paginated list of user's orders.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Orders retrieved",
  "data": [
    {
      "orderId": "order_123",
      "eventName": "Birthday Party",
      "status": "CONFIRMED"
    }
  ]
}
```

### Get order by ID
**GET** `/orders/{orderId}`

**Description:** Returns order details by ID.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "orderId": "order_123",
    "items": [],
    "totalAmount": 5000.00,
    "status": "CONFIRMED"
  }
}
```

### Update order status
**PATCH** `/orders/{orderId}/status`

**Description:** Updates the status of an order.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`status`: New status (e.g., COMPLETED)

**Response:**
```json
{
  "success": true,
  "message": "Order status updated",
  "data": {
    "orderId": "order_123",
    "status": "COMPLETED"
  }
}
```

### Cancel order
**POST** `/orders/{orderId}/cancel`

**Description:** Cancels an order.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`reason`: Cancellation reason

**Response:**
```json
{
  "success": true,
  "message": "Order cancelled",
  "data": {
    "orderId": "order_123",
    "status": "CANCELLED"
  }
}
```

### Get upcoming orders
**GET** `/orders/upcoming`

**Description:** Returns user's upcoming orders.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Upcoming orders retrieved",
  "data": [
    {
      "orderId": "order_124",
      "eventDate": "2023-12-31T20:00:00"
    }
  ]
}
```

### Get order history
**GET** `/orders/history`

**Description:** Returns user's completed/cancelled orders.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Order history retrieved",
  "data": [
    {
      "orderId": "order_100",
      "status": "COMPLETED"
    }
  ]
}
```

### Get vendor's orders (Vendor only)
**GET** `/orders/vendor`

**Description:** Returns orders assigned to vendor.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Vendor orders retrieved",
  "data": [
    {
      "orderId": "order_123",
      "customerName": "John Doe",
      "status": "CONFIRMED"
    }
  ]
}
```

## Promo Codes (`/promos`)

### Create promo code (Admin only)
**POST** `/promos`

**Description:** Creates a new promo code.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "code": "WELCOME10",
  "discountType": "PERCENTAGE",
  "discountValue": 10.0,
  "maxDiscount": 500.0,
  "minOrderAmount": 1000.0,
  "validFrom": "2023-01-01T00:00:00",
  "validUntil": "2023-12-31T23:59:59"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Promo code created",
  "data": {
    "promoCodeId": "promo_1",
    "code": "WELCOME10"
  }
}
```

### Update promo code (Admin only)
**PUT** `/promos/{promoCodeId}`

**Description:** Updates a promo code.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "validUntil": "2024-12-31T23:59:59"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Promo code updated",
  "data": {
    "promoCodeId": "promo_1",
    "validUntil": "2024-12-31T23:59:59"
  }
}
```

### Get all promo codes (Admin only)
**GET** `/promos`

**Description:** Returns all promo codes.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Promo codes retrieved",
  "data": [
    {
      "code": "WELCOME10",
      "isActive": true
    }
  ]
}
```

### Get promo code (Admin only)
**GET** `/promos/{promoCodeId}`

**Description:** Returns promo code details.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "code": "WELCOME10",
    "usageCount": 50
  }
}
```

### Deactivate promo code (Admin only)
**DELETE** `/promos/{promoCodeId}`

**Description:** Deactivates a promo code.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Promo code deactivated",
  "data": null
}
```

### Apply promo code
**POST** `/promos/apply`

**Description:** Validates and applies a promo code to an order.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "code": "WELCOME10",
  "orderAmount": 2000.00
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "code": "WELCOME10",
    "discountAmount": 200.00,
    "finalAmount": 1800.00
  }
}
```

## Reviews (`/reviews`)

### Create review
**POST** `/reviews`

**Description:** Creates a review for a completed order.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "orderId": "order_123",
  "rating": 5,
  "comment": "Excellent service!",
  "images": ["http://..."]
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

### Get review
**GET** `/reviews/{reviewId}`

**Description:** Returns review details by ID.

**Response:**
```json
{
  "success": true,
  "data": {
    "reviewId": "rev_1",
    "rating": 5,
    "comment": "Excellent service!"
  }
}
```

### Get vendor reviews
**GET** `/reviews/vendor/{vendorId}`

**Description:** Returns reviews for a vendor.

**Response:**
```json
{
  "success": true,
  "message": "Reviews retrieved",
  "data": [
    {
      "reviewId": "rev_1",
      "rating": 5,
      "userName": "John"
    }
  ]
}
```

### Get my reviews
**GET** `/reviews/my`

**Description:** Returns reviews submitted by current user.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Reviews retrieved",
  "data": [
    {
      "reviewId": "rev_1",
      "vendorName": "Tasty Catering"
    }
  ]
}
```

### Add vendor response (Vendor only)
**POST** `/reviews/{reviewId}/vendor-response`

**Description:** Adds vendor response to a review.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`responseText`: The response message

**Response:**
```json
{
  "success": true,
  "message": "Response added",
  "data": {
    "reviewId": "rev_1",
    "vendorResponse": "Thank you!"
  }
}
```

### Mark as helpful
**POST** `/reviews/{reviewId}/helpful`

**Description:** Marks a review as helpful.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Marked as helpful",
  "data": {
    "reviewId": "rev_1",
    "helpfulCount": 1
  }
}
```

### Report review
**POST** `/reviews/{reviewId}/report`

**Description:** Reports a review for moderation.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`reason`: Reason for reporting

**Response:**
```json
{
  "success": true,
  "message": "Review reported",
  "data": null
}
```

## File Upload (`/upload`)

### Upload image
**POST** `/upload/image`

**Description:** Uploads an image file (JPEG, PNG, GIF, WebP - max 5MB).

**Headers:**
`Authorization: Bearer <access_token>`
`Content-Type: multipart/form-data`

**Form Data:**
`file`: The image file
`entityType`: Optional (e.g., USER, VENDOR)
`entityId`: Optional

**Response:**
```json
{
  "success": true,
  "message": "Image uploaded successfully",
  "data": {
    "fileId": "file_123",
    "url": "https://storage.com/image.jpg",
    "fileType": "image/jpeg"
  }
}
```

### Upload document
**POST** `/upload/document`

**Description:** Uploads a document file (PDF, DOC, DOCX - max 10MB).

**Headers:**
`Authorization: Bearer <access_token>`
`Content-Type: multipart/form-data`

**Form Data:**
`file`: The document file

**Response:**
```json
{
  "success": true,
  "message": "Document uploaded successfully",
  "data": {
    "fileId": "file_124",
    "url": "https://storage.com/doc.pdf",
    "fileType": "application/pdf"
  }
}
```

### Get file info
**GET** `/upload/{fileId}`

**Description:** Returns file information by ID.

**Response:**
```json
{
  "success": true,
  "data": {
    "fileId": "file_123",
    "url": "https://storage.com/image.jpg",
    "size": 102400
  }
}
```

### Get files for entity
**GET** `/upload/entity/{entityType}/{entityId}`

**Description:** Returns all files for a specific entity.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "fileId": "file_123",
      "url": "https://storage.com/image.jpg"
    }
  ]
}
```

### Delete file
**DELETE** `/upload/{fileId}`

**Description:** Deletes an uploaded file.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "File deleted successfully",
  "data": null
}
```

## Vendors (`/vendors`)

### Register as vendor
**POST** `/vendors`

**Description:** Creates a new vendor profile for the authenticated user.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "businessName": "Tasty Catering",
  "description": "Best catering in town",
  "address": "123 Food St",
  "city": "New York",
  "cuisines": ["Italian", "French"],
  "contactEmail": "contact@tasty.com",
  "contactPhone": "+1234567890"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Vendor registered successfully. Pending approval.",
  "data": {
    "vendorId": "vendor_1",
    "businessName": "Tasty Catering",
    "status": "PENDING"
  }
}
```

### Get all vendors
**GET** `/vendors`

**Description:** Returns a paginated list of active vendors.

**Response:**
```json
{
  "success": true,
  "message": "Vendors retrieved successfully",
  "data": [
    {
      "vendorId": "vendor_1",
      "businessName": "Tasty Catering",
      "rating": 4.5
    }
  ]
}
```

### Get vendor by ID
**GET** `/vendors/{vendorId}`

**Description:** Returns vendor details by vendor ID.

**Response:**
```json
{
  "success": true,
  "data": {
    "vendorId": "vendor_1",
    "businessName": "Tasty Catering",
    "description": "Best catering in town",
    "rating": 4.5,
    "reviewCount": 20
  }
}
```

### Get my vendor profile
**GET** `/vendors/me`

**Description:** Returns the vendor profile of the authenticated user.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "vendorId": "vendor_1",
    "businessName": "Tasty Catering",
    "status": "APPROVED"
  }
}
```

### Update vendor profile
**PUT** `/vendors/{vendorId}`

**Description:** Updates the vendor profile.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "description": "Updated description",
  "cuisines": ["Italian", "French", "Asian"]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Vendor updated successfully",
  "data": {
    "vendorId": "vendor_1",
    "description": "Updated description"
  }
}
```

### Search vendors
**GET** `/vendors/search`

**Description:** Search vendors by name, city, cuisine, etc.

**Query Parameters:**
`query`: Search term
`city`: City filter
`cuisines`: List of cuisines

**Response:**
```json
{
  "success": true,
  "message": "Search results",
  "data": [
    {
      "vendorId": "vendor_1",
      "businessName": "Tasty Catering"
    }
  ]
}
```

### Get pending vendors (Admin only)
**GET** `/vendors/admin/pending`

**Description:** Returns vendors pending approval.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Pending vendors retrieved",
  "data": [
    {
      "vendorId": "vendor_new",
      "businessName": "New Catering"
    }
  ]
}
```

### Approve vendor (Admin only)
**POST** `/vendors/{vendorId}/approve`

**Description:** Approves a vendor registration.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Vendor approved successfully",
  "data": {
    "vendorId": "vendor_new",
    "status": "APPROVED"
  }
}
```

### Reject vendor (Admin only)
**POST** `/vendors/{vendorId}/reject`

**Description:** Rejects a vendor registration.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`reason`: Rejection reason

**Response:**
```json
{
  "success": true,
  "message": "Vendor rejected",
  "data": {
    "vendorId": "vendor_new",
    "status": "REJECTED"
  }
}
```

## Loyalty (`/loyalty`)

### Get loyalty balance
**GET** `/loyalty/balance`

**Description:** Returns current user's loyalty points balance and tier.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "points": 500,
    "tier": "GOLD",
    "value": 50.00
  }
}
```

### Get loyalty transactions
**GET** `/loyalty/transactions`

**Description:** Returns loyalty points transaction history.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Transactions retrieved",
  "data": [
    {
      "transactionId": "ltx_1",
      "points": 50,
      "type": "EARNED",
      "description": "Order #123"
    }
  ]
}
```

### Calculate potential points
**GET** `/loyalty/calculate`

**Description:** Calculates points that would be earned for an order.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`orderAmount`: Amount of the order

**Response:**
```json
{
  "success": true,
  "data": {
    "orderAmount": 100.00,
    "potentialPoints": 10
  }
}
```

## Payments (`/payments`)

### Initiate payment
**POST** `/payments/initiate`

**Description:** Initiates a payment for an order.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`orderId`: Order ID
`paymentType`: CARD, UPI, etc.
`amount`: Amount to pay

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

### Verify payment
**POST** `/payments/verify`

**Description:** Verifies payment after completion.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`gatewayOrderId`: Gateway order ID
`gatewayPaymentId`: Gateway payment ID
`signature`: Payment signature

**Response:**
```json
{
  "success": true,
  "message": "Payment verified successfully",
  "data": {
    "transactionId": "tx_123",
    "status": "SUCCESS"
  }
}
```

### Get user transactions
**GET** `/payments/transactions`

**Description:** Returns user's transaction history.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Transactions retrieved",
  "data": [
    {
      "transactionId": "tx_123",
      "amount": 100.00,
      "status": "SUCCESS"
    }
  ]
}
```

### Get transaction
**GET** `/payments/transactions/{transactionId}`

**Description:** Returns transaction details by ID.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "transactionId": "tx_123",
    "amount": 100.00,
    "status": "SUCCESS",
    "paymentMethod": "CARD"
  }
}
```

### Get order transactions
**GET** `/payments/order/{orderId}`

**Description:** Returns transactions for an order.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "transactionId": "tx_123",
      "amount": 100.00,
      "status": "SUCCESS"
    }
  ]
}
```

### Razorpay webhook
**POST** `/payments/webhook/razorpay`

**Description:** Handles Razorpay payment webhooks (India).

**Headers:**
`X-Razorpay-Signature: <signature>`

**Response:**
`OK`

### Stripe webhook
**POST** `/payments/webhook/stripe`

**Description:** Handles Stripe payment webhooks (USA).

**Headers:**
`Stripe-Signature: <signature>`

**Response:**
`OK`

## Support (`/support`)

### Create ticket
**POST** `/support/tickets`

**Description:** Creates a new support ticket.

**Headers:**
`Authorization: Bearer <access_token>`

**Request Body:**
```json
{
  "subject": "Issue with order",
  "description": "I haven't received my order yet.",
  "category": "ORDER_ISSUE",
  "priority": "HIGH",
  "relatedEntityId": "order_123"
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

### Get my tickets
**GET** `/support/tickets`

**Description:** Returns user's support tickets.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Tickets retrieved",
  "data": [
    {
      "ticketId": "ticket_1",
      "subject": "Issue with order",
      "status": "OPEN"
    }
  ]
}
```

### Get ticket
**GET** `/support/tickets/{ticketId}`

**Description:** Returns ticket details.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "ticketId": "ticket_1",
    "subject": "Issue with order",
    "description": "I haven't received my order yet.",
    "status": "OPEN",
    "responses": []
  }
}
```

### Get all open tickets (Support only)
**GET** `/support/admin/tickets`

**Description:** Returns all open tickets.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Open tickets retrieved",
  "data": [
    {
      "ticketId": "ticket_1",
      "subject": "Issue with order"
    }
  ]
}
```

### Get assigned tickets (Support only)
**GET** `/support/admin/my-tickets`

**Description:** Returns tickets assigned to current agent.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Assigned tickets retrieved",
  "data": [
    {
      "ticketId": "ticket_2",
      "subject": "Payment issue"
    }
  ]
}
```

### Assign ticket (Support only)
**POST** `/support/admin/tickets/{ticketId}/assign`

**Description:** Assigns a ticket to an agent.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`agentId`: ID of the agent

**Response:**
```json
{
  "success": true,
  "message": "Ticket assigned",
  "data": {
    "ticketId": "ticket_1",
    "assignedTo": "agent_1"
  }
}
```

### Update status (Support only)
**PATCH** `/support/admin/tickets/{ticketId}/status`

**Description:** Updates ticket status.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`status`: New status (e.g., IN_PROGRESS)

**Response:**
```json
{
  "success": true,
  "message": "Status updated",
  "data": {
    "ticketId": "ticket_1",
    "status": "IN_PROGRESS"
  }
}
```

### Resolve ticket (Support only)
**POST** `/support/admin/tickets/{ticketId}/resolve`

**Description:** Resolves a ticket.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`resolutionNotes`: Notes on resolution

**Response:**
```json
{
  "success": true,
  "message": "Ticket resolved",
  "data": {
    "ticketId": "ticket_1",
    "status": "RESOLVED"
  }
}
```

### Rate ticket
**POST** `/support/tickets/{ticketId}/rate`

**Description:** Rates the support experience.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`rating`: 1-5
`feedback`: Optional feedback

**Response:**
```json
{
  "success": true,
  "message": "Thank you for your feedback",
  "data": {
    "ticketId": "ticket_1",
    "rating": 5
  }
}
```

## Referral (`/referral`)

### Get referral code
**GET** `/referral/code`

**Description:** Gets or creates referral code for current user.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "code": "JOHN123",
    "shareUrl": "https://app.com/ref/JOHN123"
  }
}
```

### Get referral stats
**GET** `/referral/stats`

**Description:** Gets referral statistics for current user.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "totalReferrals": 5,
    "earnedPoints": 250
  }
}
```

### Validate referral code
**GET** `/referral/validate/{code}`

**Description:** Checks if a referral code is valid.

**Response:**
```json
{
  "success": true,
  "data": {
    "valid": true
  }
}
```

## Wishlist (`/wishlist`)

### Get wishlist
**GET** `/wishlist`

**Description:** Returns user's wishlist items.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Wishlist retrieved",
  "data": [
    {
      "wishlistItemId": "wl_1",
      "vendorItem": {
        "name": "Spring Rolls"
      }
    }
  ]
}
```

### Add to wishlist
**POST** `/wishlist/items`

**Description:** Adds an item to the wishlist.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`vendorItemId`: ID of the item

**Response:**
```json
{
  "success": true,
  "message": "Item added to wishlist",
  "data": {
    "wishlistItemId": "wl_2"
  }
}
```

### Remove from wishlist
**DELETE** `/wishlist/items/{wishlistItemId}`

**Description:** Removes an item from the wishlist.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Item removed from wishlist",
  "data": null
}
```

### Clear wishlist
**DELETE** `/wishlist`

**Description:** Clears the entire wishlist.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Wishlist cleared",
  "data": null
}
```

### Check if in wishlist
**GET** `/wishlist/check/{vendorItemId}`

**Description:** Checks if an item is in the wishlist.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "inWishlist": true
  }
}
```

### Get wishlist count
**GET** `/wishlist/count`

**Description:** Returns number of items in wishlist.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "count": 5
  }
}
```

## Analytics (`/analytics`)

### Get platform overview (Admin only)
**GET** `/analytics/overview`

**Description:** Returns platform-wide analytics overview.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "totalRevenue": 50000.00,
    "activeUsers": 1200,
    "activeVendors": 45
  }
}
```

### Get revenue analytics (Admin only)
**GET** `/analytics/revenue`

**Description:** Returns revenue analytics.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`period`: month/year/week

**Response:**
```json
{
  "success": true,
  "data": {
    "labels": ["Jan", "Feb", "Mar"],
    "data": [10000, 15000, 12000]
  }
}
```

### Get user analytics (Admin only)
**GET** `/analytics/users`

**Description:** Returns user analytics.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "newUsers": 50,
    "totalUsers": 1200
  }
}
```

### Get vendor analytics (Admin only)
**GET** `/analytics/vendors`

**Description:** Returns vendor analytics.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "topVendors": []
  }
}
```

### Get order analytics (Admin only)
**GET** `/analytics/orders`

**Description:** Returns order analytics.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "totalOrders": 500,
    "completedOrders": 450
  }
}
```

### Generate report (Admin only)
**GET** `/analytics/reports`

**Description:** Generates custom analytics report.

**Headers:**
`Authorization: Bearer <access_token>`

**Query Parameters:**
`reportType`: SALES, USERS, etc.
`startDate`: YYYY-MM-DD
`endDate`: YYYY-MM-DD

**Response:**
```json
{
  "success": true,
  "data": {
    "reportUrl": "https://storage.com/report.pdf"
  }
}
```

### Get vendor dashboard (Vendor only)
**GET** `/analytics/vendor/dashboard`

**Description:** Returns vendor dashboard analytics.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "totalSales": 5000.00,
    "totalOrders": 50,
    "averageRating": 4.5
  }
}
```

## Notifications (`/notifications`)

### Get notifications
**GET** `/notifications`

**Description:** Returns user's notifications.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Notifications retrieved",
  "data": [
    {
      "notificationId": "notif_1",
      "title": "Order Update",
      "message": "Your order has been confirmed",
      "isRead": false
    }
  ]
}
```

### Get unread notifications
**GET** `/notifications/unread`

**Description:** Returns user's unread notifications.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Unread notifications retrieved",
  "data": [
    {
      "notificationId": "notif_1",
      "title": "Order Update",
      "isRead": false
    }
  ]
}
```

### Get unread count
**GET** `/notifications/count`

**Description:** Returns count of unread notifications.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "unreadCount": 5
  }
}
```

### Mark as read
**PATCH** `/notifications/{notificationId}/read`

**Description:** Marks a notification as read.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "Marked as read",
  "data": null
}
```

### Mark all as read
**PATCH** `/notifications/read-all`

**Description:** Marks all notifications as read.

**Headers:**
`Authorization: Bearer <access_token>`

**Response:**
```json
{
  "success": true,
  "message": "All notifications marked as read",
  "data": null
}
```
