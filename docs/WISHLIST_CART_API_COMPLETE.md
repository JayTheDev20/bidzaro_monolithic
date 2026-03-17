# 🛒 Wishlist & Cart APIs — Complete Reference
**Bidzaro Catering Platform** | Shopping Cart & Wishlist Management

---

## 📋 Table of Contents
1. [Wishlist APIs](#-wishlist-apis)
2. [Shopping Cart APIs](#-shopping-cart-apis)
3. [Draft Cart APIs](#-draft-cart-apis)
4. [Common Response Format](#-common-response-format)
5. [Error Codes](#-error-codes)
6. [Integration Examples](#-integration-examples)

---

## ❤️ Wishlist APIs

### Base URL: `http://localhost:8080/api/v1/wishlist`
> All endpoints require: `Authorization: Bearer <token>`

---

### 1. Get Wishlist (Paginated)
`GET /wishlist?page=0&size=20`

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 20 | Items per page |

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Wishlist retrieved",
  "data": [
    {
      "wishlistItemId": "uuid-1",
      "userId": "user-uuid",
      "masterItemId": "menu-item-uuid-1",
      "itemName": "Biryani",
      "itemDescription": "Hyderabadi Biryani",
      "vendorName": "Royal Catering",
      "cuisine": "Indian",
      "price": 350,
      "currency": "INR",
      "imageUrl": "https://cdn.example.com/biryani.jpg",
      "addedAt": "2026-03-17T10:00:00Z"
    },
    {
      "wishlistItemId": "uuid-2",
      "userId": "user-uuid",
      "masterItemId": "menu-item-uuid-2",
      "itemName": "Paneer Tikka",
      "itemDescription": "Tandoori Paneer",
      "vendorName": "Royal Catering",
      "cuisine": "Indian",
      "price": 280,
      "currency": "INR",
      "imageUrl": "https://cdn.example.com/paneer.jpg",
      "addedAt": "2026-03-17T09:30:00Z"
    }
  ],
  "pageInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1,
    "isFirst": true,
    "isLast": true,
    "hasNext": false,
    "hasPrevious": false
  },
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 2. Add to Wishlist
`POST /wishlist/items?masterItemId={id}`

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `masterItemId` | string | ✅ | Menu master item ID to add |

**Response `201`:**
```json
{
  "success": true,
  "status": 201,
  "message": "Item added to wishlist",
  "data": {
    "wishlistItemId": "uuid-new",
    "userId": "user-uuid",
    "masterItemId": "menu-item-uuid",
    "itemName": "Biryani",
    "itemDescription": "Hyderabadi Biryani",
    "vendorName": "Royal Catering",
    "cuisine": "Indian",
    "price": 350,
    "currency": "INR",
    "imageUrl": "https://cdn.example.com/biryani.jpg",
    "addedAt": "2026-03-17T10:30:00Z"
  },
  "timestamp": "2026-03-17T10:30:00Z"
}
```

**Error `400` (Item Already in Wishlist):**
```json
{
  "success": false,
  "status": 400,
  "message": "Item already exists in wishlist",
  "error": {
    "code": "ITEM_ALREADY_IN_WISHLIST",
    "message": "This item is already in your wishlist"
  }
}
```

---

### 3. Remove from Wishlist
`DELETE /wishlist/items/{wishlistItemId}`

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `wishlistItemId` | string | ✅ | Wishlist item ID to remove |

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Item removed from wishlist",
  "data": null,
  "timestamp": "2026-03-17T10:30:00Z"
}
```

**Error `404` (Item Not Found):**
```json
{
  "success": false,
  "status": 404,
  "message": "Wishlist item not found",
  "error": {
    "code": "WISHLIST_ITEM_NOT_FOUND",
    "message": "The wishlist item does not exist or has already been removed"
  }
}
```

---

### 4. Clear Entire Wishlist
`DELETE /wishlist`

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Wishlist cleared",
  "data": null,
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 5. Check if Item is in Wishlist
`GET /wishlist/check/{masterItemId}`

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `masterItemId` | string | ✅ | Menu master item ID to check |

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "inWishlist": true
  },
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 6. Get Wishlist Count
`GET /wishlist/count`

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "count": 5
  },
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

## 🛒 Shopping Cart APIs

### Base URL: `http://localhost:8080/api/v1/cart`
> All endpoints require: `Authorization: Bearer <token>`

---

### 1. Get Cart
`GET /cart`

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": [
    {
      "cartItemId": "cart-uuid-1",
      "userId": "user-uuid",
      "vendorItemId": "vendor-item-uuid-1",
      "vendorId": "vendor-uuid",
      "vendorName": "Royal Catering Co.",
      "itemName": "Biryani",
      "itemDescription": "Hyderabadi Biryani",
      "price": 350,
      "currency": "INR",
      "quantity": 2,
      "subtotal": 700,
      "imageUrl": "https://cdn.example.com/biryani.jpg",
      "addedAt": "2026-03-17T09:00:00Z"
    },
    {
      "cartItemId": "cart-uuid-2",
      "userId": "user-uuid",
      "vendorItemId": "vendor-item-uuid-2",
      "vendorId": "vendor-uuid-2",
      "vendorName": "Grand Events Catering",
      "itemName": "Paneer Tikka",
      "itemDescription": "Tandoori Paneer",
      "price": 280,
      "currency": "INR",
      "quantity": 1,
      "subtotal": 280,
      "imageUrl": "https://cdn.example.com/paneer.jpg",
      "addedAt": "2026-03-17T10:00:00Z"
    }
  ],
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 2. Add to Cart
`POST /cart/items?vendorItemId={id}&quantity={qty}`

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `vendorItemId` | string | ✅ | - | Vendor's item ID |
| `quantity` | int | ⬜ | 1 | Quantity to add |

**Response `201`:**
```json
{
  "success": true,
  "status": 201,
  "message": "Item added to cart",
  "data": {
    "cartItemId": "cart-uuid",
    "userId": "user-uuid",
    "vendorItemId": "vendor-item-uuid",
    "vendorId": "vendor-uuid",
    "vendorName": "Royal Catering Co.",
    "itemName": "Biryani",
    "itemDescription": "Hyderabadi Biryani",
    "price": 350,
    "currency": "INR",
    "quantity": 1,
    "subtotal": 350,
    "imageUrl": "https://cdn.example.com/biryani.jpg",
    "addedAt": "2026-03-17T10:30:00Z"
  },
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 3. Batch Add to Cart
`POST /cart/items/batch`

**Request Body:**
```json
{
  "vendorId": "vendor-uuid",
  "items": [
    {
      "vendorItemId": "item-uuid-1",
      "quantity": 2
    },
    {
      "vendorItemId": "item-uuid-2",
      "quantity": 1
    }
  ]
}
```

**Response `201`:**
```json
{
  "success": true,
  "status": 201,
  "message": "Items added to cart",
  "data": [
    {
      "cartItemId": "cart-uuid-1",
      "userId": "user-uuid",
      "vendorItemId": "item-uuid-1",
      "vendorId": "vendor-uuid",
      "vendorName": "Royal Catering Co.",
      "itemName": "Biryani",
      "price": 350,
      "quantity": 2,
      "subtotal": 700,
      "addedAt": "2026-03-17T10:30:00Z"
    },
    {
      "cartItemId": "cart-uuid-2",
      "userId": "user-uuid",
      "vendorItemId": "item-uuid-2",
      "vendorId": "vendor-uuid",
      "vendorName": "Royal Catering Co.",
      "itemName": "Paneer Tikka",
      "price": 280,
      "quantity": 1,
      "subtotal": 280,
      "addedAt": "2026-03-17T10:30:00Z"
    }
  ],
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 4. Update Cart Item Quantity
`PUT /cart/items/{cartItemId}?quantity={qty}`

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `cartItemId` | string | ✅ | Cart item ID to update |

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `quantity` | int | ✅ | New quantity (must be > 0) |

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Cart item updated",
  "data": {
    "cartItemId": "cart-uuid",
    "userId": "user-uuid",
    "vendorItemId": "vendor-item-uuid",
    "vendorId": "vendor-uuid",
    "vendorName": "Royal Catering Co.",
    "itemName": "Biryani",
    "price": 350,
    "quantity": 3,
    "subtotal": 1050,
    "addedAt": "2026-03-17T10:30:00Z"
  },
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 5. Remove from Cart
`DELETE /cart/items/{cartItemId}`

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `cartItemId` | string | ✅ | Cart item ID to remove |

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Item removed from cart",
  "data": null,
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 6. Clear Cart
`DELETE /cart`

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Cart cleared",
  "data": null,
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 7. Get Cart Grouped by Vendor
`GET /cart/grouped`

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "vendor-uuid-1": [
      {
        "cartItemId": "cart-uuid-1",
        "userId": "user-uuid",
        "vendorItemId": "vendor-item-uuid-1",
        "vendorId": "vendor-uuid-1",
        "vendorName": "Royal Catering Co.",
        "itemName": "Biryani",
        "price": 350,
        "quantity": 2,
        "subtotal": 700,
        "addedAt": "2026-03-17T09:00:00Z"
      }
    ],
    "vendor-uuid-2": [
      {
        "cartItemId": "cart-uuid-2",
        "userId": "user-uuid",
        "vendorItemId": "vendor-item-uuid-2",
        "vendorId": "vendor-uuid-2",
        "vendorName": "Grand Events Catering",
        "itemName": "Paneer Tikka",
        "price": 280,
        "quantity": 1,
        "subtotal": 280,
        "addedAt": "2026-03-17T10:00:00Z"
      }
    ]
  },
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 8. Get Cart Count
`GET /cart/count`

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "count": 3
  },
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 9. Get Cart Total
`GET /cart/total`

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "total": 980.50
  },
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

## 📦 Draft Cart APIs

### Base URL: `http://localhost:8080/api/v1/cart/draft`
> All endpoints require: `Authorization: Bearer <token>`
>
> **Note**: Draft cart is used for adding items BEFORE selecting a vendor. Items are from master menu.

---

### 1. Get Draft Cart
`GET /cart/draft`

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": [
    {
      "draftCartItemId": "draft-uuid-1",
      "userId": "user-uuid",
      "masterItemId": "master-item-uuid-1",
      "itemName": "Biryani",
      "itemDescription": "Hyderabadi Biryani",
      "cuisine": "Indian",
      "price": 350,
      "currency": "INR",
      "quantity": 2,
      "subtotal": 700,
      "imageUrl": "https://cdn.example.com/biryani.jpg",
      "addedAt": "2026-03-17T10:00:00Z"
    }
  ],
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 2. Add to Draft Cart
`POST /cart/draft/items?masterItemId={id}&quantity={qty}`

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `masterItemId` | string | ✅ | - | Master menu item ID |
| `quantity` | int | ⬜ | 1 | Quantity to add |

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Item added to draft cart",
  "data": {
    "draftCartItemId": "draft-uuid",
    "userId": "user-uuid",
    "masterItemId": "master-item-uuid",
    "itemName": "Biryani",
    "itemDescription": "Hyderabadi Biryani",
    "cuisine": "Indian",
    "price": 350,
    "currency": "INR",
    "quantity": 1,
    "subtotal": 350,
    "imageUrl": "https://cdn.example.com/biryani.jpg",
    "addedAt": "2026-03-17T10:30:00Z"
  },
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 3. Batch Add to Draft Cart
`POST /cart/draft/items/batch`

**Request Body:**
```json
{
  "items": [
    {
      "masterItemId": "master-item-uuid-1",
      "quantity": 2
    },
    {
      "masterItemId": "master-item-uuid-2",
      "quantity": 1
    }
  ]
}
```

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Items added to draft cart",
  "data": [
    {
      "draftCartItemId": "draft-uuid-1",
      "userId": "user-uuid",
      "masterItemId": "master-item-uuid-1",
      "itemName": "Biryani",
      "quantity": 2,
      "subtotal": 700,
      "addedAt": "2026-03-17T10:30:00Z"
    },
    {
      "draftCartItemId": "draft-uuid-2",
      "userId": "user-uuid",
      "masterItemId": "master-item-uuid-2",
      "itemName": "Paneer Tikka",
      "quantity": 1,
      "subtotal": 280,
      "addedAt": "2026-03-17T10:30:00Z"
    }
  ],
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 4. Remove from Draft Cart
`DELETE /cart/draft/items/{masterItemId}`

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `masterItemId` | string | ✅ | Master item ID to remove |

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Item removed from draft cart",
  "data": null,
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

### 5. Clear Draft Cart
`DELETE /cart/draft`

**Response `200`:**
```json
{
  "success": true,
  "status": 200,
  "message": "Draft cart cleared",
  "data": null,
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

## 📋 Common Response Format

All endpoints return responses in this format:

```json
{
  "success": true/false,
  "status": 200,
  "message": "Human readable message",
  "data": {},
  "pageInfo": { },  // Only for paginated endpoints
  "error": {        // Only when success=false
    "code": "ERROR_CODE",
    "message": "Error details"
  },
  "timestamp": "2026-03-17T10:30:00Z"
}
```

---

## ❌ Error Codes

| Status | Code | Meaning |
|--------|------|---------|
| 400 | `VALIDATION_ERROR` | Invalid input parameters |
| 400 | `ITEM_ALREADY_IN_WISHLIST` | Item already in wishlist |
| 400 | `INVALID_QUANTITY` | Quantity must be > 0 |
| 401 | `UNAUTHORIZED` | JWT token missing/invalid/expired |
| 403 | `FORBIDDEN` | User doesn't have permission |
| 404 | `WISHLIST_ITEM_NOT_FOUND` | Wishlist item not found |
| 404 | `CART_ITEM_NOT_FOUND` | Cart item not found |
| 404 | `MENU_ITEM_NOT_FOUND` | Menu item not found |
| 404 | `VENDOR_ITEM_NOT_FOUND` | Vendor item not found |
| 500 | `INTERNAL_SERVER_ERROR` | Server error |

---

## 💻 Integration Examples

### JavaScript / React
```javascript
import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/v1';
const token = localStorage.getItem('accessToken');

const headers = { Authorization: `Bearer ${token}` };

// Add to Wishlist
async function addToWishlist(masterItemId) {
  const response = await axios.post(
    `${API_BASE}/wishlist/items?masterItemId=${masterItemId}`,
    null,
    { headers }
  );
  return response.data;
}

// Add to Cart
async function addToCart(vendorItemId, quantity = 1) {
  const response = await axios.post(
    `${API_BASE}/cart/items?vendorItemId=${vendorItemId}&quantity=${quantity}`,
    null,
    { headers }
  );
  return response.data;
}

// Get Cart
async function getCart() {
  const response = await axios.get(`${API_BASE}/cart`, { headers });
  return response.data;
}

// Update Cart Item
async function updateCartItem(cartItemId, quantity) {
  const response = await axios.put(
    `${API_BASE}/cart/items/${cartItemId}?quantity=${quantity}`,
    null,
    { headers }
  );
  return response.data;
}

// Remove from Cart
async function removeFromCart(cartItemId) {
  const response = await axios.delete(
    `${API_BASE}/cart/items/${cartItemId}`,
    { headers }
  );
  return response.data;
}

// Get Cart Grouped by Vendor
async function getCartGroupedByVendor() {
  const response = await axios.get(`${API_BASE}/cart/grouped`, { headers });
  return response.data;
}
```

### cURL Examples
```bash
# Add to Wishlist
curl -X POST "http://localhost:8080/api/v1/wishlist/items?masterItemId=uuid" \
  -H "Authorization: Bearer <token>"

# Add to Cart
curl -X POST "http://localhost:8080/api/v1/cart/items?vendorItemId=uuid&quantity=2" \
  -H "Authorization: Bearer <token>"

# Get Cart
curl -X GET "http://localhost:8080/api/v1/cart" \
  -H "Authorization: Bearer <token>"

# Update Cart Item
curl -X PUT "http://localhost:8080/api/v1/cart/items/{cartItemId}?quantity=5" \
  -H "Authorization: Bearer <token>"

# Get Cart Total
curl -X GET "http://localhost:8080/api/v1/cart/total" \
  -H "Authorization: Bearer <token>"

# Get Cart Grouped by Vendor
curl -X GET "http://localhost:8080/api/v1/cart/grouped" \
  -H "Authorization: Bearer <token>"
```

### Postman Collection
Import the following into Postman:

```json
{
  "info": {
    "name": "Wishlist & Cart APIs",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Get Wishlist",
      "request": {
        "method": "GET",
        "url": "{{base_url}}/wishlist?page=0&size=20",
        "header": [{"key": "Authorization", "value": "Bearer {{token}}"}]
      }
    },
    {
      "name": "Add to Wishlist",
      "request": {
        "method": "POST",
        "url": "{{base_url}}/wishlist/items?masterItemId={{masterItemId}}",
        "header": [{"key": "Authorization", "value": "Bearer {{token}}"}]
      }
    },
    {
      "name": "Get Cart",
      "request": {
        "method": "GET",
        "url": "{{base_url}}/cart",
        "header": [{"key": "Authorization", "value": "Bearer {{token}}"}]
      }
    },
    {
      "name": "Add to Cart",
      "request": {
        "method": "POST",
        "url": "{{base_url}}/cart/items?vendorItemId={{vendorItemId}}&quantity=1",
        "header": [{"key": "Authorization", "value": "Bearer {{token}}"}]
      }
    },
    {
      "name": "Get Cart Grouped by Vendor",
      "request": {
        "method": "GET",
        "url": "{{base_url}}/cart/grouped",
        "header": [{"key": "Authorization", "value": "Bearer {{token}}"}]
      }
    },
    {
      "name": "Get Draft Cart",
      "request": {
        "method": "GET",
        "url": "{{base_url}}/cart/draft",
        "header": [{"key": "Authorization", "value": "Bearer {{token}}"}]
      }
    },
    {
      "name": "Add to Draft Cart",
      "request": {
        "method": "POST",
        "url": "{{base_url}}/cart/draft/items?masterItemId={{masterItemId}}&quantity=1",
        "header": [{"key": "Authorization", "value": "Bearer {{token}}"}]
      }
    }
  ]
}
```

---

## 🔑 Key Points

✅ **Always use** `/wishlist/items` for POST (not just `/wishlist`)
✅ **Always use** `/cart/items` for POST (not just `/cart`)
✅ **Use Draft Cart** for items before vendor selection
✅ **Use Shopping Cart** for vendor-specific items
✅ **Check** `/cart/grouped` to see items organized by vendor
✅ **Always include** `Authorization` header with Bearer token
✅ **Batch operations** are available for efficiency

---

## 🐛 Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| 405 METHOD_NOT_ALLOWED | Using `/wishlist` instead of `/wishlist/items` | Use correct endpoint with `/items` suffix |
| 405 METHOD_NOT_ALLOWED | Using `/cart` instead of `/cart/items` | Use correct endpoint with `/items` suffix |
| 401 UNAUTHORIZED | Missing JWT token | Add `Authorization: Bearer <token>` header |
| 400 BAD_REQUEST | Invalid masterItemId or vendorItemId | Verify IDs are correct UUIDs |
| 400 ITEM_ALREADY_IN_WISHLIST | Item already in wishlist | Check `/wishlist/check/{id}` first |
| 404 NOT_FOUND | Item doesn't exist | Verify ID is valid |

---

## 📚 Related Documentation
- [Chat Integration Guide](./CHAT_INTEGRATION_GUIDE.md)
- [Menu API Docs](./VENDOR_MENU_API.md)
- [Order API Docs](./API_COMPLETE_REFERENCE.md)
- [Payment API Docs](./API_COMPLETE_REFERENCE.md)


