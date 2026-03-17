# 📦 DRAFT CART API - COMPLETE GUIDE & FLOW TO MAIN CART

**Date**: March 17, 2026
**Status**: ✅ FULLY IMPLEMENTED
**Endpoints**: 5 endpoints

---

## 🎯 WHAT IS DRAFT CART?

**Draft Cart** is a temporary shopping cart where users can:
- ✅ Browse and add items from the **master menu** (not vendor-specific)
- ✅ Plan their catering needs before selecting a vendor
- ✅ Modify quantities
- ✅ Remove items
- ✅ Later convert to main cart after vendor selection

**Use Case**: "I want biryani, butter chicken, and naan for 50 people - but from which vendor?"

---

## 📊 DRAFT CART VS MAIN CART

| Feature | Draft Cart | Main Cart |
|---------|-----------|----------|
| **Items From** | Master menu (generic) | Vendor-specific items |
| **Purpose** | Browse & plan | Ready to checkout |
| **Vendor** | N/A (browsing) | Vendor selected |
| **When Used** | Before vendor selection | After vendor selection |
| **TTL (Expiry)** | 30 days | 30 days |
| **Max Items** | Unlimited | Unlimited |
| **Quantity** | Variable | Variable |

---

## ✅ ALL 5 DRAFT CART ENDPOINTS

### 1. Get Draft Cart
```
GET /api/v1/cart/draft
Authorization: Bearer <token>
```

**Response 200:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": "draft-item-uuid-1",
      "userId": "user-uuid",
      "masterItemId": "menu-item-uuid-1",
      "itemName": "Biryani",
      "quantity": 50,
      "addedAt": "2026-03-17T10:00:00Z",
      "updatedAt": "2026-03-17T10:05:00Z",
      "expiresAt": "2026-04-16T10:00:00Z"
    },
    {
      "id": "draft-item-uuid-2",
      "userId": "user-uuid",
      "masterItemId": "menu-item-uuid-2",
      "itemName": "Butter Chicken",
      "quantity": 50,
      "addedAt": "2026-03-17T10:01:00Z",
      "updatedAt": "2026-03-17T10:01:00Z",
      "expiresAt": "2026-04-16T10:01:00Z"
    }
  ],
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 2. Add to Draft Cart
```
POST /api/v1/cart/draft/items?masterItemId={id}&quantity={qty}
Authorization: Bearer <token>
```

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `masterItemId` | string | ✅ | - | Master menu item UUID |
| `quantity` | int | ⬜ | 1 | Quantity (plate count) |

**Response 200:**
```json
{
  "success": true,
  "message": "Item added to draft cart",
  "data": {
    "id": "draft-item-uuid",
    "userId": "user-uuid",
    "masterItemId": "menu-item-uuid",
    "itemName": "Biryani",
    "quantity": 50,
    "addedAt": "2026-03-17T10:00:00Z",
    "updatedAt": "2026-03-17T10:00:00Z",
    "expiresAt": "2026-04-16T10:00:00Z"
  },
  "timestamp": "2026-03-17T10:00:00Z"
}
```

**Notes:**
- If item already in draft cart → quantity is **added** (cumulative)
- Example: Add 30, then add 20 → Total becomes 50
- Draft items expire after **30 days** of inactivity

---

### 3. Batch Add to Draft Cart
```
POST /api/v1/cart/draft/items/batch
Authorization: Bearer <token>
Content-Type: application/json

{
  "items": [
    {
      "masterItemId": "menu-item-uuid-1",
      "quantity": 50
    },
    {
      "masterItemId": "menu-item-uuid-2",
      "quantity": 50
    },
    {
      "masterItemId": "menu-item-uuid-3",
      "quantity": 30
    }
  ]
}
```

**Request Body:**
```json
{
  "items": [
    {
      "masterItemId": "string (required)",
      "quantity": "int (required)"
    }
  ]
}
```

**Response 200:**
```json
{
  "success": true,
  "message": "Items added to draft cart",
  "data": [
    {
      "id": "draft-item-uuid-1",
      "masterItemId": "menu-item-uuid-1",
      "itemName": "Biryani",
      "quantity": 50,
      "addedAt": "2026-03-17T10:00:00Z"
    },
    {
      "id": "draft-item-uuid-2",
      "masterItemId": "menu-item-uuid-2",
      "itemName": "Butter Chicken",
      "quantity": 50,
      "addedAt": "2026-03-17T10:00:00Z"
    },
    {
      "id": "draft-item-uuid-3",
      "masterItemId": "menu-item-uuid-3",
      "itemName": "Naan",
      "quantity": 30,
      "addedAt": "2026-03-17T10:00:00Z"
    }
  ],
  "timestamp": "2026-03-17T10:00:00Z"
}
```

**Benefits:**
- ✅ Add multiple items in one request
- ✅ Faster than individual POSTs
- ✅ Useful for pre-defined menus/templates

---

### 4. Remove from Draft Cart
```
DELETE /api/v1/cart/draft/items/{masterItemId}
Authorization: Bearer <token>
```

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `masterItemId` | string | ✅ | Master menu item UUID to remove |

**Response 200:**
```json
{
  "success": true,
  "message": "Item removed from draft cart",
  "data": null,
  "timestamp": "2026-03-17T10:30:00Z"
}
```

**Error 404:**
```json
{
  "success": false,
  "status": 404,
  "message": "Item not found in draft cart",
  "error": {
    "code": "DRAFT_ITEM_NOT_FOUND",
    "message": "This item is not in your draft cart"
  }
}
```

---

### 5. Clear Draft Cart
```
DELETE /api/v1/cart/draft
Authorization: Bearer <token>
```

**Response 200:**
```json
{
  "success": true,
  "message": "Draft cart cleared",
  "data": null,
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

## 🔄 FLOW: DRAFT CART → MAIN CART

### Complete Checkout Flow

```
┌─────────────────────────────────────────────────────────────────┐
│ STEP 1: USER BROWSES & ADDS ITEMS                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│ GET /api/v1/menu/items → See all available items              │
│                ↓                                                │
│ POST /api/v1/cart/draft/items?masterItemId=X&qty=50           │
│                ↓                                                │
│ GET /api/v1/cart/draft → View draft cart                      │
│                ↓                                                │
│ ✅ DRAFT CART POPULATED WITH MASTER ITEMS                     │
│                                                                │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STEP 2: FIND VENDORS MATCHING DRAFT ITEMS                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│ GET /api/v1/vendors/search?location=X&cuisine=Y              │
│                ↓                                                │
│ Display vendors with matching items                           │
│                ↓                                                │
│ User selects a vendor → Vendor ID captured                    │
│                                                                │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STEP 3: CONVERT DRAFT ITEMS TO MAIN CART (KEY STEP!)          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│ Frontend retrieves draft cart items:                          │
│ GET /api/v1/cart/draft                                        │
│                ↓                                                │
│ For each draft item:                                          │
│   - Find matching vendor item (vendor-specific version)       │
│   - Get vendor item UUID                                      │
│                ↓                                                │
│ POST /api/v1/cart/items/batch                                │
│ {                                                             │
│   "vendorId": "vendor-uuid",                                 │
│   "items": [                                                 │
│     { "vendorItemId": "vendor-item-uuid-1", "qty": 50 },    │
│     { "vendorItemId": "vendor-item-uuid-2", "qty": 50 },    │
│     { "vendorItemId": "vendor-item-uuid-3", "qty": 30 }     │
│   ]                                                           │
│ }                                                             │
│                ↓                                                │
│ ✅ ITEMS ADDED TO MAIN CART                                   │
│ ⚠️ DRAFT ITEMS AUTO-DELETED                                   │
│                                                                │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STEP 4: REVIEW MAIN CART & CHECKOUT                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│ GET /api/v1/cart → Review items with vendor prices           │
│                ↓                                                │
│ GET /api/v1/cart/grouped → See items organized by vendor     │
│                ↓                                                │
│ GET /api/v1/cart/total → See total price                     │
│                ↓                                                │
│ PUT /api/v1/cart/items/{id}?quantity=60 → Update qty         │
│                ↓                                                │
│ DELETE /api/v1/cart/items/{id} → Remove items               │
│                ↓                                                │
│ Ready to checkout! 🎉                                         │
│                                                                │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔑 KEY POINTS IN THE FLOW

### 1. Draft Items = Master Menu Items
```
Draft Cart Item:
{
  "masterItemId": "menu-item-uuid",  ← Generic menu item
  "itemName": "Biryani",
  "quantity": 50,
  "vendorId": null  ← NO VENDOR YET
}
```

### 2. Main Cart Items = Vendor-Specific Items
```
Main Cart Item:
{
  "vendorItemId": "vendor-item-uuid",  ← Vendor's version of biryani
  "vendorId": "vendor-uuid",  ← VENDOR SELECTED
  "itemName": "Biryani",
  "quantity": 50,
  "pricePerPlate": 280  ← VENDOR'S PRICE
}
```

### 3. Conversion Process
When user selects vendor and moves items from draft to main:
1. Get all draft items: `GET /api/v1/cart/draft`
2. For each draft item, find vendor's version
3. Batch add to main cart: `POST /api/v1/cart/items/batch`
4. Draft items are **automatically deleted** from draft cart

---

## 💻 FRONTEND INTEGRATION EXAMPLE

### JavaScript/React Integration

```javascript
// 1. Add items to draft cart
async function addToDraftCart(masterItemId, quantity) {
  const response = await axios.post(
    `/api/v1/cart/draft/items?masterItemId=${masterItemId}&quantity=${quantity}`,
    null,
    { headers: { Authorization: `Bearer ${token}` } }
  );
  return response.data;
}

// 2. Get draft cart
async function getDraftCart() {
  const response = await axios.get(
    '/api/v1/cart/draft',
    { headers: { Authorization: `Bearer ${token}` } }
  );
  return response.data.data; // Array of draft items
}

// 3. User selects vendor - convert draft to main cart
async function checkoutWithVendor(vendorId, draftItems) {
  // Convert draft items to vendor items
  const vendorItems = await Promise.all(
    draftItems.map(async (draftItem) => {
      // Find vendor's version of this item
      const vendorItem = await findVendorItem(vendorId, draftItem.masterItemId);
      return {
        vendorItemId: vendorItem.id,
        quantity: draftItem.quantity
      };
    })
  );

  // Batch add to main cart
  const cartResponse = await axios.post(
    '/api/v1/cart/items/batch',
    {
      vendorId: vendorId,
      items: vendorItems
    },
    { headers: { Authorization: `Bearer ${token}` } }
  );

  return cartResponse.data;
}

// 4. Clear draft cart (if user cancels)
async function cancelDraftCart() {
  await axios.delete(
    '/api/v1/cart/draft',
    { headers: { Authorization: `Bearer ${token}` } }
  );
}
```

---

## 🧪 TESTING DRAFT CART FLOW

### Test Scenario: Full Checkout

```bash
# 1. Get master menu items
curl -X GET "http://localhost:8080/api/v1/menu/items?page=0&size=10" \
  -H "Authorization: Bearer <token>"
# Note: Get masterItemIds from response

# 2. Add 3 items to draft cart
curl -X POST "http://localhost:8080/api/v1/cart/draft/items?masterItemId=item-uuid-1&quantity=50" \
  -H "Authorization: Bearer <token>"

curl -X POST "http://localhost:8080/api/v1/cart/draft/items?masterItemId=item-uuid-2&quantity=50" \
  -H "Authorization: Bearer <token>"

curl -X POST "http://localhost:8080/api/v1/cart/draft/items?masterItemId=item-uuid-3&quantity=30" \
  -H "Authorization: Bearer <token>"

# 3. View draft cart
curl -X GET "http://localhost:8080/api/v1/cart/draft" \
  -H "Authorization: Bearer <token>"

# 4. Search vendors
curl -X GET "http://localhost:8080/api/v1/vendors/search?location=NYC&cuisine=Indian" \
  -H "Authorization: Bearer <token>"
# Note: Get vendorId from response

# 5. Get vendor menu items
curl -X GET "http://localhost:8080/api/v1/vendors/{vendorId}/menu/items" \
  -H "Authorization: Bearer <token>"
# Note: Get vendorItemIds that match your draft items

# 6. Add draft items to main cart (batch operation)
curl -X POST "http://localhost:8080/api/v1/cart/items/batch" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "vendorId": "vendor-uuid",
    "items": [
      {"vendorItemId": "vendor-item-uuid-1", "quantity": 50},
      {"vendorItemId": "vendor-item-uuid-2", "quantity": 50},
      {"vendorItemId": "vendor-item-uuid-3", "quantity": 30}
    ]
  }'

# 7. Check draft cart is empty now
curl -X GET "http://localhost:8080/api/v1/cart/draft" \
  -H "Authorization: Bearer <token>"

# 8. Check main cart has items
curl -X GET "http://localhost:8080/api/v1/cart" \
  -H "Authorization: Bearer <token>"

# 9. Verify total
curl -X GET "http://localhost:8080/api/v1/cart/total" \
  -H "Authorization: Bearer <token>"
```

---

## 🔗 AUTO-DELETION ON CONVERSION

**Important**: When you do `POST /api/v1/cart/items/batch`:

```java
// In CartService.batchAddToCart()
for (BatchAddToCartRequest.ItemRequest itemRequest : request.getItems()) {
  // ... add to cart ...

  // 🔑 AUTO-DELETE from draft cart
  draftCartRepository.deleteByUserIdAndMasterItemId(userId, itemRequest.getMasterItemId());
}
```

**Result:**
- ✅ Draft items converted to main cart items
- ✅ Draft items automatically removed
- ✅ Draft cart becomes empty

---

## 📋 DRAFT CART LIFECYCLE

```
┌──────────────┐
│  USER ADDS   │
│  ITEMS TO    │
│  DRAFT CART  │
└──────┬───────┘
       │
       ↓
┌──────────────────────────────────────┐
│ ITEM IN DRAFT CART                   │
│ - TTL: 30 days                       │
│ - Status: PENDING_VENDOR_SELECTION   │
│ - Can modify quantity                │
│ - Can remove item                    │
└──────┬──────────────────────┬────────┘
       │                      │
       │ (User selects        │ (30 days
       │  vendor)             │  inactivity)
       │                      │
       ↓                      ↓
┌──────────────┐      ┌──────────────┐
│ CONVERT TO   │      │  AUTO-DELETE │
│ MAIN CART    │      │  (EXPIRED)   │
│ ✅           │      │              │
└──────────────┘      └──────────────┘
```

---

## ⚠️ ERROR CASES

### Error 1: Invalid Master Item ID
```
POST /api/v1/cart/draft/items?masterItemId=invalid-id&quantity=50

Response 404:
{
  "success": false,
  "status": 404,
  "message": "Master menu item not found",
  "error": {
    "code": "MENU_ITEM_NOT_FOUND",
    "message": "The requested menu item does not exist"
  }
}
```

### Error 2: Invalid Quantity
```
POST /api/v1/cart/draft/items?masterItemId=item-uuid&quantity=0

Response 400:
{
  "success": false,
  "status": 400,
  "message": "Invalid quantity",
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Quantity must be greater than 0"
  }
}
```

### Error 3: Missing JWT Token
```
GET /api/v1/cart/draft
(No Authorization header)

Response 401:
{
  "success": false,
  "status": 401,
  "message": "Unauthorized",
  "error": {
    "code": "UNAUTHORIZED",
    "message": "JWT token is missing or invalid"
  }
}
```

---

## 🎯 USE CASES

### Use Case 1: Wedding Catering
```
1. User: "I need 100 plates of biryani, 100 butter chicken, 50 naan"
2. Add to draft cart: POST /api/v1/cart/draft/items × 3
3. Browse vendors: GET /api/v1/vendors/search?cuisine=Indian
4. Compare prices from 5 vendors
5. Select best vendor: Batch add to main cart
6. Checkout: POST /api/v1/orders
```

### Use Case 2: Corporate Event
```
1. Event planner: "Standard menu for 250 people"
2. Batch add: POST /api/v1/cart/draft/items/batch (15 items)
3. Admin approves or suggests alternatives
4. Select from approved vendors list
5. Finalize cart with vendor's pricing
```

### Use Case 3: Customized Menu
```
1. User: "Add these 20 items to draft"
2. Batch add: POST /api/v1/cart/draft/items/batch
3. Remove 5 items: DELETE /api/v1/cart/draft/items/{id} × 5
4. Adjust quantities: GET /api/v1/cart/draft → see current state
5. Ready for vendor selection
```

---

## ✅ DRAFT CART FEATURES

| Feature | Status | Description |
|---------|--------|-------------|
| Add items | ✅ | Single or batch add |
| Remove items | ✅ | Remove by master item ID |
| View items | ✅ | Get all draft items |
| Update quantity | ⚠️ | Via remove + add |
| Clear all | ✅ | Delete entire draft cart |
| Auto-delete on conversion | ✅ | When moved to main cart |
| TTL (Expiry) | ✅ | 30 days auto-delete |
| Batch operations | ✅ | Add multiple at once |
| Price calculation | ⬜ | Draft shows generic prices |
| Vendor-specific pricing | ⬜ | Available in main cart |

---

## 🔄 COMPLETE WORKFLOW SUMMARY

```
START: User opens app
   ↓
STEP 1: Browse master menu
   GET /api/v1/menu/items
   ↓
STEP 2: Add items to draft cart
   POST /api/v1/cart/draft/items
   ↓
STEP 3: View draft cart
   GET /api/v1/cart/draft
   ↓
STEP 4: Modify draft (remove/clear if needed)
   DELETE /api/v1/cart/draft/items/{id}
   ↓
STEP 5: Search vendors
   GET /api/v1/vendors/search
   ↓
STEP 6: View vendor menu
   GET /api/v1/vendors/{id}/menu
   ↓
STEP 7: Move draft items to main cart
   POST /api/v1/cart/items/batch
   (Draft items auto-deleted)
   ↓
STEP 8: View main cart
   GET /api/v1/cart
   ↓
STEP 9: Review total
   GET /api/v1/cart/total
   ↓
STEP 10: Checkout
   POST /api/v1/orders
   ↓
END: Order created
```

---

## 📚 RELATED DOCUMENTATION

- [WISHLIST_CART_API_COMPLETE.md](./WISHLIST_CART_API_COMPLETE.md) - All cart APIs
- [WISHLIST_CART_QUICK_REFERENCE.md](./WISHLIST_CART_QUICK_REFERENCE.md) - Quick reference
- [VENDOR_API_DOCS.md](./VENDOR_API_DOCS.md) - Vendor menu items
- [CLIENT_API_DOCS.md](./CLIENT_API_DOCS.md) - Full client APIs

---

**Status**: ✅ COMPLETE & PRODUCTION READY

*Last Updated: March 17, 2026*


