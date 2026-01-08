# Catering Marketplace Platform

A production-ready Spring Boot REST API application for a Catering Marketplace with bidding functionality. Similar to Swiggy/Zomato but for catering services with a reverse auction system.

## Features

- **JWT-based Authentication** with refresh tokens
- **User Management** (Customers, Vendors, Admins, Support Agents)
- **Vendor Management** with approval workflow
- **Menu Management** (Master items and vendor-specific items)
- **Bidding System** with competitive periods and cooling periods
- **Order Management** with multi-vendor support
- **Payment Integration** with Razorpay
- **Real-time Chat** with WebSocket
- **Notifications** via Email, SMS, Push, and WhatsApp
- **Review & Rating System**
- **Support Ticketing System**
- **Analytics Dashboard**

## Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Database**: MongoDB
- **Cache**: Redis
- **Authentication**: JWT (jjwt 0.12.3)
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Payment Gateway**: Razorpay
- **Email**: SendGrid
- **SMS**: Twilio
- **Push Notifications**: Firebase
- **File Storage**: Google Cloud Storage

## Prerequisites

- Java 17+
- Maven 3.8+
- MongoDB 6.0+
- Redis 7.0+
- Docker (optional)

## Quick Start

### 1. Clone the repository

```bash
git clone <repository-url>
cd bidzaro_monolithic
```

### 2. Set up environment variables

```bash
# Copy the example file
cp .env.example .env

# Edit .env and add your credentials
nano .env  # or use your preferred editor
```

**📖 See [ENVIRONMENT_SETUP.md](ENVIRONMENT_SETUP.md) for detailed configuration guide**

Required variables:
- MongoDB connection URI
- Redis connection details
- JWT secret key
- API keys for Razorpay, SendGrid, Twilio, Firebase, GCP

### 3. Install dependencies

```bash
./mvnw clean install
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

# GCP Storage
GCP_PROJECT_ID=your-gcp-project-id
GCP_STORAGE_BUCKET=your-storage-bucket
GCP_CREDENTIALS_PATH=gcp-credentials.json

# Frontend URL
FRONTEND_URL=http://localhost:3000
```

### 3. Run with Maven

```bash
./mvnw spring-boot:run
```

### 4. Run with Docker

```bash
# Build the image
docker build -t catering-platform .

# Run the container
docker run -p 8080:8080 \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/catering_platform_db \
  -e REDIS_HOST=host.docker.internal \
  -e JWT_SECRET=your-secret-key \
  catering-platform
```

## API Documentation

Once the application is running, access the Swagger UI at:
- http://localhost:8080/api/v1/swagger-ui.html

API docs JSON:
- http://localhost:8080/api/v1/api-docs

## Project Structure

```
src/main/java/com/cateringmarketplace/
├── CateringPlatformApplication.java
├── config/                     # Configuration classes
├── scheduler/                  # Scheduled tasks
├── module/
│   ├── admin/                  # Admin dashboard & platform config
│   ├── analytics/              # Analytics & reporting
│   ├── auth/                   # Authentication & User management
│   ├── bid/                    # Bidding system
│   ├── cart/                   # Shopping cart
│   ├── chat/                   # Real-time chat (WebSocket)
│   ├── menu/                   # Menu & Categories
│   ├── notification/           # Multi-channel notifications
│   ├── order/                  # Order management
│   ├── payment/                # Payment processing (Razorpay)
│   ├── review/                 # Reviews & Ratings
│   ├── support/                # Support tickets
│   ├── upload/                 # File upload management
│   ├── user/                   # User profiles & addresses
│   ├── vendor/                 # Vendor management
│   └── wishlist/               # User wishlists
└── common/                     # Shared utilities & exceptions
```

## Complete API Endpoints (136+ endpoints)

### Authentication (11 endpoints)
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/refresh-token` - Refresh access token
- `POST /api/v1/auth/logout` - User logout
- `POST /api/v1/auth/logout-all` - Logout from all devices
- `POST /api/v1/auth/send-otp` - Send OTP
- `POST /api/v1/auth/verify-otp` - Verify OTP
- `POST /api/v1/auth/forgot-password` - Request password reset
- `POST /api/v1/auth/reset-password` - Reset password
- `POST /api/v1/auth/change-password` - Change password
- `GET /api/v1/auth/me` - Get current user

### Users (8 endpoints)
- `GET /api/v1/users/profile` - Get user profile
- `PUT /api/v1/users/profile` - Update profile
- `PATCH /api/v1/users/profile-picture` - Update profile picture
- `GET /api/v1/users/addresses` - Get addresses
- `POST /api/v1/users/addresses` - Add address
- `PUT /api/v1/users/addresses/{id}` - Update address
- `DELETE /api/v1/users/addresses/{id}` - Delete address
- `PATCH /api/v1/users/addresses/{id}/default` - Set default address

### Vendors (13 endpoints)
- `POST /api/v1/vendors` - Register as vendor
- `GET /api/v1/vendors` - List vendors
- `GET /api/v1/vendors/{id}` - Get vendor details
- `PUT /api/v1/vendors/{id}` - Update vendor
- `GET /api/v1/vendors/search` - Search vendors
- `GET /api/v1/vendors/nearby` - Get nearby vendors
- `POST /api/v1/vendors/{id}/documents` - Upload documents
- And more...

### Menu (15 endpoints)
- `GET /api/v1/menu/categories` - Get categories
- `GET /api/v1/menu/items` - Get menu items
- `GET /api/v1/menu/items/search` - Search items
- `GET /api/v1/menu/vendor-items` - Get vendor menu items
- `POST /api/v1/menu/vendor-items` - Add vendor menu item
- And more...

### Cart (6 endpoints)
- `GET /api/v1/cart` - Get cart
- `POST /api/v1/cart/items` - Add to cart
- `PUT /api/v1/cart/items/{id}` - Update cart item
- `DELETE /api/v1/cart/items/{id}` - Remove from cart
- `DELETE /api/v1/cart` - Clear cart
- `GET /api/v1/cart/count` - Get cart count

### Wishlist (5 endpoints)
- `GET /api/v1/wishlist` - Get wishlist
- `POST /api/v1/wishlist/items` - Add to wishlist
- `DELETE /api/v1/wishlist/items/{id}` - Remove from wishlist
- `DELETE /api/v1/wishlist` - Clear wishlist
- `GET /api/v1/wishlist/check/{itemId}` - Check if in wishlist

### Bids (12 endpoints)
- `POST /api/v1/bids/requests` - Create bid request
- `GET /api/v1/bids/requests` - Get bid requests
- `GET /api/v1/bids/requests/{id}` - Get bid request details
- `POST /api/v1/bids/requests/{id}/submit-bid` - Submit bid (vendor)
- `PUT /api/v1/bids/{bidId}` - Revise bid
- `POST /api/v1/bids/{bidId}/accept` - Accept bid
- And more...

### Orders (10 endpoints)
- `POST /api/v1/orders` - Create order
- `GET /api/v1/orders` - Get orders
- `GET /api/v1/orders/{id}` - Get order details
- `PATCH /api/v1/orders/{id}/status` - Update status
- `POST /api/v1/orders/{id}/cancel` - Cancel order
- And more...

### Payments (7 endpoints)
- `POST /api/v1/payments/initiate` - Initiate payment
- `POST /api/v1/payments/verify` - Verify payment
- `GET /api/v1/payments/transactions` - Get transactions
- `POST /api/v1/payments/refund` - Initiate refund
- `POST /api/v1/payments/webhook/razorpay` - Razorpay webhook

### Chat (6 endpoints)
- `GET /api/v1/chat/conversations` - Get conversations
- `POST /api/v1/chat/conversations` - Create conversation
- `GET /api/v1/chat/conversations/{id}/messages` - Get messages
- `POST /api/v1/chat/messages` - Send message
- `PATCH /api/v1/chat/conversations/{id}/read` - Mark as read

### Reviews (8 endpoints)
- `POST /api/v1/reviews` - Submit review
- `GET /api/v1/reviews` - Get reviews
- `PUT /api/v1/reviews/{id}` - Update review
- `POST /api/v1/reviews/{id}/vendor-response` - Vendor response
- `POST /api/v1/reviews/{id}/helpful` - Mark helpful

### Notifications (5 endpoints)
- `GET /api/v1/notifications` - Get notifications
- `GET /api/v1/notifications/unread` - Get unread
- `PATCH /api/v1/notifications/{id}/read` - Mark as read
- `PATCH /api/v1/notifications/read-all` - Mark all read
- `DELETE /api/v1/notifications/{id}` - Delete

### Support (8 endpoints)
- `POST /api/v1/support/tickets` - Create ticket
- `GET /api/v1/support/tickets` - Get tickets
- `GET /api/v1/support/tickets/{id}` - Get ticket details
- `POST /api/v1/support/admin/tickets/{id}/assign` - Assign ticket
- `POST /api/v1/support/admin/tickets/{id}/resolve` - Resolve ticket

### Admin (12 endpoints)
- `GET /api/v1/admin/dashboard` - Dashboard stats
- `GET /api/v1/admin/users` - Get all users
- `PATCH /api/v1/admin/users/{id}/status` - Update user status
- `GET /api/v1/admin/vendors/pending` - Pending approvals
- `POST /api/v1/admin/vendors/{id}/approve` - Approve vendor
- `POST /api/v1/admin/vendors/{id}/reject` - Reject vendor
- `GET /api/v1/admin/platform-config` - Get config
- `PUT /api/v1/admin/platform-config` - Update config
- `GET /api/v1/admin/audit-logs` - Get audit logs
- `POST /api/v1/admin/announcements` - Create announcement

### Analytics (7 endpoints)
- `GET /api/v1/analytics/overview` - Platform overview (admin)
- `GET /api/v1/analytics/revenue` - Revenue analytics (admin)
- `GET /api/v1/analytics/users` - User analytics (admin)
- `GET /api/v1/analytics/vendors` - Vendor analytics (admin)
- `GET /api/v1/analytics/orders` - Order analytics (admin)
- `GET /api/v1/analytics/vendor/dashboard` - Vendor dashboard
- `GET /api/v1/analytics/reports` - Generate reports (admin)

### File Upload (3 endpoints)
- `POST /api/v1/upload/image` - Upload image
- `POST /api/v1/upload/document` - Upload document
- `DELETE /api/v1/upload/{fileId}` - Delete file

## Bidding Flow

1. User creates a **Bid Request** with event details and requirements
2. Bid request enters **Active** status for 72 hours (competitive period)
3. Vendors submit bids (quotes) for the request
4. User can view and compare bids
5. When user **accepts** a bid, it enters **Cooling Period** (24 hours)
6. During cooling period, user can cancel without penalty
7. After cooling period, an **Order** is created
8. User pays **25% token** to confirm the order

## License

MIT License

## Support

For support, email support@cateringplatform.com

