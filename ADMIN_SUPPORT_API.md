# Admin & Support Module API Documentation

This document outlines the API endpoints for Admin management and Support ticket operations.

## Base URL
- Admin: `/admin`
- Support: `/support`

## Authentication
All endpoints require a Bearer Token. Admin endpoints require `ROLE_ADMIN`. Support agent endpoints require `ROLE_ADMIN` or `ROLE_SUPPORT_AGENT`.

---

# Admin Module

## 1. Dashboard & Analytics

### Get Dashboard Stats
Retrieves high-level statistics for the admin dashboard.

- **Endpoint:** `GET /admin/dashboard`
- **Description:** Returns admin dashboard statistics.
- **Response:** `ApiResponse<DashboardStatsResponse>`

## 2. User Management

### Get All Users
Retrieves a paginated list of all registered users.

- **Endpoint:** `GET /admin/users`
- **Description:** Returns paginated list of all users.
- **Query Parameters:**
  - `page`, `size`, `sortBy`, `sortDir`
  - `status` (String): Filter by user status.
  - `userType` (String): Filter by user role.
- **Response:** `ApiResponse<List<UserResponse>>`

### Update User Status
Activates or suspends a user account.

- **Endpoint:** `PATCH /admin/users/{userId}/status`
- **Description:** Updates user status (activate/suspend).
- **Path Parameters:**
  - `userId` (String): The ID of the user.
- **Query Parameters:**
  - `status` (String): New status (e.g., ACTIVE, SUSPENDED).
  - `reason` (String): Reason for status change.
- **Response:** `ApiResponse<UserResponse>`

## 3. Vendor Management (Admin View)

### Get Pending Vendors
Retrieves vendors awaiting approval.

- **Endpoint:** `GET /admin/vendors/pending`
- **Description:** Returns vendors pending approval.
- **Response:** `ApiResponse<List<VendorResponse>>`

### Approve Vendor
Approves a vendor.

- **Endpoint:** `POST /admin/vendors/{vendorId}/approve`
- **Description:** Approves a vendor registration.
- **Response:** `ApiResponse<VendorResponse>`

### Reject Vendor
Rejects a vendor.

- **Endpoint:** `POST /admin/vendors/{vendorId}/reject`
- **Description:** Rejects a vendor registration with a reason.
- **Query Parameters:**
  - `reason` (String): Reason for rejection.
- **Response:** `ApiResponse<VendorResponse>`

## 4. Menu Management (Admin)

### Create Category
Creates a new menu category.

- **Endpoint:** `POST /admin/categories`
- **Description:** Creates a new menu category.
- **Request Body:** `CategoryRequest`
  ```json
  {
    "categoryName": "Appetizers",
    "description": "Starters and snacks",
    "imageUrl": "http://example.com/image.jpg"
  }
  ```
- **Response:** `ApiResponse<CategoryResponse>`

### Update Category
Updates an existing menu category.

- **Endpoint:** `PUT /admin/categories/{categoryId}`
- **Description:** Updates an existing category.
- **Path Parameters:**
  - `categoryId` (String): The ID of the category.
- **Request Body:** `CategoryRequest`
- **Response:** `ApiResponse<CategoryResponse>`

### Create Master Menu Item
Creates a new master menu item.

- **Endpoint:** `POST /admin/menu-items`
- **Description:** Creates a new master menu item.
- **Request Body:** `MasterMenuItemRequest`
  ```json
  {
    "name": "Chicken Biryani",
    "description": "Classic Hyderabadi Biryani",
    "categoryId": "category-id-123",
    "basePrice": 250.00,
    "isVeg": false,
    "imageUrl": "http://example.com/biryani.jpg"
  }
  ```
- **Response:** `ApiResponse<MenuItemResponse>`

### Update Master Menu Item
Updates an existing master menu item.

- **Endpoint:** `PUT /admin/menu-items/{itemId}`
- **Description:** Updates an existing master menu item.
- **Path Parameters:**
  - `itemId` (String): The ID of the menu item.
- **Request Body:** `MasterMenuItemRequest`
- **Response:** `ApiResponse<MenuItemResponse>`

## 5. Order Management

### Get All Orders
Retrieves all orders across the platform.

- **Endpoint:** `GET /admin/orders`
- **Description:** Returns all platform orders.
- **Query Parameters:**
  - `page`, `size`, `sortBy`, `sortDir`
  - `status` (String): Filter by order status.
- **Response:** `ApiResponse<List<OrderResponse>>`

## 6. Platform Configuration

### Get Platform Config
Retrieves global platform settings.

- **Endpoint:** `GET /admin/platform-config`
- **Query Parameters:**
  - `country` (String): Default "India".
- **Response:** `ApiResponse<PlatformConfig>`

### Update Platform Config
Updates global platform settings.

- **Endpoint:** `PUT /admin/platform-config`
- **Request Body:** `UpdatePlatformConfigRequest`
- **Response:** `ApiResponse<PlatformConfig>`

## 7. Audit Logs & Announcements

### Get Audit Logs
Retrieves system audit logs.

- **Endpoint:** `GET /admin/audit-logs`
- **Query Parameters:**
  - `entityType` (String): Filter by entity type.
  - `action` (String): Filter by action type.
- **Response:** `ApiResponse<List<AuditLog>>`

### Get Announcements
Retrieves all platform announcements.

- **Endpoint:** `GET /admin/announcements`
- **Response:** `ApiResponse<List<Announcement>>`

### Create Announcement
Creates a new announcement.

- **Endpoint:** `POST /admin/announcements`
- **Request Body:** `CreateAnnouncementRequest`
- **Response:** `ApiResponse<Announcement>`

### Delete Announcement
Deletes an announcement.

- **Endpoint:** `DELETE /admin/announcements/{announcementId}`
- **Response:** `ApiResponse<Void>`

---

# Support Module

## 1. User Ticket Operations

### Create Ticket
Creates a new support ticket.

- **Endpoint:** `POST /support/tickets`
- **Request Body:** `CreateTicketRequest`
  ```json
  {
    "subject": "Issue with order #123",
    "description": "I haven't received my refund yet.",
    "priority": "HIGH",
    "category": "BILLING"
  }
  ```
- **Response:** `ApiResponse<TicketResponse>`

### Get My Tickets
Retrieves tickets created by the current user.

- **Endpoint:** `GET /support/tickets`
- **Response:** `ApiResponse<List<TicketResponse>>`

### Get Ticket Details
Retrieves details of a specific ticket.

- **Endpoint:** `GET /support/tickets/{ticketId}`
- **Response:** `ApiResponse<TicketResponse>`

### Rate Ticket
Allows a user to rate the support received.

- **Endpoint:** `POST /support/tickets/{ticketId}/rate`
- **Query Parameters:**
  - `rating` (int): 1-5 stars.
  - `feedback` (String): Optional comment.
- **Response:** `ApiResponse<TicketResponse>`

## 2. Agent & Admin Operations

### Get Open Tickets
Retrieves all open tickets for support agents.

- **Endpoint:** `GET /support/admin/tickets`
- **Permissions:** `ROLE_ADMIN`, `ROLE_SUPPORT_AGENT`
- **Response:** `ApiResponse<List<TicketResponse>>`

### Get My Assigned Tickets
Retrieves tickets assigned to the current agent.

- **Endpoint:** `GET /support/admin/my-tickets`
- **Permissions:** `ROLE_ADMIN`, `ROLE_SUPPORT_AGENT`
- **Response:** `ApiResponse<List<TicketResponse>>`

### Assign Ticket
Assigns a ticket to a specific agent.

- **Endpoint:** `POST /support/admin/tickets/{ticketId}/assign`
- **Permissions:** `ROLE_ADMIN`, `ROLE_SUPPORT_AGENT`
- **Query Parameters:**
  - `agentId` (String): ID of the agent.
- **Response:** `ApiResponse<TicketResponse>`

### Update Ticket Status
Updates the status of a ticket.

- **Endpoint:** `PATCH /support/admin/tickets/{ticketId}/status`
- **Permissions:** `ROLE_ADMIN`, `ROLE_SUPPORT_AGENT`
- **Query Parameters:**
  - `status` (String): New status (e.g., IN_PROGRESS, RESOLVED).
- **Response:** `ApiResponse<TicketResponse>`

### Resolve Ticket
Marks a ticket as resolved with notes.

- **Endpoint:** `POST /support/admin/tickets/{ticketId}/resolve`
- **Permissions:** `ROLE_ADMIN`, `ROLE_SUPPORT_AGENT`
- **Query Parameters:**
  - `resolutionNotes` (String): Details of the resolution.
- **Response:** `ApiResponse<TicketResponse>`
