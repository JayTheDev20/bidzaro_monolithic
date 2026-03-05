# 🎧 SUPPORT AGENT API DOCUMENTATION
## Bidzaro Catering Platform — Complete Support Reference (Real DTO-Based)

**Version:** 1.0.0 | **Base URL:** `http://localhost:8080/api/v1`
**Auth:** `Authorization: Bearer {accessToken}` | **Role Required:** `SUPPORT_AGENT` or `ADMIN`
**Content-Type:** `application/json`

> All field names, types, and response shapes taken directly from actual Java DTO classes.

---

## 🔐 Support Agent Auth
```
POST /auth/login  →  { identifier: "agent@bidzaro.com", password: "..." }
All support APIs  →  Authorization: Bearer {accessToken}
Role required     →  SUPPORT_AGENT or ADMIN
```

---

# 1. GET ALL TICKETS (Agent View)

```http
GET /support/tickets/all?page=0&size=20&status=OPEN&priority=HIGH
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Type | Values |
|-------|------|--------|
| `page` | int | Page number (0-based) |
| `size` | int | Page size (default 20) |
| `status` | String | `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `ESCALATED` |
| `priority` | String | `LOW`, `MEDIUM`, `HIGH`, `URGENT` |
| `category` | String | `ORDER`, `PAYMENT`, `VENDOR`, `ACCOUNT`, `OTHER` |
| `assignedTo` | String | Filter by agent userId |
| `unassigned` | Boolean | `true` to show only unassigned |

### Success Response `200 OK` — List of `TicketResponse` DTOs
```json
{
  "success": true,
  "data": [
    {
      "ticketId": "tkt-99001-22334-eeff",
      "ticketNumber": "TKT-20260224-001",
      "createdBy": "550e8400-e29b-41d4-a716-446655440000",
      "createdByName": "John Doe",
      "category": "ORDER",
      "subcategory": "DELIVERY_ISSUE",
      "priority": "HIGH",
      "subject": "Food arrived 2 hours late for my wedding",
      "description": "The catering team was supposed to arrive by 5 PM but arrived at 7 PM, causing significant inconvenience to 500 guests. This impacted the entire wedding schedule.",
      "relatedEntities": {
        "orderId": "order-54321-12345-ccdd",
        "vendorId": "vendor-12345-67890",
        "paymentId": null
      },
      "assignedTo": null,
      "assignedAt": null,
      "conversationId": "conv-support-tkt99001",
      "status": "OPEN",
      "sla": {
        "firstResponseDue": "2026-02-24T11:30:45.123456Z",
        "resolutionDue": "2026-02-25T10:30:45.123456Z",
        "firstResponseAt": null,
        "resolvedAt": null,
        "slaBreached": false
      },
      "resolution": null,
      "customerSatisfaction": null,
      "createdAt": "2026-02-24T10:30:45.123456Z",
      "closedAt": null
    },
    {
      "ticketId": "tkt-88002-33445-ffgg",
      "ticketNumber": "TKT-20260224-002",
      "createdBy": "user-aabb-ccdd-eeff",
      "createdByName": "Priya Sharma",
      "category": "PAYMENT",
      "subcategory": "REFUND_NOT_RECEIVED",
      "priority": "URGENT",
      "subject": "Refund not received after 10 days",
      "description": "I cancelled my order on Feb 14 and was told refund would arrive in 5-7 days. It is now 10 days and I have not received anything.",
      "relatedEntities": {
        "orderId": "order-11111-22222-aabb",
        "vendorId": null,
        "paymentId": "txn-77665-55443-xxyy"
      },
      "assignedTo": "agent-priya-001",
      "assignedAt": "2026-02-24T09:00:00.000000Z",
      "conversationId": "conv-support-tkt88002",
      "status": "IN_PROGRESS",
      "sla": {
        "firstResponseDue": "2026-02-24T09:15:00.000000Z",
        "resolutionDue": "2026-02-24T13:00:00.000000Z",
        "firstResponseAt": "2026-02-24T09:10:00.000000Z",
        "resolvedAt": null,
        "slaBreached": false
      },
      "resolution": null,
      "customerSatisfaction": null,
      "createdAt": "2026-02-24T09:00:00.000000Z",
      "closedAt": null
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 156, "totalPages": 8 }
}
```

---

# 2. GET TICKET BY ID

```http
GET /support/tickets/{ticketId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — Full `TicketResponse` DTO
```json
{
  "success": true,
  "data": {
    "ticketId": "tkt-99001-22334-eeff",
    "ticketNumber": "TKT-20260224-001",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdByName": "John Doe",
    "category": "ORDER",
    "subcategory": "DELIVERY_ISSUE",
    "priority": "HIGH",
    "subject": "Food arrived 2 hours late for my wedding",
    "description": "The catering team was supposed to arrive by 5 PM but arrived at 7 PM, causing significant inconvenience to 500 guests. This impacted the entire wedding schedule.",
    "relatedEntities": {
      "orderId": "order-54321-12345-ccdd",
      "vendorId": "vendor-12345-67890",
      "paymentId": null
    },
    "assignedTo": "agent-priya-001",
    "assignedAt": "2026-02-24T10:31:00.000000Z",
    "conversationId": "conv-support-tkt99001",
    "status": "IN_PROGRESS",
    "sla": {
      "firstResponseDue": "2026-02-24T11:30:45.123456Z",
      "resolutionDue": "2026-02-25T10:30:45.123456Z",
      "firstResponseAt": "2026-02-24T11:00:00.000000Z",
      "resolvedAt": null,
      "slaBreached": false
    },
    "resolution": null,
    "customerSatisfaction": null,
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "closedAt": null
  }
}
```

---

# 3. ASSIGN TICKET TO AGENT

```http
PATCH /support/tickets/{ticketId}/assign
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "agentId": "agent-priya-001" }
```

### Success Response `200 OK` — Full `TicketResponse` DTO
```json
{
  "success": true,
  "message": "Ticket assigned successfully",
  "data": {
    "ticketId": "tkt-99001-22334-eeff",
    "ticketNumber": "TKT-20260224-001",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdByName": "John Doe",
    "category": "ORDER",
    "subcategory": "DELIVERY_ISSUE",
    "priority": "HIGH",
    "subject": "Food arrived 2 hours late for my wedding",
    "description": "The catering team was supposed to arrive by 5 PM but arrived at 7 PM...",
    "relatedEntities": { "orderId": "order-54321-12345-ccdd", "vendorId": "vendor-12345-67890", "paymentId": null },
    "assignedTo": "agent-priya-001",
    "assignedAt": "2026-02-24T10:31:00.000000Z",
    "conversationId": "conv-support-tkt99001",
    "status": "IN_PROGRESS",
    "sla": {
      "firstResponseDue": "2026-02-24T11:30:45.123456Z",
      "resolutionDue": "2026-02-25T10:30:45.123456Z",
      "firstResponseAt": null,
      "resolvedAt": null,
      "slaBreached": false
    },
    "resolution": null,
    "customerSatisfaction": null,
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "closedAt": null
  }
}
```

---

# 4. UPDATE TICKET STATUS

```http
PATCH /support/tickets/{ticketId}/status
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{
  "status": "IN_PROGRESS",
  "notes": "Investigating the delivery delay. Contacted vendor for explanation."
}
```

| Status | Description |
|--------|-------------|
| `OPEN` | Newly created, unassigned |
| `IN_PROGRESS` | Agent working on it |
| `WAITING_FOR_CUSTOMER` | Waiting on customer info |
| `WAITING_FOR_VENDOR` | Waiting on vendor info |
| `ESCALATED` | Escalated to senior/admin |
| `RESOLVED` | Issue resolved |
| `CLOSED` | Ticket closed |

### Success Response `200 OK` — Full `TicketResponse` DTO
```json
{
  "success": true,
  "message": "Ticket status updated",
  "data": {
    "ticketId": "tkt-99001-22334-eeff",
    "ticketNumber": "TKT-20260224-001",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdByName": "John Doe",
    "category": "ORDER",
    "subcategory": "DELIVERY_ISSUE",
    "priority": "HIGH",
    "subject": "Food arrived 2 hours late for my wedding",
    "description": "The catering team was supposed to arrive by 5 PM...",
    "relatedEntities": { "orderId": "order-54321-12345-ccdd", "vendorId": "vendor-12345-67890", "paymentId": null },
    "assignedTo": "agent-priya-001",
    "assignedAt": "2026-02-24T10:31:00.000000Z",
    "conversationId": "conv-support-tkt99001",
    "status": "IN_PROGRESS",
    "sla": {
      "firstResponseDue": "2026-02-24T11:30:45.123456Z",
      "resolutionDue": "2026-02-25T10:30:45.123456Z",
      "firstResponseAt": "2026-02-24T11:00:00.000000Z",
      "resolvedAt": null,
      "slaBreached": false
    },
    "resolution": null,
    "customerSatisfaction": null,
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "closedAt": null
  }
}
```

---

# 5. RESOLVE TICKET

```http
PATCH /support/tickets/{ticketId}/resolve
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{
  "resolutionType": "COMPENSATION",
  "resolutionNotes": "Vendor confirmed the 2-hour delay was due to traffic. Issued ₹5,000 loyalty points compensation to customer. Vendor warned — third such complaint will result in suspension.",
  "resolvedBy": "agent-priya-001"
}
```

| resolutionType | Description |
|----------------|-------------|
| `RESOLVED` | Issue resolved, no compensation |
| `COMPENSATION` | Compensation provided |
| `REFUND` | Refund issued |
| `ESCALATED` | Escalated to higher team |
| `VENDOR_ACTION` | Action taken against vendor |
| `NO_ACTION_REQUIRED` | Not a valid complaint |

### Success Response `200 OK` — Full `TicketResponse` DTO with resolution
```json
{
  "success": true,
  "message": "Ticket resolved",
  "data": {
    "ticketId": "tkt-99001-22334-eeff",
    "ticketNumber": "TKT-20260224-001",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdByName": "John Doe",
    "category": "ORDER",
    "subcategory": "DELIVERY_ISSUE",
    "priority": "HIGH",
    "subject": "Food arrived 2 hours late for my wedding",
    "description": "The catering team was supposed to arrive by 5 PM...",
    "relatedEntities": { "orderId": "order-54321-12345-ccdd", "vendorId": "vendor-12345-67890", "paymentId": null },
    "assignedTo": "agent-priya-001",
    "assignedAt": "2026-02-24T10:31:00.000000Z",
    "conversationId": "conv-support-tkt99001",
    "status": "RESOLVED",
    "sla": {
      "firstResponseDue": "2026-02-24T11:30:45.123456Z",
      "resolutionDue": "2026-02-25T10:30:45.123456Z",
      "firstResponseAt": "2026-02-24T11:00:00.000000Z",
      "resolvedAt": "2026-02-24T16:00:00.000000Z",
      "slaBreached": false
    },
    "resolution": {
      "resolutionType": "COMPENSATION",
      "resolutionNotes": "Vendor confirmed the 2-hour delay was due to traffic. Issued ₹5,000 loyalty points compensation to customer. Vendor warned.",
      "resolvedBy": "agent-priya-001",
      "resolvedAt": "2026-02-24T16:00:00.000000Z"
    },
    "customerSatisfaction": null,
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "closedAt": null
  }
}
```

---

# 6. ESCALATE TICKET

```http
PATCH /support/tickets/{ticketId}/escalate
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{
  "reason": "Customer is threatening legal action. Refund amount is ₹2,50,000. Requires senior approval.",
  "escalateTo": "admin-001"
}
```

### Success Response `200 OK` — Full `TicketResponse` DTO
```json
{
  "success": true,
  "message": "Ticket escalated",
  "data": {
    "ticketId": "tkt-88002-33445-ffgg",
    "ticketNumber": "TKT-20260224-002",
    "createdBy": "user-aabb-ccdd-eeff",
    "createdByName": "Priya Sharma",
    "category": "PAYMENT",
    "subcategory": "REFUND_NOT_RECEIVED",
    "priority": "URGENT",
    "subject": "Refund not received after 10 days",
    "description": "I cancelled my order on Feb 14 and was told refund would arrive in 5-7 days...",
    "relatedEntities": { "orderId": "order-11111-22222-aabb", "vendorId": null, "paymentId": "txn-77665-55443-xxyy" },
    "assignedTo": "admin-001",
    "assignedAt": "2026-02-24T12:00:00.000000Z",
    "conversationId": "conv-support-tkt88002",
    "status": "ESCALATED",
    "sla": {
      "firstResponseDue": "2026-02-24T09:15:00.000000Z",
      "resolutionDue": "2026-02-24T13:00:00.000000Z",
      "firstResponseAt": "2026-02-24T09:10:00.000000Z",
      "resolvedAt": null,
      "slaBreached": true
    },
    "resolution": null,
    "customerSatisfaction": null,
    "createdAt": "2026-02-24T09:00:00.000000Z",
    "closedAt": null
  }
}
```

---

# 7. CLOSE TICKET

```http
PATCH /support/tickets/{ticketId}/close
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "reason": "Issue fully resolved. Customer confirmed satisfaction. Ticket closed." }
```

### Success Response `200 OK` — Full `TicketResponse` DTO
```json
{
  "success": true,
  "message": "Ticket closed",
  "data": {
    "ticketId": "tkt-99001-22334-eeff",
    "ticketNumber": "TKT-20260224-001",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdByName": "John Doe",
    "category": "ORDER",
    "subcategory": "DELIVERY_ISSUE",
    "priority": "HIGH",
    "subject": "Food arrived 2 hours late for my wedding",
    "description": "The catering team was supposed to arrive by 5 PM...",
    "relatedEntities": { "orderId": "order-54321-12345-ccdd", "vendorId": "vendor-12345-67890", "paymentId": null },
    "assignedTo": "agent-priya-001",
    "assignedAt": "2026-02-24T10:31:00.000000Z",
    "conversationId": "conv-support-tkt99001",
    "status": "CLOSED",
    "sla": {
      "firstResponseDue": "2026-02-24T11:30:45.123456Z",
      "resolutionDue": "2026-02-25T10:30:45.123456Z",
      "firstResponseAt": "2026-02-24T11:00:00.000000Z",
      "resolvedAt": "2026-02-24T16:00:00.000000Z",
      "slaBreached": false
    },
    "resolution": {
      "resolutionType": "COMPENSATION",
      "resolutionNotes": "Vendor confirmed the delay. Issued ₹5,000 loyalty points compensation.",
      "resolvedBy": "agent-priya-001",
      "resolvedAt": "2026-02-24T16:00:00.000000Z"
    },
    "customerSatisfaction": {
      "rating": 4,
      "feedback": "Agent was helpful and resolved quickly"
    },
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "closedAt": "2026-02-24T16:30:00.000000Z"
  }
}
```

---

# 8. REOPEN TICKET

```http
PATCH /support/tickets/{ticketId}/reopen
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "reason": "Customer says issue was not resolved. Loyalty points were not credited to account." }
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Ticket reopened",
  "data": {
    "ticketId": "tkt-99001-22334-eeff",
    "ticketNumber": "TKT-20260224-001",
    "status": "IN_PROGRESS",
    "sla": {
      "firstResponseDue": "2026-02-24T11:30:45.123456Z",
      "resolutionDue": "2026-02-26T10:00:00.000000Z",
      "firstResponseAt": "2026-02-24T11:00:00.000000Z",
      "resolvedAt": null,
      "slaBreached": false
    },
    "resolution": null,
    "customerSatisfaction": null,
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "closedAt": null,
    "...rest of TicketResponse fields..."
  }
}
```

---

# 9. SEND MESSAGE TO CUSTOMER (via Ticket Chat)

```http
POST /support/tickets/{ticketId}/messages
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{
  "message": "Hi John, I have investigated the delivery delay with Spice Garden Catering. The vendor confirmed it was due to heavy traffic on Palace Grounds Road. I am issuing 5,000 loyalty points as compensation. Please check your loyalty balance. Apologies for the inconvenience.",
  "isInternalNote": false,
  "attachmentUrls": []
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `message` | ✅ | Message text (min 1 char) |
| `isInternalNote` | ✅ | `true` = only visible to agents; `false` = visible to customer |
| `attachmentUrls` | ❌ | List of file URLs |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "messageId": "tmsg-001-aabb",
    "ticketId": "tkt-99001-22334-eeff",
    "ticketNumber": "TKT-20260224-001",
    "senderId": "agent-priya-001",
    "senderName": "Priya Sharma",
    "senderRole": "SUPPORT_AGENT",
    "message": "Hi John, I have investigated the delivery delay with Spice Garden Catering...",
    "isInternalNote": false,
    "attachmentUrls": [],
    "createdAt": "2026-02-24T11:00:00.000000Z"
  }
}
```

---

# 10. GET TICKET MESSAGES

```http
GET /support/tickets/{ticketId}/messages?page=0&size=50
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "messageId": "tmsg-000-orig",
      "ticketId": "tkt-99001-22334-eeff",
      "ticketNumber": "TKT-20260224-001",
      "senderId": "550e8400-e29b-41d4-a716-446655440000",
      "senderName": "John Doe",
      "senderRole": "USER",
      "message": "The catering team was supposed to arrive by 5 PM but arrived at 7 PM, causing significant inconvenience to 500 guests.",
      "isInternalNote": false,
      "attachmentUrls": ["http://localhost:8080/uploads/images/complaint-photo-1.jpg"],
      "createdAt": "2026-02-24T10:30:45.123456Z"
    },
    {
      "messageId": "tmsg-001-note",
      "ticketId": "tkt-99001-22334-eeff",
      "ticketNumber": "TKT-20260224-001",
      "senderId": "agent-priya-001",
      "senderName": "Priya Sharma",
      "senderRole": "SUPPORT_AGENT",
      "message": "Internal note: Contacted vendor. Vendor claims traffic jam on Palace Grounds road. Checking GPS logs.",
      "isInternalNote": true,
      "attachmentUrls": [],
      "createdAt": "2026-02-24T10:45:00.000000Z"
    },
    {
      "messageId": "tmsg-002-reply",
      "ticketId": "tkt-99001-22334-eeff",
      "ticketNumber": "TKT-20260224-001",
      "senderId": "agent-priya-001",
      "senderName": "Priya Sharma",
      "senderRole": "SUPPORT_AGENT",
      "message": "Hi John, I have investigated the delivery delay. The vendor confirmed it was due to heavy traffic. I am issuing 5,000 loyalty points as compensation.",
      "isInternalNote": false,
      "attachmentUrls": [],
      "createdAt": "2026-02-24T11:00:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 50, "totalElements": 3, "totalPages": 1 }
}
```

---

# 11. LOOK UP ORDER DETAILS (For Investigation)

```http
GET /admin/orders/{orderId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — Full `OrderResponse` DTO
```json
{
  "success": true,
  "data": {
    "orderId": "order-54321-12345-ccdd",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "bidRequestId": "breq-88990-77665-aabb",
    "eventDetails": {
      "eventType": "WEDDING",
      "eventName": "Priya & Rahul Wedding",
      "eventDate": "2026-05-20",
      "eventTime": "18:00",
      "numberOfGuests": 500,
      "venueAddress": {
        "streetAddress": "Palace Grounds, Jayamahal Road",
        "city": "Bangalore",
        "state": "Karnataka",
        "postalCode": "560080",
        "country": "India"
      }
    },
    "vendorOrders": [
      {
        "vendorOrderId": "vorder-001-aabb",
        "vendorId": "vendor-12345-67890",
        "vendorUserId": null,
        "vendorName": "Spice Garden Catering",
        "items": [
          { "vendorItemId": "vitem-001", "itemName": "Paneer Tikka", "quantity": 500, "pricePerPlate": 175.00, "totalPrice": 87500.00 },
          { "vendorItemId": "vitem-002", "itemName": "Butter Chicken", "quantity": 400, "pricePerPlate": 218.75, "totalPrice": 87500.00 }
        ],
        "subtotal": 175000.00,
        "serviceCharge": 17500.00,
        "taxAmount": 8750.00,
        "totalAmount": 201250.00,
        "vendorStatus": "DELIVERED",
        "deliveryStatus": "DELIVERED"
      }
    ],
    "pricing": {
      "currency": "INR",
      "subtotal": 175000.00,
      "serviceCharges": 17500.00,
      "taxAmount": 8750.00,
      "platformFee": 4025.00,
      "discountAmount": 0.00,
      "totalAmount": 205275.00
    },
    "paymentDetails": {
      "tokenAmount": 51318.75,
      "tokenPaid": true,
      "tokenPaidAt": "2026-02-25T11:05:00.000000Z",
      "totalPaid": 51318.75,
      "balanceDue": 153956.25,
      "paymentStatus": "TOKEN_PAID"
    },
    "contactInfo": {
      "primaryContactName": "John Doe",
      "primaryContactPhone": "+917890123456",
      "primaryContactEmail": "john.doe@gmail.com"
    },
    "specialInstructions": "Separate veg and non-veg sections. Food ready by 6:30 PM sharp.",
    "status": "CONFIRMED",
    "cancellation": null,
    "createdAt": "2026-02-25T11:00:00.000000Z",
    "confirmedAt": "2026-02-25T11:00:00.000000Z",
    "deliveredAt": "2026-05-20T20:00:00.000000Z",
    "completedAt": null
  }
}
```

---

# 12. LOOK UP USER DETAILS (For Investigation)

```http
GET /admin/users/{userId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — Full `UserResponse` DTO
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "vendorId": null,
    "email": "john.doe@gmail.com",
    "phone": "+917890123456",
    "userType": "USER",
    "firstName": "John",
    "lastName": "Doe",
    "fullName": "John Doe",
    "profilePictureUrl": "http://localhost:8080/uploads/images/profile-550e8400.jpg",
    "dateOfBirth": "1992-06-15",
    "gender": "MALE",
    "emailVerified": true,
    "phoneVerified": true,
    "twoFactorEnabled": false,
    "preferredLanguage": "en",
    "preferredCurrency": "INR",
    "country": "INDIA",
    "status": "ACTIVE",
    "notificationPreferences": {
      "emailNotifications": { "orderUpdates": true, "bidUpdates": true, "promotional": false, "newsletter": false, "paymentReminders": true, "securityAlerts": true },
      "smsNotifications": { "orderUpdates": true, "bidUpdates": true, "paymentReminders": true, "securityAlerts": true },
      "pushNotifications": { "orderUpdates": true, "bidUpdates": true, "promotional": false, "paymentReminders": true },
      "whatsappNotifications": { "orderUpdates": false, "bidUpdates": false }
    },
    "lastLoginAt": "2026-02-24T10:30:45.123456Z",
    "createdAt": "2026-01-10T08:00:00.000000Z"
  }
}
```

---

# 13. LOOK UP VENDOR DETAILS (For Investigation)

```http
GET /admin/vendors/{vendorId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — Full `VendorResponse` DTO
```json
{
  "success": true,
  "data": {
    "vendorId": "vendor-12345-67890",
    "userId": "vendor-user-550e8400",
    "registeredEmail": "owner@spicegarden.com",
    "registeredPhone": "+917890123456",
    "registeredEmailVerified": true,
    "registeredPhoneVerified": true,
    "businessName": "Spice Garden Catering",
    "businessEmail": "info@spicegarden.com",
    "businessPhone": "+917890123456",
    "businessEmailVerified": true,
    "businessPhoneVerified": true,
    "businessType": "CATERING",
    "businessRegistrationNumber": "KA-REG-2015-12345",
    "taxId": "29ABCDE1234F1Z5",
    "logoUrl": "http://localhost:8080/uploads/images/logo-spice.jpg",
    "bannerUrl": "http://localhost:8080/uploads/images/banner-spice.jpg",
    "description": "Authentic South Indian catering since 2010.",
    "establishedYear": 2010,
    "cuisinesOffered": ["South Indian", "North Indian", "Continental"],
    "specialties": ["Weddings", "Corporate Events"],
    "businessAddress": { "streetAddress": "25, 3rd Cross, Jayanagar 4th Block", "city": "Bangalore", "state": "Karnataka", "postalCode": "560041", "country": "India" },
    "ownerInfo": { "firstName": "Rajesh", "lastName": "Kumar", "phone": "+917890123456", "email": "owner@spicegarden.com", "idProofType": "AADHAR", "idProofNumber": "XXXX-XXXX-9012" },
    "serviceAreas": [ { "city": "Bangalore", "state": "Karnataka", "radiusKm": 30 } ],
    "capacity": { "minGuests": 50, "maxGuests": 3000, "concurrentEvents": 4 },
    "pricing": { "currency": "INR", "startingPricePerPlate": 350.00, "averagePricePerPlate": 500.00 },
    "ratings": { "averageRating": 4.7, "totalReviews": 312 },
    "stats": { "totalOrders": 600, "completedOrders": 596 },
    "status": "ACTIVE",
    "approvalStatus": "APPROVED",
    "verified": true,
    "featured": false,
    "createdAt": "2026-01-15T08:00:00.000000Z",
    "documents": [
      {
        "documentId": "doc-001-aabb",
        "documentType": "BUSINESS_LICENSE",
        "documentName": "FSSAI Food License",
        "documentUrl": "http://localhost:8080/uploads/documents/fssai-license.pdf",
        "documentNumber": "FSSAI-2024-123456",
        "issueDate": "2024-01-15T00:00:00.000000Z",
        "expiryDate": "2027-01-14T00:00:00.000000Z",
        "verificationStatus": "VERIFIED",
        "uploadedAt": "2026-01-15T08:00:00.000000Z"
      }
    ],
    "country": "INDIA"
  }
}
```

---

# 14. LOOK UP PAYMENT / TRANSACTION (For Investigation)

```http
GET /admin/payments/{transactionId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "transactionId": "txn-77665-55443-xxyy",
    "orderId": "order-11111-22222-aabb",
    "userId": "user-aabb-ccdd-eeff",
    "vendorId": "vendor-12345-67890",
    "paymentType": "FULL",
    "amount": 205275.00,
    "currency": "INR",
    "status": "SUCCESS",
    "paymentGateway": "RAZORPAY",
    "gatewayOrderId": "order_RazpABC1234567890XY",
    "gatewayTransactionId": "pay_RazpDEF9876543210AB",
    "refundStatus": "REFUND_INITIATED",
    "refundAmount": 205275.00,
    "refundInitiatedAt": "2026-02-14T12:00:00.000000Z",
    "processedAt": "2026-02-10T09:00:00.000000Z",
    "createdAt": "2026-02-10T09:00:00.000000Z"
  }
}
```

---

# 15. GET USER'S ORDERS (Investigation Context)

```http
GET /admin/orders?userId=550e8400-e29b-41d4-a716-446655440000&page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — Paginated list of `OrderResponse` DTOs (same structure as section 11)

---

# 16. CHAT WITH USER (Support Conversation)

```http
POST /chat/conversations
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "otherUserId": "550e8400-e29b-41d4-a716-446655440000", "type": "SUPPORT" }
```

### Success Response `201 Created` — `ConversationResponse` DTO
```json
{
  "success": true,
  "data": {
    "conversationId": "conv-support-99001-aabb",
    "participants": [
      { "userId": "agent-priya-001", "userType": "SUPPORT_AGENT", "name": "Priya Sharma" },
      { "userId": "550e8400-e29b-41d4-a716-446655440000", "userType": "USER", "name": "John Doe" }
    ],
    "otherParticipant": { "userId": "550e8400-e29b-41d4-a716-446655440000", "userType": "USER", "name": "John Doe" },
    "lastMessage": null,
    "unreadCount": 0,
    "status": "ACTIVE",
    "createdAt": "2026-02-24T10:31:00.000000Z",
    "updatedAt": "2026-02-24T10:31:00.000000Z"
  }
}
```

---

# 17. GET SUPPORT CONVERSATIONS

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
      "conversationId": "conv-support-99001-aabb",
      "participants": [
        { "userId": "agent-priya-001", "userType": "SUPPORT_AGENT", "name": "Priya Sharma" },
        { "userId": "550e8400-e29b-41d4-a716-446655440000", "userType": "USER", "name": "John Doe" }
      ],
      "otherParticipant": { "userId": "550e8400-e29b-41d4-a716-446655440000", "userType": "USER", "name": "John Doe" },
      "lastMessage": {
        "message": "Hi Priya, I still haven't received the loyalty points.",
        "senderId": "550e8400-e29b-41d4-a716-446655440000",
        "senderType": "USER",
        "timestamp": "2026-02-24T16:45:00.000000Z"
      },
      "unreadCount": 1,
      "status": "ACTIVE",
      "createdAt": "2026-02-24T10:31:00.000000Z",
      "updatedAt": "2026-02-24T16:45:00.000000Z"
    }
  ]
}
```

---

# 18. GET CHAT MESSAGES IN CONVERSATION

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
      "messageId": "msg-001-aaaa",
      "conversationId": "conv-support-99001-aabb",
      "senderId": "agent-priya-001",
      "senderType": "SUPPORT_AGENT",
      "senderName": null,
      "message": "Hi John, we've processed 5,000 loyalty points to your account. Please check your balance.",
      "messageType": "TEXT",
      "attachments": null,
      "timestamp": "2026-02-24T11:30:00.000000Z"
    },
    {
      "messageId": "msg-002-bbbb",
      "conversationId": "conv-support-99001-aabb",
      "senderId": "550e8400-e29b-41d4-a716-446655440000",
      "senderType": "USER",
      "senderName": null,
      "message": "Hi Priya, I still haven't received the loyalty points.",
      "messageType": "TEXT",
      "attachments": null,
      "timestamp": "2026-02-24T16:45:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 50, "totalElements": 2, "totalPages": 1 }
}
```

### WebSocket — Support Agent Real-Time Chat
```
SEND to: /app/chat.sendMessage
{
  "conversationId": "conv-support-99001-aabb",
  "message": "I can see the points were issued. Please try refreshing your loyalty balance page.",
  "messageType": "TEXT"
}
SUBSCRIBE: /topic/conversations.conv-support-99001-aabb
```

---

# 📌 SLA Reference Table

| Priority | First Response Due | Resolution Due |
|----------|--------------------|----------------|
| `URGENT` | 15 minutes | 4 hours |
| `HIGH` | 1 hour | 24 hours |
| `MEDIUM` | 4 hours | 48 hours |
| `LOW` | 24 hours | 72 hours |

> `slaBreached: true` means SLA was violated. Monitor these tickets immediately.

---

# 📌 Ticket Category Reference

| Category | Subcategories |
|----------|--------------|
| `ORDER` | `DELIVERY_ISSUE`, `QUALITY_COMPLAINT`, `WRONG_ITEMS`, `MISSING_ITEMS`, `VENDOR_NO_SHOW` |
| `PAYMENT` | `REFUND_NOT_RECEIVED`, `DOUBLE_CHARGED`, `PAYMENT_FAILED`, `INCORRECT_AMOUNT` |
| `VENDOR` | `VENDOR_FRAUD`, `VENDOR_UNRESPONSIVE`, `QUALITY_ISSUE`, `WRONG_QUOTE` |
| `ACCOUNT` | `LOGIN_ISSUE`, `ACCOUNT_LOCKED`, `PROFILE_UPDATE`, `DATA_ISSUE` |
| `OTHER` | `GENERAL_INQUIRY`, `FEATURE_REQUEST`, `BUG_REPORT` |

---

# 📌 Common Investigation Workflows

## 🔍 Scenario 1: Delivery Delay Complaint
```
1. GET /admin/orders/{orderId}           → Check vendorStatus and deliveredAt
2. GET /admin/users/{userId}             → Verify customer is legitimate
3. GET /admin/vendors/{vendorId}         → Check vendor history (stats.completedOrders)
4. POST /support/tickets/{id}/messages   → Reply to customer with findings
5. PATCH /support/tickets/{id}/resolve   → resolutionType: COMPENSATION or VENDOR_ACTION
```

## 🔍 Scenario 2: Refund Not Received
```
1. GET /admin/payments/{transactionId}   → Check refundStatus, refundInitiatedAt
2. GET /admin/orders/{orderId}           → Check cancellation.refundStatus
3. POST /support/tickets/{id}/messages   → Inform customer of refund timeline
4. POST /admin/payments/{id}/refund      → If refund not initiated yet, trigger it
5. PATCH /support/tickets/{id}/resolve   → resolutionType: REFUND
```

## 🔍 Scenario 3: Wrong Items Delivered
```
1. GET /admin/orders/{orderId}           → Check vendorOrders[].items vs customer claim
2. GET /admin/vendors/{vendorId}         → Check vendor complaint history
3. POST /support/tickets/{id}/messages   → Ask customer for photo evidence
4. PATCH /support/tickets/{id}/escalate  → If amount > ₹50,000, escalate to admin
5. PATCH /support/tickets/{id}/resolve   → resolutionType: COMPENSATION or REFUND
```

## 🔍 Scenario 4: Login / Account Issue
```
1. GET /admin/users?search={email}       → Find user account
2. GET /admin/users/{userId}             → Check status (LOCKED, SUSPENDED)
3. PATCH /admin/users/{id}/activate      → If wrongly locked, reactivate
4. POST /support/tickets/{id}/messages   → Guide customer
5. PATCH /support/tickets/{id}/resolve   → resolutionType: RESOLVED
```

---

# 📌 Error Code Reference

| HTTP | Error Code | Description |
|------|-----------|-------------|
| 400 | `BAD_REQUEST` | Invalid request data |
| 400 | `TICKET_ALREADY_CLOSED` | Cannot update closed ticket |
| 400 | `INVALID_STATUS_TRANSITION` | Invalid status change |
| 401 | `UNAUTHORIZED` | Token missing or expired |
| 403 | `FORBIDDEN` | Not a support agent or admin |
| 404 | `TICKET_NOT_FOUND` | Ticket does not exist |
| 404 | `ORDER_NOT_FOUND` | Order does not exist |
| 404 | `USER_NOT_FOUND` | User does not exist |
| 404 | `VENDOR_NOT_FOUND` | Vendor does not exist |
| 404 | `TRANSACTION_NOT_FOUND` | Transaction does not exist |
| 409 | `TICKET_ALREADY_ASSIGNED` | Ticket is already assigned |

---

*SUPPORT_API_DOCS.md — Based on actual Java DTOs (TicketResponse, CreateTicketRequest, UserResponse, VendorResponse, OrderResponse, ChatMessageResponse, ConversationResponse, PaymentInitiationResponse)*
*Bidzaro Catering Platform v1.0.0 | Generated: February 24, 2026*

