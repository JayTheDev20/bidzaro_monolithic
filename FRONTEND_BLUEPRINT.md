# 🚀 Ultimate "Bidzaro" Frontend Monorepo Blueprint

**Role:** Principal Full-Stack Architect.
**Task:** Scaffold a **TurboRepo Monorepo** for "Bidzaro", a Catering Marketplace.
**Backend:** Spring Boot Modular Monolith (Verified). Base URL: `http://localhost:8080/api/v1`.
**Goal:** Build 4 distinct Next.js Applications that consume the backend APIs fully.

---

## 1️⃣ Global Architecture & Stack

*   **Repo Structure:** TurboRepo (`/apps`, `/packages`).
*   **Package Manager:** pnpm.
*   **Apps:**
    1.  `apps/customer` (Port 3000) - For End Users (B2C).
    2.  `apps/vendor` (Port 3001) - For Service Providers (B2B).
    3.  `apps/admin` (Port 3002) - For Platform Admins (Internal).
    4.  `apps/support` (Port 3003) - For Helpdesk Agents (Internal).
*   **Framework:** Next.js 14 (App Router).
*   **Language:** TypeScript (Strict Mode).
*   **Styling:** Tailwind CSS + Shadcn/UI (Radix).
*   **State Management:** Zustand (Global Store), TanStack Query v5 (Server State/Caching).
*   **Forms:** React Hook Form + Zod.
*   **Real-time:** `sockjs-client` + `@stomp/stompjs` (for Spring Boot WebSockets).
*   **HTTP Client:** Axios (with Interceptors for Auto-Refresh Token).

---

## 2️⃣ Shared Packages (`/packages`)

### **A. `packages/types` (The Contract)**
Create TypeScript interfaces matching the API Payloads/Responses defined in Section 3.

### **B. `packages/api` (The Client)**
*   Create a singleton `axios` instance.
*   **Interceptor**:
    *   **Request**: Attach `Authorization: Bearer <token>` from Zustand store.
    *   **Response**: Intercept `401 Unauthorized`. Call `POST /auth/refresh-token`. If success, retry original request. If fail, clear store & redirect to login.

### **C. `packages/ui` (The Design System)**
*   **Shadcn Components**: Button, Input, Table, Dialog, Card, Badge, Form, Toast, DropdownMenu, Avatar, Skeleton.
*   **Custom Components**:
    *   `<FileUploader />`: Drag & drop zone. Calls `/upload/image` or `/upload/document`. Returns URL.
    *   `<ChatWindow />`: Reusable chat UI. Handles WebSocket connection, typing indicators, and message history.
    *   `<StatusBadge />`: Color-coded badges for Order/Bid statuses.
    *   `<MapPicker />`: Leaflet/Google Maps wrapper to pick Lat/Lng for addresses.
    *   `<DataTable />`: Reusable TanStack Table with pagination and sorting.

### **D. `packages/hooks`**
*   `useAuth()`: Session management.
*   `useSocket()`: Global WebSocket connection to `/ws/chat`.
*   `useGeoLocation()`: Fetch user coordinates.

---

## 3️⃣ API Integration Specs (Complete CRUD & Payloads)

**Instruction:** Implement these API calls in `packages/api`. Use the exact JSON keys provided.

### **1. Auth Module**
*   **Login**: `POST /auth/login`
    *   Payload: `{ "identifier": "user@email.com", "password": "...", "fcmToken": "...", "deviceInfo": "..." }`
    *   Response: `{ "accessToken": "...", "refreshToken": "...", "user": { "userId": "...", "userType": "USER|VENDOR|ADMIN" } }`
*   **Register**: `POST /auth/register`
    *   Payload: `{ "email": "...", "phone": "...", "password": "...", "firstName": "...", "lastName": "...", "userType": "USER|VENDOR", "country": "USA" }`
*   **Send OTP**: `POST /auth/send-otp` -> Payload: `{ "identifier": "...", "type": "EMAIL|PHONE" }`
*   **Verify OTP**: `POST /auth/verify-otp` -> Payload: `{ "identifier": "...", "otp": "...", "type": "..." }`
*   **Refresh Token**: `POST /auth/refresh-token` -> Payload: `{ "refreshToken": "..." }`
*   **Recover Email**: `POST /auth/recover/forgot-email` -> Payload: `{ "phone": "..." }`
*   **Recover Phone**: `POST /auth/recover/forgot-phone` -> Payload: `{ "email": "..." }`

### **2. User Module**
*   **Get Profile**: `GET /users/profile`
*   **Update Profile**: `PUT /users/profile` -> Payload: `{ "firstName": "...", "lastName": "...", "gender": "...", "dateOfBirth": "YYYY-MM-DD" }`
*   **Update Picture**: `PATCH /users/profile-picture?imageUrl=...`
*   **Addresses**:
    *   `GET /users/addresses`
    *   `POST /users/addresses` -> Payload: `{ "label": "Home", "streetAddress": "...", "city": "...", "state": "...", "postalCode": "...", "country": "...", "latitude": 12.34, "longitude": 56.78, "isDefault": true }`
    *   `DELETE /users/addresses/{id}`

### **3. Vendor Module**
*   **Register**: `POST /vendors`
    *   Payload: `{ "businessName": "...", "businessEmail": "...", "businessType": "CATERING", "taxId": "...", "documents": [{ "documentType": "LICENSE", "documentUrl": "..." }] }`
*   **Search**: `GET /vendors/search` -> Params: `city`, `cuisine`, `rating`, `query`. Response: `Page<VendorResponse>`.
*   **Get Profile**: `GET /vendors/{id}` (Public), `GET /vendors/me` (Private).
*   **Update Profile**: `PUT /vendors/{id}` -> Payload: `{ "description": "...", "serviceAreas": [...], "capacity": {...} }`
*   **Admin Pending**: `GET /vendors/admin/pending`
*   **Approve/Reject**: `POST /vendors/{id}/approve`, `POST /vendors/{id}/reject?reason=...`

### **4. Menu Module**
*   **Master Items**: `GET /menu/items` (Public), `POST /admin/menu-items` (Admin).
*   **Vendor Items**: `GET /menu/vendor-items?vendorId={id}`
*   **Add Vendor Item**: `POST /menu/vendor-items`
    *   Payload: `{ "masterItemId": "...", "pricePerPlate": 25.00, "discountPercentage": 0, "isAvailable": true, "customizationOptions": [...] }`
*   **Toggle Availability**: `PATCH /menu/vendor-items/{id}/availability?isAvailable=true`

### **5. Bid Module (Core Engine)**
*   **Create Request**: `POST /bids/requests`
    *   Payload:
        ```json
        {
          "eventDetails": { "eventType": "WEDDING", "eventDate": "2024-12-25T18:00:00", "numberOfGuests": 100, "venueAddress": {...} },
          "menuItems": [{ "masterItemId": "...", "quantity": 100 }],
          "budget": { "estimatedBudget": 5000, "currency": "USD" },
          "targetedVendors": []
        }
        ```
*   **Get My Requests**: `GET /bids/requests`
*   **Vendor Get Leads**: `GET /bids/active`
*   **Submit Bid**: `POST /bids/requests/{id}/submit-bid`
    *   Payload:
        ```json
        {
          "quotedPrice": { "subtotal": 4000, "totalAmount": 4500 },
          "itemizedPricing": [{ "vendorItemId": "...", "pricePerPlate": 40 }],
          "validityPeriodHours": 48
        }
        ```
*   **Accept Bid**: `POST /bids/{bidId}/accept` (Starts Cooling Period).

### **6. Order Module**
*   **Create Order**: `POST /orders?bidRequestId={id}` (Called after cooling period).
*   **List**: `GET /orders` (User), `GET /orders/vendor` (Vendor).
*   **Details**: `GET /orders/{id}`.
*   **Update Status**: `PATCH /orders/{id}/status?status={status}`
    *   Statuses: `CONFIRMED`, `IN_PREPARATION`, `READY_FOR_DELIVERY`, `DELIVERED`, `COMPLETED`.
*   **Cancel**: `POST /orders/{id}/cancel?reason=...`

### **7. Payment Module**
*   **Initiate**: `POST /payments/initiate`
    *   Payload: `{ "orderId": "...", "amount": 100.00, "paymentType": "TOKEN" }`
    *   Response: `{ "gatewayOrderId": "...", "keyId": "..." (Razorpay), "clientSecret": "..." (Stripe) }`
*   **Verify**: `POST /payments/verify` -> Payload: `{ "gatewayOrderId": "...", "gatewayPaymentId": "...", "signature": "..." }`

### **8. Chat Module**
*   **List**: `GET /chat/conversations`
*   **History**: `GET /chat/conversations/{id}/messages`
*   **Start**: `POST /chat/conversations?otherUserId={id}`
*   **WebSocket**:
    *   Send: `/app/chat.sendMessage` -> Payload: `{ "conversationId": "...", "message": "...", "messageType": "TEXT" }`
    *   Subscribe: `/topic/conversations.{id}`

### **9. Support Module**
*   **Create**: `POST /support/tickets` -> Payload: `{ "subject": "...", "description": "...", "priority": "HIGH", "category": "ORDER" }`
*   **List**: `GET /support/tickets` (User), `GET /support/admin/tickets` (Agent).
*   **Resolve**: `POST /support/admin/tickets/{id}/resolve`

### **10. Review Module**
*   **Create**: `POST /reviews` -> Payload: `{ "orderId": "...", "rating": 5, "reviewText": "...", "images": [] }`
*   **Reply**: `POST /reviews/{id}/vendor-response`

### **11. Growth Modules**
*   **Promo**: `POST /promos/apply` -> Payload: `{ "code": "SAVE10", "orderTotal": 1000 }`
*   **Loyalty**: `GET /loyalty/balance`
*   **Referral**: `GET /referral/code`
*   **Wishlist**: `POST /wishlist/items?vendorItemId={id}`
*   **Upload**: `POST /upload/image` (Multipart) -> Response: `{ "fileUrl": "..." }`

### **12. Analytics Module**
*   **Admin**: `GET /analytics/overview`
*   **Vendor**: `GET /analytics/vendor/dashboard`

---

## 4️⃣ Application Logic & Pages

### **APP 1: CUSTOMER (`/apps/customer`)**
*   **Auth**: Login, Register, Forgot Password/Email/Phone.
*   **Dashboard**:
    *   "My Bids" (Active/Past).
    *   "Upcoming Orders" (Timeline view).
    *   "Loyalty Points" widget.
*   **Bid Wizard**: 3-Step Form (Event -> Menu -> Budget).
*   **Bid Comparison**: Table comparing Vendor Bids (Price vs Rating). Action: "Accept Bid".
*   **Checkout**:
    *   Order Summary.
    *   Apply Promo Code input.
    *   Redeem Loyalty Points slider.
    *   Pay Token (25%) via Stripe/Razorpay Element.
*   **Tracking**: Live Order Status + Chat with Vendor.
*   **Profile**: Manage Addresses (Map Picker), Notifications, Referrals.

### **APP 2: VENDOR (`/apps/vendor`)**
*   **Onboarding**: Profile setup + Document Upload (License/Tax).
*   **Lead Board**: List of `ACTIVE` requests matching vendor's city/cuisine.
    *   Action: "Bid Now" -> Modal with Pricing Form.
*   **Bid Management**: Track submitted bids. **Revise Bid** button (if outbid).
*   **Order Kanban**: Drag & drop orders (`CONFIRMED` -> `PREPARING` -> `READY` -> `DELIVERED`).
*   **Menu Manager**:
    *   Import from Master Catalog.
    *   Set Price/Discount.
    *   Toggle Availability.
*   **Analytics**: Revenue charts, Order stats.

### **APP 3: ADMIN (`/apps/admin`)**
*   **Dashboard**: Platform Revenue, User Growth, Active Bids.
*   **Approvals**: Vendor Verification Queue (View Docs -> Approve/Reject).
*   **Config**: Edit `PlatformConfig` (Commission %, Bidding Timer).
*   **Promos**: Create/Manage Promo Codes.
*   **Audit**: View Audit Logs table.

### **APP 4: SUPPORT (`/apps/support`)**
*   **Ticket Queue**: Sorted by SLA (Urgent first).
*   **Workspace**:
    *   Chat with User.
    *   Internal Notes.
    *   Linked Order/Vendor Details panel.
    *   Action: Resolve/Escalate.

---

## 5️⃣ Real-Time & Scheduler Handling

The backend has 4 schedulers. The frontend must handle these state changes via WebSocket events or auto-refresh:

1.  **Bid Expiry**: If a bid expires, auto-refresh the "Active Bids" list.
2.  **SLA Breach**: If a ticket breaches SLA, highlight it in Red in the Support App.
3.  **Order Completion**: Auto-move delivered orders to "Past Orders" after the scheduled job runs.
4.  **Promo Expiry**: Show error if a user tries to apply an expired code.

---

## 6️⃣ Implementation Strategy

1.  **Scaffold**: Run `npx create-turbo@latest`.
2.  **Config**: Set up `tailwind.config.js` and `tsconfig.json` in shared packages.
3.  **Core**: Implement `packages/api` (Axios + Interceptors) and `packages/types`.
4.  **UI**: Generate Shadcn components in `packages/ui`.
5.  **Apps**:
    *   Build **Customer App** first (Auth -> Bid Request -> Order).
    *   Build **Vendor App** second (Bid Submission -> Order Fulfillment).
    *   Build **Admin/Support** last.
