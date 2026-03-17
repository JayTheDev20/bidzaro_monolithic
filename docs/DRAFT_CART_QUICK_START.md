# 📦 DRAFT CART - QUICK REFERENCE & FLOW

**Quick Navigation**:
- [All 5 Endpoints](#-all-5-endpoints)
- [Draft → Main Cart Flow](#-draft--main-cart-conversion-flow)
- [Code Examples](#-code-examples)
- [Common Errors](#-common-errors)

---

## 🎯 All 5 Endpoints

### GET Draft Cart
```
GET /api/v1/cart/draft
Authorization: Bearer <token>

Response: List of items in draft cart
```

### ADD to Draft Cart
```
POST /api/v1/cart/draft/items?masterItemId={id}&quantity={qty}
Authorization: Bearer <token>

Response: 200 OK with added item
```

### BATCH Add to Draft Cart
```
POST /api/v1/cart/draft/items/batch
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "items": [
    {"masterItemId": "id-1", "quantity": 50},
    {"masterItemId": "id-2", "quantity": 30}
  ]
}

Response: 200 OK with all added items
```

### REMOVE from Draft Cart
```
DELETE /api/v1/cart/draft/items/{masterItemId}
Authorization: Bearer <token>

Response: 200 OK
```

### CLEAR Draft Cart
```
DELETE /api/v1/cart/draft
Authorization: Bearer <token>

Response: 200 OK
```

---

## 🔄 DRAFT → MAIN CART CONVERSION FLOW

### The Key Step: User Selects Vendor

```
Draft Cart (BEFORE vendor selection)
├─ Biryani (50 plates)
├─ Butter Chicken (50 plates)
└─ Naan (30 pieces)

        ↓ USER SELECTS VENDOR ↓

Main Cart (AFTER vendor selection)
├─ Vendor's Biryani (50 plates) @ ₹280/plate
├─ Vendor's Butter Chicken (50 plates) @ ₹250/plate
└─ Vendor's Naan (30 pieces) @ ₹20/piece
```

### Step-by-Step Conversion

```
STEP 1: Get Draft Cart Items
GET /api/v1/cart/draft
Response: [
  { masterItemId: "item-1", quantity: 50 },
  { masterItemId: "item-2", quantity: 50 },
  { masterItemId: "item-3", quantity: 30 }
]

       ↓

STEP 2: User Selects Vendor
Vendor ID: "vendor-xyz"

       ↓

STEP 3: Find Vendor's Version of Each Item
GET /api/v1/vendors/vendor-xyz/menu
Map master items to vendor items

       ↓

STEP 4: Batch Move to Main Cart
POST /api/v1/cart/items/batch
{
  "vendorId": "vendor-xyz",
  "items": [
    { "vendorItemId": "vendor-item-1", "quantity": 50 },
    { "vendorItemId": "vendor-item-2", "quantity": 50 },
    { "vendorItemId": "vendor-item-3", "quantity": 30 }
  ]
}

       ↓

STEP 5: Automatic Cleanup
🟢 Items added to main cart
🟢 Draft items auto-deleted
🟢 Draft cart becomes empty

       ↓

READY TO CHECKOUT! ✅
```

---

## 💻 CODE EXAMPLES

### JavaScript: Full Draft → Main Cart Flow

```javascript
const API_BASE = 'http://localhost:8080/api/v1';
const headers = { Authorization: `Bearer ${token}` };

// 1. Add items to draft cart
async function buildDraftCart() {
  const items = [
    { masterItemId: 'item-1', quantity: 50 },
    { masterItemId: 'item-2', quantity: 50 },
    { masterItemId: 'item-3', quantity: 30 }
  ];

  for (const item of items) {
    await axios.post(
      `${API_BASE}/cart/draft/items?masterItemId=${item.masterItemId}&quantity=${item.quantity}`,
      null,
      { headers }
    );
  }
}

// 2. Get draft cart to verify
async function viewDraftCart() {
  const response = await axios.get(`${API_BASE}/cart/draft`, { headers });
  console.log('Draft cart:', response.data.data);
  return response.data.data;
}

// 3. Search vendors
async function findVendors(location) {
  const response = await axios.get(
    `${API_BASE}/vendors/search?location=${location}`,
    { headers }
  );
  return response.data.data; // Array of vendors
}

// 4. Get vendor's menu
async function getVendorMenu(vendorId) {
  const response = await axios.get(
    `${API_BASE}/vendors/${vendorId}/menu`,
    { headers }
  );
  return response.data.data; // Array of vendor items
}

// 5. Map draft items to vendor items
async function mapDraftToVendorItems(draftItems, vendorId) {
  const vendorMenu = await getVendorMenu(vendorId);

  return draftItems.map(draftItem => {
    // Find matching vendor item
    // In real app: match by name, cuisine, etc.
    const vendorItem = vendorMenu.find(
      item => item.itemName === draftItem.itemName
    );

    return {
      vendorItemId: vendorItem.id,
      quantity: draftItem.quantity
    };
  });
}

// 6. MAIN STEP: Convert draft to main cart
async function checkoutWithVendor(vendorId, draftItems) {
  const items = await mapDraftToVendorItems(draftItems, vendorId);

  const response = await axios.post(
    `${API_BASE}/cart/items/batch`,
    {
      vendorId: vendorId,
      items: items
    },
    { headers }
  );

  console.log('✅ Moved to main cart:', response.data.data);
  return response.data.data;
}

// 7. Complete flow
async function completeWorkflow() {
  // Step 1: Add to draft
  await buildDraftCart();

  // Step 2: View draft
  const draft = await viewDraftCart();

  // Step 3: Find vendors
  const vendors = await findVendors('New York');
  console.log('Available vendors:', vendors);

  // Step 4: User selects vendor (hardcoded for demo)
  const selectedVendor = vendors[0];
  console.log('Selected vendor:', selectedVendor);

  // Step 5: Move to main cart
  await checkoutWithVendor(selectedVendor.id, draft);

  console.log('✅ Checkout complete!');
}
```

### cURL: Full Conversion Flow

```bash
#!/bin/bash

TOKEN="<your-jwt-token>"
VENDOR_ID="vendor-xyz-123"

echo "=== STEP 1: Add items to draft cart ==="
curl -X POST "http://localhost:8080/api/v1/cart/draft/items?masterItemId=item-1&quantity=50" \
  -H "Authorization: Bearer $TOKEN"

curl -X POST "http://localhost:8080/api/v1/cart/draft/items?masterItemId=item-2&quantity=50" \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n=== STEP 2: View draft cart ==="
curl -X GET "http://localhost:8080/api/v1/cart/draft" \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n=== STEP 3: Search vendors ==="
curl -X GET "http://localhost:8080/api/v1/vendors/search?location=NYC&cuisine=Indian" \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n=== STEP 4: Get vendor menu ==="
curl -X GET "http://localhost:8080/api/v1/vendors/$VENDOR_ID/menu" \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n=== STEP 5: Convert draft to main cart ==="
curl -X POST "http://localhost:8080/api/v1/cart/items/batch" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "vendorId": "'$VENDOR_ID'",
    "items": [
      {
        "vendorItemId": "vendor-item-1",
        "quantity": 50
      },
      {
        "vendorItemId": "vendor-item-2",
        "quantity": 50
      }
    ]
  }'

echo -e "\n=== STEP 6: Verify draft is empty ==="
curl -X GET "http://localhost:8080/api/v1/cart/draft" \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n=== STEP 7: View main cart ==="
curl -X GET "http://localhost:8080/api/v1/cart" \
  -H "Authorization: Bearer $TOKEN"
```

---

## ⚠️ COMMON ERRORS

### ❌ Error: Missing /items suffix
```
POST /api/v1/cart/draft  ← WRONG
Response: 405 METHOD_NOT_ALLOWED

POST /api/v1/cart/draft/items  ← CORRECT ✅
Response: 200 OK
```

### ❌ Error: Invalid quantity
```
POST /api/v1/cart/draft/items?masterItemId=id&quantity=0
Response: 400 VALIDATION_ERROR
"Quantity must be greater than 0"

POST /api/v1/cart/draft/items?masterItemId=id&quantity=50  ← CORRECT ✅
Response: 200 OK
```

### ❌ Error: Master item not found
```
POST /api/v1/cart/draft/items?masterItemId=invalid-uuid
Response: 404 NOT_FOUND
"Master menu item not found"

→ Verify masterItemId from: GET /api/v1/menu/items
```

### ❌ Error: Vendor item not found
```
POST /api/v1/cart/items/batch
{
  "vendorId": "vendor-id",
  "items": [
    { "vendorItemId": "invalid-uuid", "quantity": 50 }
  ]
}
Response: 404 NOT_FOUND

→ Verify vendorItemId from: GET /api/v1/vendors/{vendorId}/menu
```

---

## 🧪 QUICK TEST

```bash
# 1. Add 3 items to draft
for i in 1 2 3; do
  curl -X POST \
    "http://localhost:8080/api/v1/cart/draft/items?masterItemId=menu-item-$i&quantity=$((50-i*5))" \
    -H "Authorization: Bearer $TOKEN"
done

# 2. View draft
curl -X GET "http://localhost:8080/api/v1/cart/draft" \
  -H "Authorization: Bearer $TOKEN" | jq '.data | length'

# 3. Clear draft
curl -X DELETE "http://localhost:8080/api/v1/cart/draft" \
  -H "Authorization: Bearer $TOKEN"

# 4. Verify empty
curl -X GET "http://localhost:8080/api/v1/cart/draft" \
  -H "Authorization: Bearer $TOKEN" | jq '.data | length'
```

---

## 📊 DRAFT VS MAIN CART

| Aspect | Draft Cart | Main Cart |
|--------|-----------|-----------|
| **Items** | Master menu items | Vendor-specific items |
| **Purpose** | Plan/browse | Ready to buy |
| **Vendor Selected** | ❌ No | ✅ Yes |
| **Pricing** | Generic | Vendor-specific |
| **Operations** | Add/Remove/Clear | Add/Remove/Update/Clear |
| **Expiry** | 30 days | 30 days |

---

## ✅ SUMMARY: DRAFT CART FLOW

1. ✅ User browses menu
2. ✅ Adds items to draft: `POST /cart/draft/items`
3. ✅ Views draft: `GET /cart/draft`
4. ✅ Searches vendors: `GET /vendors/search`
5. ✅ Selects vendor
6. ✅ Maps draft items to vendor items
7. ✅ Moves to main cart: `POST /cart/items/batch`
8. ✅ Draft auto-deleted
9. ✅ Ready for checkout!

---

**For Complete Details**: See [DRAFT_CART_API_COMPLETE_GUIDE.md](./DRAFT_CART_API_COMPLETE_GUIDE.md)

*Last Updated: March 17, 2026*


