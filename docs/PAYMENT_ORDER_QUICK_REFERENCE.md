# 💳 PAYMENT & ORDER APIs - QUICK REFERENCE

## 🎯 ENDPOINTS AT A GLANCE

### Payment Endpoints
```
POST   /api/v1/payments/initiate              → Start payment
POST   /api/v1/payments/verify                → Verify & create order
GET    /api/v1/payments/transactions          → User payment history
GET    /api/v1/payments/transactions/{id}     → Transaction details
GET    /api/v1/payments/order/{orderId}       → Order payments
POST   /api/v1/payments/webhook/razorpay      → Razorpay webhook
POST   /api/v1/payments/webhook/stripe        → Stripe webhook
```

### Order Endpoints
```
GET    /api/v1/orders                         → User orders
GET    /api/v1/orders/{orderId}               → Order details
PATCH  /api/v1/orders/{orderId}/status        → Update status
POST   /api/v1/orders/{orderId}/cancel        → Cancel order
GET    /api/v1/orders/upcoming                → Upcoming events
GET    /api/v1/orders/history                 → Past orders
GET    /api/v1/orders/vendor                  → Vendor assigned orders
GET    /api/v1/orders/vendor/my               → Vendor shorthand
```

---

## 💰 PAYMENT FLOW (CRITICAL)

```
1. POST /payments/initiate
   → Body: { orderId, bidId, paymentType, amount }
   ← Response: { transactionId, gatewayOrderId, clientSecret }

2. User pays in Stripe/Razorpay UI

3. Gateway sends webhook

4. POST /payments/webhook/razorpay or /stripe
   → Order created automatically ✅

5. GET /orders
   → See new order ✅
```

---

## 📋 REQUEST/RESPONSE EXAMPLES

### 1. Initiate Payment
```
POST /api/v1/payments/initiate
Authorization: Bearer <token>

{
  "orderId": null,
  "bidId": "bid-123",
  "paymentType": "BID",
  "amount": 5000.00
}

← 200 OK
{
  "transactionId": "txn-abc-123",
  "gatewayOrderId": "razorpay_order_xyz",
  "clientSecret": "secret_xyz",
  "paymentGateway": "RAZORPAY",
  "status": "PENDING"
}
```

### 2. Verify Payment
```
POST /api/v1/payments/verify
Authorization: Bearer <token>

{
  "gatewayOrderId": "razorpay_order_xyz",
  "gatewayPaymentId": "razorpay_payment_abc",
  "signature": "signature_hash"
}

← 200 OK
{
  "transactionId": "txn-abc-123",
  "status": "SUCCESS",
  "orderId": "order-456",
  "amount": 5000.00
}
```

### 3. Get Orders
```
GET /api/v1/orders?page=0&size=20
Authorization: Bearer <token>

← 200 OK
{
  "data": [
    {
      "orderId": "order-456",
      "status": "CONFIRMED",
      "totalAmount": 5000.00,
      "items": [...],
      "createdAt": "2026-03-17T08:30:00Z"
    }
  ]
}
```

---

## 🌍 GATEWAY MAPPING

| Country | Gateway | Currency |
|---------|---------|----------|
| USA | Stripe | USD |
| India | Razorpay | INR |

Auto-selected by user location!

---

## 💡 KEY POINTS

✅ **Order creation is AUTOMATIC after payment**
✅ **No manual order creation endpoint**
✅ **Supports USD (USA) and INR (India)**
✅ **Webhook signatures verified**
✅ **Idempotent - no duplicate charges**
✅ **Full refund support**

---

## 🚀 START HERE

1. **User pays** → `POST /payments/initiate`
2. **Gateway processes** → Stripe or Razorpay UI
3. **We get webhook** → Auto creates order
4. **User sees order** → `GET /orders`

**That's it!** 🎉

---

## 📞 ERROR HANDLING

```
❌ 400 Bad Request → Invalid amount/fields
❌ 401 Unauthorized → Missing token
❌ 404 Not Found → Order/transaction not found
❌ 409 Conflict → Duplicate payment
❌ 500 Server Error → Try again later
```

---

## 🔐 SECURITY HEADERS

**Razorpay Webhook**:
```
X-Razorpay-Signature: <signature>
```

**Stripe Webhook**:
```
Stripe-Signature: <signature>
```

Backend auto-verifies signatures!

---

## 📊 ORDER STATUSES

```
CONFIRMED  → Ready to prepare
PREPARING  → Being cooked
READY      → Ready for pickup
COMPLETED  → Delivered
CANCELLED  → Cancelled (refund issued)
```

---

## 🎯 COMMON FLOWS

### Flow 1: Buy from Cart
```
1. POST /payments/initiate (orderId = cart contents)
2. Pay in Stripe/Razorpay
3. GET /orders → Order ready ✅
```

### Flow 2: Accept Vendor Bid
```
1. POST /payments/initiate (bidId = vendor bid)
2. Pay in gateway
3. GET /orders → Order from bid ✅
```

### Flow 3: Check Order Status
```
GET /orders/{orderId}
PATCH /orders/{orderId}/status?status=PREPARING
GET /orders/upcoming (events coming soon)
```

---

**All Gateways**: ✅ LIVE & TESTED

Ready to accept payments! 💳


