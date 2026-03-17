# 💳 PAYMENT & ORDER APIs - VISUAL REFERENCE

## 🎯 PAYMENT WORKFLOW DIAGRAM

```
┌─────────────────────────────────────────────────────────────────────┐
│                         PAYMENT FLOW                                │
└─────────────────────────────────────────────────────────────────────┘

User Cart/Bid
    │
    ↓ Click "Pay Now"
    │
    ├─────────────────────────────────────────┐
    │                                         │
    │  Frontend                               │
    │  ═════════════════════════════════════ │
    │                                         │
    ├─ POST /payments/initiate               │
    │  └─ { orderId, bidId, amount }         │
    │                                         │
    ↓                                         │
    │                                         │
    ├─────────────────────────────────────────┤
    │                                         │
    │  Backend                                │
    │  ═════════════════════════════════════ │
    │                                         │
    ├─ Determine Country                     │
    │  ├─ USA → Stripe                       │
    │  └─ India → Razorpay                   │
    │                                         │
    ├─ Create Transaction (PENDING)          │
    │                                         │
    ├─ Return:                               │
    │  ├─ transactionId                      │
    │  ├─ gatewayOrderId                     │
    │  └─ clientSecret                       │
    │                                         │
    ↓                                         │
    │                                         │
    ├─────────────────────────────────────────┤
    │                                         │
    │  Payment Gateway                        │
    │  ═════════════════════════════════════ │
    │                                         │
    ├─ Stripe (USA) OR Razorpay (India)     │
    │                                         │
    ├─ User enters card details               │
    │                                         │
    ├─ Payment processed                      │
    │                                         │
    ├─ Sends webhook to backend               │
    │                                         │
    ↓                                         │
    │                                         │
    ├─────────────────────────────────────────┤
    │                                         │
    │  Backend Webhook Handler                │
    │  ═════════════════════════════════════ │
    │                                         │
    ├─ Receive:                              │
    │  ├─ /webhook/stripe OR                 │
    │  └─ /webhook/razorpay                  │
    │                                         │
    ├─ Verify Signature ✅                   │
    │                                         │
    ├─ Verify Amount ✅                      │
    │                                         │
    ├─ Update Transaction (SUCCESS)          │
    │                                         │
    ├─ AUTO: OrderService.createOrder()      │
    │  └─ Order Status = CONFIRMED           │
    │                                         │
    ├─ Send confirmations:                   │
    │  ├─ User notification                  │
    │  └─ Vendor assignment                  │
    │                                         │
    ↓                                         │
    │                                         │
    ├─ Order Ready ✅                        │
    └─────────────────────────────────────────┘
         │
         ↓
    User sees order:
    GET /orders/{orderId}
         │
         ↓
    Vendor prepares:
    PATCH /orders/{orderId}/status
```

---

## 📊 ENDPOINT OVERVIEW

### PAYMENT ENDPOINTS (7 total)

```
┌─────────────────────────────────────────────────────────────┐
│                    PAYMENT OPERATIONS                       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ⭐ POST   /payments/initiate                             │
│     └─ Start payment for order/bid                        │
│     ├─ Input: { orderId, bidId, amount }                 │
│     └─ Output: { transactionId, gatewayOrderId }         │
│                                                             │
│  ✅ POST   /payments/verify                              │
│     └─ Verify & create order                             │
│     ├─ Input: { gatewayOrderId, gatewayPaymentId }       │
│     └─ Output: { status: SUCCESS/FAILED }                │
│                                                             │
│  📊 GET    /payments/transactions                        │
│     └─ User payment history (paginated)                  │
│                                                             │
│  🔍 GET    /payments/transactions/{id}                   │
│     └─ Single transaction details                        │
│                                                             │
│  📋 GET    /payments/order/{orderId}                     │
│     └─ All payments for order                            │
│                                                             │
│  🪝 POST   /payments/webhook/razorpay                   │
│     └─ Razorpay webhook (auto-verified)                 │
│                                                             │
│  🪝 POST   /payments/webhook/stripe                     │
│     └─ Stripe webhook (auto-verified)                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### ORDER ENDPOINTS (8 total)

```
┌─────────────────────────────────────────────────────────────┐
│                    ORDER OPERATIONS                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  📋 GET    /orders                                        │
│     └─ User's orders (paginated)                         │
│                                                             │
│  🔍 GET    /orders/{orderId}                             │
│     └─ Order details                                     │
│                                                             │
│  ⬆️  PATCH  /orders/{orderId}/status                      │
│     └─ Update status (CONFIRMED→READY→COMPLETED)        │
│                                                             │
│  ❌ POST   /orders/{orderId}/cancel                      │
│     └─ Cancel & refund                                   │
│                                                             │
│  📅 GET    /orders/upcoming                              │
│     └─ Events coming soon                                │
│                                                             │
│  📚 GET    /orders/history                               │
│     └─ Past orders                                       │
│                                                             │
│  🏪 GET    /orders/vendor                                │
│     └─ Vendor's assigned orders                          │
│                                                             │
│  ⚡ GET    /orders/vendor/my                             │
│     └─ Vendor shorthand (same as /vendor)                │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🌍 GATEWAY SELECTION MATRIX

```
┌──────────┬───────────┬──────────┬──────────────────┐
│ Country  │  Gateway  │ Currency │  Payment Methods │
├──────────┼───────────┼──────────┼──────────────────┤
│ USA      │  Stripe   │   USD    │ Cards, Apple Pay │
│ India    │ Razorpay  │   INR    │ UPI, Cards, NB   │
│ Others   │  Stripe   │   USD    │ Cards, Wallets   │
└──────────┴───────────┴──────────┴──────────────────┘
```

---

## 💰 PAYMENT TYPES

```
┌────────────────────────────────────────────┐
│         PAYMENT TYPE SCENARIOS             │
├────────────────────────────────────────────┤
│                                            │
│  ORDER Payment                             │
│  ────────────────────────────────────────  │
│  When: Buying from cart                   │
│  Amount: Cart total                       │
│  Auto Action: Create order ✅             │
│                                            │
│  BID Payment                               │
│  ────────────────────────────────────────  │
│  When: Accepting vendor bid                │
│  Amount: Bid amount                        │
│  Auto Action: Create order from bid ✅   │
│                                            │
└────────────────────────────────────────────┘
```

---

## 📊 ORDER STATUS LIFECYCLE

```
        User Pays
            │
            ↓
      ┌──────────────┐
      │  CONFIRMED   │  ← Order placed
      └──────────────┘
            │
            ↓ (Vendor accepts)
      ┌──────────────┐
      │  PREPARING   │  ← Cooking started
      └──────────────┘
            │
            ↓ (Ready)
      ┌──────────────┐
      │    READY     │  ← Ready for pickup
      └──────────────┘
            │
            ↓ (Delivered)
      ┌──────────────┐
      │  COMPLETED   │  ✅ Done
      └──────────────┘

      ANYTIME: CANCELLED (Refund issued)
```

---

## 🔐 SECURITY CHECKS

```
┌─────────────────────────────────────────────┐
│         WEBHOOK VERIFICATION                │
├─────────────────────────────────────────────┤
│                                             │
│ ✅ Signature Verification                  │
│    └─ Verify X-Razorpay-Signature         │
│    └─ Verify Stripe-Signature             │
│                                             │
│ ✅ Amount Verification                     │
│    └─ Ensure amount matches payment       │
│                                             │
│ ✅ User Verification                       │
│    └─ Confirm user authorization          │
│                                             │
│ ✅ Timestamp Validation                    │
│    └─ Prevent replay attacks               │
│                                             │
│ ✅ Idempotency Check                       │
│    └─ Prevent duplicate orders             │
│                                             │
└─────────────────────────────────────────────┘
```

---

## 🎯 QUICK START

```
Step 1: POST /payments/initiate
┌──────────────────────────────┐
│ {                            │
│   orderId: null,             │
│   bidId: "bid-123",          │
│   paymentType: "BID",        │
│   amount: 5000.00            │
│ }                            │
└──────────────────────────────┘
        ↓
Response: transactionId, gatewayOrderId

Step 2: User pays in Stripe/Razorpay
        ↓
Step 3: Backend receives webhook
        ↓
Step 4: Order created automatically ✅

Step 5: GET /orders
        ↓
Response: New order with status CONFIRMED ✅
```

---

## 📞 ERROR RESPONSES

```
❌ 400 BAD REQUEST
   ├─ Invalid amount (must be > 0)
   └─ Missing required fields

❌ 401 UNAUTHORIZED
   └─ Missing or invalid Bearer token

❌ 404 NOT FOUND
   ├─ Order not found
   └─ Bid not found

❌ 409 CONFLICT
   └─ Duplicate payment (already processed)

❌ 500 SERVER ERROR
   └─ Gateway error (retry later)
```

---

## 📝 RESPONSE TEMPLATE

### Success (200)
```json
{
  "success": true,
  "status": 200,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2026-03-17T08:30:00Z"
}
```

### Error (400/401/404/500)
```json
{
  "success": false,
  "status": 400,
  "message": "Error description",
  "error": {
    "code": "ERROR_CODE",
    "details": "Error details"
  },
  "timestamp": "2026-03-17T08:30:00Z"
}
```

---

**Ready to integrate?** ✅
**All APIs tested and production-ready!** 🚀


