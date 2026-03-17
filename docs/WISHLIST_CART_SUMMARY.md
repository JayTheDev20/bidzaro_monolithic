# 📊 WISHLIST & CART - Complete API Summary

**Status**: ✅ All APIs Working
**Total Endpoints**: 20
**Last Updated**: 2026-03-17

---

## 🎯 Quick Overview

### All Wishlist APIs (6 endpoints)
```
GET     /api/v1/wishlist                      (List with pagination)
POST    /api/v1/wishlist/items                (Add item)
DELETE  /api/v1/wishlist/items/{id}           (Remove item)
DELETE  /api/v1/wishlist                      (Clear all)
GET     /api/v1/wishlist/check/{itemId}       (Check if exists)
GET     /api/v1/wishlist/count                (Get count)
```

### All Cart APIs (9 endpoints)
```
GET     /api/v1/cart                          (List items)
POST    /api/v1/cart/items                    (Add item)
POST    /api/v1/cart/items/batch              (Batch add)
PUT     /api/v1/cart/items/{id}               (Update quantity)
DELETE  /api/v1/cart/items/{id}               (Remove item)
DELETE  /api/v1/cart                          (Clear all)
GET     /api/v1/cart/grouped                  (Group by vendor)
GET     /api/v1/cart/count                    (Get count)
GET     /api/v1/cart/total                    (Get total price)
```

### All Draft Cart APIs (5 endpoints)
```
GET     /api/v1/cart/draft                    (List items)
POST    /api/v1/cart/draft/items              (Add item)
POST    /api/v1/cart/draft/items/batch        (Batch add)
DELETE  /api/v1/cart/draft/items/{id}         (Remove item)
DELETE  /api/v1/cart/draft                    (Clear all)
```

---

## ❌ Issues Fixed

### Issue #1: POST /api/v1/wishlist Returns 405
**Problem**: Client was POSTing to base path without `/items`
**Solution**: Must use `POST /api/v1/wishlist/items?masterItemId={id}`
**Status**: ✅ FIXED

### Issue #2: POST /api/v1/cart Returns 405
**Problem**: Client was POSTing to base path without `/items`
**Solution**: Must use `POST /api/v1/cart/items?vendorItemId={id}&quantity={qty}`
**Status**: ✅ FIXED

---

## 📚 Documentation

### Comprehensive Guides
1. **WISHLIST_CART_API_COMPLETE.md** - Full specification with request/response examples
2. **WISHLIST_CART_QUICK_REFERENCE.md** - Quick lookup guide with examples
3. **WISHLIST_CART_API_FIX.md** - Issue analysis and solutions

---

## 🔑 Key Differences Between Cart Types

| Feature | Wishlist | Shopping Cart | Draft Cart |
|---------|----------|---------------|-----------|
| **Purpose** | Save favorites | Ready to purchase | Pre-selection browsing |
| **Item Type** | Master menu items | Vendor-specific items | Master menu items |
| **Qty Per Item** | 1 (fixed) | Variable | Variable |
| **Vendor** | Multiple allowed | One per item | N/A |
| **Base URL** | `/api/v1/wishlist` | `/api/v1/cart` | `/api/v1/cart/draft` |

---

## 🚀 Common Operations

### Add to Wishlist
```bash
POST /api/v1/wishlist/items?masterItemId=<id>
Authorization: Bearer <token>
# Response: 201 CREATED
```

### Add to Cart
```bash
POST /api/v1/cart/items?vendorItemId=<id>&quantity=2
Authorization: Bearer <token>
# Response: 201 CREATED
```

### Get Cart Grouped by Vendor (Most Useful!)
```bash
GET /api/v1/cart/grouped
Authorization: Bearer <token>
# Response: 200 OK
# Shows items organized by vendor for checkout
```

### Update Cart Quantity
```bash
PUT /api/v1/cart/items/<cartItemId>?quantity=5
Authorization: Bearer <token>
# Response: 200 OK
```

### Check if Item in Wishlist
```bash
GET /api/v1/wishlist/check/<masterItemId>
Authorization: Bearer <token>
# Response: { "inWishlist": true/false }
```

---

## ✅ Verification Checklist

- ✅ Wishlist GET endpoint works (paginated)
- ✅ Wishlist POST endpoint works (add item with `/items` suffix)
- ✅ Wishlist DELETE item endpoint works
- ✅ Wishlist DELETE all endpoint works
- ✅ Wishlist check endpoint works
- ✅ Wishlist count endpoint works
- ✅ Cart GET endpoint works
- ✅ Cart POST endpoint works (add item with `/items` suffix)
- ✅ Cart batch POST endpoint works
- ✅ Cart PUT endpoint works (update quantity)
- ✅ Cart DELETE item endpoint works
- ✅ Cart DELETE all endpoint works
- ✅ Cart grouped endpoint works
- ✅ Cart count endpoint works
- ✅ Cart total endpoint works
- ✅ Draft Cart GET endpoint works
- ✅ Draft Cart POST endpoint works (add item with `/items` suffix)
- ✅ Draft Cart batch POST endpoint works
- ✅ Draft Cart DELETE item endpoint works
- ✅ Draft Cart DELETE all endpoint works

---

## 🔧 Controller Files

### WishlistController.java
- Location: `src/main/java/com/cateringmarketplace/module/wishlist/controller/WishlistController.java`
- Status: ✅ All endpoints implemented and working

### CartController.java
- Location: `src/main/java/com/cateringmarketplace/module/cart/controller/CartController.java`
- Status: ✅ All endpoints implemented and working

### DraftCartController.java
- Location: `src/main/java/com/cateringmarketplace/module/cart/controller/DraftCartController.java`
- Status: ✅ All endpoints implemented and working

---

## 📋 Response Format

All endpoints return:
```json
{
  "success": true/false,
  "status": 200,
  "message": "Operation description",
  "data": {},
  "pageInfo": {},     // Only for paginated endpoints
  "error": {},        // Only on failure
  "timestamp": "ISO-8601"
}
```

---

## 🐛 Troubleshooting

| Problem | Solution |
|---------|----------|
| 405 METHOD_NOT_ALLOWED | Add `/items` to POST path |
| 401 UNAUTHORIZED | Include `Authorization: Bearer <token>` header |
| 400 ITEM_ALREADY_IN_WISHLIST | Item already wishlisted |
| 404 NOT_FOUND | Invalid item ID |
| 400 INVALID_QUANTITY | Quantity must be > 0 |

---

## 🎓 Learning Points

1. **Path Mapping**: `@RequestMapping("/cart")` + `@PostMapping("/items")` = `/cart/items`
2. **Context Path**: Server context `/api/v1` is added automatically
3. **Batch Operations**: Use `/items/batch` endpoint for adding multiple items
4. **Grouping**: Use `/cart/grouped` to organize items by vendor
5. **Draft vs Cart**: Use Draft for master items, Cart for vendor items

---

## 📞 Support

For issues or questions:
1. Check [WISHLIST_CART_API_COMPLETE.md](./WISHLIST_CART_API_COMPLETE.md) for detailed specs
2. Review [WISHLIST_CART_QUICK_REFERENCE.md](./WISHLIST_CART_QUICK_REFERENCE.md) for quick lookup
3. See [WISHLIST_CART_API_FIX.md](./WISHLIST_CART_API_FIX.md) for issue analysis


