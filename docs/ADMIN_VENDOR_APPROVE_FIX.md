# 🔧 ADMIN VENDOR APPROVE/REJECT - 405 FIX

**Date**: March 17, 2026
**Issue**: 405 METHOD_NOT_ALLOWED on vendor approve/reject endpoints
**Status**: ✅ FIXED

---

## ❌ PROBLEM

```
Frontend Request:
PUT /api/v1/admin/vendors/f1f6c167-0e89-461d-8346-42ff1542f771/approve
Response: 405 METHOD_NOT_ALLOWED

Error:
api.ts:248  PUT http://localhost:8080/api/v1/admin/vendors/{vendorId}/approve
405 (Method Not Allowed)
```

---

## 🔍 ROOT CAUSE

**Backend** had: `@PostMapping("/vendors/{vendorId}/approve")`
**Frontend** was sending: `PUT /admin/vendors/{vendorId}/approve`

Mismatch between HTTP method and mapping annotation.

---

## ✅ SOLUTION APPLIED

Changed AdminController endpoints from `@PostMapping` to `@PutMapping`:

### BEFORE (Wrong)
```java
@PostMapping("/vendors/{vendorId}/approve")
public ResponseEntity<ApiResponse<VendorResponse>> approveVendor(...)

@PostMapping("/vendors/{vendorId}/reject")
public ResponseEntity<ApiResponse<VendorResponse>> rejectVendor(...)
```

### AFTER (Correct)
```java
@PutMapping("/vendors/{vendorId}/approve")
public ResponseEntity<ApiResponse<VendorResponse>> approveVendor(...)

@PutMapping("/vendors/{vendorId}/reject")
public ResponseEntity<ApiResponse<VendorResponse>> rejectVendor(...)
```

**File Modified**: `AdminController.java`

---

## ✅ ALL ADMIN VENDOR ENDPOINTS

| Endpoint | Method | Status | Purpose |
|----------|--------|--------|---------|
| `/admin/vendors` | GET | ✅ | List all vendors |
| `/admin/vendors/{vendorId}` | GET | ✅ | Get vendor details |
| `/admin/vendors/{vendorId}/approve` | PUT | ✅ | Approve vendor |
| `/admin/vendors/{vendorId}/reject` | PUT | ✅ | Reject vendor |
| `/admin/vendors/{vendorId}/suspend` | PATCH | ✅ | Suspend vendor |
| `/admin/vendors/{vendorId}/activate` | PATCH | ✅ | Activate vendor |
| `/admin/vendors/pending` | GET | ✅ | Get pending vendors |

---

## 🧪 TEST THE FIX

### cURL Test
```bash
# Approve vendor
curl -X PUT "http://localhost:8080/api/v1/admin/vendors/f1f6c167-0e89-461d-8346-42ff1542f771/approve" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"

# Expected Response: 200 OK
{
  "success": true,
  "status": 200,
  "message": "Vendor approved successfully",
  "data": {
    "vendorId": "f1f6c167-0e89-461d-8346-42ff1542f771",
    "status": "ACTIVE",
    "approvalStatus": "APPROVED"
  }
}
```

### JavaScript Test
```javascript
const response = await axios.put(
  '/api/v1/admin/vendors/{vendorId}/approve',
  null,
  { headers: { Authorization: `Bearer ${token}` } }
);
// Response: 200 OK ✅
```

---

## 📋 RELATED ENDPOINTS (Also Fixed)

### Reject Vendor
```
PUT /api/v1/admin/vendors/{vendorId}/reject
Authorization: Bearer <token>

Request Body:
{
  "reason": "FSSAI license expired"
}

Response: 200 OK
```

---

## 🎯 VERIFICATION

- ✅ Changed approve from POST to PUT
- ✅ Changed reject from POST to PUT
- ✅ No compilation errors
- ✅ Endpoints now match frontend expectations
- ✅ 200 OK response expected

---

## 📚 DOCUMENTATION UPDATED

Documentation already had PUT in these files:
- ADMIN_API_DOCS.md (line 488-520)
- API_COMPLETE_REFERENCE.md

Frontend now matches backend! ✅

---

**Status**: 🟢 **FIXED**

*Test the endpoints now - 405 error should be gone!*


