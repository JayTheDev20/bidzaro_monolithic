# 📚 Bidzaro Catering Platform — Documentation Index

**Base URL:** `http://localhost:8080/api/v1`
**Swagger UI:** `http://localhost:8080/api/v1/swagger-ui.html`
**Last updated:** March 6, 2026

---

## 📂 Document List

| File | Role / Audience | Description |
|------|----------------|-------------|
| [CLIENT_API_DOCS.md](CLIENT_API_DOCS.md) | 👤 Customer / User | All user-facing APIs: auth, profile, vendors, cart, bids, orders, payments, reviews, loyalty, promos, referrals, chat, notifications, support |
| [VENDOR_API_DOCS.md](VENDOR_API_DOCS.md) | 🏪 Vendor | Vendor onboarding, menu management, bid marketplace, order management, dashboard analytics |
| [ADMIN_API_DOCS.md](ADMIN_API_DOCS.md) | 🛡️ Admin | Dashboard, user/vendor management, support agents, menu/promo/config management, audit logs, announcements, analytics |
| [SUPPORT_API_DOCS.md](SUPPORT_API_DOCS.md) | 🎧 Support Agent | Ticket management, resolution workflow, chat with users/vendors, SLA monitoring |
| [CHAT_INTEGRATION_GUIDE.md](CHAT_INTEGRATION_GUIDE.md) | 💻 Frontend Dev | WebSocket/STOMP setup, React/React Native code, message types, read receipts, file sharing |
| [FIREBASE_IMPLEMENTATION_GUIDE.md](FIREBASE_IMPLEMENTATION_GUIDE.md) | 💻 Frontend + Backend Dev | Firebase project setup, service account, FCM token management, push notification integration |
| [WISHLIST_CART_API_COMPLETE.md](WISHLIST_CART_API_COMPLETE.md) | 👤 Frontend Dev | Complete wishlist & cart API specs with all 20 endpoints, request/response formats, examples |
| [WISHLIST_CART_QUICK_REFERENCE.md](WISHLIST_CART_QUICK_REFERENCE.md) | 👤 Frontend Dev | Quick reference guide for wishlist, cart, and draft cart APIs |
| [WISHLIST_CART_API_FIX.md](WISHLIST_CART_API_FIX.md) | 🔧 Debugging | Issue analysis (405 errors), root causes, and fixes for POST endpoints |
| [WISHLIST_CART_SUMMARY.md](WISHLIST_CART_SUMMARY.md) | 📊 Overview | Summary of all 20 wishlist/cart APIs, endpoint checklist, and key differences |

---

## 🔢 Quick Enums Reference (All Roles)

### UserType
`USER` · `VENDOR` · `ADMIN` · `SUPPORT_AGENT`

### UserStatus
`PENDING_VERIFICATION` · `ACTIVE` · `SUSPENDED` · `DELETED`

### OrderStatus (lifecycle)
```
PENDING_TOKEN_PAYMENT → CONFIRMED → IN_PREPARATION → READY_FOR_DELIVERY → DELIVERING → DELIVERED → COMPLETED
                                                                                                   ↘ CANCELLED
```

### BidRequestStatus (lifecycle)
```
ACTIVE → COMPETITIVE → PENDING_TOKEN_PAYMENT → ACCEPTED
      ↘ EXPIRED / CANCELLED
```

### BidStatus
`SUBMITTED` · `UNDER_REVIEW` · `ACCEPTED` · `REJECTED` · `WITHDRAWN` · `EXPIRED`

### VendorStatus / ApprovalStatus
`PENDING` · `ACTIVE` · `SUSPENDED` · `INACTIVE` | `PENDING` · `APPROVED` · `REJECTED`

### PaymentType
`TOKEN` · `BALANCE` · `FULL`

### TransactionStatus
`PENDING` · `PROCESSING` · `SUCCESS` · `FAILED` · `REFUNDED` · `CANCELLED`

### LoyaltyTier
`BRONZE (0 pts, 1×)` · `SILVER (1000 pts, 1.5×)` · `GOLD (5000 pts, 2×)` · `PLATINUM (10000 pts, 3×)`

### TicketStatus
`OPEN` · `ASSIGNED` · `IN_PROGRESS` · `WAITING_FOR_CUSTOMER` · `RESOLVED` · `CLOSED`

### TicketPriority (SLA)
`URGENT (1h/4h)` · `HIGH (4h/24h)` · `MEDIUM (8h/48h)` · `LOW (24h/72h)`

### ConversationType
`USER_VENDOR` · `USER_SUPPORT` · `VENDOR_SUPPORT`

### MessageType
`TEXT` · `IMAGE` · `FILE` · `SYSTEM`

### NotificationChannel
`EMAIL` · `SMS` · `PUSH` · `IN_APP` · `WHATSAPP`

### PromoType / ApplicableTo
`PERCENTAGE` · `FIXED` | `ALL` · `SPECIFIC_VENDORS` · `SPECIFIC_USERS`

### Country / Gateway
`USA → USD → Stripe` | `INDIA → INR → Razorpay`

---

## 🗺️ Platform Flow Diagrams

### Complete Ordering Flow
```
User Registration → OTP Verify
         ↓
  Browse Vendors → Add to Cart
         ↓
  Create Bid Request (with event details + menu items)
         ↓
  Vendors Submit Bids (up to 168h / 7 days)
         ↓
  User Reviews Bids (ranked by price) → Accept Best Bid
         ↓
  PENDING_TOKEN_PAYMENT (24h window)
         ↓
  Initiate Payment → POST /payments/initiate
         ↓
  Razorpay/Stripe Checkout (frontend)
         ↓
  Verify Payment → POST /payments/verify
         ↓
  Order AUTO-CREATED (CONFIRMED status)
         ↓
  Event Day: Vendor updates status (IN_PREPARATION → READY → DELIVERING → DELIVERED)
         ↓
  Auto-completes 24h after DELIVERED → COMPLETED
         ↓
  User submits Review → Vendor responds
```

### Vendor Onboarding Flow
```
Register (userType: VENDOR) → Verify OTP
         ↓
  Create Vendor Profile (POST /vendors)
         ↓
  Admin Reviews → Approve / Reject
         ↓
  ACTIVE → Can receive bids & orders

  If no profile within reminders:
  5min → 1h×3 → 24h×3 → Account DELETED
```

### Support Ticket Flow
```
User/Vendor creates ticket (POST /support/tickets)
         ↓
  Auto-assigned to agent with fewest active tickets
  + Chat conversation auto-created
         ↓
  Agent updates status: ASSIGNED → IN_PROGRESS
         ↓
  Agent ↔ User/Vendor chat via conversationId
         ↓
  Agent resolves: PUT /support/tickets/{id}/resolve
         ↓
  User rates: POST /support/tickets/{id}/rate (CSAT 1-5)
```

---

## 🔑 Authentication Quick Reference

All protected endpoints need:
```
Authorization: Bearer <accessToken>
```

| Action | Endpoint |
|--------|----------|
| Register | `POST /auth/register` |
| Login | `POST /auth/login` |
| Refresh token | `POST /auth/refresh` |
| Logout | `POST /auth/logout` |
| Send OTP | `POST /auth/otp/send` |
| Verify OTP | `POST /auth/otp/verify` |
| Forgot password | `POST /auth/forgot-password` |
| Reset password | `POST /auth/reset-password` |

> ⚠️ Account locks after **3 failed login attempts**. Unlock via password reset or admin.

---

## 💳 Payment Integration Quick Reference

### India (Razorpay)
```
1. POST /payments/initiate → get { gatewayOrderId, keyId, amount, currency: "INR" }
2. Open Razorpay checkout on frontend with { key: keyId, order_id: gatewayOrderId, amount }
3. On success callback: POST /payments/verify → { gatewayOrderId, gatewayPaymentId, signature }
4. Order auto-created if token payment for bid
```

### USA (Stripe)
```
1. POST /payments/initiate → get { clientSecret, amount, currency: "USD" }
2. Use Stripe Elements/PaymentSheet with clientSecret
3. On success: POST /payments/verify → { gatewayOrderId, gatewayPaymentId }
4. Order auto-created if token payment for bid
```

---

## 🔔 Notification Channels

| Channel | Config Required | Status |
|---------|----------------|--------|
| Email (SMTP) | `SMTP_HOST`, `SMTP_USERNAME`, `SMTP_PASSWORD` | ✅ Fully implemented |
| SMS (Twilio) | `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_PHONE_NUMBER` | ✅ Fully implemented |
| WhatsApp (Twilio) | `TWILIO_WHATSAPP_NUMBER` | ✅ Fully implemented |
| Push (Firebase) | `FIREBASE_PROJECT_ID`, `firebase-credentials.json` | ✅ Fully implemented |
| In-App | None (stored in MongoDB) | ✅ Fully implemented |

---

## ⏰ Background Schedulers

| Scheduler | Frequency | Task |
|-----------|-----------|------|
| `BidExpiryScheduler` | Every hour | Expire stale bid requests & vendor bids |
| `BidExpiryScheduler` | Every 15 min | Process cooling period end |
| `BidExpiryScheduler` | Daily 3 AM | Expire old vendor bids |
| `OrderStatusScheduler` | Every hour | Auto-cancel 24h-old `PENDING_TOKEN_PAYMENT` orders |
| `OrderStatusScheduler` | Every hour | Auto-complete 24h-old `DELIVERED` orders |
| `OrderStatusScheduler` | Daily 9 AM | Payment reminders (7 days before event) |
| `PromoExpiryScheduler` | Daily midnight | Expire old promo codes |
| `SLAMonitorScheduler` | Every 15 min | Check SLA breaches on open tickets |
| `SLAMonitorScheduler` | Every hour | Auto-escalate unassigned tickets |
| `VendorOnboardingScheduler` | Every minute | Multi-stage vendor profile reminders + cleanup |

---

## 📁 Source Code Structure

```
src/main/java/com/cateringmarketplace/
├── CateringPlatformApplication.java
├── config/          AsyncConfig, MongoConfig, RedisConfig, SecurityConfig, WebSocketConfig, SwaggerConfig, ...
├── common/
│   ├── config/      DatabaseInitializer
│   ├── constant/    AppConstants, ErrorMessages
│   ├── exception/   GlobalExceptionHandler + 9 exception types
│   ├── filter/      JwtAuthenticationFilter
│   ├── response/    ApiResponse, ErrorResponse, PageInfo
│   └── util/        JwtUtil, OTPUtil, PasswordUtil, DateUtil, FileUtil, LocationUtil
├── module/
│   ├── auth/        User, OTPVerification, RefreshToken, AuthService, AuthController, ...
│   ├── vendor/      Vendor, VendorService, VendorController, VendorOnboardingScheduler
│   ├── bid/         BidRequest, VendorBid, BidService, BidController
│   ├── cart/        CartItem, DraftCartItem, MasterCartItem, CartService, ...
│   ├── order/       Order, OrderService, OrderController
│   ├── payment/     Transaction, PaymentService, Razorpay/Stripe gateways
│   ├── menu/        Category, MenuItem, VendorMenuItem, MenuService, AdminMenuController
│   ├── chat/        Conversation, Message, ChatService, ChatController, ChatWebSocketController
│   ├── notification/ Notification, NotificationService, EmailService, TwilioService, FirebaseService
│   ├── loyalty/     LoyaltyInfo, LoyaltyTransaction, LoyaltyService
│   ├── promo/       PromoCode, PromoRedemption, PromoService
│   ├── review/      Review, ReviewService, ReviewController
│   ├── support/     Ticket, TicketMessage, SupportService, SupportController
│   ├── user/        Address, UserService, UserController
│   ├── referral/    ReferralCode, ReferralEvent, ReferralService
│   ├── upload/      UploadedFile, FileUploadService, FileUploadController
│   ├── analytics/   AnalyticsService, AnalyticsController
│   ├── admin/       AdminService, Announcement, AuditLog, PlatformConfig
│   └── wishlist/    WishlistItem, WishlistService, WishlistController
└── scheduler/
    ├── BidExpiryScheduler
    ├── OrderStatusScheduler
    ├── PromoExpiryScheduler
    └── SLAMonitorScheduler
```

---

## ⚠️ Standard Error Response

```json
{
  "success": false,
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Order not found",
    "path": "/api/v1/orders/invalid-id",
    "timestamp": "2026-03-06T10:00:00Z",
    "fieldErrors": {}
  }
}
```

## ✅ Standard Success Response

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "pageInfo": { "page": 0, "size": 20, "totalElements": 100, "totalPages": 5, "first": true, "last": false }
}
```
