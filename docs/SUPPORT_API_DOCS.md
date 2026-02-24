# 🎧 SUPPORT API DOCUMENTATION
## Bidzaro Catering Platform — Complete Support Agent Guide

**Version:** 1.0.0
**Base URL:** `http://localhost:8080/api/v1`
**Target Audience:** Support Agents & Support Managers
**Authentication:** JWT Bearer Token (Role: SUPPORT_AGENT or SUPPORT_MANAGER)

---

## 📋 Table of Contents

1. [Support Agent Authentication](#1-support-agent-authentication)
2. [Ticket Management](#2-ticket-management)
3. [Ticket Messaging](#3-ticket-messaging)
4. [Chat with Users & Vendors](#4-chat-with-users--vendors)
5. [Order Inquiry & Lookup](#5-order-inquiry--lookup)
6. [User Account Lookup](#6-user-account-lookup)
7. [Vendor Account Lookup](#7-vendor-account-lookup)
8. [Payment Inquiry](#8-payment-inquiry)
9. [Notifications](#9-notifications)
10. [Dashboard & My Statistics](#10-dashboard--my-statistics)

---

## 🔐 Support Agent Auth Flow

```
1. Admin creates support agent account (POST /admin/users/create-agent)
2. Agent receives credentials via email
3. Agent Login  →  POST /auth/login  →  accessToken
4. All support endpoints require: Authorization: Bearer {accessToken}
5. Support Manager role has elevated permissions
```

### Role Capabilities
| Action | SUPPORT_AGENT | SUPPORT_MANAGER |
|--------|:---:|:---:|
| View tickets | ✅ | ✅ |
| Respond to tickets | ✅ | ✅ |
| Resolve tickets | ✅ | ✅ |
| Assign tickets | ❌ | ✅ |
| View all agent stats | ❌ | ✅ |
| Close tickets | ✅ | ✅ |
| Escalate tickets | ✅ | ✅ |

---

# 1. Support Agent Authentication

## 1.1 Support Agent Login

```http
POST /auth/login
Content-Type: application/json
```

### Request Payload
```json
{
  "identifier": "agent.priya@bidzaro.com",
  "password": "AgentPass@2026",
  "fcmToken": "firebase_agent_device_token",
  "deviceInfo": "Support Dashboard - Firefox Browser"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 604800,
    "user": {
      "userId": "agent-priya-001",
      "email": "agent.priya@bidzaro.com",
      "firstName": "Priya",
      "lastName": "Sharma",
      "userType": "SUPPORT_AGENT",
      "status": "ACTIVE"
    }
  }
}
```

---

## 1.2 Refresh Token

```http
POST /auth/refresh-token
Content-Type: application/json
```

### Request Payload
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...(new)...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...(new)...",
    "expiresIn": 604800
  }
}
```

---

# 2. Ticket Management

## 2.1 Get All Support Tickets (Agent View)

**Use:** View all tickets in the system — filtered for your assigned tickets or all open ones.

```http
GET /support/admin/tickets?page=0&size=20&status=OPEN&priority=HIGH
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Values | Description |
|-------|--------|-------------|
| `status` | `OPEN`, `ASSIGNED`, `IN_PROGRESS`, `WAITING_FOR_CUSTOMER`, `RESOLVED`, `CLOSED` | Ticket status |
| `priority` | `LOW`, `MEDIUM`, `HIGH`, `URGENT` | Priority level |
| `category` | `ORDER`, `PAYMENT`, `VENDOR`, `ACCOUNT`, `OTHER` | Category |
| `assignedTo` | string | Filter by agent ID (use own ID to see your tickets) |
| `page` | integer | Page number |
| `size` | integer | Items per page |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "ticketId": "tkt-99001-22334",
      "ticketNumber": "TKT-20260224123456",
      "status": "OPEN",
      "priority": "HIGH",
      "category": "ORDER",
      "subcategory": "DELIVERY_ISSUE",
      "subject": "Food arrived 2 hours late for wedding",
      "description": "The catering team was supposed to arrive by 5 PM but arrived at 7 PM, causing significant inconvenience to our guests.",
      "createdBy": "550e8400-e29b-41d4-a716-446655440000",
      "createdByType": "USER",
      "createdByName": "John Doe",
      "createdByEmail": "john.doe@gmail.com",
      "createdByPhone": "+917890123456",
      "assignedTo": "agent-priya-001",
      "assignedToName": "Priya Sharma",
      "relatedEntities": {
        "orderId": "order-54321-12345",
        "vendorId": "vendor-12345-67890",
        "paymentId": null
      },
      "attachments": [
        {
          "fileUrl": "http://localhost:8080/uploads/images/complaint-photo-1.jpg",
          "uploadedAt": "2026-02-24T10:30:45.123Z"
        }
      ],
      "sla": {
        "priority": "HIGH",
        "responseTimeMinutes": 60,
        "resolutionTimeMinutes": 1440,
        "responseDeadline": "2026-02-24T11:30:00.000Z",
        "resolutionDeadline": "2026-02-25T10:30:00.000Z",
        "status": "ON_TRACK"
      },
      "createdAt": "2026-02-24T10:30:45.123Z",
      "lastActivityAt": "2026-02-24T10:30:45.123Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 450,
    "totalPages": 23
  }
}
```

---

## 2.2 Get My Assigned Tickets

**Use:** View only tickets assigned to you.

```http
GET /support/admin/tickets?assignedTo=agent-priya-001&status=IN_PROGRESS&page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "ticketId": "tkt-99001-22334",
      "ticketNumber": "TKT-20260224123456",
      "status": "IN_PROGRESS",
      "priority": "HIGH",
      "subject": "Food arrived 2 hours late for wedding",
      "createdByName": "John Doe",
      "createdByEmail": "john.doe@gmail.com",
      "sla": {
        "resolutionDeadline": "2026-02-25T10:30:00.000Z",
        "status": "ON_TRACK"
      },
      "createdAt": "2026-02-24T10:30:45.123Z"
    }
  ]
}
```

---

## 2.3 Get Single Ticket Details

**Use:** View full details of a specific ticket before responding.

```http
GET /support/tickets/{ticketId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "ticketId": "tkt-99001-22334",
    "ticketNumber": "TKT-20260224123456",
    "status": "OPEN",
    "priority": "HIGH",
    "category": "ORDER",
    "subcategory": "DELIVERY_ISSUE",
    "subject": "Food arrived 2 hours late for wedding",
    "description": "The catering team was supposed to arrive by 5 PM but arrived at 7 PM, causing significant inconvenience to our guests. We had 500 guests waiting. This affected the overall event schedule.",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdByType": "USER",
    "createdByName": "John Doe",
    "createdByEmail": "john.doe@gmail.com",
    "createdByPhone": "+917890123456",
    "assignedTo": "agent-priya-001",
    "assignedToName": "Priya Sharma",
    "assignedAt": "2026-02-24T10:35:00.000Z",
    "relatedEntities": {
      "orderId": "order-54321-12345",
      "vendorId": "vendor-12345-67890",
      "paymentId": null
    },
    "attachments": [
      {
        "fileUrl": "http://localhost:8080/uploads/images/complaint-photo-1.jpg",
        "fileName": "complaint-photo-1.jpg",
        "uploadedAt": "2026-02-24T10:30:45.123Z"
      }
    ],
    "conversationId": "conv-support-77665",
    "sla": {
      "priority": "HIGH",
      "responseTimeMinutes": 60,
      "resolutionTimeMinutes": 1440,
      "responseDeadline": "2026-02-24T11:30:00.000Z",
      "resolutionDeadline": "2026-02-25T10:30:00.000Z",
      "firstResponseAt": null,
      "resolvedAt": null,
      "closedAt": null,
      "status": "ON_TRACK"
    },
    "messages": [
      {
        "messageId": "tmsg-001",
        "senderType": "USER",
        "senderName": "John Doe",
        "message": "The catering team was supposed to arrive by 5 PM but arrived at 7 PM.",
        "attachmentUrls": ["http://localhost:8080/uploads/images/complaint-photo-1.jpg"],
        "isInternal": false,
        "createdAt": "2026-02-24T10:30:45.123Z"
      }
    ],
    "createdAt": "2026-02-24T10:30:45.123Z",
    "updatedAt": "2026-02-24T10:35:00.000Z"
  }
}
```

---

## 2.4 Update Ticket Status

**Use:** Move the ticket through the workflow stages.

```http
PATCH /support/admin/tickets/{ticketId}/status
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "status": "IN_PROGRESS",
  "note": "Contacted vendor to understand the reason for delay. Awaiting response."
}
```

### Ticket Status Workflow
```
OPEN → ASSIGNED → IN_PROGRESS → WAITING_FOR_CUSTOMER → RESOLVED → CLOSED
                                                      ↘ ESCALATED
```

### Status Values & When to Use
| Status | When to Set |
|--------|-------------|
| `IN_PROGRESS` | When actively working on it |
| `WAITING_FOR_CUSTOMER` | Waiting for customer to provide more info |
| `WAITING_FOR_VENDOR` | Waiting for vendor to respond |
| `ESCALATED` | Issue needs senior review |
| `RESOLVED` | Issue fixed, inform customer |
| `CLOSED` | Customer confirmed resolution or no response in 48hrs |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "ticketId": "tkt-99001-22334",
    "ticketNumber": "TKT-20260224123456",
    "status": "IN_PROGRESS",
    "note": "Contacted vendor to understand the reason for delay.",
    "updatedBy": "agent-priya-001",
    "updatedAt": "2026-02-24T10:40:00.000Z"
  }
}
```

---

## 2.5 Resolve Ticket

**Use:** Mark ticket as resolved with a resolution summary.

```http
POST /support/admin/tickets/{ticketId}/resolve
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "resolution": "Investigated with the vendor. They confirmed a traffic delay on the Outer Ring Road due to road works. As a goodwill gesture, we have issued a 10% discount promo code (SORRY10) valid for 3 months on the customer's next order.",
  "resolutionType": "COMPENSATION",
  "compensationDetails": {
    "type": "PROMO_CODE",
    "value": "SORRY10",
    "description": "10% discount code for next order"
  },
  "rootCause": "Vendor transportation delay due to traffic",
  "preventionNote": "Reminded vendor to plan for traffic delays especially for peak event times"
}
```

### Field Rules
| Field | Required | Description |
|-------|----------|-------------|
| `resolution` | ✅ | Full resolution description |
| `resolutionType` | ✅ | `INFORMATION_PROVIDED`, `REFUND_ISSUED`, `COMPENSATION`, `ESCALATED`, `NO_ACTION_REQUIRED` |

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Ticket resolved successfully",
  "data": {
    "ticketId": "tkt-99001-22334",
    "ticketNumber": "TKT-20260224123456",
    "status": "RESOLVED",
    "resolution": "Investigated with the vendor...",
    "resolvedBy": "agent-priya-001",
    "resolvedByName": "Priya Sharma",
    "resolvedAt": "2026-02-24T15:00:00.000Z",
    "sla": {
      "status": "MET",
      "resolutionTimeMinutes": 270
    }
  }
}
```

---

## 2.6 Close Ticket

**Use:** Close ticket after 48 hours without customer response, or after confirmation.

```http
POST /support/admin/tickets/{ticketId}/close
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "closureNote": "Customer confirmed satisfaction with the resolution. Ticket closed."
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "ticketId": "tkt-99001-22334",
    "status": "CLOSED",
    "closedBy": "agent-priya-001",
    "closedAt": "2026-02-26T10:00:00.000Z"
  }
}
```

---

## 2.7 Escalate Ticket

**Use:** Escalate ticket to support manager or senior team for complex issues.

```http
POST /support/admin/tickets/{ticketId}/escalate
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "escalationReason": "Customer is requesting a full refund for a ₹2,00,000 wedding order. This exceeds my authorization limit. Needs manager approval.",
  "escalateTo": "manager-001",
  "priority": "URGENT"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "ticketId": "tkt-99001-22334",
    "status": "ESCALATED",
    "escalatedTo": "manager-001",
    "escalatedBy": "agent-priya-001",
    "escalationReason": "Customer requesting full refund exceeding authorization limit",
    "escalatedAt": "2026-02-24T11:00:00.000Z"
  }
}
```

---

## 2.8 Reopen Ticket

**Use:** Reopen a resolved ticket if the customer says the issue is not fixed.

```http
POST /support/admin/tickets/{ticketId}/reopen
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "reason": "Customer says the promo code provided is not working. Need to investigate further."
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "ticketId": "tkt-99001-22334",
    "status": "IN_PROGRESS",
    "reopenedBy": "agent-priya-001",
    "reopenReason": "Customer says the promo code provided is not working.",
    "reopenedAt": "2026-02-25T10:00:00.000Z"
  }
}
```

---

# 3. Ticket Messaging

## 3.1 Send Reply to Customer

**Use:** Send a message to the customer via the ticket thread.

```http
POST /support/tickets/{ticketId}/messages
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload — Reply to Customer
```json
{
  "message": "Dear John, thank you for reaching out. I have investigated your complaint regarding the delayed catering service. I have contacted the vendor and they have confirmed a traffic delay on the Outer Ring Road. I completely understand your frustration and I sincerely apologize for the inconvenience. As a goodwill gesture, I am sending you a 10% discount code SORRY10 which is valid on your next order. Please let me know if you have any further concerns.",
  "isInternal": false,
  "attachmentUrls": []
}
```

### Request Payload — Internal Note (Not Visible to Customer)
```json
{
  "message": "Vendor confirmed traffic delay. Need to check if this was the 3rd incident this month. May need to issue a warning to vendor.",
  "isInternal": true,
  "attachmentUrls": []
}
```

| Field | Required | Description |
|-------|----------|-------------|
| `message` | ✅ | The reply content |
| `isInternal` | ✅ | `false` = visible to customer; `true` = internal note only |
| `attachmentUrls` | ❌ | Links to uploaded files |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "messageId": "tmsg-002",
    "ticketId": "tkt-99001-22334",
    "senderType": "SUPPORT_AGENT",
    "senderName": "Priya Sharma",
    "message": "Dear John, thank you for reaching out...",
    "isInternal": false,
    "createdAt": "2026-02-24T10:45:00.000Z"
  }
}
```

---

## 3.2 Get All Messages in Ticket

**Use:** View the complete conversation thread for a ticket.

```http
GET /support/tickets/{ticketId}/messages?page=0&size=50&includeInternal=true
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Values | Description |
|-------|--------|-------------|
| `includeInternal` | `true`, `false` | Include internal notes (agents only) |
| `page` | integer | Page number |
| `size` | integer | Items per page |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "messageId": "tmsg-001",
      "senderType": "USER",
      "senderName": "John Doe",
      "message": "The catering team was supposed to arrive by 5 PM but arrived at 7 PM.",
      "attachmentUrls": ["http://localhost:8080/uploads/images/complaint-photo-1.jpg"],
      "isInternal": false,
      "createdAt": "2026-02-24T10:30:45.123Z"
    },
    {
      "messageId": "tmsg-002",
      "senderType": "SUPPORT_AGENT",
      "senderName": "Priya Sharma",
      "message": "Dear John, thank you for reaching out. I have investigated...",
      "attachmentUrls": [],
      "isInternal": false,
      "createdAt": "2026-02-24T10:45:00.000Z"
    },
    {
      "messageId": "tmsg-003",
      "senderType": "SUPPORT_AGENT",
      "senderName": "Priya Sharma",
      "message": "Vendor confirmed traffic delay. Need to check incident history.",
      "isInternal": true,
      "createdAt": "2026-02-24T10:42:00.000Z"
    }
  ],
  "pageInfo": { "totalElements": 3, "totalPages": 1 }
}
```

---

# 4. Chat with Users & Vendors

## 4.1 Start Support Chat with User

**Use:** Initiate a direct support chat conversation with a customer.

```http
POST /chat/conversations
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "otherUserId": "550e8400-e29b-41d4-a716-446655440000",
  "type": "USER_SUPPORT"
}
```

### Conversation Type Reference
| Type | Use Case |
|------|----------|
| `USER_SUPPORT` | Support agent chatting with a customer |
| `VENDOR_SUPPORT` | Support agent chatting with a vendor |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "conversationId": "conv-support-77665",
    "conversationType": "USER_SUPPORT",
    "status": "ACTIVE",
    "participants": [
      {
        "userId": "agent-priya-001",
        "userType": "SUPPORT_AGENT",
        "name": "Priya Sharma"
      },
      {
        "userId": "550e8400-e29b-41d4-a716-446655440000",
        "userType": "USER",
        "name": "John Doe"
      }
    ],
    "createdAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 4.2 Get All My Conversations (Support Chats)

```http
GET /chat/conversations?page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "conversationId": "conv-support-77665",
      "conversationType": "USER_SUPPORT",
      "participants": [
        { "name": "John Doe", "userType": "USER" }
      ],
      "lastMessage": {
        "message": "Thank you for the resolution Priya!",
        "senderId": "550e8400-e29b-41d4-a716-446655440000",
        "timestamp": "2026-02-24T16:00:00.000Z"
      },
      "unreadCount": 1
    },
    {
      "conversationId": "conv-support-88990",
      "conversationType": "VENDOR_SUPPORT",
      "participants": [
        { "name": "Spice Garden Catering", "userType": "VENDOR" }
      ],
      "lastMessage": {
        "message": "We had a traffic delay on ORR, sorry for the inconvenience.",
        "senderId": "vendor-user-550e8400",
        "timestamp": "2026-02-24T11:30:00.000Z"
      },
      "unreadCount": 0
    }
  ]
}
```

---

## 4.3 Get Chat History

```http
GET /chat/conversations/{conversationId}/messages?page=0&size=50
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "messageId": "msg-chat-001",
      "senderId": "agent-priya-001",
      "senderType": "SUPPORT_AGENT",
      "message": "Hi John, this is Priya from Bidzaro support. I understand you had an issue with your wedding catering?",
      "messageType": "TEXT",
      "timestamp": "2026-02-24T10:35:00.000Z"
    },
    {
      "messageId": "msg-chat-002",
      "senderId": "550e8400-e29b-41d4-a716-446655440000",
      "senderType": "USER",
      "message": "Yes, the food arrived 2 hours late. It caused a lot of stress during my wedding.",
      "messageType": "TEXT",
      "timestamp": "2026-02-24T10:37:00.000Z"
    }
  ],
  "pageInfo": { "totalElements": 8, "totalPages": 1 }
}
```

---

## 4.4 Send Chat Message (Real-Time via WebSocket)

```
WebSocket URL: ws://localhost:8080/api/v1/ws
Headers: Authorization: Bearer {accessToken}

// Send to: /app/chat.sendMessage
{
  "conversationId": "conv-support-77665",
  "message": "I have issued the 10% discount code SORRY10 to your account. Please check your email.",
  "messageType": "TEXT"
}

// Subscribe to receive messages: /topic/conversations.conv-support-77665
```

---

# 5. Order Inquiry & Lookup

## 5.1 Get Order by ID (for Investigation)

**Use:** Look up order details when investigating a customer complaint.

```http
GET /orders/{orderId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "orderId": "order-54321-12345",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "COMPLETED",
    "confirmedAt": "2026-02-25T11:00:00.000Z",
    "completedAt": "2026-05-21T08:00:00.000Z",
    "eventDetails": {
      "eventType": "WEDDING",
      "eventName": "Priya & Rahul Wedding",
      "eventDate": "2026-05-20",
      "eventTime": "18:00",
      "numberOfGuests": 500,
      "venueAddress": {
        "streetAddress": "Palace Grounds, Jayamahal",
        "city": "Bangalore",
        "state": "Karnataka"
      }
    },
    "vendorOrders": [
      {
        "vendorOrderId": "vorder-001",
        "vendorId": "vendor-12345-67890",
        "vendorName": "Spice Garden Catering",
        "vendorStatus": "DELIVERED",
        "deliveryStatus": "DELIVERED",
        "items": [
          { "itemName": "Paneer Tikka", "quantity": 500, "pricePerPlate": 175 },
          { "itemName": "Butter Chicken", "quantity": 400, "pricePerPlate": 206 }
        ],
        "totalAmount": 198000,
        "deliveredAt": "2026-05-20T19:00:00.000Z"
      }
    ],
    "pricing": {
      "subtotal": 169900,
      "serviceCharges": 16990,
      "taxAmount": 16990,
      "platformFee": 3980,
      "totalAmount": 207860,
      "currency": "INR"
    },
    "paymentDetails": {
      "tokenAmount": 51965,
      "tokenPaid": true,
      "tokenPaidAt": "2026-02-25T11:05:00.000Z",
      "balancePaid": 155895,
      "totalPaid": 207860,
      "paymentStatus": "FULLY_PAID",
      "balanceDue": 0
    },
    "specialInstructions": "Separate veg and non-veg counters. Food ready by 6:30 PM.",
    "cancellation": null,
    "createdAt": "2026-02-25T11:00:00.000Z"
  }
}
```

---

## 5.2 Get Orders by User ID

**Use:** See all orders placed by a specific user.

```http
GET /orders?userId=550e8400-e29b-41d4-a716-446655440000&page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "orderId": "order-54321-12345",
      "status": "COMPLETED",
      "eventDetails": { "eventName": "Priya & Rahul Wedding", "eventDate": "2026-05-20" },
      "pricing": { "totalAmount": 207860, "currency": "INR" },
      "paymentDetails": { "paymentStatus": "FULLY_PAID" }
    }
  ]
}
```

---

## 5.3 Get Orders by Vendor ID

**Use:** Check if vendor has delivery issues across multiple orders.

```http
GET /orders?vendorId=vendor-12345-67890&status=DELIVERED&page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "orderId": "order-54321-12345",
      "status": "COMPLETED",
      "eventDetails": { "eventDate": "2026-05-20", "numberOfGuests": 500 },
      "vendorOrders": [
        { "vendorName": "Spice Garden Catering", "deliveryStatus": "DELIVERED" }
      ]
    }
  ]
}
```

---

# 6. User Account Lookup

## 6.1 Get User Profile by ID

**Use:** Look up a user's account details when handling their ticket.

```http
GET /admin/users/{userId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "john.doe@gmail.com",
    "phone": "+917890123456",
    "firstName": "John",
    "lastName": "Doe",
    "fullName": "John Doe",
    "userType": "USER",
    "status": "ACTIVE",
    "country": "INDIA",
    "emailVerified": true,
    "phoneVerified": true,
    "lastLoginAt": "2026-02-24T10:30:45.123Z",
    "createdAt": "2026-01-10T08:00:00.000Z"
  }
}
```

---

## 6.2 Search User by Email or Phone

**Use:** Find a user from their email or phone number provided in ticket.

```http
GET /admin/users/search?query=john.doe@gmail.com&page=0&size=5
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "fullName": "John Doe",
      "email": "john.doe@gmail.com",
      "phone": "+917890123456",
      "userType": "USER",
      "status": "ACTIVE",
      "country": "INDIA",
      "createdAt": "2026-01-10T08:00:00.000Z"
    }
  ]
}
```

---

## 6.3 Get User Loyalty Balance

**Use:** Check if user's loyalty points are correct (for loyalty-related tickets).

```http
GET /loyalty/balance?userId=550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "pointsBalance": 1250,
    "lifetimePoints": 3500,
    "tier": "GOLD",
    "rupeesEquivalent": 312.50
  }
}
```

---

# 7. Vendor Account Lookup

## 7.1 Get Vendor Details by ID

**Use:** Investigate a vendor's profile when handling delivery or quality complaints.

```http
GET /vendors/{vendorId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "vendorId": "vendor-12345-67890",
    "businessName": "Spice Garden Catering",
    "businessEmail": "info@spicegarden.com",
    "businessPhone": "+917890123456",
    "country": "INDIA",
    "verified": true,
    "status": "ACTIVE",
    "approvalStatus": "APPROVED",
    "ratings": {
      "averageRating": 4.7,
      "totalReviews": 312
    },
    "stats": {
      "totalOrders": 600,
      "completedOrders": 596,
      "cancelledOrders": 4,
      "responseTimeMinutes": 12
    },
    "serviceAreas": [
      { "city": "Bangalore", "radiusKm": 30 }
    ],
    "createdAt": "2026-01-15T08:00:00.000Z"
  }
}
```

---

## 7.2 Get Vendor Reviews

**Use:** Check recent vendor reviews for quality complaints.

```http
GET /reviews/vendor/{vendorId}?page=0&size=10
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "reviewId": "rev-11223-44556",
      "userName": "John Doe",
      "rating": 3,
      "reviewText": "Food was good but arrived 2 hours late. Caused a lot of stress during my wedding.",
      "createdAt": "2026-05-21T10:00:00.000Z"
    },
    {
      "reviewId": "rev-11224-44557",
      "userName": "Meera Iyer",
      "rating": 4,
      "reviewText": "Good food but they were 30 minutes late to our corporate event.",
      "createdAt": "2026-05-18T11:00:00.000Z"
    }
  ]
}
```

---

# 8. Payment Inquiry

## 8.1 Get Transaction Details

**Use:** Investigate payment issues — check if payment was captured successfully.

```http
GET /admin/transactions/{transactionId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "transactionId": "txn-99887-66554",
    "orderId": "order-54321-12345",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "paymentType": "TOKEN",
    "amount": {
      "currency": "INR",
      "amount": 51965
    },
    "paymentGateway": "RAZORPAY",
    "gatewayOrderId": "order_RazorpayOrderId123",
    "gatewayTransactionId": "pay_RazorpayPaymentId456",
    "status": "SUCCESS",
    "initiatedAt": "2026-02-25T11:00:00.000Z",
    "processedAt": "2026-02-25T11:05:00.000Z"
  }
}
```

---

## 8.2 Get All Transactions for an Order

**Use:** See full payment history for an order.

```http
GET /payments/order/{orderId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "transactionId": "txn-99887-66554",
      "paymentType": "TOKEN",
      "amount": 51965,
      "currency": "INR",
      "status": "SUCCESS",
      "paymentGateway": "RAZORPAY",
      "processedAt": "2026-02-25T11:05:00.000Z"
    },
    {
      "transactionId": "txn-88776-55443",
      "paymentType": "FULL",
      "amount": 155895,
      "currency": "INR",
      "status": "SUCCESS",
      "paymentGateway": "RAZORPAY",
      "processedAt": "2026-05-18T09:00:00.000Z"
    }
  ]
}
```

---

## 8.3 Check Refund Status

**Use:** Check if a refund has been processed for a customer.

```http
GET /admin/transactions/{transactionId}
Authorization: Bearer {accessToken}
```

### Success Response with Refund Info `200 OK`
```json
{
  "success": true,
  "data": {
    "transactionId": "txn-99887-66554",
    "status": "REFUNDED",
    "refundDetails": {
      "refundId": "refund-001",
      "refundAmount": 51965,
      "refundStatus": "COMPLETED",
      "refundedAt": "2026-02-27T10:00:00.000Z",
      "refundGatewayId": "rfnd_RazorpayRefundId789",
      "estimatedArrival": "2026-03-02"
    }
  }
}
```

---

# 9. Notifications

## 9.1 Get My Notifications

```http
GET /notifications?page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "notificationId": "notif-agent-001",
      "title": "New Ticket Assigned",
      "message": "Ticket TKT-20260224123456 (HIGH priority) has been assigned to you.",
      "type": "TICKET_ASSIGNED",
      "isRead": false,
      "referenceId": "tkt-99001-22334",
      "referenceType": "TICKET",
      "createdAt": "2026-02-24T10:35:00.000Z"
    },
    {
      "notificationId": "notif-agent-002",
      "title": "SLA Breach Warning",
      "message": "Ticket TKT-20260224123456 SLA resolution deadline is in 2 hours!",
      "type": "SLA_WARNING",
      "isRead": false,
      "referenceId": "tkt-99001-22334",
      "referenceType": "TICKET",
      "createdAt": "2026-02-24T08:30:00.000Z"
    },
    {
      "notificationId": "notif-agent-003",
      "title": "New Customer Message",
      "message": "John Doe replied to ticket TKT-20260224123456.",
      "type": "TICKET_REPLY",
      "isRead": true,
      "referenceId": "tkt-99001-22334",
      "referenceType": "TICKET",
      "createdAt": "2026-02-24T11:00:00.000Z"
    }
  ]
}
```

---

## 9.2 Mark Notification as Read

```http
PATCH /notifications/{notificationId}/read
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": { "notificationId": "notif-agent-001", "isRead": true }
}
```

---

## 9.3 Mark All Notifications as Read

```http
PATCH /notifications/read-all
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "All notifications marked as read"
}
```

---

# 10. Dashboard & My Statistics

## 10.1 Get Support Dashboard Statistics

```http
GET /support/admin/stats
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "totalTickets": 2450,
    "openTickets": 45,
    "assignedTickets": 120,
    "inProgressTickets": 85,
    "resolvedTickets": 2180,
    "closedTickets": 20,
    "breachedSLA": 12,
    "averageResolutionTimeMinutes": 280,
    "ticketsByPriority": {
      "URGENT": 5,
      "HIGH": 40,
      "MEDIUM": 125,
      "LOW": 280
    },
    "ticketsByCategory": {
      "ORDER": 200,
      "PAYMENT": 100,
      "VENDOR": 80,
      "ACCOUNT": 50,
      "OTHER": 20
    }
  }
}
```

---

## 10.2 Get My Performance Metrics (Agent Only)

```http
GET /support/agent/my-stats
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "agentId": "agent-priya-001",
    "agentName": "Priya Sharma",
    "period": "This Month",
    "totalAssigned": 52,
    "totalResolved": 48,
    "totalClosed": 45,
    "totalEscalated": 3,
    "averageResolutionTimeMinutes": 220,
    "slaMetRate": 94.2,
    "customerSatisfactionScore": 4.6,
    "openTickets": 7,
    "ticketsByPriority": {
      "URGENT": 2,
      "HIGH": 12,
      "MEDIUM": 28,
      "LOW": 10
    }
  }
}
```

---

# 📋 SLA Reference Guide

## SLA Deadlines by Priority

| Priority | First Response | Resolution |
|----------|---------------|------------|
| `URGENT` | 15 minutes | 4 hours |
| `HIGH` | 1 hour | 24 hours |
| `MEDIUM` | 4 hours | 48 hours |
| `LOW` | 24 hours | 72 hours |

## SLA Status Values

| Status | Meaning |
|--------|---------|
| `ON_TRACK` | Within deadline |
| `AT_RISK` | Within 2 hours of deadline |
| `BREACHED` | Deadline has passed |
| `MET` | Resolved within deadline |

---

# 📋 Ticket Category & Subcategory Reference

| Category | Subcategories |
|----------|---------------|
| `ORDER` | `DELIVERY_ISSUE`, `QUALITY_ISSUE`, `MISSING_ITEMS`, `WRONG_ITEMS`, `ORDER_CANCELLATION`, `OTHER` |
| `PAYMENT` | `PAYMENT_NOT_REFLECTED`, `DOUBLE_CHARGE`, `REFUND_NOT_RECEIVED`, `REFUND_DELAY`, `OTHER` |
| `VENDOR` | `VENDOR_NOT_RESPONDING`, `VENDOR_MISCONDUCT`, `PRICING_DISPUTE`, `OTHER` |
| `ACCOUNT` | `CANT_LOGIN`, `ACCOUNT_SUSPENDED`, `EMAIL_CHANGE`, `PHONE_CHANGE`, `OTHER` |
| `OTHER` | `FEEDBACK`, `SUGGESTION`, `GENERAL_INQUIRY` |

---

# 📌 Support API — Complete Error Code Reference

| Error Code | HTTP Status | Description |
|------------|-------------|-------------|
| `TICKET_NOT_FOUND` | 404 | Ticket doesn't exist |
| `MESSAGE_NOT_FOUND` | 404 | Ticket message not found |
| `TICKET_ALREADY_CLOSED` | 400 | Cannot update a closed ticket |
| `TICKET_ALREADY_RESOLVED` | 400 | Ticket is already resolved |
| `UNAUTHORIZED` | 401 | Not logged in or token expired |
| `FORBIDDEN` | 403 | Action not allowed for your role |
| `USER_NOT_FOUND` | 404 | User doesn't exist |
| `VENDOR_NOT_FOUND` | 404 | Vendor doesn't exist |
| `ORDER_NOT_FOUND` | 404 | Order doesn't exist |
| `TRANSACTION_NOT_FOUND` | 404 | Transaction doesn't exist |
| `VALIDATION_ERROR` | 422 | Field validation failed |
| `CONVERSATION_NOT_FOUND` | 404 | Chat conversation not found |

---

# 📌 Support Agent Quick Reference

## Ticket Handling Checklist

```
1. New Ticket Received:
   ✅ Read subject and description carefully
   ✅ Check related order ID / vendor ID / payment ID
   ✅ Look up order details (GET /orders/{orderId})
   ✅ Look up user details (GET /admin/users/{userId})
   ✅ Update status to IN_PROGRESS
   ✅ Send acknowledgement reply to customer (isInternal: false)
   ✅ Add investigation notes (isInternal: true)

2. During Investigation:
   ✅ Check vendor details if vendor complaint
   ✅ Check transaction status if payment issue
   ✅ Check order status and timeline
   ✅ Chat with vendor if needed (USER_VENDOR chat)
   ✅ Keep adding internal notes

3. Resolution:
   ✅ Draft resolution clearly
   ✅ If compensation needed: get manager approval
   ✅ Send resolution to customer (isInternal: false)
   ✅ Mark ticket as RESOLVED
   ✅ Follow up in 48 hours
   ✅ Close ticket after confirmation
```

## Common Investigation Scenarios

### Scenario 1: Customer says payment deducted but order not confirmed
```
1. GET /orders/{orderId}  — Check order status
2. GET /payments/order/{orderId}  — Check all transactions
3. GET /admin/transactions/{transactionId}  — Check payment status
4. If SUCCESS but order pending → Order confirmation stuck, escalate to tech
5. If FAILED → Payment failed, guide customer to retry
6. If PENDING → Gateway still processing, wait 30 min
```

### Scenario 2: Food quality complaint
```
1. GET /orders/{orderId}  — Confirm order was delivered
2. GET /vendors/{vendorId}  — Check vendor profile and ratings
3. GET /reviews/vendor/{vendorId}  — Check if other customers have same complaint
4. Contact vendor via support chat (USER_VENDOR)
5. If repeated complaints → Escalate to manager for vendor warning
```

### Scenario 3: Refund not received
```
1. GET /admin/transactions/{transactionId}  — Check refund status
2. If COMPLETED → Inform customer of bank processing time (3-7 days)
3. If PENDING → Check with payment gateway team
4. If NOT_INITIATED → Raise to admin for refund processing
```

### Scenario 4: Cannot login
```
1. GET /admin/users/search?query={email}  — Find user
2. Check user status (ACTIVE / SUSPENDED / LOCKED)
3. If LOCKED (failed attempts) → Guide to use forgot password
4. If SUSPENDED → Check suspension reason, escalate if unwarranted
5. If email not verified → Send OTP: POST /auth/send-otp
```

---

# 📌 Important Notes for Support Agents

1. **SLA First:** Always check SLA deadlines first. URGENT tickets = respond in 15 minutes.
2. **Internal Notes:** Use `isInternal: true` for all investigation notes — customers cannot see these.
3. **No Refund Authority:** Refunds above ₹10,000 need manager/admin approval. Always escalate.
4. **Promo Compensation:** You can issue pre-approved promo codes as compensation (check with manager).
5. **Privacy:** Never share user PII (email/phone) with vendors or vice versa.
6. **Vendor Complaints:** Contact vendor via chat before issuing any complaints or warnings.
7. **Escalation:** Always escalate URGENT tickets if you cannot resolve within 1 hour.
8. **Ticket Transfer:** Only support managers can reassign tickets between agents.
9. **Documentation:** Always document what you did in internal notes before closing a ticket.
10. **Reopen Policy:** If customer replies to resolved ticket within 7 days, it auto-reopens.

---

*Support API Documentation — Bidzaro Catering Platform v1.0.0 | Updated: February 24, 2026*

