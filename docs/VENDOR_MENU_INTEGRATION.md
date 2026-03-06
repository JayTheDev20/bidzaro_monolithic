# 🔗 VENDOR MENU INTEGRATION GUIDE

**Date**: March 6, 2026
**Purpose**: Complete vendor menu management within the Bidzaro ecosystem

---

## 📚 Document Map

### For Developers
1. **VENDOR_MENU_MANAGEMENT.md** ⭐ (Start here)
   - Complete API documentation
   - All endpoints with full payloads
   - Field validation rules
   - Error handling

2. **VENDOR_MENU_QUICK_REFERENCE.md**
   - Quick endpoint summary
   - Common examples
   - Error troubleshooting
   - Workflow overview

3. **VENDOR_API_DOCS.md**
   - Complete vendor API reference
   - All other vendor operations (bids, orders, profile, etc.)
   - Authentication details
   - Global response formats

### For Frontend Developers
- Use VENDOR_MENU_QUICK_REFERENCE.md for implementation
- Reference VENDOR_MENU_MANAGEMENT.md for detailed field info
- Check example payloads for real-world usage

### For QA/Testing
- VENDOR_MENU_MANAGEMENT.md has complete test cases
- VENDOR_MENU_QUICK_REFERENCE.md has error scenarios
- Test all 7 endpoints with provided examples

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                  VENDOR DASHBOARD                        │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ┌──────────────────────────────────────────────────┐  │
│  │         VENDOR MENU MANAGEMENT                   │  │
│  │  (This Document's Focus)                         │  │
│  ├──────────────────────────────────────────────────┤  │
│  │ • Browse Master Items                            │  │
│  │ • Manage Vendor Menu                             │  │
│  │ • Set Pricing & Discounts                        │  │
│  │ • Control Availability                           │  │
│  └──────────────────────────────────────────────────┘  │
│                         ↓                                 │
│  ┌──────────────────────────────────────────────────┐  │
│  │         BID MARKETPLACE                          │  │
│  ├──────────────────────────────────────────────────┤  │
│  │ • Receive Bids from Customers                    │  │
│  │ • Submit Proposals                               │  │
│  │ • Negotiate Pricing                              │  │
│  │ • Accept/Reject Bids                             │  │
│  └──────────────────────────────────────────────────┘  │
│                         ↓                                 │
│  ┌──────────────────────────────────────────────────┐  │
│  │         ORDER MANAGEMENT                         │  │
│  ├──────────────────────────────────────────────────┤  │
│  │ • Receive Orders from Won Bids                   │  │
│  │ • Track Order Status                             │  │
│  │ • Update Delivery Status                         │  │
│  │ • Manage Catering Details                        │  │
│  └──────────────────────────────────────────────────┘  │
│                         ↓                                 │
│  ┌──────────────────────────────────────────────────┐  │
│  │         ANALYTICS & REVIEWS                      │  │
│  ├──────────────────────────────────────────────────┤  │
│  │ • View Performance Metrics                       │  │
│  │ • See Customer Reviews & Ratings                 │  │
│  │ • Track Revenue                                  │  │
│  │ • Analyze Popular Items                          │  │
│  └──────────────────────────────────────────────────┘  │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

---

## 🔄 Data Flow: Menu → Bids → Orders

### 1. Menu Setup Phase
```
Vendor creates/updates menu items
    ↓
Customizes names, descriptions, pricing
    ↓
Manages availability
    ↓
Menu ready for bids
```

**Related Endpoints**:
- `POST /vendor-items` - Add item
- `PUT /vendor-items/{id}` - Update item
- `PATCH /vendor-items/{id}/availability` - Toggle availability

---

### 2. Bid Reception Phase
```
Customer browses and filters vendors' menus
    ↓
Customer creates a bid request with items
    ↓
Vendor receives bid with their menu items
    ↓
Vendor can see item prices they set
```

**Data Connection**:
- Vendor menu items are referenced in customer's bid requests
- Pricing from menu is used as baseline for negotiations

---

### 3. Order Fulfillment Phase
```
Customer accepts vendor's bid → Order Created
    ↓
Order includes items from vendor's menu
    ↓
Vendor prepares order
    ↓
Delivery & completion
```

**Data Connection**:
- Order items linked to vendor menu items
- Pricing locked at order creation time
- Quantity from bid copied to order

---

## 🔌 API Integration Points

### With Authentication
```
GET /auth/login → Get JWT Token
    ↓
Include token in all menu endpoints:
  Authorization: Bearer <token>
```

### With Vendor Profile
```
Vendor has vendorId
    ↓
Menu items linked to vendorId
    ↓
Only vendor can manage their items
```

### With Bid System
```
Master Items (created by admin)
    ↓
Vendor Menu Items (vendor customization)
    ↓
Bid Requests (customer creates bids)
    ↓
Bids link to vendor menu items
```

### With Order System
```
Vendor Menu Items with pricing
    ↓
Bids proposed with vendor menu items
    ↓
Orders created from accepted bids
    ↓
Order items reference vendor menu
```

---

## 🎯 Complete Vendor Journey

### Day 1: Setup
```
1. Vendor registers account
   POST /auth/register

2. Creates vendor profile
   POST /vendors/profile

3. Explores available items
   GET /menu/items?page=0&size=20

4. Adds 10 items to menu with custom pricing
   POST /vendor-items (× 10 times)

5. Views complete menu
   GET /vendor-items?page=0&size=20
```

### Day 2-7: Operations
```
1. Receives bids from customers
   (Bids contain their menu items)

2. Submits proposals with pricing
   PUT /vendors/bids/{bidId}

3. Wins bids based on pricing & menu

4. Receives orders
   GET /vendors/orders

5. Updates order status
   PATCH /vendors/orders/{orderId}/status
```

### Day 8+: Optimization
```
1. Views which items are popular
   GET /vendors/analytics/menu

2. Updates pricing on best sellers
   PUT /vendor-items/{id}

3. Adds new seasonal items
   POST /vendor-items

4. Temporarily unavailable items
   PATCH /vendor-items/{id}/availability?isAvailable=false

5. Analyzes reviews & ratings
   GET /vendors/analytics/reviews
```

---

## 🔐 Security & Authorization

### Authentication Flow
```
1. User logs in
   POST /auth/login

2. Returns JWT token with:
   - userId
   - userType: "VENDOR"
   - exp: expiry time

3. Include in all requests:
   Authorization: Bearer <token>

4. Server validates token on each request
```

### Authorization for Menu Operations
```
Only VENDOR users can:
  ✅ POST /vendor-items
  ✅ PUT /vendor-items/{id}
  ✅ PATCH /vendor-items/{id}/availability
  ✅ DELETE /vendor-items/{id}

Non-vendors get:
  ❌ 403 FORBIDDEN
  Message: "Access denied"

Vendors can only modify:
  ✅ Their own items (vendorId match check)
  ❌ Other vendor's items → 403 error
```

---

## 📊 Entity Relationships

### Data Model
```
Master Menu Item (created by Admin)
├─ Item Name
├─ Description
├─ Category
├─ Base Specifications
└─ Image

    ↓ (Referenced by)

Vendor Menu Item (created by Vendor)
├─ Reference to Master Item
├─ Vendor ID
├─ Custom Name (optional)
├─ Custom Description (optional)
├─ Custom Pricing
│  ├─ Base Price
│  ├─ Discount %
│  └─ Calculated Discount Price
├─ Availability Status
├─ Minimum Order Quantity
└─ Status (ACTIVE/INACTIVE)

    ↓ (Referenced by)

Bid Item (in Bid Request)
├─ Reference to Vendor Menu Item
├─ Quantity
├─ Unit Price
└─ Total Price

    ↓ (Becomes)

Order Item (in Order)
├─ Reference to Vendor Menu Item
├─ Quantity
├─ Price (locked at order time)
└─ Status
```

---

## 🛠️ Implementation Checklist

### Backend Implementation
- [x] MenuService with vendor item operations
- [x] MenuController with endpoints
- [x] VendorMenuItemRequest/Response DTOs
- [x] Authorization checks (vendor can only modify own items)
- [x] Validation (price > 0, discount 0-100, etc.)
- [x] Error handling with specific error codes
- [x] Pagination support
- [x] Soft delete (status to INACTIVE)

### Frontend Implementation
- [ ] Menu browse page (list master items)
- [ ] Add to menu dialog (POST /vendor-items)
- [ ] Menu management list (GET /vendor-items)
- [ ] Edit item modal (PUT /vendor-items/{id})
- [ ] Availability toggle (PATCH availability)
- [ ] Delete confirmation (DELETE /vendor-items/{id})
- [ ] Price calculator (show discounted price)
- [ ] Loading states and error messages

### Testing Checklist
- [ ] All 7 endpoints with valid requests
- [ ] Required field validation
- [ ] Field length validation
- [ ] Price/discount range validation
- [ ] Duplicate item handling
- [ ] Unauthorized access (401/403)
- [ ] Not found handling (404)
- [ ] Pagination with various page sizes

---

## 📖 Related Documents

| Document | Purpose | Link |
|----------|---------|------|
| **VENDOR_MENU_MANAGEMENT.md** | Complete API docs | `/docs/VENDOR_MENU_MANAGEMENT.md` |
| **VENDOR_MENU_QUICK_REFERENCE.md** | Quick reference | `/docs/VENDOR_MENU_QUICK_REFERENCE.md` |
| **VENDOR_API_DOCS.md** | All vendor endpoints | `/docs/VENDOR_API_DOCS.md` |
| **API_ROUTING_GUIDE.md** | All platform endpoints | `/docs/API_ROUTING_GUIDE.md` |

---

## 🎓 Example: Complete Menu Item Lifecycle

### Add Item
```bash
curl -X POST http://localhost:8080/api/v1/menu/vendor-items \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "masterItemId": "biryani-123",
    "customName": "House Special Biryani",
    "pricePerPlate": 450.00,
    "minimumOrderQuantity": 10,
    "discountPercentage": 10
  }'

Response (201 Created):
{
  "vendorItemId": "vi-456",
  "status": "ACTIVE"
}
```

### Update Pricing
```bash
curl -X PUT http://localhost:8080/api/v1/menu/vendor-items/vi-456 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "pricePerPlate": 500.00,
    "discountPercentage": 15
  }'

Response (200 OK):
{
  "vendorItemId": "vi-456",
  "pricing": {
    "pricePerPlate": 500.00,
    "discountedPrice": 425.00
  }
}
```

### Make Unavailable
```bash
curl -X PATCH \
  'http://localhost:8080/api/v1/menu/vendor-items/vi-456/availability?isAvailable=false&reason=Closed%20for%20event' \
  -H "Authorization: Bearer <token>"

Response (200 OK):
{
  "availability": {
    "isAvailable": false,
    "unavailableReason": "Closed for event"
  }
}
```

### Available Again
```bash
curl -X PATCH \
  'http://localhost:8080/api/v1/menu/vendor-items/vi-456/availability?isAvailable=true' \
  -H "Authorization: Bearer <token>"

Response (200 OK):
{
  "availability": {
    "isAvailable": true
  }
}
```

### Delete Item
```bash
curl -X DELETE \
  http://localhost:8080/api/v1/menu/vendor-items/vi-456 \
  -H "Authorization: Bearer <token>"

Response (200 OK):
{
  "success": true,
  "message": "Item deleted"
}
```

---

## 🚀 Getting Started

### For Developers
1. Read: VENDOR_MENU_MANAGEMENT.md (complete guide)
2. Understand: Data model and API flow
3. Implement: Frontend components
4. Test: All endpoints with examples

### For Testers
1. Read: VENDOR_MENU_QUICK_REFERENCE.md
2. Prepare: Test data and scenarios
3. Execute: All endpoint tests
4. Verify: Error handling and edge cases

### For DevOps
1. Ensure: MongoDB running with proper indexes
2. Configure: API base URL
3. Monitor: Menu endpoints in APM
4. Backup: Database regularly

---

**Status**: ✅ Production Ready
**Last Updated**: March 6, 2026
**Version**: 1.0


