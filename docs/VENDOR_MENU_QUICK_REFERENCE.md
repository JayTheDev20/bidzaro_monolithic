# 📋 VENDOR MENU MANAGEMENT - Quick Reference Guide

**Created**: March 6, 2026
**Location**: `docs/VENDOR_MENU_MANAGEMENT.md`

---

## 🚀 Quick Start for Vendors

### Endpoint Summary

| Operation | HTTP | Endpoint | Description |
|-----------|------|----------|-------------|
| **Browse Items** | GET | `/menu/items?page=0&size=20` | See all available master items |
| **View Menu** | GET | `/vendor-items?page=0&size=20` | List your custom menu |
| **Get Item Details** | GET | `/vendor-items/{id}` | View one item from your menu |
| **Add Item** | POST | `/vendor-items` | Add item to your menu |
| **Update Item** | PUT | `/vendor-items/{id}` | Update pricing & details |
| **Toggle Availability** | PATCH | `/vendor-items/{id}/availability` | Make available/unavailable |
| **Delete Item** | DELETE | `/vendor-items/{id}` | Remove from your menu |

---

## 📝 Key Request/Response Fields

### Add/Update Item Request
```json
{
  "masterItemId": "required-string",
  "customName": "optional-string",
  "customDescription": "optional-string",
  "pricePerPlate": "required-number",
  "minimumOrderQuantity": "optional-integer",
  "discountPercentage": "optional-number-0-100"
}
```

### Success Response
```json
{
  "success": true,
  "status": 200/201,
  "message": "Description",
  "data": { /* item object */ }
}
```

### Error Response
```json
{
  "success": false,
  "status": 400/401/403/404/409,
  "message": "Error description",
  "error": {
    "code": "ERROR_CODE",
    "message": "Detailed message"
  }
}
```

---

## ✅ Field Requirements Summary

### Add New Item (POST)
| Field | Required | Type | Validation |
|-------|----------|------|-----------|
| `masterItemId` | ✅ Yes | string | Must exist in master menu |
| `pricePerPlate` | ✅ Yes | decimal | > 0, max 2 decimals |
| `customName` | ⬜ No | string | Max 255 chars, defaults to master name |
| `customDescription` | ⬜ No | string | Max 1000 chars |
| `minimumOrderQuantity` | ⬜ No | integer | >= 1, defaults to 1 |
| `discountPercentage` | ⬜ No | decimal | 0-100, max 2 decimals |

### Update Item (PUT)
- **All fields are optional**
- Only send what you want to change
- Discount auto-calculated if price or discount % changes

### Toggle Availability (PATCH)
| Parameter | Required | Type | Values |
|-----------|----------|------|--------|
| `isAvailable` | ✅ Yes | boolean | true or false |
| `reason` | ⬜ No | string | Why unavailable (max 500 chars) |

---

## 🔐 Authentication

**All endpoints require**:
```
Authorization: Bearer <your-jwt-token>
Content-Type: application/json
```

**Token must be for**:
- User Type: `VENDOR`
- Valid JWT token from login

---

## 📊 Real-World Examples

### Example 1: Adding Biryani to Menu

**Request**:
```bash
POST /menu/vendor-items
Authorization: Bearer eyJhbGciOi...

{
  "masterItemId": "biryani-master-123",
  "customName": "Special House Biryani",
  "customDescription": "Prepared with aged meat and exotic spices",
  "pricePerPlate": 450.00,
  "minimumOrderQuantity": 10,
  "discountPercentage": 10.5
}
```

**Calculation**:
- Base Price: ₹450
- Discount (10.5%): ₹47.25
- Final Price: ₹402.75 per plate
- Minimum Order: 10 plates = ₹4,027.50

**Response**:
```json
{
  "success": true,
  "status": 201,
  "message": "Item added to menu",
  "data": {
    "vendorItemId": "vi-xyz789",
    "customName": "Special House Biryani",
    "pricing": {
      "pricePerPlate": 450.00,
      "discountedPrice": 402.75,
      "minimumOrderQuantity": 10
    },
    "status": "ACTIVE"
  }
}
```

---

### Example 2: Updating Price During Promotion

**Request**:
```bash
PUT /menu/vendor-items/vi-xyz789
Authorization: Bearer eyJhbGciOi...

{
  "pricePerPlate": 400.00,
  "discountPercentage": 15.0
}
```

**Calculation**:
- New Base Price: ₹400
- New Discount (15%): ₹60
- New Final Price: ₹340 per plate

**Response**:
```json
{
  "success": true,
  "message": "Item updated",
  "data": {
    "vendorItemId": "vi-xyz789",
    "pricing": {
      "pricePerPlate": 400.00,
      "discountPercentage": 15.0,
      "discountedPrice": 340.00
    }
  }
}
```

---

### Example 3: Making Item Temporarily Unavailable

**Request**:
```bash
PATCH /menu/vendor-items/vi-xyz789/availability?isAvailable=false&reason=Preparing%20for%20large%20event
Authorization: Bearer eyJhbGciOi...
```

**Response**:
```json
{
  "success": true,
  "message": "Availability updated",
  "data": {
    "vendorItemId": "vi-xyz789",
    "availability": {
      "isAvailable": false,
      "unavailableReason": "Preparing for large event",
      "lastUpdated": "2026-03-06T16:30:00Z"
    },
    "status": "INACTIVE"
  }
}
```

---

### Example 4: Making Item Available Again

**Request**:
```bash
PATCH /menu/vendor-items/vi-xyz789/availability?isAvailable=true
Authorization: Bearer eyJhbGciOi...
```

**Response**:
```json
{
  "success": true,
  "message": "Availability updated",
  "data": {
    "availability": {
      "isAvailable": true,
      "lastUpdated": "2026-03-06T18:00:00Z"
    },
    "status": "ACTIVE"
  }
}
```

---

## ❌ Common Errors & Solutions

| Error | Cause | Solution |
|-------|-------|----------|
| **401 UNAUTHORIZED** | Missing/invalid token | Include valid Authorization header |
| **403 FORBIDDEN** | Not a VENDOR or wrong item | Verify user role and item ownership |
| **404 NOT_FOUND** | Item doesn't exist | Check masterItemId or vendorItemId |
| **409 CONFLICT** | Item already in menu | Item exists, use PUT to update instead |
| **400 BAD_REQUEST** | Invalid data | Check field types and validation rules |

---

## 🎯 Complete Menu Workflow

```
Step 1: Login & Get Token
  → POST /auth/login
  → Get: accessToken

Step 2: Browse Available Items
  → GET /menu/items?page=0&size=20
  → Find items you want to add

Step 3: Add Items to Menu
  → POST /menu/vendor-items
  → Set custom name, price, discount

Step 4: View Your Menu
  → GET /menu/vendor-items?page=0&size=20
  → See all items you've added

Step 5: Update Items as Needed
  → PUT /menu/vendor-items/{id}
  → Change price, name, description

Step 6: Manage Availability
  → PATCH /menu/vendor-items/{id}/availability
  → Temporarily unavailable/available

Step 7: Remove Items (Optional)
  → DELETE /menu/vendor-items/{id}
  → Remove from your menu
```

---

## 💡 Pro Tips

### Pricing Strategy
- Set prices slightly above cost for profitability
- Use discounts for promotions/off-peak hours
- Adjust minimum quantities based on cooking efficiency
- Monitor competitor pricing monthly

### Availability Management
- Update real-time as stock changes
- Provide clear reasons when unavailable
- Customers see unavailability reason in app
- Re-enable quickly when stock available

### Menu Optimization
- Keep top-selling items always available
- Highlight signature dishes with custom names
- Update descriptions with preparation style
- Add seasonal items when in season

### Customer Communication
- Clear descriptions help customers order
- Specific minimum quantities set expectations
- Transparency about unavailability builds trust
- Competitive pricing attracts more bids

---

## 📞 Support

For issues or questions:
1. Check error codes in response
2. Review validation rules for your input
3. Verify token is valid and user is VENDOR
4. Check item IDs are correct

---

**Version**: 1.0
**Last Updated**: March 6, 2026
**Maintained By**: Development Team


