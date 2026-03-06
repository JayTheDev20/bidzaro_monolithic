# 💬 Chat Module — Integration Guide
**Bidzaro Catering Platform** | Real-time chat via STOMP over WebSocket

---

## 📋 Table of Contents
1. [Overview](#-overview)
2. [Chat Architecture](#-chat-architecture)
3. [Conversation Types](#-conversation-types)
4. [REST API Reference](#-rest-api-reference)
5. [WebSocket Connection](#-websocket-connection)
6. [STOMP Integration](#-stomp-integration)
7. [Frontend Integration (React / React Native)](#-frontend-integration)
8. [Message Types & Payloads](#-message-types--payloads)
9. [Chat with Support — Ticket Flow](#-chat-with-support--ticket-flow)
10. [Read Receipts](#-read-receipts)
11. [File/Image Sharing in Chat](#-fileimage-sharing-in-chat)
12. [Push Notifications for Chat](#-push-notifications-for-chat)

---

## 🌐 Overview

The chat system supports:
- **User ↔ Vendor** messaging
- **User ↔ Support Agent** messaging
- **Vendor ↔ Support Agent** messaging

Both **REST API** (for fetching history) and **WebSocket / STOMP** (for real-time messaging) are supported.

---

## 🏗️ Chat Architecture

```
Client (REST) ──────────── GET /chat/conversations
Client (REST) ──────────── GET /chat/conversations/{id}/messages
Client (REST) ──────────── POST /chat/conversations/{id}/messages

Client (WebSocket/STOMP) ── CONNECT ws://host/api/v1/ws?token=JWT
                          ── SUBSCRIBE /topic/conversations.{id}
                          ── SEND /app/chat/{id}  → Message broadcast
                          ── SUBSCRIBE /topic/conversations.{id}.read
```

```
Backend Stores:
  MongoDB Collection: conversations  → Conversation metadata
  MongoDB Collection: messages       → Individual messages
```

---

## 🗂️ Conversation Types

| Type | Participants | Created By |
|------|-------------|------------|
| `USER_VENDOR` | User + Vendor | User (POST /chat/conversations) |
| `USER_SUPPORT` | User + Support Agent | Auto on ticket creation |
| `VENDOR_SUPPORT` | Vendor + Support Agent | Auto on ticket creation |

---

## 🔗 REST API Reference

### Base URL: `http://localhost:8080/api/v1`
> All endpoints require: `Authorization: Bearer <token>`

---

### Get My Conversations
`GET /chat/conversations?page=0&size=20`

**Response `200`:**
```json
{
  "data": [
    {
      "conversationId": "conv-uuid",
      "conversationType": "USER_VENDOR",
      "status": "ACTIVE",
      "participants": [
        { "userId": "user-uuid", "userType": "USER", "name": "Rahul Sharma" },
        { "userId": "vendor-user-uuid", "userType": "VENDOR", "name": "Royal Catering Co." }
      ],
      "unreadCount": { "user-uuid": 3, "vendor-user-uuid": 0 },
      "lastMessage": {
        "message": "What is your per-plate rate?",
        "senderId": "user-uuid",
        "timestamp": "2026-03-06T11:00:00Z"
      },
      "createdAt": "2026-03-06T10:00:00Z"
    }
  ]
}
```

---

### Create or Get Conversation
`POST /chat/conversations`

**User ↔ Vendor:**
```json
{
  "vendorId": "vendor-uuid",
  "type": "USER_VENDOR"
}
```

**User ↔ Support:**
```json
{
  "type": "USER_SUPPORT"
}
```

> If conversation already exists between the two parties, the existing one is returned.

**Response `200`:**
```json
{
  "data": {
    "conversationId": "conv-uuid",
    "conversationType": "USER_VENDOR",
    "status": "ACTIVE",
    "participants": [ ... ],
    "unreadCount": {},
    "lastMessage": null,
    "createdAt": "2026-03-06T10:00:00Z"
  }
}
```

---

### Get Conversation by ID
`GET /chat/conversations/{conversationId}`

---

### Get Messages (Paginated — Oldest First)
`GET /chat/conversations/{conversationId}/messages?page=0&size=50`

**Response `200`:**
```json
{
  "data": [
    {
      "messageId": "msg-uuid",
      "conversationId": "conv-uuid",
      "senderId": "user-uuid",
      "senderType": "USER",
      "message": "Hello, can you cater for 300 guests?",
      "messageType": "TEXT",
      "fileUrl": null,
      "timestamp": "2026-03-06T10:05:00Z",
      "readBy": [
        { "userId": "vendor-user-uuid", "readAt": "2026-03-06T10:06:00Z" }
      ],
      "isDeleted": false
    }
  ]
}
```

---

### Send Message (REST fallback)
`POST /chat/conversations/{conversationId}/messages`
```json
{
  "message": "Yes, we can handle 300 guests. Our rate is ₹400/plate.",
  "messageType": "TEXT"
}
```

**Response `201`:**
```json
{
  "data": {
    "messageId": "msg-uuid",
    "conversationId": "conv-uuid",
    "senderId": "vendor-user-uuid",
    "senderType": "VENDOR",
    "message": "Yes, we can handle 300 guests.",
    "messageType": "TEXT",
    "timestamp": "2026-03-06T10:10:00Z",
    "readBy": [],
    "isDeleted": false
  }
}
```

---

### Mark Messages as Read
`PUT /chat/conversations/{conversationId}/read`

**Response `200`:** `{ "success": true }`

> Resets unread count for calling user. Broadcasts read receipt via STOMP.

---

### Delete Message (Soft Delete — Own Messages Only)
`DELETE /chat/messages/{messageId}`

---

## 🔌 WebSocket Connection

### Endpoint
| Protocol | URL |
|----------|-----|
| WebSocket | `ws://localhost:8080/api/v1/ws?token=<JWT>` |
| SockJS | `http://localhost:8080/api/v1/ws/sockjs/chat` |

> **Authentication** is done via query parameter `?token=<accessToken>` at connection time.

---

## 📡 STOMP Integration

### STOMP Destinations

| Type | Destination | Description |
|------|------------|-------------|
| Subscribe | `/topic/conversations.{conversationId}` | Receive new messages |
| Subscribe | `/topic/conversations.{conversationId}.read` | Receive read receipts |
| Subscribe | `/user/queue/errors` | Receive personal error messages |
| Send | `/app/chat/{conversationId}` | Send a message |

---

### Full WebSocket Flow

```
1. Obtain accessToken via POST /auth/login

2. CONNECT to ws://localhost:8080/api/v1/ws?token=<accessToken>

3. SUBSCRIBE /topic/conversations.{conversationId}
   → You will receive new messages broadcast to all participants

4. SEND /app/chat/{conversationId}
   Body: { "message": "Hello!", "messageType": "TEXT" }
   → Message saved to MongoDB, broadcast to all subscribers

5. SUBSCRIBE /topic/conversations.{conversationId}.read
   → Receive read receipt events
```

---

## 💻 Frontend Integration

### React / Web (using `@stomp/stompjs` + `sockjs-client`)

**Install:**
```bash
npm install @stomp/stompjs sockjs-client
```

**ChatService.js:**
```javascript
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const BASE_URL = 'http://localhost:8080/api/v1';

let stompClient = null;

export function connectWebSocket(accessToken, conversationId, onMessage, onReadReceipt) {
  stompClient = new Client({
    webSocketFactory: () => new SockJS(`${BASE_URL}/ws/sockjs/chat`),
    connectHeaders: {
      Authorization: `Bearer ${accessToken}`
    },
    // Alternative: pass token in query param
    // webSocketFactory: () => new SockJS(`${BASE_URL}/ws/sockjs/chat?token=${accessToken}`),

    onConnect: () => {
      console.log('WebSocket connected');

      // Subscribe to new messages
      stompClient.subscribe(
        `/topic/conversations.${conversationId}`,
        (frame) => {
          const message = JSON.parse(frame.body);
          onMessage(message);
        }
      );

      // Subscribe to read receipts
      stompClient.subscribe(
        `/topic/conversations.${conversationId}.read`,
        (frame) => {
          const receipt = JSON.parse(frame.body);
          onReadReceipt(receipt);
        }
      );
    },

    onDisconnect: () => console.log('WebSocket disconnected'),
    onStompError: (error) => console.error('STOMP error:', error),
    reconnectDelay: 5000
  });

  stompClient.activate();
}

export function sendMessage(conversationId, message, messageType = 'TEXT') {
  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination: `/app/chat/${conversationId}`,
      body: JSON.stringify({ message, messageType })
    });
  }
}

export function disconnectWebSocket() {
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
  }
}
```

**ChatScreen.jsx:**
```jsx
import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { connectWebSocket, sendMessage, disconnectWebSocket } from './ChatService';

export default function ChatScreen({ conversationId, accessToken, currentUserId }) {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const bottomRef = useRef(null);

  useEffect(() => {
    // Load message history
    axios.get(`/api/v1/chat/conversations/${conversationId}/messages`, {
      headers: { Authorization: `Bearer ${accessToken}` }
    }).then(res => setMessages(res.data.data));

    // Mark as read
    axios.put(`/api/v1/chat/conversations/${conversationId}/read`, null, {
      headers: { Authorization: `Bearer ${accessToken}` }
    });

    // Connect WebSocket
    connectWebSocket(
      accessToken,
      conversationId,
      (newMessage) => {
        setMessages(prev => [...prev, newMessage]);
        bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
      },
      (readReceipt) => {
        // Update read status in UI
        console.log('Read receipt:', readReceipt);
      }
    );

    return () => disconnectWebSocket();
  }, [conversationId]);

  const handleSend = () => {
    if (input.trim()) {
      sendMessage(conversationId, input.trim(), 'TEXT');
      setInput('');
    }
  };

  return (
    <div className="chat-screen">
      <div className="messages">
        {messages.map(msg => (
          <div key={msg.messageId}
               className={msg.senderId === currentUserId ? 'sent' : 'received'}>
            <p>{msg.message}</p>
            <span>{new Date(msg.timestamp).toLocaleTimeString()}</span>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>
      <div className="input-area">
        <input value={input} onChange={e => setInput(e.target.value)}
               onKeyDown={e => e.key === 'Enter' && handleSend()} />
        <button onClick={handleSend}>Send</button>
      </div>
    </div>
  );
}
```

---

### React Native / Expo

**Install:**
```bash
npm install @stomp/stompjs
npm install text-encoding  # Required polyfill for React Native
```

**App.js polyfill:**
```javascript
// At the top of App.js (before imports)
import 'text-encoding';
```

**ChatService.js (React Native):**
```javascript
import { Client } from '@stomp/stompjs';

const BASE_URL = 'ws://localhost:8080/api/v1/ws';

let stompClient = null;

export function connectChat(accessToken, conversationId, onMessage, onRead) {
  stompClient = new Client({
    brokerURL: `${BASE_URL}?token=${accessToken}`,

    onConnect: () => {
      stompClient.subscribe(`/topic/conversations.${conversationId}`, frame => {
        onMessage(JSON.parse(frame.body));
      });
      stompClient.subscribe(`/topic/conversations.${conversationId}.read`, frame => {
        onRead(JSON.parse(frame.body));
      });
    },

    reconnectDelay: 5000
  });

  stompClient.activate();
}

export function sendChatMessage(conversationId, message) {
  if (stompClient?.connected) {
    stompClient.publish({
      destination: `/app/chat/${conversationId}`,
      body: JSON.stringify({ message, messageType: 'TEXT' })
    });
  }
}

export function disconnectChat() {
  stompClient?.deactivate();
  stompClient = null;
}
```

---

## 📨 Message Types & Payloads

### Sending a Text Message
```json
{ "message": "Hello! What is your rate?", "messageType": "TEXT" }
```

### Sending an Image (URL)
```json
{
  "message": "Please check this photo of the venue.",
  "messageType": "IMAGE",
  "fileUrl": "https://cdn.example.com/uploads/images/uuid.jpg"
}
```
> Upload image first via `POST /upload/image`, get `fileUrl`, then send in chat.

### Sending a File
```json
{
  "message": "Please find the event brief attached.",
  "messageType": "FILE",
  "fileUrl": "https://cdn.example.com/uploads/documents/uuid.pdf"
}
```

### Received Message Format (STOMP broadcast)
```json
{
  "messageId": "uuid",
  "conversationId": "conv-uuid",
  "senderId": "user-uuid",
  "senderType": "USER",
  "message": "Hello! What is your rate?",
  "messageType": "TEXT",
  "fileUrl": null,
  "timestamp": "2026-03-06T10:05:00Z",
  "readBy": [],
  "isDeleted": false
}
```

### MessageType Enum
| Value | Description |
|-------|-------------|
| `TEXT` | Plain text message |
| `IMAGE` | Image with URL |
| `FILE` | Document/file with URL |
| `SYSTEM` | System-generated message |

---

## 🎫 Chat with Support — Ticket Flow

When a user or vendor creates a support ticket:

1. **Ticket created** → `POST /support/tickets`
2. Backend **auto-assigns** to the agent with the least active tickets
3. A **chat conversation is automatically created** between the ticket creator and the agent
4. The `conversationId` is returned in the ticket response
5. Both parties can chat via `GET /chat/conversations/{conversationId}/messages`

**Ticket response includes:**
```json
{
  "ticketId": "uuid",
  "ticketNumber": "TKT-1234-00001",
  "status": "ASSIGNED",
  "conversationId": "conv-uuid",
  ...
}
```

**Use the `conversationId` from the ticket to open the chat.**

---

## ✅ Read Receipts

### How Read Receipts Work

1. User calls `PUT /chat/conversations/{conversationId}/read`
2. Backend:
   - Updates `readBy` array in all unread messages
   - Resets `unreadCount[userId]` to 0 in the conversation
   - Broadcasts read event via STOMP to `/topic/conversations.{id}.read`

### Read Receipt STOMP Broadcast
```json
{
  "conversationId": "conv-uuid",
  "userId": "user-uuid",
  "readAt": "2026-03-06T10:30:00Z"
}
```

---

## 📎 File/Image Sharing in Chat

**Step 1:** Upload file via REST
```
POST /api/v1/upload/image
Content-Type: multipart/form-data
file: <image-file>
entityType: "CHAT"
entityId: "{conversationId}"
```

**Response:**
```json
{
  "data": {
    "fileId": "uuid",
    "fileUrl": "http://localhost:8080/uploads/images/uuid.jpg"
  }
}
```

**Step 2:** Send in chat (REST or STOMP)
```json
{
  "message": "Here is the event venue photo",
  "messageType": "IMAGE",
  "fileUrl": "http://localhost:8080/uploads/images/uuid.jpg"
}
```

---

## 🔔 Push Notifications for Chat

When a message is sent, the backend:
1. Saves message to MongoDB
2. Updates conversation's `lastMessage`
3. Increments `unreadCount` for recipient
4. Sends **Firebase Push Notification** to recipient's FCM token
5. Broadcasts via **STOMP** to all active WebSocket subscribers

**Push notification payload:**
```json
{
  "title": "New message from Royal Catering",
  "body": "Yes, we can accommodate 300 guests!",
  "data": {
    "conversationId": "conv-uuid",
    "senderId": "vendor-user-uuid",
    "deepLink": "/chat/conv-uuid"
  }
}
```

---

## ⚠️ Common Issues & Fixes

| Problem | Cause | Fix |
|---------|-------|-----|
| WebSocket 403 | Invalid/expired JWT | Re-authenticate and reconnect |
| Messages not received | Wrong topic subscription | Verify `conversationId` is correct |
| `Could not create conversation` | Vendor userId not found | The API accepts both `vendorId` (vendor's business ID) and `otherUserId` |
| Messages loading slowly | Loading entire history | Use pagination: `?page=0&size=50` |
| Duplicate messages | Subscribed twice | Ensure single subscription per conversation |
| Read count not updating | Not calling mark-as-read | Call `PUT /chat/conversations/{id}/read` when opening chat |
| Push not triggering | FCM token not set | Ensure `PUT /users/me/fcm-token` is called after login |

---

## 📋 Integration Checklist

- [ ] Authenticate and get `accessToken`
- [ ] Create or get conversation via REST
- [ ] Load message history via `GET /messages`
- [ ] Connect to WebSocket with token
- [ ] Subscribe to `/topic/conversations.{id}`
- [ ] Subscribe to `/topic/conversations.{id}.read`
- [ ] Implement `sendMessage` via STOMP
- [ ] Call `PUT .../read` when chat is opened
- [ ] Handle read receipt events in UI
- [ ] Handle image/file upload flow
- [ ] Register FCM token for push notifications when app is backgrounded
- [ ] Handle WebSocket disconnect + reconnect logic

