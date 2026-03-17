# 📊 DRAFT CART TO MAIN CART - FLOW DIAGRAM & ARCHITECTURE

**Date**: March 17, 2026

---

## 🏗️ ARCHITECTURE OVERVIEW

```
┌─────────────────────────────────────────────────────────────────────┐
│                        BIDZARO CATERING PLATFORM                    │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────┐
│                          FRONTEND (React/Mobile)                     │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  Home Screen                      Vendor Search                      │
│  ├─ Browse Menu              →    ├─ Filter by location             │
│  │  (Get Master Items)           ├─ Filter by cuisine              │
│  │                               ├─ Sort by rating                 │
│  └─ Add to Draft Cart            └─ View vendor details            │
│     (POST /draft/items)                   ↓                         │
│                            Select Vendor & Map Items                │
│                            (Convert to vendor items)                │
│                                    ↓                                │
│  Draft Cart                   Checkout                              │
│  ├─ View items            →   ├─ Review vendor items               │
│  ├─ Modify qty               ├─ See vendor pricing                 │
│  ├─ Remove items             ├─ Update quantities                  │
│  └─ Clear all               └─ Proceed to payment                 │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
                              ↕ HTTP REST
┌──────────────────────────────────────────────────────────────────────┐
│                         BACKEND (Spring Boot)                        │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  DraftCartController              CartController                    │
│  ├─ GET /draft                 ├─ GET /cart                        │
│  ├─ POST /draft/items          ├─ POST /cart/items               │
│  ├─ POST /draft/items/batch    ├─ POST /cart/items/batch        │
│  ├─ DELETE /draft/items        ├─ PUT /cart/items/{id}          │
│  └─ DELETE /draft              ├─ DELETE /cart/items/{id}       │
│                                ├─ DELETE /cart                    │
│                                └─ GET /cart/grouped              │
│         ↓                                ↓                         │
│  DraftCartService              CartService                         │
│         ↓                                ↓                         │
│  DraftCartRepository       CartRepository                          │
│         ↓                                ↓                         │
│  ┌─────────────────────────────────────────┐                      │
│  │      MongoDB Collections                │                      │
│  │  ├─ draft_cart_items                    │                      │
│  │  ├─ cart_items                          │                      │
│  │  └─ menu_items (master)                 │                      │
│  └─────────────────────────────────────────┘                      │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 DETAILED FLOW DIAGRAM

### State Transitions

```
┌─────────────────────────────────────────────────────────────────────┐
│                      USER JOURNEY STATE MACHINE                      │
└─────────────────────────────────────────────────────────────────────┘

1. INITIAL STATE
   ┌──────────────┐
   │ HOME SCREEN  │
   │ No items     │
   └──────┬───────┘
          │ GET /menu/items
          ↓

2. BROWSING STATE
   ┌──────────────────────────┐
   │ VIEW MASTER MENU         │
   │ - 50+ menu items         │
   │ - Browse by cuisine      │
   │ - See generic pricing    │
   └──────┬───────────────────┘
          │ POST /cart/draft/items
          ↓

3. DRAFTING STATE
   ┌──────────────────────────┐
   │ DRAFT CART POPULATED     │
   │ ✅ Biryani × 50          │
   │ ✅ Butter Chicken × 50   │
   │ ✅ Naan × 30             │
   │                          │
   │ Actions:                 │
   │ - View (GET /draft)      │
   │ - Add more               │
   │ - Remove items           │
   │ - Clear all              │
   └──────┬───────────────────┘
          │ GET /vendors/search
          ↓

4. VENDOR SEARCH STATE
   ┌──────────────────────────┐
   │ VENDOR MATCHING          │
   │ - 5 vendors found        │
   │ - All have Biryani       │
   │ - Different prices       │
   │ - Different ratings      │
   └──────┬───────────────────┘
          │ SELECT VENDOR
          ↓

5. VENDOR SELECTION STATE
   ┌──────────────────────────┐
   │ SELECTED: Royal Catering │
   │                          │
   │ Pricing:                 │
   │ - Biryani: ₹280/plate    │
   │ - Butter Chicken: ₹250   │
   │ - Naan: ₹20/piece        │
   └──────┬───────────────────┘
          │ POST /cart/items/batch
          │ (Convert draft items)
          ↓

6. CHECKOUT STATE
   ┌──────────────────────────┐
   │ MAIN CART READY          │
   │                          │
   │ Items from Royal Catering│
   │ ✅ Biryani × 50 @ ₹280   │
   │ ✅ Butter Chicken × 50   │
   │ ✅ Naan × 30             │
   │                          │
   │ Total: ₹17,000           │
   │                          │
   │ Actions:                 │
   │ - Review                 │
   │ - Modify qty             │
   │ - Pay                    │
   └──────┬───────────────────┘
          │ POST /orders
          ↓

7. FINAL STATE
   ┌──────────────────────────┐
   │ ORDER CREATED            │
   │ Status: PENDING_PAYMENT  │
   │                          │
   │ Order ID: ORD-123456     │
   │ Total: ₹17,000           │
   │ Vendor: Royal Catering   │
   └──────────────────────────┘
```

---

## 📝 API CALL SEQUENCE DIAGRAM

```
┌─────────┐                 ┌─────────────┐              ┌──────────┐
│ Frontend│                 │   Backend   │              │ MongoDB  │
└────┬────┘                 └──────┬──────┘              └────┬─────┘
     │                             │                         │
     │ 1. GET /menu/items          │                         │
     ├────────────────────────────→│                         │
     │                             │ Query master items      │
     │                             ├────────────────────────→│
     │                             │ Return items            │
     │                             │←────────────────────────┤
     │ Display menu                │                         │
     │←────────────────────────────┤                         │
     │                             │                         │
     │ 2. [User adds 3 items]      │                         │
     │ POST /draft/items (item-1)  │                         │
     ├────────────────────────────→│                         │
     │                             │ Create draft_item_1     │
     │                             ├────────────────────────→│
     │                             │ Return created          │
     │                             │←────────────────────────┤
     │ Confirm added               │                         │
     │←────────────────────────────┤                         │
     │                             │                         │
     │ 3. POST /draft/items (item-2)  (similar flow)       │
     │ 4. POST /draft/items (item-3)  (similar flow)       │
     │                             │                         │
     │ 5. GET /draft               │                         │
     ├────────────────────────────→│                         │
     │                             │ Query draft items       │
     │                             ├────────────────────────→│
     │                             │ [item-1, item-2, item-3]
     │                             │←────────────────────────┤
     │ Show draft cart             │                         │
     │←────────────────────────────┤                         │
     │                             │                         │
     │ 6. [User searches vendors]  │                         │
     │ GET /vendors/search?loc=NYC │                         │
     ├────────────────────────────→│                         │
     │                             │ Query vendors           │
     │                             ├────────────────────────→│
     │                             │ [Royal Catering, ...]   │
     │                             │←────────────────────────┤
     │ Display vendors             │                         │
     │←────────────────────────────┤                         │
     │                             │                         │
     │ 7. [User selects vendor]    │                         │
     │ GET /vendors/id/menu        │                         │
     ├────────────────────────────→│                         │
     │                             │ Query vendor items      │
     │                             ├────────────────────────→│
     │                             │ [vendor-item-1, ...]    │
     │                             │←────────────────────────┤
     │ Map draft → vendor items    │                         │
     │←────────────────────────────┤                         │
     │                             │                         │
     │ 8. [KEY STEP] Convert draft │                         │
     │ POST /cart/items/batch      │                         │
     │ {vendorId, items[...]}      │                         │
     ├────────────────────────────→│                         │
     │                             │ For each vendor item:   │
     │                             │ - Create cart_item      │
     │                             ├────────────────────────→│
     │                             │ - Delete draft_item     │
     │                             ├────────────────────────→│
     │                             │ Return created items    │
     │                             │←────────────────────────┤
     │ Confirm conversion          │                         │
     │←────────────────────────────┤                         │
     │                             │                         │
     │ 9. GET /cart                │                         │
     ├────────────────────────────→│                         │
     │                             │ Query cart items        │
     │                             ├────────────────────────→│
     │                             │ [vendor-item-1, ...]    │
     │                             │←────────────────────────┤
     │ Show cart with prices       │                         │
     │←────────────────────────────┤                         │
     │                             │                         │
     │ 10. GET /cart/total         │                         │
     ├────────────────────────────→│                         │
     │                             │ Calculate total         │
     │ Display total: ₹17,000      │                         │
     │←────────────────────────────┤                         │
     │                             │                         │
     │ 11. POST /orders            │                         │
     ├────────────────────────────→│                         │
     │                             │ Create order            │
     │                             ├────────────────────────→│
     │                             │ Order created           │
     │                             │←────────────────────────┤
     │ Order confirmed!            │                         │
     │←────────────────────────────┤                         │
     │                             │                         │
```

---

## 💾 DATABASE SCHEMA & TRANSFORMATIONS

### Draft Cart Item (MongoDB: `draft_cart_items`)
```javascript
{
  "_id": ObjectId,
  "user_id": "user-uuid-123",
  "master_item_id": "menu-item-biryani",
  "item_name": "Biryani",
  "quantity": 50,
  "added_at": ISODate("2026-03-17T10:00:00"),
  "updated_at": ISODate("2026-03-17T10:00:00"),
  "expires_at": ISODate("2026-04-16T10:00:00"),  // 30 days TTL
  "_class": "com.cateringmarketplace.module.cart.model.DraftCartItem"
}
```

### Main Cart Item (MongoDB: `cart_items`)
```javascript
{
  "_id": ObjectId,
  "user_id": "user-uuid-123",
  "vendor_id": "vendor-royal-catering",
  "vendor_item_id": "vendor-item-biryani-royal",
  "item_name": "Biryani",
  "quantity": 50,
  "price_per_plate": 280.00,
  "currency": "INR",
  "total_price": 14000.00,
  "added_at": ISODate("2026-03-17T10:10:00"),
  "updated_at": ISODate("2026-03-17T10:10:00"),
  "expires_at": ISODate("2026-04-16T10:10:00"),  // 30 days TTL
  "_class": "com.cateringmarketplace.module.cart.model.CartItem"
}
```

### Transformation During Conversion

```
Draft Item (Generic)
┌──────────────────────────────┐
│ user_id: user-123            │
│ master_item_id: menu-biryani │
│ item_name: Biryani           │
│ quantity: 50                 │
│ NO VENDOR                    │
│ NO PRICING                   │
└──────────┬───────────────────┘
           │ User selects vendor
           │ "Royal Catering"
           ↓
Cart Item (Vendor-Specific)
┌──────────────────────────────────────┐
│ user_id: user-123                    │
│ vendor_id: vendor-royal-catering     │
│ vendor_item_id: vendor-item-biryani  │
│ item_name: Biryani                   │
│ quantity: 50                         │
│ price_per_plate: 280                 │
│ total_price: 14000                   │
│ currency: INR                        │
└──────────────────────────────────────┘
           ↓ Auto-delete from draft
           ↓
Draft cart empty ✅
Ready for checkout!
```

---

## 🔐 SECURITY & AUTHORIZATION

```
┌─────────────────────────────────────┐
│  All Draft Cart Endpoints            │
├─────────────────────────────────────┤
│                                     │
│ ✅ Require JWT Token (Bearer)       │
│ ✅ Validate user ownership          │
│ ✅ Prevent cross-user access        │
│ ✅ Log all operations               │
│                                     │
│ Example: User A cannot see/modify   │
│ User B's draft cart                 │
│                                     │
│ Check in each endpoint:             │
│ ├─ Extract userId from JWT          │
│ ├─ Query userId in repository       │
│ ├─ Validate match                   │
│ └─ Return 403 if mismatch           │
│                                     │
└─────────────────────────────────────┘
```

---

## 📊 DATA FLOW SUMMARY

```
┌─ STEP 1: MENU BROWSING ─┐
│ Frontend                 │
│ ├─ GET menu items        │
│ └─ Display list          │
│           ↓              │
├─────────────────────────┤
│                         │
│ STEP 2: DRAFT CART      │ ← NEW ITEMS HERE
│ POST /draft/items       │
│ - Item 1: Biryani       │
│ - Item 2: Butter Chick  │
│ - Item 3: Naan          │
│           ↓              │
├─────────────────────────┤
│                         │
│ STEP 3: VENDOR SEARCH   │
│ GET /vendors/search     │
│ - Find matching vendors │
│           ↓              │
├─────────────────────────┤
│                         │
│ STEP 4: CONVERSION      │ ← KEY TRANSFORMATION
│ POST /cart/items/batch  │
│ Vendor: Royal Catering  │
│ Map items + get prices  │
│           ↓              │
├─────────────────────────┤
│                         │
│ STEP 5: MAIN CART READY │ ← WITH PRICES
│ GET /cart               │
│ - Biryani: ₹14,000      │
│ - Butter Chick: ₹12,500 │
│ - Naan: ₹600            │
│ TOTAL: ₹27,100          │
│           ↓              │
├─────────────────────────┤
│                         │
│ STEP 6: CHECKOUT        │
│ POST /orders            │
│ ✅ ORDER CREATED        │
│                         │
└─────────────────────────┘
```

---

## ⚡ KEY ARCHITECTURAL DECISIONS

| Decision | Why | Benefit |
|----------|-----|---------|
| **Separate Draft & Main** | Items unknown until vendor selected | Flexibility, no pricing conflict |
| **Auto-delete on conversion** | Keep draft clean | No duplicate items, no confusion |
| **30-day TTL** | Prevent stale data | Save storage, fresh data |
| **Cumulative quantities** | Add 30, then add 20 → 50 | User-friendly, natural behavior |
| **Batch operations** | Convert multiple items | Performance, efficiency |
| **Vendor-specific pricing** | Each vendor has own prices | Fair pricing, competitive |

---

## 🧪 TEST SCENARIOS

### Scenario 1: Happy Path
```
1. Add 3 items to draft ✅
2. View draft ✅
3. Select vendor ✅
4. Convert to main cart ✅
5. Verify draft empty ✅
6. Verify main cart has items ✅
7. Checkout ✅
```

### Scenario 2: Modification Path
```
1. Add items to draft ✅
2. Remove one item ✅
3. View draft (2 items) ✅
4. Convert remaining to cart ✅
5. Verify only 2 items in cart ✅
```

### Scenario 3: Cancellation Path
```
1. Add items to draft ✅
2. Change mind ✅
3. Clear draft ✅
4. Verify draft empty ✅
5. No items in main cart ✅
```

---

**Status**: ✅ COMPLETE & PRODUCTION READY

*Last Updated: March 17, 2026*


