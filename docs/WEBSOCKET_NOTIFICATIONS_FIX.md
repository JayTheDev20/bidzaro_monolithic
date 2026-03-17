# 🔧 WEBSOCKET NOTIFICATIONS - CONNECTION FIX

**Date**: March 17, 2026
**Issue**: WebSocket/STOMP connection errors for real-time notifications
**Status**: ✅ FIXED

---

## ❌ PROBLEMS IDENTIFIED

### Error 1: SyntaxError
```
SyntaxError: The URL's scheme must be either 'http:' or 'https:'. 'ws:' is not allowed.
```
**Cause**: Frontend was trying to use `ws://` protocol when running in HTTP context

### Error 2: CORS Block
```
Access to XMLHttpRequest at 'http://localhost:8080/ws/sockjs/notifications/info'
has been blocked by CORS policy
```
**Cause**: SockJS was trying HTTP fallback, but CORS wasn't allowing it properly

### Error 3: 404 Not Found
```
GET http://localhost:8080/ws/sockjs/notifications/info?t=1773730475132
404 (Not Found)
```
**Cause**: The notifications WebSocket endpoint `/ws/sockjs/notifications` didn't exist on the backend

---

## ✅ BACKEND FIX (COMPLETED)

### Added Notifications WebSocket Endpoints

**File**: `WebSocketConfig.java`

Added two new endpoints for notifications:

```java
// 5. Raw WebSocket Endpoint for notifications
registry.addEndpoint("/ws/notifications")
        .setAllowedOriginPatterns("*")
        .addInterceptors(new JwtHandshakeInterceptor());

// 6. SockJS Endpoint for notifications
registry.addEndpoint("/ws/sockjs/notifications")
        .setAllowedOriginPatterns("*")
        .addInterceptors(new JwtHandshakeInterceptor())
        .withSockJS();
```

**What this does:**
- ✅ Registers `/ws/notifications` endpoint for native WebSocket
- ✅ Registers `/ws/sockjs/notifications` endpoint for SockJS fallback
- ✅ Allows CORS from all origins (`*`)
- ✅ Adds JWT authentication interceptor
- ✅ Enables SockJS fallback for HTTP-only environments

---

## ✅ FRONTEND FIX (REQUIRED)

The frontend `realTimeNotificationService.ts` needs to be updated to:

1. **Use correct URL scheme** (ws/wss instead of http/https)
2. **Use SockJS with proper endpoint**
3. **Handle HTTPS→WSS conversion**

### Fix Example (TypeScript)

```typescript
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Convert http to ws, https to wss
const getWebSocketUrl = (): string => {
  const url = new URL(`${BASE_URL}/api/v1/ws/sockjs/notifications`);
  if (url.protocol === 'https:') {
    url.protocol = 'wss:';
  } else {
    url.protocol = 'ws:';
  }
  return url.toString();
};

export class RealTimeNotificationService {
  private stompClient: Client | null = null;

  connectService(accessToken: string, onNotification: (msg: any) => void) {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(getWebSocketUrl()),
      connectHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
      debug: (msg) => console.log(msg),
      onConnect: () => {
        console.log('WebSocket connected');
        this.stompClient?.subscribe('/user/topic/notifications', (message) => {
          const notification = JSON.parse(message.body);
          onNotification(notification);
        });
      },
      onStompError: (error) => {
        console.error('STOMP Error:', error);
      },
      onWebSocketClose: () => {
        console.log('WebSocket disconnected');
      },
    });

    this.stompClient.activate();
  }

  disconnectService() {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
  }
}
```

---

## 🎯 COMPLETE WEBSOCKET ENDPOINTS

### Available Endpoints (After Fix)

| Endpoint | Protocol | Purpose | Transport |
|----------|----------|---------|-----------|
| `/ws` | WS/WSS | General WebSocket | Native + SockJS |
| `/ws/chat` | WS | Chat messaging | Native only |
| `/ws/sockjs/chat` | HTTP/HTTPS | Chat messaging | SockJS fallback |
| `/ws/notifications` | WS | Real-time notifications | Native only ✅ NEW |
| `/ws/sockjs/notifications` | HTTP/HTTPS | Real-time notifications | SockJS fallback ✅ NEW |

---

## 🔐 AUTHENTICATION

All endpoints support JWT token in two ways:

### Method 1: Query Parameter (Recommended)
```
ws://localhost:8080/api/v1/ws/notifications?token=eyJhbGc...
```

### Method 2: STOMP Header
```
CONNECT
Authorization: Bearer eyJhbGc...
```

---

## 📊 MESSAGE FLOW

```
Frontend (http://localhost:5173)
    ↓
[Determine protocol: http→ws, https→wss]
    ↓
Connect to: ws://localhost:8080/api/v1/ws/sockjs/notifications
    ↓
JwtHandshakeInterceptor validates token
    ↓
STOMP Client authenticates
    ↓
Subscribe to: /user/topic/notifications
    ↓
Receive real-time notifications ✅
```

---

## 🧪 TESTING

### Test Connection (Browser Console)

```javascript
// Using native fetch to test WebSocket endpoint
const testWebSocket = () => {
  const url = 'ws://localhost:8080/api/v1/ws/notifications?token=YOUR_TOKEN';
  const ws = new WebSocket(url);

  ws.onopen = () => console.log('✅ Connected');
  ws.onmessage = (evt) => console.log('📨', evt.data);
  ws.onerror = (err) => console.error('❌', err);
  ws.onclose = () => console.log('⛔ Disconnected');

  return ws;
};

const ws = testWebSocket();
```

### Test with cURL (SockJS)

```bash
# Get connection info
curl http://localhost:8080/api/v1/ws/sockjs/notifications/info?t=1234567890

# Expected response:
# {
#   "websocket": true,
#   "origins": ["*:*"],
#   "cookie_needed": false,
#   "entropy": 123456789
# }
```

---

## 🚀 DEPLOYMENT NOTES

### For HTTPS in Production

When deploying with HTTPS:
- Use `wss://` (WebSocket Secure) instead of `ws://`
- Ensure SSL certificate is valid
- Update frontend to use `wss://` URLs
- CORS headers will automatically handle it with `setAllowedOriginPatterns("*")`

### Example Production URL

```
wss://app.example.com/api/v1/ws/sockjs/notifications?token=...
```

---

## 📝 FILES MODIFIED

**Backend:**
- `WebSocketConfig.java` - Added notifications endpoints

**Frontend (TODO):**
- `realTimeNotificationService.ts` - Fix URL scheme and endpoint
- `NotificationContext.tsx` - Update connection logic

---

## ✅ VERIFICATION CHECKLIST

- ✅ Backend WebSocket endpoints configured
- ✅ CORS enabled for all origins
- ✅ JWT authentication interceptor active
- ✅ SockJS fallback available
- 📋 Frontend needs to fix URL scheme (ws:// vs http://)
- 📋 Frontend needs to update endpoint path

---

**Status**: 🟡 **PARTIALLY FIXED**

Backend is ready. Frontend needs URL scheme fix.

*Last Updated: March 17, 2026*


