# 🚀 WISHLIST & CART APIs — QUICK START GUIDE

## ⚡ TL;DR - Most Common Endpoints

### Wishlist
```
GET  /api/v1/wishlist                      - Get all wishlisted items
POST /api/v1/wishlist/items?masterItemId=X - Add item to wishlist
DELETE /api/v1/wishlist/items/{id}         - Remove from wishlist
GET  /api/v1/wishlist/check/{itemId}      - Check if item in wishlist
```

### Shopping Cart
```
GET  /api/v1/cart                          - Get cart items
POST /api/v1/cart/items?vendorItemId=X    - Add to cart
PUT  /api/v1/cart/items/{id}?quantity=5   - Update quantity
DELETE /api/v1/cart/items/{id}             - Remove from cart
GET  /api/v1/cart/grouped                  - Get items by vendor
GET  /api/v1/cart/total                    - Get total price
```

### Draft Cart
```
GET  /api/v1/cart/draft                    - Get draft items
POST /api/v1/cart/draft/items?masterItemId=X - Add to draft
DELETE /api/v1/cart/draft/items/{id}       - Remove from draft
```

---

## ❌ COMMON MISTAKES & FIXES

### ❌ WRONG - Returns 405 METHOD_NOT_ALLOWED
```
POST /api/v1/wishlist
POST /api/v1/cart
```

### ✅ RIGHT - Returns 201 CREATED
```
POST /api/v1/wishlist/items?masterItemId={id}
POST /api/v1/cart/items?vendorItemId={id}
```

---

## 📝 Quick Examples

### 1. Add Item to Wishlist
```bash
POST /api/v1/wishlist/items?masterItemId=abc-123
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Item added to wishlist",
  "data": { "wishlistItemId": "...", ... }
}
```

---

### 2. Add Item to Cart
```bash
POST /api/v1/cart/items?vendorItemId=xyz-789&quantity=2
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Item added to cart",
  "data": { "cartItemId": "...", "quantity": 2, "subtotal": 700 }
}
```

---

### 3. Get Cart Grouped by Vendor (Most Useful!)
```bash
GET /api/v1/cart/grouped
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "vendor-1-id": [
      { "cartItemId": "...", "vendorName": "Royal Catering", ... }
    ],
    "vendor-2-id": [
      { "cartItemId": "...", "vendorName": "Grand Events", ... }
    ]
  }
}
```

---

### 4. Update Cart Item Quantity
```bash
PUT /api/v1/cart/items/cart-item-uuid?quantity=5
Authorization: Bearer <token>
```

---

### 5. Remove from Cart
```bash
DELETE /api/v1/cart/items/cart-item-uuid
Authorization: Bearer <token>
```

---

### 6. Check if Item in Wishlist
```bash
GET /api/v1/wishlist/check/master-item-uuid
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "data": { "inWishlist": true }
}
```

---

## 🔑 Key Differences

| Feature | Wishlist | Cart | Draft Cart |
|---------|----------|------|-----------|
| **Purpose** | Save items for later | Purchase items | Browse before vendor selection |
| **Item Type** | Master items | Vendor items | Master items |
| **Quantity** | 1 per item | Configurable | Configurable |
| **Vendor** | Multiple vendors | One vendor per item | N/A (before vendor selection) |
| **Use Case** | Favorites/bookmarks | Ready to checkout | Planning/browsing |

---

## ✅ Complete Endpoint List

### WISHLIST (6 endpoints)
- ✅ `GET /wishlist` - List all
- ✅ `POST /wishlist/items` - Add item
- ✅ `DELETE /wishlist/items/{id}` - Remove item
- ✅ `DELETE /wishlist` - Clear all
- ✅ `GET /wishlist/check/{itemId}` - Check exists
- ✅ `GET /wishlist/count` - Count items

### CART (9 endpoints)
- ✅ `GET /cart` - List all items
- ✅ `POST /cart/items` - Add item
- ✅ `POST /cart/items/batch` - Add multiple items
- ✅ `PUT /cart/items/{id}` - Update quantity
- ✅ `DELETE /cart/items/{id}` - Remove item
- ✅ `DELETE /cart` - Clear all
- ✅ `GET /cart/grouped` - Group by vendor
- ✅ `GET /cart/count` - Count items
- ✅ `GET /cart/total` - Total price

### DRAFT CART (5 endpoints)
- ✅ `GET /cart/draft` - List all
- ✅ `POST /cart/draft/items` - Add item
- ✅ `POST /cart/draft/items/batch` - Add multiple
- ✅ `DELETE /cart/draft/items/{id}` - Remove item
- ✅ `DELETE /cart/draft` - Clear all

**Total: 20 working API endpoints!**

---

## 🧪 Testing with Postman/cURL

### Set Variables (Postman)
```
{{base_url}} = http://localhost:8080/api/v1
{{token}} = <your-jwt-token>
{{masterItemId}} = <menu-item-uuid>
{{vendorItemId}} = <vendor-item-uuid>
{{cartItemId}} = <cart-item-uuid>
```

### Test Flow
1. **Check Wishlist Count**: `GET {{base_url}}/wishlist/count`
2. **Add to Wishlist**: `POST {{base_url}}/wishlist/items?masterItemId={{masterItemId}}`
3. **Get Cart**: `GET {{base_url}}/cart`
4. **Add to Cart**: `POST {{base_url}}/cart/items?vendorItemId={{vendorItemId}}&quantity=2`
5. **Update Quantity**: `PUT {{base_url}}/cart/items/{{cartItemId}}?quantity=5`
6. **Get Cart Total**: `GET {{base_url}}/cart/total`
7. **Get Grouped Cart**: `GET {{base_url}}/cart/grouped`

---

## 🐛 Troubleshooting

### Problem: 405 METHOD_NOT_ALLOWED
**Cause**: Using wrong endpoint path
**Fix**: Use `/wishlist/items` and `/cart/items` (with `/items` suffix)

### Problem: 401 UNAUTHORIZED
**Cause**: Missing or invalid JWT token
**Fix**: Add header: `Authorization: Bearer <valid-token>`

### Problem: 400 ITEM_ALREADY_IN_WISHLIST
**Cause**: Item already wishlisted
**Fix**: Check `/wishlist/check/{id}` first, or delete and re-add

### Problem: 404 NOT_FOUND
**Cause**: Invalid item ID
**Fix**: Verify item exists in menu/vendor inventory

---

## 📚 Full Documentation
See [WISHLIST_CART_API_COMPLETE.md](./WISHLIST_CART_API_COMPLETE.md) for detailed specs, request/response formats, and advanced examples.


