# 🔥 Firebase Push Notification — Implementation Guide
**Bidzaro Catering Platform**

---

## 📋 Table of Contents
1. [Overview](#-overview)
2. [Firebase Console Setup](#-firebase-console-setup)
3. [Service Account Credentials](#-service-account-credentials)
4. [Backend Configuration](#-backend-configuration)
5. [Frontend / Mobile Integration](#-frontend--mobile-integration)
6. [How Push Notifications Are Triggered](#-how-push-notifications-are-triggered)
7. [FCM Token Management](#-fcm-token-management)
8. [Notification Types & Payloads](#-notification-types--payloads)
9. [Testing Push Notifications](#-testing-push-notifications)
10. [Troubleshooting](#-troubleshooting)

---

## 🌐 Overview

The platform uses **Firebase Cloud Messaging (FCM)** via the **Firebase Admin SDK** (`firebase-admin 9.2.0`) for push notifications.

**Supported delivery modes:**
- `sendPushNotification(fcmToken, title, body, data)` — Single device
- `sendMulticastNotification(tokens, title, body, data)` — Multiple devices
- `sendTopicNotification(topic, title, body, data)` — Topic-based broadcast

**Notification is sent in addition to:**
- Email (SMTP/SendGrid)
- SMS (Twilio)
- WhatsApp (Twilio)
- In-App (stored in MongoDB)

---

## 🔧 Firebase Console Setup

### Step 1 — Create / Select Firebase Project

1. Go to **https://console.firebase.google.com/**
2. Click **Add project** (or select existing)
3. Enter project name: `bidzaro-catering`
4. Enable/disable Google Analytics as preferred → **Create project**

---

### Step 2 — Enable Cloud Messaging

1. In Firebase Console → **Project Settings** (⚙️ gear icon)
2. Click **Cloud Messaging** tab
3. Note your **Server Key** (legacy) — not needed for Admin SDK
4. The **Project ID** shown here is what you need

---

### Step 3 — Register Your Apps

#### For React Native / Expo (Mobile)
1. **Project Settings → Your apps → Add app → Android**
   - Package name: `com.bidzaro.app`
   - Download `google-services.json` → place in `/android/app/`
2. **Add app → iOS**
   - Bundle ID: `com.bidzaro.app`
   - Download `GoogleService-Info.plist` → place in `/ios/`

#### For Web (React/Next.js)
1. **Project Settings → Your apps → Add app → Web**
   - App nickname: `Bidzaro Web`
   - Copy the Firebase config object

---

## 🔑 Service Account Credentials

### Step 1 — Generate Service Account Key
1. Firebase Console → **Project Settings → Service accounts**
2. Click **Generate new private key**
3. A JSON file will be downloaded (e.g., `bidzaro-firebase-adminsdk.json`)

### Step 2 — Place the File

**Option A: Filesystem (recommended for development)**
```
bidzaro_monolithic/
  firebase-credentials.json    ← Place here (gitignored)
```

**Option B: Classpath (production/Docker)**
```
src/main/resources/firebase-credentials.json
```

> ⚠️ **IMPORTANT**: Never commit this file to Git. Add to `.gitignore`:
> ```
> firebase-credentials.json
> ```

### Step 3 — The JSON Format
```json
{
  "type": "service_account",
  "project_id": "bidzaro-catering",
  "private_key_id": "abc123...",
  "private_key": "-----BEGIN RSA PRIVATE KEY-----\n...\n-----END RSA PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-xxxx@bidzaro-catering.iam.gserviceaccount.com",
  "client_id": "123456789",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/..."
}
```

---

## ⚙️ Backend Configuration

### application.yml
```yaml
firebase:
  credentials-path: ${FIREBASE_CREDENTIALS_PATH:firebase-credentials.json}
  project-id: ${FIREBASE_PROJECT_ID:bidzaro-catering}
```

### .env file
```env
FIREBASE_CREDENTIALS_PATH=firebase-credentials.json
FIREBASE_PROJECT_ID=bidzaro-catering
```

### Docker (docker-compose.yml)
```yaml
services:
  app:
    environment:
      - FIREBASE_PROJECT_ID=bidzaro-catering
      - FIREBASE_CREDENTIALS_PATH=/app/config/firebase-credentials.json
    volumes:
      - ./firebase-credentials.json:/app/config/firebase-credentials.json:ro
```

### Verification on Startup
Check logs for:
```
INFO  FirebaseService : Firebase initialized successfully for project: bidzaro-catering
```

Or if not configured:
```
WARN  FirebaseService : Firebase project-id not configured. Push notifications will be disabled.
```

---

## 📱 Frontend / Mobile Integration

### React Native / Expo Setup

**Install packages:**
```bash
npx expo install @react-native-firebase/app @react-native-firebase/messaging
# OR with Expo Go:
npx expo install expo-notifications
```

**Get FCM Token:**
```javascript
import * as Notifications from 'expo-notifications';
import * as Device from 'expo-device';

async function getFcmToken() {
  if (!Device.isDevice) return null;

  const { status } = await Notifications.requestPermissionsAsync();
  if (status !== 'granted') return null;

  const token = (await Notifications.getExpoPushTokenAsync()).data;
  // OR for raw FCM token:
  // const { data: token } = await Notifications.getDevicePushTokenAsync();
  return token;
}
```

**Send FCM token to backend after login:**
```javascript
const fcmToken = await getFcmToken();
await axios.put('/api/v1/users/me/fcm-token',
  { fcmToken },
  { headers: { Authorization: `Bearer ${accessToken}` } }
);
```

### Web (React) Setup

**Install:**
```bash
npm install firebase
```

**firebase.js:**
```javascript
import { initializeApp } from 'firebase/app';
import { getMessaging, getToken, onMessage } from 'firebase/messaging';

const firebaseConfig = {
  apiKey: "YOUR_API_KEY",
  authDomain: "bidzaro-catering.firebaseapp.com",
  projectId: "bidzaro-catering",
  storageBucket: "bidzaro-catering.appspot.com",
  messagingSenderId: "YOUR_SENDER_ID",
  appId: "YOUR_APP_ID"
};

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

export async function requestFcmToken() {
  try {
    const token = await getToken(messaging, {
      vapidKey: 'YOUR_VAPID_KEY' // From Firebase Console > Cloud Messaging > Web Push certificates
    });
    return token;
  } catch (err) {
    console.error('Failed to get FCM token:', err);
    return null;
  }
}

// Listen for foreground messages
export function onForegroundMessage(callback) {
  return onMessage(messaging, callback);
}
```

**public/firebase-messaging-sw.js** (service worker for background messages):
```javascript
importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-messaging-compat.js');

firebase.initializeApp({
  apiKey: "YOUR_API_KEY",
  projectId: "bidzaro-catering",
  messagingSenderId: "YOUR_SENDER_ID",
  appId: "YOUR_APP_ID"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function(payload) {
  const notificationTitle = payload.notification.title;
  const notificationOptions = {
    body: payload.notification.body,
    icon: '/firebase-logo.png'
  };
  self.registration.showNotification(notificationTitle, notificationOptions);
});
```

---

## 🔔 How Push Notifications Are Triggered

### Via `NotificationService`
The `NotificationService.sendNotification()` is called with `channel: PUSH`:
```java
notificationService.sendNotification(
  userId,
  "Order Confirmed!",
  "Your order #12345 has been confirmed.",
  "ORDER_CONFIRMED",
  NotificationChannel.PUSH,
  Map.of("orderId", orderId, "deepLink", "/orders/" + orderId)
);
```

This internally calls `FirebaseService.sendPushNotification(fcmToken, title, body, dataMap)`.

### Key Events that trigger PUSH:
| Event | Triggered In |
|-------|-------------|
| Bid accepted | `BidService.acceptBid()` |
| Token payment confirmed | `PaymentService.verifyPayment()` |
| Order status changed | `OrderService.updateOrderStatus()` |
| New message in chat | `ChatService.sendMessage()` |
| New bid received | `BidService.submitBid()` |
| Vendor approved | `VendorService.approveVendor()` |
| Support ticket assigned | `SupportService.assignTicket()` |

---

## 🎫 FCM Token Management

### Store token on login
`PUT /api/v1/users/me/fcm-token` 🔒
```json
{ "fcmToken": "cNh-w3k4RH-xxxxxxxxxxxxxxxx..." }
```

### Token Refresh
FCM tokens expire. Handle refresh in mobile:
```javascript
import messaging from '@react-native-firebase/messaging';

messaging().onTokenRefresh(async (token) => {
  await updateFcmToken(token);
});
```

### Multiple Devices
The current implementation stores **one FCM token per user** in `User.fcmToken`. For multi-device support, you would extend the `User` model to store a list of tokens.

---

## 📨 Notification Types & Payloads

### FCM Message Structure
```json
{
  "token": "device-fcm-token",
  "notification": {
    "title": "Order Confirmed!",
    "body": "Your catering order for April 15 has been confirmed."
  },
  "data": {
    "orderId": "uuid",
    "deepLink": "/orders/uuid",
    "notificationType": "ORDER_CONFIRMED"
  },
  "android": { "priority": "HIGH" },
  "apns": { "aps": { "sound": "default" } }
}
```

### Data Payload Keys
| Key | Description |
|-----|-------------|
| `orderId` | Related order UUID |
| `bidId` | Related bid UUID |
| `conversationId` | Chat conversation UUID |
| `deepLink` | App navigation path |
| `notificationType` | `ORDER_CONFIRMED`, `BID_ACCEPTED`, `NEW_MESSAGE`, etc. |
| `extra` | Raw string data (legacy fallback) |

---

## 🧪 Testing Push Notifications

### Using Firebase Console (Manual Test)
1. Firebase Console → **Cloud Messaging → Send your first message**
2. Notification text: `Test push from Firebase`
3. Target: **Single device** → paste FCM token from your device
4. Click **Send message**

### Using Firebase REST API
```bash
curl -X POST https://fcm.googleapis.com/v1/projects/bidzaro-catering/messages:send \
  -H "Authorization: Bearer $(gcloud auth print-access-token)" \
  -H "Content-Type: application/json" \
  -d '{
    "message": {
      "token": "YOUR_FCM_TOKEN",
      "notification": { "title": "Test", "body": "Hello from Bidzaro!" }
    }
  }'
```

### Via Backend API (Postman)
Call the notification endpoint which triggers push:
```
PUT /api/v1/users/me/fcm-token
Body: { "fcmToken": "your-real-device-token" }
```
Then place an order/accept a bid to trigger a push notification.

---

## 🛠️ Troubleshooting

| Problem | Cause | Fix |
|---------|-------|-----|
| `Firebase project-id not configured` | Missing env var | Set `FIREBASE_PROJECT_ID` in `.env` |
| `Firebase credentials file not found` | Wrong path | Check `FIREBASE_CREDENTIALS_PATH`, ensure file exists |
| `Failed to initialize Firebase: invalid_grant` | Expired service account key | Regenerate key in Firebase Console |
| `UNREGISTERED` error | Stale FCM token | Token expired — user needs to refresh token |
| `INVALID_ARGUMENT` error | Malformed token | Check token format; ensure it's a valid FCM registration token |
| Push not received on iOS | Missing APNs config | Configure APNs in Firebase Console → Project Settings → Cloud Messaging → iOS |
| Push not received on Android | Missing `google-services.json` | Ensure file is in `/android/app/` and build is updated |
| Background messages not showing (Web) | Missing service worker | Ensure `firebase-messaging-sw.js` is in `public/` folder |

---

## 📋 Summary Checklist

- [ ] Firebase project created
- [ ] Cloud Messaging enabled
- [ ] Android/iOS/Web apps registered in Firebase Console
- [ ] Service account key generated
- [ ] `firebase-credentials.json` placed in project root (gitignored)
- [ ] `FIREBASE_PROJECT_ID` set in `.env`
- [ ] Frontend installs Firebase SDK
- [ ] Frontend requests notification permission
- [ ] Frontend gets FCM token and sends to `PUT /users/me/fcm-token`
- [ ] FCM token refresh handler implemented
- [ ] Test push notification via Firebase Console

