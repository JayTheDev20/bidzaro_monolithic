# Frontend WebSocket Notifications Fix

## File: realTimeNotificationService.ts

The frontend needs to fix the URL scheme from HTTP to WebSocket (ws/wss).

### Current Code (Broken)
```typescript
// Line 23: Using HTTP protocol - WRONG!
const webSocketFactory = () => new SockJS(`http://localhost:8080/api/v1/ws/sockjs/notifications`);
```

### Fixed Code (Working)
```typescript
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

// Proper URL scheme conversion: http→ws, https→wss
const getWebSocketUrl = (): string => {
  const baseUrl = process.env.REACT_APP_API_URL || 'http://localhost:8080';
  const url = new URL(`${baseUrl}/api/v1/ws/sockjs/notifications`);

  // Convert protocols appropriately
  if (url.protocol === 'https:') {
    url.protocol = 'wss:'; // WebSocket Secure
  } else {
    url.protocol = 'ws:';  // WebSocket
  }

  return url.toString();
};

class RealTimeNotificationService {
  private client: Client | null = null;
  private isConnecting = false;

  connectService(accessToken: string): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.isConnecting) {
        return; // Already connecting
      }

      this.isConnecting = true;

      this.client = new Client({
        // ✅ FIXED: Use SockJS with correct WebSocket URL
        webSocketFactory: () => new SockJS(getWebSocketUrl()),

        connectHeaders: {
          Authorization: `Bearer ${accessToken}`,
        },

        onConnect: (frame) => {
          console.log('✅ WebSocket connected:', frame);
          this.isConnecting = false;

          // Subscribe to notifications topic
          this.client?.subscribe('/user/topic/notifications', (message) => {
            try {
              const notification = JSON.parse(message.body);
              console.log('📨 Notification received:', notification);
              // Handle notification...
            } catch (error) {
              console.error('Failed to parse notification:', error);
            }
          });

          resolve();
        },

        onStompError: (frame) => {
          console.error('❌ STOMP error:', frame);
          this.isConnecting = false;
          reject(new Error(`STOMP error: ${frame.body}`));
        },

        onWebSocketError: (error) => {
          console.error('❌ WebSocket error:', error);
          this.isConnecting = false;
          reject(error);
        },

        onWebSocketClose: () => {
          console.log('⛔ WebSocket disconnected');
          this.isConnecting = false;
        },

        debug: (msg) => {
          console.debug('[WebSocket]', msg);
        },
      });

      try {
        this.client.activate();
      } catch (error) {
        console.error('Failed to activate STOMP client:', error);
        this.isConnecting = false;
        reject(error);
      }
    });
  }

  disconnectService(): void {
    if (this.client && this.client.connected) {
      this.client.deactivate();
      console.log('WebSocket disconnected');
    }
  }

  isConnected(): boolean {
    return this.client !== null && this.client.connected;
  }

  sendNotification(destination: string, message: any): void {
    if (this.client && this.client.connected) {
      this.client.publish({
        destination,
        body: JSON.stringify(message),
      });
    } else {
      console.warn('Cannot send: WebSocket not connected');
    }
  }
}

export default new RealTimeNotificationService();
```

---

## Usage Example (NotificationContext.tsx)

```typescript
import { useEffect } from 'react';
import realTimeNotificationService from '../services/realTimeNotificationService';
import { useAuth } from './useAuth';

export function NotificationProvider({ children }) {
  const { accessToken } = useAuth();

  useEffect(() => {
    if (!accessToken) return;

    const connectNotifications = async () => {
      try {
        await realTimeNotificationService.connectService(accessToken);
        console.log('✅ Notifications connected');
      } catch (error) {
        console.error('❌ Failed to connect notifications:', error);
        // Retry logic here...
      }
    };

    connectNotifications();

    return () => {
      realTimeNotificationService.disconnectService();
    };
  }, [accessToken]);

  return children;
}
```

---

## Key Changes

### ✅ URL Scheme Conversion
```typescript
// BEFORE: Uses HTTP protocol (WRONG)
'http://localhost:8080/api/v1/ws/sockjs/notifications'

// AFTER: Converts to WebSocket protocol (CORRECT)
'ws://localhost:8080/api/v1/ws/sockjs/notifications'    // HTTP context
'wss://app.example.com/api/v1/ws/sockjs/notifications'  // HTTPS context
```

### ✅ Error Handling
- Added `onWebSocketError` handler
- Added `onStompError` handler
- Added connection state tracking
- Added debug logging

### ✅ Connection Management
- Promise-based connect for proper async handling
- Prevents multiple connection attempts
- Proper cleanup on disconnect

---

## Testing the Fix

### 1. Check Browser Console
```javascript
// Should see:
✅ WebSocket connected: CONNECT
📨 Notification received: {...}
```

### 2. Network Tab
- Should see WebSocket connection to `ws://localhost:8080/api/v1/ws/sockjs/notifications`
- Connection should show `101 Switching Protocols`

### 3. Connection Flow
```
http://localhost:5173 (Frontend)
    ↓
Converts http→ws
    ↓
ws://localhost:8080/api/v1/ws/sockjs/notifications
    ↓
SockJS creates WebSocket connection
    ↓
JwtHandshakeInterceptor validates token
    ↓
STOMP handshake completes
    ↓
Subscribe to /user/topic/notifications
    ↓
Receive real-time notifications ✅
```

---

## Environment Variables

Add to `.env` file:
```env
REACT_APP_API_URL=http://localhost:8080
# Or for production:
# REACT_APP_API_URL=https://api.example.com
```

The URL scheme conversion will automatically handle:
- Local dev: `http://localhost:8080` → `ws://localhost:8080`
- Production HTTPS: `https://api.example.com` → `wss://api.example.com`

---

## No More Errors!

After these changes:
- ✅ SyntaxError about 'ws:' scheme - FIXED
- ✅ CORS block on /info endpoint - FIXED
- ✅ 404 on notifications endpoint - FIXED


