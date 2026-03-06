# 🎧 SUPPORT AGENT API Documentation
**Bidzaro Catering Platform** | Base URL: `http://localhost:8080/api/v1`

> 🔒 = Requires `Authorization: Bearer <accessToken>` with `userType: SUPPORT_AGENT`
> ✅ = Required field | ⬜ = Optional field

---

## 📋 Table of Contents
1. [Enums Reference](#-enums-reference)
2. [Authentication](#-authentication)
3. [Ticket Management](#-ticket-management)
4. [Chat with Users & Vendors](#-chat-with-users--vendors)
5. [View User / Order Context](#-view-user--order-context)
6. [Notifications](#-notifications)

---

## 🔢 Enums Reference

### TicketStatus
| Value | Description | Who Sets It |
|-------|-------------|-------------|
| `OPEN` | Ticket created, not yet assigned | System (auto) |
| `ASSIGNED` | Assigned to an agent | System (auto-assign) / Admin (manual) |
| `IN_PROGRESS` | Agent actively working on it | Agent |
| `WAITING_FOR_CUSTOMER` | Waiting for user's response | Agent |
| `RESOLVED` | Issue resolved | Agent |
| `CLOSED` | Closed after resolution or inactivity | Agent / System |
| `ESCALATED` | Escalated due to SLA breach or complexity | System / Admin |

**Valid Status Transitions:**
```
OPEN → ASSIGNED → IN_PROGRESS → WAITING_FOR_CUSTOMER → IN_PROGRESS → RESOLVED → CLOSED
Any status → ESCALATED (by system/admin on SLA breach)
```

### TicketPriority
| Value | First Response SLA | Resolution SLA | Auto-Escalation |
|-------|-------------------|----------------|-----------------|
| `URGENT` | 1 hour | 4 hours | After 2h unassigned |
| `HIGH` | 4 hours | 24 hours | After 8h unassigned |
| `MEDIUM` | 8 hours | 48 hours | After 24h unassigned |
| `LOW` | 24 hours | 72 hours | After 48h unassigned |

### ConversationType
| Value | Description |
|-------|-------------|
| `USER_SUPPORT` | Chat between customer and support agent |
| `VENDOR_SUPPORT` | Chat between vendor and support agent |
| `USER_VENDOR` | Customer ↔ Vendor chat (agent can view only) |

### MessageType
| Value |
|-------|
| `TEXT` |
| `IMAGE` |
| `FILE` |
| `SYSTEM` |

### OrderStatus
| Value | Description |
|-------|-------------|
| `PENDING_TOKEN_PAYMENT` | Awaiting token payment |
| `CONFIRMED` | Token paid, confirmed |
| `IN_PREPARATION` | Food being prepared |
| `READY_FOR_DELIVERY` | Ready to deliver |
| `DELIVERING` | Out for delivery |
| `DELIVERED` | Delivered to venue |
| `COMPLETED` | Order completed |
| `CANCELLED` | Cancelled |

### PaymentStatus
| Value |
|-------|
| `TOKEN_PENDING` |
| `TOKEN_PAID` |
| `PARTIALLY_PAID` |
| `FULLY_PAID` |

### TransactionStatus
| Value |
|-------|
| `PENDING` |
| `PROCESSING` |
| `SUCCESS` |
| `FAILED` |
| `REFUNDED` |
| `PARTIALLY_REFUNDED` |

### VendorStatus
| Value |
|-------|
| `PENDING_APPROVAL` |
| `ACTIVE` |
| `SUSPENDED` |
| `REJECTED` |

### NotificationChannel
| Value |
|-------|
| `EMAIL` |
| `SMS` |
| `PUSH` |
| `IN_APP` |
| `WHATSAPP` |

---

## 🔐 Authentication

`POST /auth/login`

| Field | Type | Required |
|-------|------|----------|
| `identifier` | string | ✅ | Email or phone |
| `password` | string | ✅ | |
| `fcmToken` | string | ⬜ | Firebase device token |

**Request:**
```json
{
  "identifier": "priya.support@bidzaro.com",
  "password": "AgentPass@123",
  "fcmToken": "firebase-device-token"
}
```

**Response `200`:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 604800,
    "user": {
      "userId": "uuid",
      "vendorId": null,
      "email": "priya.support@bidzaro.com",
      "phone": "+919999988888",
      "userType": "SUPPORT_AGENT",
      "firstName": "Priya",
      "lastName": "Reddy",
      "fullName": "Priya Reddy",
      "profilePictureUrl": null,
      "dateOfBirth": null,
      "gender": null,
      "emailVerified": true,
      "phoneVerified": true,
      "twoFactorEnabled": false,
      "preferredLanguage": "en",
      "preferredCurrency": "INR",
      "country": "INDIA",
      "status": "ACTIVE",
      "notificationPreferences": {
        "emailNotifications": { "orderUpdates": true, "bidUpdates": true, "securityAlerts": true },
        "pushNotifications": { "orderUpdates": true, "chatMessages": true, "bidUpdates": true }
      },
      "lastLoginAt": "2026-03-06T10:00:00Z",
      "createdAt": "2026-01-20T10:00:00Z"
    }
  }
}
```

---

### Refresh Token
`POST /auth/refresh`

**Request:** `{ "refreshToken": "eyJhbGciOiJIUzI1NiJ9..." }`
**Response `200`:** `{ "data": { "accessToken": "...", "refreshToken": "...", "tokenType": "Bearer", "expiresIn": 604800 } }`

---

### Logout
`POST /auth/logout` 🔒

**Request:** `{ "refreshToken": "eyJhbGciOiJIUzI1NiJ9..." }`
**Response `200`:** `{ "success": true, "message": "Logged out successfully" }`

---

## 🎫 Ticket Management

### Get My Assigned Tickets
`GET /support/tickets/agent/my?page=0&size=20&status=IN_PROGRESS` 🔒

| Query Param | Type | Required | Values |
|-------------|------|----------|--------|
| `page` | int | ⬜ | Default 0 |
| `size` | int | ⬜ | Default 20 |
| `status` | string | ⬜ | `OPEN`, `ASSIGNED`, `IN_PROGRESS`, `WAITING_FOR_CUSTOMER`, `RESOLVED`, `CLOSED`, `ESCALATED` |

**Response `200`:**
```json
{
  "data": [
    {
      "ticketId": "uuid",
      "ticketNumber": "TKT-000001",
      "createdBy": "user-uuid",
      "createdByName": "Rahul Sharma",
      "category": "ORDER",
      "subcategory": "DELIVERY_ISSUE",
      "priority": "HIGH",
      "subject": "Vendor not responding after payment",
      "description": "I paid the token amount but the vendor hasn't confirmed the order.",
      "relatedEntities": {
        "orderId": "order-uuid",
        "vendorId": "vendor-uuid",
        "paymentId": "payment-uuid"
      },
      "assignedTo": "agent-uuid",
      "assignedAt": "2026-03-06T10:05:00Z",
      "conversationId": "chat-conv-uuid",
      "status": "IN_PROGRESS",
      "sla": {
        "firstResponseDue": "2026-03-06T14:00:00Z",
        "resolutionDue": "2026-03-07T10:00:00Z",
        "firstResponseAt": "2026-03-06T10:30:00Z",
        "resolvedAt": null,
        "slaBreached": false
      },
      "resolution": null,
      "customerSatisfaction": null,
      "createdAt": "2026-03-06T10:00:00Z",
      "closedAt": null
    }
  ],
  "pageInfo": { "page": 0, "size": 20, "totalElements": 8 }
}
```

---

### Get All Open/Unassigned Tickets
`GET /support/tickets/open?page=0&size=20` 🔒

**Response `200`:** Paginated list of `TicketResponse` with `status: OPEN`.

---

### Get All Tickets (Support Queue)
`GET /support/tickets?page=0&size=20&status=ASSIGNED` 🔒

**Response `200`:** Paginated list of all `TicketResponse` visible to agent.

---

### Get Ticket by ID
`GET /support/tickets/{ticketId}` 🔒

**Response `200`:**
```json
{
  "data": {
    "ticketId": "uuid",
    "ticketNumber": "TKT-000001",
    "createdBy": "user-uuid",
    "createdByName": "Rahul Sharma",
    "category": "ORDER",
    "subcategory": "DELIVERY_ISSUE",
    "priority": "HIGH",
    "subject": "Vendor not responding after payment",
    "description": "I paid the token amount but the vendor hasn't confirmed the order.",
    "relatedEntities": {
      "orderId": "order-uuid",
      "vendorId": "vendor-uuid",
      "paymentId": "payment-uuid"
    },
    "assignedTo": "agent-uuid",
    "assignedAt": "2026-03-06T10:05:00Z",
    "conversationId": "chat-conv-uuid",
    "status": "IN_PROGRESS",
    "sla": {
      "firstResponseDue": "2026-03-06T14:00:00Z",
      "resolutionDue": "2026-03-07T10:00:00Z",
      "firstResponseAt": "2026-03-06T10:30:00Z",
      "resolvedAt": null,
      "slaBreached": false
    },
    "resolution": null,
    "customerSatisfaction": null,
    "createdAt": "2026-03-06T10:00:00Z",
    "closedAt": null
  }
}
```

---

### Update Ticket Status
`PUT /support/tickets/{ticketId}/status` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `status` | string | ✅ | Valid status value |

**Request:**
```json
{ "status": "IN_PROGRESS" }
```

**Response `200`:** Updated `TicketResponse` with new status.

**Valid Status Transitions for Agent:**
```
ASSIGNED → IN_PROGRESS
IN_PROGRESS → WAITING_FOR_CUSTOMER
WAITING_FOR_CUSTOMER → IN_PROGRESS
IN_PROGRESS → RESOLVED
RESOLVED → CLOSED
```

---

### Resolve Ticket
`PUT /support/tickets/{ticketId}/resolve` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `resolutionNotes` | string | ✅ | Description of resolution |
| `resolvedBy` | string | ✅ | Agent's userId |

**Request:**
```json
{
  "resolutionNotes": "Contacted vendor directly. Vendor has confirmed the order. User notified via chat and email.",
  "resolvedBy": "agent-uuid"
}
```

**Response `200`:**
```json
{
  "data": {
    "ticketId": "uuid",
    "ticketNumber": "TKT-000001",
    "status": "RESOLVED",
    "resolution": {
      "resolutionType": "RESOLVED",
      "resolutionNotes": "Contacted vendor directly. Vendor has confirmed the order.",
      "resolvedBy": "agent-uuid",
      "resolvedAt": "2026-03-06T14:00:00Z"
    },
    "sla": {
      "firstResponseDue": "2026-03-06T14:00:00Z",
      "resolutionDue": "2026-03-07T10:00:00Z",
      "firstResponseAt": "2026-03-06T10:30:00Z",
      "resolvedAt": "2026-03-06T14:00:00Z",
      "slaBreached": false
    },
    "customerSatisfaction": null,
    "closedAt": null
  }
}
```

---

### Close Ticket
`PUT /support/tickets/{ticketId}/close` 🔒
**Response `200`:** Updated `TicketResponse` with `status: CLOSED` and `closedAt` timestamp.

---

## 💬 Chat with Users & Vendors

> When a ticket is created by a user or vendor, a **chat conversation is automatically created** between them and the assigned support agent. The `conversationId` is present on the `TicketResponse`.

### Get Conversation (from ticket's conversationId)
`GET /chat/conversations/{conversationId}` 🔒

**Response `200`:**
```json
{
  "data": {
    "conversationId": "uuid",
    "participants": [
      {
        "userId": "user-uuid",
        "userType": "USER",
        "name": "Rahul Sharma",
        "profilePictureUrl": null
      },
      {
        "userId": "agent-uuid",
        "userType": "SUPPORT_AGENT",
        "name": "Priya Reddy",
        "profilePictureUrl": null
      }
    ],
    "conversationType": "USER_SUPPORT",
    "relatedTo": {
      "entityType": "TICKET",
      "entityId": "ticket-uuid"
    },
    "lastMessage": {
      "message": "I still haven't received confirmation from the vendor.",
      "senderId": "user-uuid",
      "timestamp": "2026-03-06T10:10:00Z"
    },
    "unreadCount": {
      "agent-uuid": 1,
      "user-uuid": 0
    },
    "status": "ACTIVE",
    "createdAt": "2026-03-06T10:05:00Z",
    "updatedAt": "2026-03-06T10:10:00Z"
  }
}
```

---

### Get All My Chat Conversations
`GET /chat/conversations?page=0&size=20` 🔒

**Response `200`:** Paginated list of `ConversationResponse` objects (see above for structure).

---

### Get Messages in Conversation
`GET /chat/conversations/{conversationId}/messages?page=0&size=50` 🔒

**Response `200`:**
```json
{
  "data": [
    {
      "messageId": "uuid",
      "conversationId": "uuid",
      "senderId": "user-uuid",
      "senderType": "USER",
      "message": "I still haven't received confirmation from the vendor.",
      "messageType": "TEXT",
      "attachments": [],
      "readBy": [],
      "isDeleted": false,
      "deletedAt": null,
      "timestamp": "2026-03-06T10:10:00Z",
      "createdAt": "2026-03-06T10:10:00Z"
    },
    {
      "messageId": "uuid",
      "conversationId": "uuid",
      "senderId": "agent-uuid",
      "senderType": "SUPPORT_AGENT",
      "message": "Hello Rahul! I have contacted the vendor on your behalf. They will respond within 2 hours. I'll keep you updated.",
      "messageType": "TEXT",
      "attachments": [],
      "readBy": [
        { "userId": "user-uuid", "readAt": "2026-03-06T10:32:00Z" }
      ],
      "isDeleted": false,
      "deletedAt": null,
      "timestamp": "2026-03-06T10:30:00Z",
      "createdAt": "2026-03-06T10:30:00Z"
    }
  ],
  "pageInfo": { "page": 0, "size": 50, "totalElements": 12 }
}
```

---

### Send Message to User/Vendor
`POST /chat/conversations/{conversationId}/messages` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `message` | string | ✅ | Message text |
| `messageType` | string | ⬜ | `TEXT` (default), `IMAGE`, `FILE` |
| `fileUrl` | string | ⬜ | For IMAGE or FILE type messages |

**Request:**
```json
{
  "message": "Hello Rahul! I have contacted the vendor on your behalf. They will confirm within 2 hours.",
  "messageType": "TEXT"
}
```

**Response `201`:**
```json
{
  "data": {
    "messageId": "uuid",
    "conversationId": "uuid",
    "senderId": "agent-uuid",
    "senderType": "SUPPORT_AGENT",
    "message": "Hello Rahul! I have contacted the vendor on your behalf.",
    "messageType": "TEXT",
    "attachments": [],
    "readBy": [],
    "isDeleted": false,
    "deletedAt": null,
    "timestamp": "2026-03-06T10:30:00Z",
    "createdAt": "2026-03-06T10:30:00Z"
  }
}
```

---

### Send File/Image in Chat
`POST /chat/conversations/{conversationId}/messages` 🔒

**Request (file attachment):**
```json
{
  "message": "Please review the attached screenshot.",
  "messageType": "IMAGE",
  "fileUrl": "http://localhost:8080/uploads/chat/screenshot-uuid.jpg"
}
```
> First upload the file using `POST /upload/image`, then use the returned `fileUrl` in this request.

---

### Mark Messages as Read
`PUT /chat/conversations/{conversationId}/read` 🔒
**Response `200`:** `{ "success": true }`

---

### WebSocket Real-time Chat

**Connection (SockJS/STOMP):**
```
ws://localhost:8080/api/v1/ws?token=<accessToken>
```
**SockJS fallback:**
```
http://localhost:8080/api/v1/ws/sockjs/chat?token=<accessToken>
```

**Subscribe to messages:**
```
SUBSCRIBE /topic/conversations.{conversationId}
```

**Receive message payload:**
```json
{
  "messageId": "uuid",
  "conversationId": "uuid",
  "senderId": "user-uuid",
  "senderType": "USER",
  "message": "Please help me urgently!",
  "messageType": "TEXT",
  "timestamp": "2026-03-06T10:30:00Z"
}
```

**Send message via WebSocket:**
```
SEND /app/chat/{conversationId}
Body: { "message": "I am looking into your issue right now.", "messageType": "TEXT" }
```

**Subscribe to read receipts:**
```
SUBSCRIBE /topic/conversations.{conversationId}.read
```

**Receive read receipt payload:**
```json
{
  "conversationId": "uuid",
  "userId": "user-uuid",
  "readAt": "2026-03-06T10:35:00Z"
}
```

---

## 🔍 View User / Order Context

> Support agents can view full details of users, orders, and transactions to provide better assistance. These are read-only for agents.

### View User Profile
`GET /admin/users/{userId}` 🔒

**Response `200`:** Full `UserResponse` object:
```json
{
  "data": {
    "userId": "uuid",
    "email": "rahul@example.com",
    "phone": "+919876543210",
    "userType": "USER",
    "firstName": "Rahul",
    "lastName": "Sharma",
    "fullName": "Rahul Sharma",
    "emailVerified": true,
    "phoneVerified": true,
    "preferredCurrency": "INR",
    "country": "INDIA",
    "status": "ACTIVE",
    "lastLoginAt": "2026-03-06T09:00:00Z",
    "createdAt": "2026-01-15T10:00:00Z"
  }
}
```

---

### View Order Details
`GET /orders/{orderId}` 🔒

**Response `200`:** Full `OrderResponse` object:
```json
{
  "data": {
    "orderId": "uuid",
    "userId": "uuid",
    "bidRequestId": "uuid",
    "eventDetails": {
      "eventType": "WEDDING",
      "eventName": "Sharma Wedding Reception",
      "eventDate": "2026-04-15",
      "eventTime": "2026-04-15T18:00:00",
      "numberOfGuests": 300,
      "venueAddress": {
        "streetAddress": "Taj Banjara Hotel",
        "city": "Hyderabad",
        "state": "Telangana",
        "postalCode": "500034",
        "country": "India"
      }
    },
    "vendorOrders": [
      {
        "vendorOrderId": "uuid",
        "vendorId": "uuid",
        "vendorUserId": "uuid",
        "vendorName": "Royal Catering Co.",
        "items": [
          {
            "vendorItemId": "uuid",
            "itemName": "Chicken Biryani",
            "quantity": 300,
            "pricePerPlate": 400.00,
            "totalPrice": 120000.00
          }
        ],
        "subtotal": 120000.00,
        "serviceCharge": 6000.00,
        "taxAmount": 22680.00,
        "totalAmount": 148680.00,
        "vendorStatus": "ACCEPTED",
        "deliveryStatus": "PENDING"
      }
    ],
    "pricing": {
      "currency": "INR",
      "subtotal": 120000.00,
      "serviceCharges": 6000.00,
      "taxAmount": 22680.00,
      "platformFee": 2973.60,
      "discountAmount": 0.00,
      "totalAmount": 151653.60
    },
    "paymentDetails": {
      "tokenAmount": 37913.40,
      "tokenPaid": true,
      "tokenPaidAt": "2026-03-06T15:00:00Z",
      "totalPaid": 37913.40,
      "balanceDue": 113740.20,
      "paymentStatus": "TOKEN_PAID"
    },
    "contactInfo": {
      "primaryContactName": "Rahul Sharma",
      "primaryContactPhone": "+919876543210",
      "primaryContactEmail": "rahul@example.com"
    },
    "specialInstructions": "Halal food only",
    "status": "CONFIRMED",
    "cancellation": null,
    "createdAt": "2026-03-06T15:00:00Z",
    "confirmedAt": "2026-03-06T15:00:00Z",
    "deliveredAt": null,
    "completedAt": null
  }
}
```

---

### View Transaction Details
`GET /payments/{transactionId}` 🔒

**Response `200`:** Full transaction object:
```json
{
  "data": {
    "transactionId": "uuid",
    "orderId": "uuid",
    "bidId": "uuid",
    "userId": "uuid",
    "vendorId": "uuid",
    "paymentType": "TOKEN",
    "installmentNumber": null,
    "amount": {
      "currency": "INR",
      "amount": 37913.40,
      "platformFee": 758.27,
      "vendorPayout": 37155.13
    },
    "paymentGateway": "RAZORPAY",
    "gatewayTransactionId": "pay_XYZ789GHI012",
    "gatewayOrderId": "order_ABC123DEF456",
    "paymentMethod": "UPI",
    "paymentMethodDetails": {
      "cardLastFour": null,
      "cardBrand": null,
      "cardNetwork": null,
      "upiId": "rahul@upi",
      "bankName": null,
      "walletName": null
    },
    "status": "SUCCESS",
    "failureReason": null,
    "initiatedAt": "2026-03-06T14:55:00Z",
    "processedAt": "2026-03-06T15:00:00Z",
    "settledAt": null,
    "createdAt": "2026-03-06T14:55:00Z"
  }
}
```

---

### View Vendor Profile
`GET /vendors/{vendorId}` 🔒

**Response `200`:** Full `VendorResponse` object including all documents, ratings, and stats.

---

## 🔔 Notifications

### Get My Notifications
`GET /notifications?page=0&size=20` 🔒

**Response `200`:**
```json
{
  "data": [
    {
      "notificationId": "uuid",
      "userId": "agent-uuid",
      "title": "New Ticket Assigned",
      "message": "Ticket TKT-000001 has been assigned to you.",
      "type": "TICKET_ASSIGNED",
      "channel": "IN_APP",
      "isRead": false,
      "data": { "ticketId": "uuid", "ticketNumber": "TKT-000001" },
      "createdAt": "2026-03-06T10:05:00Z"
    },
    {
      "notificationId": "uuid",
      "userId": "agent-uuid",
      "title": "SLA Breach Warning",
      "message": "Ticket TKT-000003 URGENT priority response deadline in 30 minutes.",
      "type": "SLA_WARNING",
      "channel": "IN_APP",
      "isRead": false,
      "data": { "ticketId": "uuid", "ticketNumber": "TKT-000003" },
      "createdAt": "2026-03-06T11:30:00Z"
    },
    {
      "notificationId": "uuid",
      "userId": "agent-uuid",
      "title": "New Message",
      "message": "Rahul Sharma sent a message: 'Has the vendor responded yet?'",
      "type": "NEW_CHAT_MESSAGE",
      "channel": "IN_APP",
      "isRead": false,
      "data": { "conversationId": "uuid", "ticketId": "uuid" },
      "createdAt": "2026-03-06T11:00:00Z"
    }
  ]
}
```

---

### Get Unread Count
`GET /notifications/unread/count` 🔒
**Response `200`:** `{ "data": { "count": 3 } }`

---

### Mark as Read
`PUT /notifications/{notificationId}/read` 🔒
**Response `200`:** `{ "success": true }`

---

### Mark All as Read
`PUT /notifications/read-all` 🔒
**Response `200`:** `{ "success": true }`

---

## 📊 Agent Workload Info

The system **auto-assigns** new tickets to the agent with the fewest active tickets (load balancing). Active ticket statuses counted for workload:
- `OPEN`
- `ASSIGNED`
- `IN_PROGRESS`
- `WAITING_FOR_CUSTOMER`

---

## 📌 SLA Monitoring (Automatic)

The `SLAMonitorScheduler` runs every **15 minutes** and:

1. **Marks `slaBreached: true`** on tickets that exceeded resolution deadline
2. **Escalates** unassigned tickets based on priority:

| Priority | Escalate After |
|----------|---------------|
| `URGENT` | 2 hours unassigned |
| `HIGH` | 8 hours unassigned |
| `MEDIUM` | 24 hours unassigned |
| `LOW` | 48 hours unassigned |

3. **Sends SLA breach notifications** to agents and admins

---

## ⚠️ Standard Error Response

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "You are not authorized to access this ticket",
    "path": "/api/v1/support/tickets/uuid",
    "timestamp": "2026-03-06T10:00:00Z",
    "fieldErrors": {}
  }
}
```

**Support Agent Error Codes:**
| Code | HTTP | Description |
|------|------|-------------|
| `UNAUTHORIZED` | 401 | Not authenticated |
| `FORBIDDEN` | 403 | Ticket assigned to another agent |
| `RESOURCE_NOT_FOUND` | 404 | Ticket, message, or conversation not found |
| `INVALID_STATUS` | 400 | Invalid ticket status value |
| `INVALID_STATUS_TRANSITION` | 400 | Cannot change to this status from current state |
| `TICKET_ALREADY_RESOLVED` | 400 | Ticket is already resolved/closed |
