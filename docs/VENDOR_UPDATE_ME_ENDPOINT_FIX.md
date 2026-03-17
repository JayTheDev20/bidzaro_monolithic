# 🔧 VENDOR PROFILE UPDATE - "/me" ENDPOINT FIX

**Date**: March 17, 2026
**Issue**: 404 NOT_FOUND when calling `PUT /api/v1/vendors/me`
**Status**: ✅ FIXED

---

## ❌ PROBLEM

```
Frontend Request:
PUT /api/v1/vendors/me
Body: { vendorName: "...", businessPhone: "...", ...}

Response: 404 NOT_FOUND
Error: Vendor not found

Logs:
Updating vendor: me by user: 1e827d19-f930-45af-b9b5-50cc064e8dae
find using query: { "vendor_id" : "me"}
Resource not found: Vendor not found
```

---

## 🔍 ROOT CAUSE

The frontend was sending `PUT /vendors/me`, but:
1. Only `@GetMapping("/me")` endpoint existed (GET only)
2. No `@PutMapping("/me")` endpoint for updates
3. Spring matched it with `PUT /{vendorId}` where vendorId="me"
4. Backend tried to find vendor with literal ID "me" → 404 error

---

## ✅ SOLUTION

### Added Two New Methods

#### 1. Controller Endpoint (VendorController.java)
```java
@PutMapping("/me")
@Operation(summary = "Update my vendor profile", description = "Updates the vendor profile of the authenticated user")
@SecurityRequirement(name = "bearerAuth")
public ResponseEntity<ApiResponse<VendorResponse>> updateMyVendorProfile(
        @Valid @RequestBody VendorUpdateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
    log.info("Vendor update request for current user: {}", userDetails.getUserId());
    VendorResponse response = vendorService.updateVendorByUserId(userDetails.getUserId(), request);
    return ResponseEntity.ok(ApiResponse.success(response, "Vendor updated successfully"));
}
```

#### 2. Service Method (VendorService.java)
```java
/**
 * Updates vendor profile by user ID (for /me endpoint).
 */
@Transactional
public VendorResponse updateVendorByUserId(String userId, VendorUpdateRequest request) {
    log.info("Updating vendor by user: {}", userId);

    Vendor vendor = vendorRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Vendor profile not found for this user"));

    // Delegate to existing updateVendor logic using the vendor ID
    return updateVendor(vendor.getVendorId(), request, userId);
}
```

---

## 🎯 FLOW

```
Frontend: PUT /api/v1/vendors/me
                ↓
Spring matches: @PutMapping("/me")
                ↓
Controller: updateMyVendorProfile()
                ↓
Service: updateVendorByUserId(userId, request)
                ↓
Get vendor by userId (instead of vendorId!)
                ↓
Call: updateVendor(vendor.getVendorId(), request, userId)
                ↓
Update vendor profile ✅
                ↓
Response: 200 OK with updated vendor data
```

---

## ✅ NOW WORKING ENDPOINTS

### Get My Vendor Profile
```
GET /api/v1/vendors/me
Authorization: Bearer <token>

Response: 200 OK
{
  "data": {
    "vendorId": "vendor-123",
    "businessName": "Royal Catering",
    "status": "ACTIVE",
    ...
  }
}
```

### Update My Vendor Profile (NEW!)
```
PUT /api/v1/vendors/me
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
  "businessName": "Royal Catering Co.",
  "businessPhone": "+1-555-1234",
  "description": "Premium catering services",
  "cuisinesOffered": ["Indian", "Continental"],
  ...
}

Response: 200 OK
{
  "data": {
    "vendorId": "vendor-123",
    "businessName": "Royal Catering Co.",
    "status": "ACTIVE",
    ...
  }
}
```

### Update Specific Vendor (Already Existed)
```
PUT /api/v1/vendors/{vendorId}
Authorization: Bearer <token>
(Same request/response as above)
```

---

## 🧪 TEST THE FIX

### cURL Test
```bash
# Update my vendor profile
curl -X PUT "http://localhost:8080/api/v1/vendors/me" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "businessName": "Updated Catering Business",
    "businessPhone": "+1-555-9999"
  }'

# Expected Response: 200 OK
{
  "success": true,
  "status": 200,
  "message": "Vendor updated successfully",
  "data": {
    "vendorId": "...",
    "businessName": "Updated Catering Business",
    "businessPhone": "+1-555-9999"
  }
}
```

### JavaScript Test
```javascript
const updateMyVendor = async (token, updateData) => {
  const response = await fetch('/api/v1/vendors/me', {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(updateData)
  });

  const data = await response.json();
  console.log('Updated:', data.data);
  return data.data;
};

// Usage
await updateMyVendor(token, {
  businessName: 'New Name',
  description: 'New Description'
});
```

---

## 📋 ALL VENDOR ENDPOINTS

| Endpoint | Method | Auth | Purpose | Status |
|----------|--------|------|---------|--------|
| `/vendors` | POST | ✅ | Register as vendor | ✅ |
| `/vendors` | GET | ⬜ | List all vendors | ✅ |
| `/vendors/me` | GET | ✅ | Get my profile | ✅ |
| `/vendors/me` | PUT | ✅ | Update my profile | ✅ NEW |
| `/vendors/{vendorId}` | GET | ⬜ | Get vendor by ID | ✅ |
| `/vendors/{vendorId}` | PUT | ✅ | Update vendor (admin) | ✅ |
| `/vendors/{vendorId}/menu/simple` | GET | ⬜ | Get vendor menu | ✅ |
| `/vendors/search` | GET | ⬜ | Search vendors | ✅ |
| `/admin/vendors/{vendorId}/approve` | PUT | ✅ | Approve vendor | ✅ |
| `/admin/vendors/{vendorId}/reject` | PUT | ✅ | Reject vendor | ✅ |

---

## 🔐 SECURITY

✅ `/me` endpoint requires JWT token
✅ Can only update own vendor profile
✅ Automatic user ownership validation
✅ Proper error handling

---

## 📊 FILES MODIFIED

1. **VendorController.java**
   - Added `@PutMapping("/me")` endpoint
   - Line: ~101-109

2. **VendorService.java**
   - Added `updateVendorByUserId()` method
   - Line: ~397-405

---

## 🎉 SUMMARY

✅ Added `PUT /vendors/me` endpoint
✅ Frontend can now update vendor profile using "/me"
✅ Backend resolves "me" to actual userId
✅ No more 404 errors
✅ Both endpoints work:
   - `PUT /vendors/me` (current user)
   - `PUT /vendors/{vendorId}` (specific vendor)

---

**Status**: 🟢 **FIXED & READY**

The vendor profile update using "/me" should now work correctly!

*Last Updated: March 17, 2026*


