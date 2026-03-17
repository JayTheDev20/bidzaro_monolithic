# 💳 PAYMENT GATEWAY & ORDER CREATION APIs

**Date**: March 17, 2026
**Platform**: Multi-region (USA via Stripe, India via Razorpay)
**Status**: ✅ FULLY INTEGRATED

---

## 🎯 OVERVIEW

The system supports **dual payment gateways**:
- 🇺🇸 **Stripe** (USA) - Credit cards, digital wallets
- 🇮🇳 **Razorpay** (India) - UPI, cards, netbanking

Orders are **automatically created after successful payment verification**.

---

## 📋 PAYMENT FLOW

```
User Selects Items
    ↓
Click Checkout
    ↓
POST /payments/initiate
    ↓ (Determine region & gateway)
    ├─ USA → Stripe
    └─ India → Razorpay
    ↓
User completes payment
    ↓
Payment gateway sends webhook
    ↓
Backend verifies & creates order
    ↓
Order ready for fulfillment ✅
```

---

## 🔑 API ENDPOINTS

### 1️⃣ PAYMENT INITIATION

**Endpoint**: `POST /api/v1/payments/initiate`

**Authentication**: ✅ Required (Bearer Token)

**Purpose**: Initialize payment for order or bid

**Request Body**:
```json
{
  "orderId": "order-uuid-or-null",
  "bidId": "bid-uuid-or-null",
  "paymentType": "ORDER|BID",
  "amount": 5000.00
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "status": 200,
  "message": "Payment initiated",
  "data": {
    "transactionId": "txn-uuid-12345",
    "orderId": "order-uuid",
    "bidId": null,
    "userId": "user-uuid",
    "paymentType": "ORDER",
    "amount": {
      "value": 5000.00,
      "currency": "USD|INR"
    },
    "paymentGateway": "STRIPE|RAZORPAY",
    "gatewayOrderId": "stripe_pi_xxxxx|razorpay_order_xxxxx",
    "clientSecret": "pi_xxxxx_secret_yyyyy",
    "status": "PENDING",
    "initiatedAt": "2026-03-17T08:30:00Z"
  }
}
```

**Used When**:
- ✅ Checkout from shopping cart
- ✅ Accepting a vendor bid
- ✅ Paying for custom catering request

---

### 2️⃣ PAYMENT VERIFICATION

**Endpoint**: `POST /api/v1/payments/verify`

**Authentication**: ✅ Required (Bearer Token)

**Purpose**: Verify payment after gateway confirmation

**Request Body**:
```json
{
  "gatewayOrderId": "stripe_pi_xxxxx|razorpay_order_xxxxx",
  "gatewayPaymentId": "stripe_ch_xxxxx|razorpay_payment_xxxxx",
  "signature": "webhook_signature_hash"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "status": 200,
  "message": "Payment verified successfully",
  "data": {
    "transactionId": "txn-uuid-12345",
    "orderId": "order-uuid",
    "userId": "user-uuid",
    "amount": {
      "value": 5000.00,
      "currency": "USD"
    },
    "paymentGateway": "STRIPE",
    "status": "SUCCESS",
    "verifiedAt": "2026-03-17T08:32:00Z"
  }
}
```

**Triggers**:
- 🟢 Order creation
- 🟢 Order confirmation
- 🟢 Payment confirmation email sent

---

### 3️⃣ GET USER TRANSACTIONS

**Endpoint**: `GET /api/v1/payments/transactions?page=0&size=20`

**Authentication**: ✅ Required

**Purpose**: View payment history

**Query Parameters**:
```
page: 0-based page number (default: 0)
size: results per page (default: 20)
```

**Response** (200 OK):
```json
{
  "success": true,
  "status": 200,
  "message": "Transactions retrieved",
  "data": [
    {
      "transactionId": "txn-uuid-1",
      "orderId": "order-uuid-1",
      "amount": {
        "value": 5000.00,
        "currency": "USD"
      },
      "status": "SUCCESS",
      "createdAt": "2026-03-17T08:30:00Z"
    },
    {
      "transactionId": "txn-uuid-2",
      "orderId": "order-uuid-2",
      "amount": {
        "value": 3500.00,
        "currency": "USD"
      },
      "status": "SUCCESS",
      "createdAt": "2026-03-15T15:45:00Z"
    }
  ],
  "pagination": {
    "currentPage": 0,
    "pageSize": 20,
    "totalElements": 25,
    "totalPages": 2
  }
}
```

---

### 4️⃣ GET TRANSACTION DETAILS

**Endpoint**: `GET /api/v1/payments/transactions/{transactionId}`

**Authentication**: ✅ Required

**Purpose**: Get specific transaction details

**Response** (200 OK):
```json
{
  "success": true,
  "status": 200,
  "data": {
    "transactionId": "txn-uuid-12345",
    "orderId": "order-uuid",
    "bidId": null,
    "userId": "user-uuid",
    "paymentType": "ORDER",
    "amount": {
      "value": 5000.00,
      "currency": "USD"
    },
    "paymentGateway": "STRIPE",
    "gatewayOrderId": "stripe_pi_xxxxx",
    "gatewayPaymentId": "stripe_ch_xxxxx",
    "status": "SUCCESS",
    "initiatedAt": "2026-03-17T08:30:00Z",
    "verifiedAt": "2026-03-17T08:32:00Z",
    "metadata": {
      "customerEmail": "user@example.com",
      "customerName": "John Doe"
    }
  }
}
```

---

### 5️⃣ GET ORDER TRANSACTIONS

**Endpoint**: `GET /api/v1/payments/order/{orderId}`

**Authentication**: ✅ Required

**Purpose**: Get all transactions for an order

**Response** (200 OK):
```json
{
  "success": true,
  "status": 200,
  "data": [
    {
      "transactionId": "txn-uuid-1",
      "status": "SUCCESS",
      "amount": 5000.00,
      "currency": "USD",
      "createdAt": "2026-03-17T08:30:00Z"
    },
    {
      "transactionId": "txn-uuid-2",
      "status": "REFUNDED",
      "amount": 2500.00,
      "currency": "USD",
      "createdAt": "2026-03-17T09:15:00Z"
    }
  ]
}
```

---

### 6️⃣ WEBHOOK - RAZORPAY

**Endpoint**: `POST /api/v1/payments/webhook/razorpay`

**Headers Required**:
```
X-Razorpay-Signature: signature_hash
```

**Payload** (from Razorpay):
```json
{
  "event": "payment.authorized|payment.failed|payment.captured",
  "payload": {
    "payment": {
      "entity": "payment",
      "id": "pay_xxxxx",
      "entity_id": "order_xxxxx",
      "amount": 500000,
      "currency": "INR",
      "status": "authorized|failed|captured"
    }
  }
}
```

**Triggers**:
- ✅ Order creation on successful payment
- ✅ Refund processing on failed payment
- ✅ Notification emails sent

---

### 7️⃣ WEBHOOK - STRIPE

**Endpoint**: `POST /api/v1/payments/webhook/stripe`

**Headers Required**:
```
Stripe-Signature: signature_hash
```

**Payload** (from Stripe):
```json
{
  "type": "charge.succeeded|charge.failed|charge.refunded",
  "data": {
    "object": {
      "id": "ch_xxxxx",
      "payment_intent": "pi_xxxxx",
      "amount": 500000,
      "currency": "usd",
      "status": "succeeded|failed"
    }
  }
}
```

---

## 📦 ORDER CREATION (AUTO)

Orders are **automatically created** after payment verification.

No manual `/orders/create` endpoint needed!

### Automatic Flow

```
Payment Verified
    ↓
TransactionStatus = SUCCESS
    ↓
PaymentService.verifyPayment()
    ↓
OrderService.createOrder() [auto]
    ↓
Order Status = CONFIRMED
    ↓
Vendor notified ✅
    ↓
User gets order details ✅
```

---

## 🎯 ORDER MANAGEMENT APIs

### 1. GET USER ORDERS

**Endpoint**: `GET /api/v1/orders?page=0&size=20`

**Response**:
```json
{
  "success": true,
  "data": [
    {
      "orderId": "order-uuid",
      "vendorId": "vendor-uuid",
      "userId": "user-uuid",
      "status": "CONFIRMED|PREPARING|READY|COMPLETED|CANCELLED",
      "items": [
        {
          "itemId": "item-uuid",
          "name": "Biryani",
          "quantity": 50,
          "pricePerPlate": 280,
          "totalPrice": 14000
        }
      ],
      "eventDetails": {
        "eventDate": "2026-04-15",
        "eventTime": "18:00:00",
        "guestCount": 50,
        "venue": "Hotel Grand, Mumbai"
      },
      "totalAmount": 15000.00,
      "createdAt": "2026-03-17T08:30:00Z"
    }
  ]
}
```

---

### 2. GET ORDER BY ID

**Endpoint**: `GET /api/v1/orders/{orderId}`

**Response**: Full order details (same as above)

---

### 3. UPDATE ORDER STATUS

**Endpoint**: `PATCH /api/v1/orders/{orderId}/status?status=PREPARING`

**Allowed Transitions**:
```
CONFIRMED → PREPARING
PREPARING → READY
READY → COMPLETED
Any → CANCELLED
```

---

### 4. CANCEL ORDER

**Endpoint**: `POST /api/v1/orders/{orderId}/cancel`

**Request Parameters**:
```
reason: "Cancellation reason" (optional)
```

**Refund Policy**:
- ✅ Full refund if < 24 hours
- ⚠️ 50% refund if 24-48 hours
- ❌ No refund if > 48 hours

---

### 5. GET UPCOMING ORDERS

**Endpoint**: `GET /api/v1/orders/upcoming?page=0&size=20`

**Returns**: Orders with eventDate >= today

---

### 6. GET ORDER HISTORY

**Endpoint**: `GET /api/v1/orders/history?page=0&size=20`

**Returns**: Completed & cancelled orders

---

### 7. VENDOR: GET ASSIGNED ORDERS

**Endpoint**: `GET /api/v1/orders/vendor?page=0&size=20`

**Authentication**: ✅ Vendor role required

**Returns**: Orders assigned to vendor

---

## 💰 PAYMENT TYPES

### ORDER Payment
```
Scenario: User buys from shopping cart
Amount: Cart total
Gateway: Determined by user region
Auto Action: Create order after payment
```

### BID Payment
```
Scenario: User accepts vendor bid
Amount: Bid amount
Gateway: User region specific
Auto Action: Create order from bid details
```

---

## 🌍 GATEWAY SELECTION

**Logic**:
```
if (user.country == "USA") {
  gateway = STRIPE
  currency = USD
} else if (user.country == "INDIA") {
  gateway = RAZORPAY
  currency = INR
}
```

---

## 🔐 SECURITY

### Transaction Verification
- ✅ Signature validation (Razorpay/Stripe)
- ✅ Amount verification
- ✅ User ID verification
- ✅ Idempotency check (prevent double charges)

### Webhook Security
- ✅ IP whitelist check
- ✅ Signature verification
- ✅ Timestamp validation
- ✅ Duplicate prevention

---

## 📊 TRANSACTION STATUSES

| Status | Meaning | Next Action |
|--------|---------|------------|
| PENDING | Payment initiated, awaiting completion | User pays in gateway |
| SUCCESS | Payment captured | Order created automatically |
| FAILED | Payment declined | Retry or use another method |
| REFUNDED | Money returned to customer | Order cancelled |
| DISPUTED | Chargeback by customer | Admin review |

---

## 🧪 TEST FLOW

### Local Testing

**1. Initiate Payment**:
```bash
curl -X POST http://localhost:8080/api/v1/payments/initiate \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": null,
    "bidId": "bid-uuid",
    "paymentType": "BID",
    "amount": 5000.00
  }'
```

**2. Complete Payment in Gateway UI**
- Stripe: Use test card `4242 4242 4242 4242`
- Razorpay: Use test credentials

**3. Verify Payment**:
```bash
curl -X POST http://localhost:8080/api/v1/payments/verify \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "gatewayOrderId": "stripe_pi_xxxxx",
    "gatewayPaymentId": "stripe_ch_xxxxx",
    "signature": "signature_hash"
  }'
```

**4. Check Order Created**:
```bash
curl -X GET http://localhost:8080/api/v1/orders \
  -H "Authorization: Bearer <token>"
```

---

## 📈 FLOW SUMMARY

```
✅ COMPLETE PAYMENT & ORDER CREATION FLOW

1. User clicks Checkout
   ↓
2. Frontend calls POST /payments/initiate
   ↓
3. Backend returns transactionId + gatewayOrderId
   ↓
4. Frontend opens payment gateway UI
   ↓
5. User completes payment in Stripe/Razorpay
   ↓
6. Gateway sends webhook to /webhook/razorpay or /stripe
   ↓
7. Backend verifies signature & amount
   ↓
8. Backend calls OrderService.createOrder() AUTO
   ↓
9. Order Status = CONFIRMED
   ↓
10. Vendor receives notification
    ↓
11. User receives order confirmation email
    ↓
12. User can track order in /orders endpoint
    ✅ COMPLETE!
```

---

## 📝 ERROR CODES

| Code | Meaning | Fix |
|------|---------|-----|
| 400 | Invalid amount | Check amount is > 0 |
| 401 | Unauthorized | Add Bearer token |
| 404 | Order/Bid not found | Check ID validity |
| 409 | Duplicate transaction | Already processed |
| 500 | Gateway error | Retry later |

---

**Status**: ✅ **PRODUCTION READY**

All payment gateways tested and fully integrated!


