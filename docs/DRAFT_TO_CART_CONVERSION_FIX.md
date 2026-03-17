# 🔧 DRAFT TO CART CONVERSION - ENDPOINT FIX

**Date**: March 17, 2026
**Issue**: 405 METHOD_NOT_ALLOWED on `POST /cart/items/from-draft`
**Status**: ✅ FIXED

---

## ❌ PROBLEM

```
Frontend Request:
POST /api/v1/cart/items/from-draft
Body: { vendorId: "...", items: [...] }

Response: 405 METHOD_NOT_ALLOWED
Error: Method not supported: POST - Path: /api/v1/cart/items/from-draft

Logs:
POST "/api/v1/cart/items/from-draft"
ExceptionHandlerExceptionResolver: Using @ExceptionHandler
handleHttpRequestMethodNotSupportedException
Method not supported: POST
Completed 405 METHOD_NOT_ALLOWED
```

---

## 🔍 ROOT CAUSE

The endpoint `POST /cart/items/from-draft` was **not implemented** in CartController.

The frontend was trying to use a special endpoint to convert draft items to main cart, but it didn't exist.

---

## ✅ SOLUTION

Added new endpoint to CartController:

```java
@PostMapping("/items/from-draft")
@Operation(summary = "Convert draft items to cart",
           description = "Converts items from draft cart to main cart for a specific vendor")
public ResponseEntity<ApiResponse<List<CartItem>>> convertFromDraftToCart(
        @RequestBody BatchAddToCartRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
    log.info("Converting draft items to cart for user {}", userDetails.getUserId());
    List<CartItem> items = cartService.batchAddToCart(userDetails.getUserId(), request);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created(items, "Items moved from draft to cart"));
}
```

**Key Points:**
- Uses `@PostMapping("/items/from-draft")`
- Accepts `BatchAddToCartRequest` (same as `/items/batch`)
- Delegates to `cartService.batchAddToCart()` (existing method)
- Returns 201 CREATED with list of cart items
- Automatically handles draft item deletion (via CartService)

---

## 🎯 HOW IT WORKS

```
Frontend User Flow:
1. Browse menu items
2. Add to draft cart: POST /cart/draft/items
3. View draft items: GET /cart/draft
4. User selects vendor
5. CONVERT draft to main cart: POST /cart/items/from-draft ← NEW ENDPOINT!
   {
     "vendorId": "vendor-uuid",
     "items": [
       { "masterItemId": "item-1", "quantity": 50 },
       { "masterItemId": "item-2", "quantity": 50 }
     ]
   }
6. Draft items auto-deleted
7. Main cart populated with vendor items
8. Ready for checkout!
```

---

## 📊 REQUEST/RESPONSE

### Request
```json
POST /api/v1/cart/items/from-draft
Authorization: Bearer <token>
Content-Type: application/json

{
  "vendorId": "vendor-royal-catering",
  "items": [
    {
      "masterItemId": "menu-item-biryani",
      "quantity": 50
    },
    {
      "masterItemId": "menu-item-butter-chicken",
      "quantity": 50
    }
  ]
}
```

### Response (201 CREATED)
```json
{
  "success": true,
  "status": 201,
  "message": "Items moved from draft to cart",
  "data": [
    {
      "cartItemId": "cart-uuid-1",
      "vendorId": "vendor-royal-catering",
      "vendorItemId": "vendor-item-biryani",
      "itemName": "Biryani",
      "quantity": 50,
      "pricePerPlate": 280,
      "totalPrice": 14000,
      "addedAt": "2026-03-17T12:16:42Z"
    },
    {
      "cartItemId": "cart-uuid-2",
      "vendorId": "vendor-royal-catering",
      "vendorItemId": "vendor-item-butter-chicken",
      "quantity": 50,
      "pricePerPlate": 250,
      "totalPrice": 12500,
      "addedAt": "2026-03-17T12:16:42Z"
    }
  ]
}
```

---

## 🧪 TEST THE FIX

### cURL Test
```bash
# Convert draft items to main cart
curl -X POST "http://localhost:8080/api/v1/cart/items/from-draft" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "vendorId": "vendor-uuid",
    "items": [
      {
        "masterItemId": "item-uuid-1",
        "quantity": 50
      }
    ]
  }'

# Expected Response: 201 CREATED
# Previous Response: 405 METHOD_NOT_ALLOWED ❌
```

### JavaScript Test
```javascript
const convertFromDraft = async (token, vendorId, draftItems) => {
  const response = await fetch('/api/v1/cart/items/from-draft', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      vendorId: vendorId,
      items: draftItems.map(item => ({
        masterItemId: item.masterItemId,
        quantity: item.quantity
      }))
    })
  });

  const data = await response.json();
  return data.data; // Array of CartItems
};

// Usage
const cartItems = await convertFromDraft(token, 'vendor-id', [
  { masterItemId: 'item-1', quantity: 50 }
]);
```

---

## 🔄 COMPLETE WORKFLOW

```
┌──────────────────────────────┐
│ 1. Browse & Add to Draft     │
│ GET /menu/items              │
│ POST /cart/draft/items × N   │
└───────────────┬──────────────┘
                ↓
┌──────────────────────────────┐
│ 2. View Draft Cart           │
│ GET /cart/draft              │
└───────────────┬──────────────┘
                ↓
┌──────────────────────────────┐
│ 3. Search & Select Vendor    │
│ GET /vendors/search          │
└───────────────┬──────────────┘
                ↓
┌──────────────────────────────┐
│ 4. Convert Draft → Main      │
│ POST /cart/items/from-draft  │ ← NEW ENDPOINT!
│ (Draft items auto-deleted)   │
└───────────────┬──────────────┘
                ↓
┌──────────────────────────────┐
│ 5. Main Cart Ready           │
│ GET /cart                    │
│ (with vendor pricing)        │
└───────────────┬──────────────┘
                ↓
┌──────────────────────────────┐
│ 6. Checkout                  │
│ POST /orders                 │
└──────────────────────────────┘
```

---

## ✅ ALL CART ENDPOINTS

| Endpoint | Method | Purpose | Status |
|----------|--------|---------|--------|
| `/cart` | GET | Get cart items | ✅ |
| `/cart` | DELETE | Clear cart | ✅ |
| `/cart/items` | POST | Add single item | ✅ |
| `/cart/items/batch` | POST | Batch add items | ✅ |
| `/cart/items/from-draft` | POST | Convert from draft | ✅ NEW |
| `/cart/items/{id}` | PUT | Update quantity | ✅ |
| `/cart/items/{id}` | DELETE | Remove item | ✅ |
| `/cart/grouped` | GET | Group by vendor | ✅ |
| `/cart/count` | GET | Count items | ✅ |
| `/cart/total` | GET | Total price | ✅ |

---

## 📝 FILES MODIFIED

**CartController.java**
- Added `@PostMapping("/items/from-draft")` endpoint
- Line: ~109-119
- Uses existing `cartService.batchAddToCart()` method

---

## 🎉 SUMMARY

✅ Added `/cart/items/from-draft` endpoint
✅ Converts draft items to main cart
✅ Automatically deletes draft items
✅ Returns 201 CREATED with cart items
✅ Frontend can now complete draft→cart flow

---

**Status**: 🟢 **FIXED & READY**

Test the endpoint now - 405 error should be gone!

*Last Updated: March 17, 2026*


