# 🔧 CORS, WebSocket & Payment Fixes

## Issues Identified

### 1. CORS Policy Errors (WebSocket & REST)
```
Access to XMLHttpRequest at 'http://localhost:8080/ws/sockjs/notifications/info'
blocked by CORS policy: No 'Access-Control-Allow-Origin' header
```

**Root Cause:**
- WebSocket SockJS fallback endpoints require CORS headers
- Application context path is `/api/v1` which affects routing

### 2. Missing Razorpay Key ID
```
Missing Razorpay key ID in payment response
```

**Root Cause:**
- `razorpay.key-id` property is empty (not set in environment)
- Frontend expects `keyId` in payment response

### 3. Orders API CORS Issue
```
Access to XMLHttpRequest at 'http://localhost:8080/api/user/53329557.../orders'
blocked by CORS policy
```

**Root Cause:**
- Orders endpoint routing might not match CORS patterns
- Should be `/api/v1/orders` not `/api/user/{userId}/orders`

---

## ✅ Fixes Required

### Fix 1: Update SecurityConfig for WebSocket CORS

**File:** `src/main/java/com/cateringmarketplace/config/SecurityConfig.java`

The CORS configuration already covers all paths with `/**` pattern, but we need to ensure:
1. SockJS fallback endpoints are properly exposed
2. No credentialsinvalidation for WebSocket URLs

**Status:** ✅ Already configured correctly

### Fix 2: Update WebSocketConfig for SockJS Headers

**File:** `src/main/java/com/cateringmarketplace/config/WebSocketConfig.java`

The WebSocket configuration needs explicit CORS headers for SockJS.

**Status:** ✅ Already configured correctly

### Fix 3: Set Razorpay Credentials

**File:** `src/main/resources/application.yml` (or environment variables)

```yaml
razorpay:
  key-id: ${RAZORPAY_KEY_ID:rzp_test_1234567890abcd}  # Set this!
  key-secret: ${RAZORPAY_KEY_SECRET:your_secret_key}
  webhook-secret: ${RAZORPAY_WEBHOOK_SECRET:your_webhook_secret}
```

### Fix 4: Verify Payment Response Includes keyId

**File:** `src/main/java/com/cateringmarketplace/module/payment/dto/PaymentInitiationResponse.java`

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiationResponse {
    private String transactionId;
    private String gatewayOrderId;
    private String gatewayName;
    private BigDecimal amount;
    private String currency;
    private String keyId;              // ✅ Already present
    private String clientSecret;
    private String publishableKey;
}
```

**Status:** ✅ Already present

### Fix 5: Check Razorpay Gateway Implementation

**File:** `src/main/java/com/cateringmarketplace/module/payment/gateway/impl/RazorpayPaymentGateway.java`

The `createPaymentIntent` method already returns keyId:

```java
return PaymentInitiationResponse.builder()
        .transactionId(request.getTransactionId())
        .gatewayOrderId(orderId)
        .gatewayName("RAZORPAY")
        .amount(request.getAmount())
        .currency(request.getCurrency())
        .keyId(keyId)  // ✅ Already set
        .build();
```

**Status:** ✅ Already implemented correctly

---

## 🔍 Root Cause Analysis

### Why is Razorpay keyId Missing?

**The issue is NOT in the code - it's in configuration!**

The `keyId` field is returned correctly, but if `${RAZORPAY_KEY_ID}` environment variable is not set, it will be empty string.

**Check:**
```yaml
razorpay:
  key-id: ${RAZORPAY_KEY_ID:}  # ⚠️ EMPTY DEFAULT!
```

### CORS Issue Resolution

The CORS is actually configured correctly. The issue might be:

1. **Browser caching** - Clear browser cache
2. **Preflight requests** - Already allowed with OPTIONS matcher
3. **Context path** - App runs on `/api/v1` - Check frontend is calling correct URL

**Expected:**
- Frontend should call: `http://localhost:8080/api/v1/ws/sockjs/notifications`
- NOT: `http://localhost:8080/ws/sockjs/notifications`

---

## 🚀 Action Items

### Step 1: Set Environment Variables

Create `.env` file or set system environment:

```bash
# Razorpay Keys (India)
export RAZORPAY_KEY_ID=rzp_live_1234567890abcd
export RAZORPAY_KEY_SECRET=your_secret_key
export RAZORPAY_WEBHOOK_SECRET=your_webhook_secret

# CORS Origins
export CORS_ORIGINS=http://localhost:3000,http://localhost:5173,http://localhost:5174

# Frontend URL
export FRONTEND_URL=http://localhost:5173
```

### Step 2: Update Frontend API URLs

**Check:** `src/frontend/utils/api.ts` or similar

Current (WRONG):
```typescript
const BASE_URL = 'http://localhost:8080';
const WS_URL = 'ws://localhost:8080/ws/sockjs/notifications';
```

Should be (CORRECT):
```typescript
const BASE_URL = 'http://localhost:8080/api/v1';
const WS_URL = 'ws://localhost:8080/api/v1/ws/sockjs/notifications';
// OR with context path in API config
const WS_URL = 'ws://localhost:8080/ws/sockjs/notifications'; // if no context path
```

### Step 3: Verify Orders Endpoint

Frontend is calling:
```typescript
'/api/user/{userId}/orders' // ❌ WRONG
```

Should be:
```typescript
'/api/v1/orders' // ✅ CORRECT - endpoint handles auth
```

### Step 4: WebSocket Connection Fix

Frontend WebSocket creation (likely in `realTimeNotificationService.ts`):

```typescript
// ❌ CURRENT (may be wrong)
const webSocketFactory = () => {
  return new SockJS('http://localhost:8080/ws/sockjs/notifications');
};

// ✅ SHOULD BE (check context path)
const webSocketFactory = () => {
  return new SockJS('http://localhost:8080/api/v1/ws/sockjs/notifications');
  // OR if context path not applied to WebSocket:
  // return new SockJS('http://localhost:8080/ws/sockjs/notifications');
};
```

---

## 📋 Summary Table

| Issue | Status | Root Cause | Fix |
|-------|--------|-----------|-----|
| Missing Razorpay keyId | 🟡 Code OK | Empty env var | Set `RAZORPAY_KEY_ID` |
| WebSocket CORS Error | 🟡 Config OK | Wrong WS URL | Update frontend WS_URL |
| Orders endpoint CORS | 🟡 Config OK | Wrong REST URL | Update frontend API base |
| SockJS preflight 404 | 🟡 Config OK | Context path mismatch | Check `/api/v1` prefix |

---

## 🔗 Related Documentation

- API Routing Guide: See `docs/API_ROUTING_GUIDE.md`
- WebSocket Config: See `docs/WEBSOCKET_QUICK_REFERENCE.md`
- Payment Gateway: See `docs/PAYMENT_GATEWAY_ORDER_CREATION_API.md`

---

## ✨ Quick Checklist

- [ ] Razorpay credentials set in environment
- [ ] Frontend uses `/api/v1/` prefix for all REST calls
- [ ] Frontend uses `/api/v1/ws/sockjs/` for WebSocket
- [ ] Browser cache cleared
- [ ] Application restarted
- [ ] Test payment flow end-to-end

