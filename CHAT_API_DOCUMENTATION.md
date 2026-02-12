# Chat Module Master Documentation

This document is the **single source of truth** for developing the Chat Module in the frontend. It includes all REST APIs, WebSocket events, Payloads, and Responses.

## 1. Base Configuration

*   **REST Base URL:** `http://localhost:8080/api/v1/chat`
*   **WebSocket URL (Raw):** `ws://localhost:8080/api/v1/ws/chat`
*   **WebSocket URL (SockJS):** `http://localhost:8080/api/v1/ws/sockjs/chat`
*   **Authentication:** All endpoints require a valid JWT token.
    *   **REST:** Header `Authorization: Bearer <TOKEN>`
    *   **WebSocket:** Query Parameter `?token=<TOKEN>`

---

## 2. REST APIs (Management)

### A. Create or Get Conversation
Starts a chat. You can use either the User ID or the Vendor ID.

*   **URL:** `/conversations`
*   **Method:** `POST`
*   **Payload (Option 1: Using User ID):**
    ```json
    {
      "otherUserId": "e7b26c6a-...", // Target User ID
      "type": "USER_VENDOR"
    }
    ```
*   **Payload (Option 2: Using Vendor ID):**
    *Use this when you only have the Vendor Profile ID (e.g., from Order History).*
    ```json
    {
      "vendorId": "899f1263-...", // Vendor Profile ID
      "type": "USER_VENDOR"
    }
    ```
*   **Response (200 OK):**
    ```json
    {
      "success": true,
      "data": {
        "conversationId": "conv_123",
        "participants": [ ... ],
        "otherParticipant": {  // <--- USE THIS to display chat name/avatar
          "userId": "e7b26c6a...",
          "name": "Pavan Catering",
          "profilePictureUrl": "http://..."
        },
        "lastMessage": null,
        "unreadCount": 0,
        "status": "ACTIVE"
      }
    }
    ```

### B. Get My Conversations
Retrieves a list of all active conversations.

*   **URL:** `/conversations`
*   **Method:** `GET`
*   **Query Params:** `page=0`, `size=20`
*   **Response (200 OK):**
    ```json
    {
      "success": true,
      "data": [
        {
          "conversationId": "conv_123",
          "otherParticipant": {
            "userId": "e7b26c6a...",
            "name": "Pavan Catering",
            "profilePictureUrl": "..."
          },
          "lastMessage": {
            "message": "Hello",
            "timestamp": "2026-02-11T10:00:00Z"
          },
          "unreadCount": 2
        }
      ]
    }
    ```

### C. Get Message History
Loads past messages for a specific conversation.

*   **URL:** `/conversations/{conversationId}/messages`
*   **Method:** `GET`
*   **Query Params:** `page=0`, `size=50`
*   **Response (200 OK):**
    ```json
    {
      "success": true,
      "data": [
        {
          "messageId": "msg_123",
          "conversationId": "conv_123",
          "senderId": "e7b26c6a...",
          "message": "Hello! How can I help?",
          "messageType": "TEXT",
          "attachments": [],
          "timestamp": "2026-02-11T10:05:00Z"
        }
      ]
    }
    ```

### D. Mark as Read
Marks all messages in a conversation as read. Call this when the user opens the chat window.

*   **URL:** `/conversations/{conversationId}/read`
*   **Method:** `PATCH`
*   **Response:** Success message. Triggers WebSocket event.

### E. Upload File
Uploads an image or document.

*   **URL:** `/upload`
*   **Method:** `POST`
*   **Content-Type:** `multipart/form-data`
*   **Form Data:** `file` (Binary)
*   **Response:**
    ```json
    {
      "success": true,
      "data": {
        "fileName": "menu.pdf",
        "fileUrl": "http://localhost:8080/uploads/chat/menu_123.pdf",
        "fileType": "pdf",
        "fileSize": 102400
      }
    }
    ```

---

## 3. WebSocket Real-Time Events

### Connection
*   **URL:** `ws://localhost:8080/api/v1/ws/chat?token=<YOUR_JWT>`

### A. Sending Messages (Client -> Server)
*   **Destination:** `/app/chat.sendMessage`
*   **Payload (Text):**
    ```json
    {
      "conversationId": "conv_123",
      "message": "Is the food ready?",
      "messageType": "TEXT"
    }
    ```
*   **Payload (File):**
    ```json
    {
      "conversationId": "conv_123",
      "message": "Here is the menu",
      "messageType": "FILE",
      "attachments": [
        { "fileName": "menu.pdf", "fileUrl": "...", "fileType": "pdf", "fileSize": 1024 }
      ]
    }
    ```

### B. Sending Typing Status (Client -> Server)
*   **Destination:** `/app/chat.typing`
*   **Payload:**
    ```json
    {
      "conversationId": "conv_123",
      "userId": "my_user_id",
      "typing": true
    }
    ```

### C. Receiving Messages (Server -> Client)
*   **Subscribe To:** `/topic/conversations.{conversationId}`
*   **Payload:**
    ```json
    {
      "messageId": "msg_456",
      "conversationId": "conv_123",
      "senderId": "e7b26c6a...",
      "message": "Yes, it is ready.",
      "messageType": "TEXT",
      "timestamp": "2026-02-11T10:06:00Z"
    }
    ```

### D. Receiving Typing Status (Server -> Client)
*   **Subscribe To:** `/topic/conversations.{conversationId}.typing`
*   **Payload:** `{ "userId": "...", "typing": true }`

### E. Receiving Read Receipts (Server -> Client)
*   **Subscribe To:** `/topic/conversations.{conversationId}.read`
*   **Payload:** `{ "userId": "...", "readAt": "..." }`

### F. Receiving Online Status (Server -> Client)
*   **Subscribe To:** `/topic/public.users`
*   **Payload:** `{ "userId": "...", "status": "ONLINE" }`

---

## 4. Push Notifications (FCM)
The backend automatically sends a Push Notification via Firebase if the recipient is not connected to the WebSocket.

*   **Title:** "New Message from [Sender Name]"
*   **Body:** "[Message Content]"
*   **Data:** `{ "conversationId": "conv_123" }`

---

## 5. Frontend Implementation Checklist

1.  **Chat List:** Call `GET /conversations`. Use `otherParticipant` to show name/avatar.
2.  **Start Chat:** Call `POST /conversations` with `vendorId` (from Order) or `otherUserId`.
3.  **Chat Window:**
    *   Call `GET .../messages` for history.
    *   Connect WebSocket.
    *   Subscribe to `/topic/conversations.{id}`.
    *   Call `PATCH .../read`.
4.  **Send:** Publish to `/app/chat.sendMessage`.
5.  **Typing:** Publish to `/app/chat.typing` on input change.
6.  **Blue Ticks:** Listen to `/topic/.../read` and update message status.
