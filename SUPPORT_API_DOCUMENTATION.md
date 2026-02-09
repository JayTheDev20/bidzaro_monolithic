# Support Module API Documentation

This document outlines all the REST APIs available for the Support Module in the Catering Marketplace application.

## Base URL
`http://localhost:8080/api/v1`

## Authentication
All endpoints require a valid JWT token in the header:
`Authorization: Bearer <YOUR_ACCESS_TOKEN>`

---

## 1. User APIs (Customer & Vendor)

These APIs are for regular users to create, view, and manage their support tickets.

### A. Create Support Ticket
Creates a new support ticket. The system will automatically assign it to the least busy support agent.

*   **URL:** `/support/tickets`
*   **Method:** `POST`
*   **Payload:**

```json
{
  "category": "ORDER_ISSUE", 
  // Options: ORDER_ISSUE, PAYMENT, ACCOUNT, TECHNICAL, GENERAL, VENDOR_ISSUE
  
  "subcategory": "LATE_DELIVERY", 
  // Optional. Examples: REFUND_REQUEST, LOGIN_ISSUE, APP_CRASH
  
  "priority": "HIGH", 
  // Options: LOW, MEDIUM, HIGH, URGENT
  
  "subject": "Order #123 is late", 
  // Required. Min 5, Max 200 characters.
  
  "description": "The vendor has not arrived yet and it is past the event start time.", 
  // Required. Min 10, Max 2000 characters.
  
  "orderId": "order_123", 
  // Optional. Link to a specific order.
  
  "vendorId": "vendor_456", 
  // Optional. Link to a specific vendor.
  
  "paymentId": "pay_789", 
  // Optional. Link to a specific payment transaction.
  
  "attachmentUrls": [ 
    // Optional. List of URLs for screenshots/documents.
    "https://example.com/screenshot1.jpg"
  ]
}
```

*   **Response (201 Created):**

```json
{
  "success": true,
  "data": {
    "ticketId": "uuid-string",
    "ticketNumber": "TKT-2024-00001",
    "status": "ASSIGNED", // Automatically assigned
    "subject": "Order #123 is late",
    "createdAt": "2024-01-30T10:00:00Z"
  }
}
```

### B. Get My Tickets
Retrieves a paginated list of tickets created by the logged-in user.

*   **URL:** `/support/tickets`
*   **Method:** `GET`
*   **Query Parameters:**
    *   `page` (Optional, default: 0)
    *   `size` (Optional, default: 20)
*   **Response (200 OK):**

```json
{
  "success": true,
  "data": [
    {
      "ticketId": "uuid-string",
      "ticketNumber": "TKT-2024-00001",
      "status": "IN_PROGRESS",
      "subject": "Order #123 is late",
      "priority": "HIGH",
      "createdAt": "2024-01-30T10:00:00Z"
    }
  ],
  "pageInfo": { ... }
}
```

### C. Get Ticket Details
Retrieves full details of a specific ticket.

*   **URL:** `/support/tickets/{ticketId}`
*   **Method:** `GET`
*   **Response (200 OK):** Returns full ticket object including description, attachments, and resolution.

### D. Rate Support Experience
Allows the user to rate the support service after the ticket is resolved or closed.

*   **URL:** `/support/tickets/{ticketId}/rate`
*   **Method:** `POST`
*   **Query Parameters:**
    *   `rating` (Required): Integer (1-5)
    *   `feedback` (Optional): String comment
*   **Response (200 OK):** Success message.

---

## 2. Support Agent / Admin APIs

These APIs are restricted to users with `ROLE_SUPPORT_AGENT` or `ROLE_ADMIN`.

### A. Get All Open Tickets
Retrieves a list of all open/unresolved tickets, sorted by priority (Urgent first).

*   **URL:** `/support/admin/tickets`
*   **Method:** `GET`
*   **Query Parameters:**
    *   `page` (Default: 0)
    *   `size` (Default: 20)
*   **Response (200 OK):** List of tickets.

### B. Get My Assigned Tickets
Retrieves tickets assigned specifically to the logged-in agent.

*   **URL:** `/support/admin/my-tickets`
*   **Method:** `GET`
*   **Response (200 OK):** List of tickets assigned to me.

### C. Assign Ticket (Manual)
Manually assigns a ticket to a specific agent (overrides auto-assignment).

*   **URL:** `/support/admin/tickets/{ticketId}/assign`
*   **Method:** `POST`
*   **Query Parameters:**
    *   `agentId` (Required): The User ID of the support agent.
*   **Response (200 OK):** Updated ticket.

### D. Update Ticket Status
Updates the status of a ticket (e.g., to indicate work has started).

*   **URL:** `/support/admin/tickets/{ticketId}/status`
*   **Method:** `PATCH`
*   **Query Parameters:**
    *   `status` (Required): `OPEN`, `IN_PROGRESS`, `WAITING_FOR_CUSTOMER`, `RESOLVED`, `CLOSED`
*   **Response (200 OK):** Updated ticket.

### E. Resolve Ticket
Marks a ticket as resolved and adds resolution notes.

*   **URL:** `/support/admin/tickets/{ticketId}/resolve`
*   **Method:** `POST`
*   **Query Parameters:**
    *   `resolutionNotes` (Required): Explanation of the fix.
*   **Response (200 OK):** Updated ticket with status `RESOLVED`.

---

## 3. Ticket Status Lifecycle

1.  **OPEN:** Ticket created, not yet assigned (rare with auto-assign).
2.  **ASSIGNED:** Ticket assigned to an agent.
3.  **IN_PROGRESS:** Agent is working on it.
4.  **WAITING_FOR_CUSTOMER:** Agent needs more info from user.
5.  **RESOLVED:** Issue fixed, waiting for user confirmation/rating.
6.  **CLOSED:** Final state.

## 4. Automatic Assignment Logic
*   The system automatically finds all users with role `SUPPORT_AGENT`.
*   It counts their active tickets (`OPEN`, `ASSIGNED`, `IN_PROGRESS`, `WAITING`).
*   It assigns the new ticket to the agent with the **lowest** active ticket count.
