# 📢 Announcement CRUD Operations - Complete Documentation

## ✅ All CRUD Operations Implemented

### 1. CREATE (POST)
### 2. READ (GET) - All & By ID
### 3. UPDATE (PUT)
### 4. DELETE (DELETE)

---

## 🔗 API Endpoints Overview

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/api/v1/admin/announcements` | Create new announcement | ✅ Ready |
| GET | `/api/v1/admin/announcements` | Get all announcements (paginated) | ✅ Ready |
| GET | `/api/v1/admin/announcements/{announcementId}` | Get announcement by ID | ✅ Ready |
| PUT | `/api/v1/admin/announcements/{announcementId}` | Update announcement | ✅ Ready |
| DELETE | `/api/v1/admin/announcements/{announcementId}` | Delete announcement | ✅ Ready |

---

## 📝 1. CREATE - POST /api/v1/admin/announcements

### Request
```http
POST /api/v1/admin/announcements
Authorization: Bearer {adminToken}
Content-Type: application/json
```

### Request Body (CreateAnnouncementRequest)

```json
{
  "title": "System Maintenance Scheduled",
  "message": "Our platform will undergo scheduled maintenance on March 5, 2026 from 2 AM to 4 AM UTC. All services will be temporarily unavailable during this period.",
  "targetAudience": "ALL",
  "priority": "HIGH",
  "startDate": "2026-03-05T00:00:00Z",
  "endDate": "2026-03-05T05:00:00Z"
}
```

### Validation Rules

| Field | Required | Min | Max | Valid Values |
|-------|----------|-----|-----|--------------|
| `title` | ✅ YES | 5 | 200 | Any string |
| `message` | ✅ YES | 10 | 2000 | Any string |
| `targetAudience` | ❌ NO | - | - | ALL, USERS, VENDORS, ADMINS |
| `priority` | ❌ NO | - | - | LOW, NORMAL, HIGH, URGENT |
| `startDate` | ❌ NO | - | - | ISO-8601 UTC format |
| `endDate` | ❌ NO | - | - | ISO-8601 UTC format |

### Response (201 Created)

```json
{
  "success": true,
  "status": 201,
  "message": "Announcement created successfully",
  "data": {
    "announcementId": "ann-550e8400-e29b",
    "title": "System Maintenance Scheduled",
    "message": "Our platform will undergo scheduled maintenance...",
    "targetAudience": "ALL",
    "priority": "HIGH",
    "startDate": "2026-03-05T00:00:00Z",
    "endDate": "2026-03-05T05:00:00Z",
    "isActive": true,
    "createdBy": "admin-001",
    "createdAt": "2026-03-05T14:30:00Z"
  }
}
```

---

## 📖 2. READ - GET /api/v1/admin/announcements

### Get All Announcements (Paginated)

```http
GET /api/v1/admin/announcements?page=0&size=20
Authorization: Bearer {adminToken}
```

### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | Integer | 0 | Page number (0-indexed) |
| `size` | Integer | 20 | Items per page |

### Response (200 OK)

```json
{
  "success": true,
  "status": 200,
  "message": "Announcements retrieved",
  "data": [
    {
      "announcementId": "ann-550e8400-e29b",
      "title": "System Maintenance Scheduled",
      "message": "Our platform will undergo scheduled maintenance...",
      "targetAudience": "ALL",
      "priority": "HIGH",
      "startDate": "2026-03-05T00:00:00Z",
      "endDate": "2026-03-05T05:00:00Z",
      "isActive": true,
      "createdBy": "admin-001",
      "createdAt": "2026-03-05T14:30:00Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

---

### Get Announcement by ID

```http
GET /api/v1/admin/announcements/{announcementId}
Authorization: Bearer {adminToken}
```

### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `announcementId` | String | Unique announcement ID |

### Response (200 OK)

```json
{
  "success": true,
  "status": 200,
  "message": "Announcement retrieved",
  "data": {
    "announcementId": "ann-550e8400-e29b",
    "title": "System Maintenance Scheduled",
    "message": "Our platform will undergo scheduled maintenance...",
    "targetAudience": "ALL",
    "priority": "HIGH",
    "startDate": "2026-03-05T00:00:00Z",
    "endDate": "2026-03-05T05:00:00Z",
    "isActive": true,
    "createdBy": "admin-001",
    "createdAt": "2026-03-05T14:30:00Z"
  }
}
```

### Error Response (404 Not Found)

```json
{
  "success": false,
  "status": 404,
  "message": "Announcement not found",
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Announcement not found"
  }
}
```

---

## ✏️ 3. UPDATE - PUT /api/v1/admin/announcements/{announcementId}

### Request

```http
PUT /api/v1/admin/announcements/{announcementId}
Authorization: Bearer {adminToken}
Content-Type: application/json
```

### Request Body (UpdateAnnouncementRequest)

**Note:** All fields are optional. Only provided fields will be updated.

```json
{
  "title": "System Maintenance Rescheduled",
  "message": "The maintenance has been rescheduled to March 6, 2026 from 2 AM to 4 AM UTC.",
  "targetAudience": "USERS",
  "priority": "URGENT",
  "startDate": "2026-03-06T00:00:00Z",
  "endDate": "2026-03-06T05:00:00Z",
  "isActive": true
}
```

### Validation Rules

| Field | Required | Min | Max | Valid Values |
|-------|----------|-----|-----|--------------|
| `title` | ❌ NO | 5 | 200 | Any string |
| `message` | ❌ NO | 10 | 2000 | Any string |
| `targetAudience` | ❌ NO | - | - | ALL, USERS, VENDORS, ADMINS |
| `priority` | ❌ NO | - | - | LOW, NORMAL, HIGH, URGENT |
| `startDate` | ❌ NO | - | - | ISO-8601 UTC format |
| `endDate` | ❌ NO | - | - | ISO-8601 UTC format |
| `isActive` | ❌ NO | - | - | true or false |

### Response (200 OK)

```json
{
  "success": true,
  "status": 200,
  "message": "Announcement updated",
  "data": {
    "announcementId": "ann-550e8400-e29b",
    "title": "System Maintenance Rescheduled",
    "message": "The maintenance has been rescheduled...",
    "targetAudience": "USERS",
    "priority": "URGENT",
    "startDate": "2026-03-06T00:00:00Z",
    "endDate": "2026-03-06T05:00:00Z",
    "isActive": true,
    "createdBy": "admin-001",
    "createdAt": "2026-03-05T14:30:00Z"
  }
}
```

---

## 🗑️ 4. DELETE - DELETE /api/v1/admin/announcements/{announcementId}

### Request

```http
DELETE /api/v1/admin/announcements/{announcementId}
Authorization: Bearer {adminToken}
```

### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `announcementId` | String | Unique announcement ID |

### Response (200 OK)

```json
{
  "success": true,
  "status": 200,
  "message": "Announcement deleted"
}
```

### Error Response (404 Not Found)

```json
{
  "success": false,
  "status": 404,
  "message": "Announcement not found",
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Announcement not found"
  }
}
```

---

## 🧪 cURL Examples

### 1. Create Announcement

```bash
curl -X POST http://localhost:8080/api/v1/admin/announcements \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "System Maintenance Scheduled",
    "message": "Our platform will undergo scheduled maintenance on March 5, 2026 from 2 AM to 4 AM UTC.",
    "targetAudience": "ALL",
    "priority": "HIGH",
    "startDate": "2026-03-05T00:00:00Z",
    "endDate": "2026-03-05T05:00:00Z"
  }'
```

### 2. Get All Announcements

```bash
curl -X GET "http://localhost:8080/api/v1/admin/announcements?page=0&size=20" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

### 3. Get Announcement by ID

```bash
curl -X GET http://localhost:8080/api/v1/admin/announcements/{announcementId} \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

### 4. Update Announcement

```bash
curl -X PUT http://localhost:8080/api/v1/admin/announcements/{announcementId} \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "System Maintenance Rescheduled",
    "message": "The maintenance has been rescheduled to March 6, 2026.",
    "priority": "URGENT"
  }'
```

### 5. Delete Announcement

```bash
curl -X DELETE http://localhost:8080/api/v1/admin/announcements/{announcementId} \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

---

## 📊 Database Storage

**Collection:** `announcements`
**Database:** MongoDB

### Document Structure

```json
{
  "_id": ObjectId("507f1f77bcf86cd799439011"),
  "announcement_id": "ann-550e8400-e29b",
  "title": "System Maintenance Scheduled",
  "message": "Our platform will undergo scheduled maintenance...",
  "target_audience": "ALL",
  "priority": "HIGH",
  "start_date": ISODate("2026-03-05T00:00:00Z"),
  "end_date": ISODate("2026-03-05T05:00:00Z"),
  "is_active": true,
  "created_by": "admin-001",
  "created_at": ISODate("2026-03-05T14:30:00Z")
}
```

---

## 🔒 Security & Permissions

- ✅ **Admin Only** - All CRUD operations require ADMIN role
- ✅ **JWT Authentication** - All endpoints require valid JWT token
- ✅ **Audit Logging** - All operations are logged in audit log
- ✅ **Soft Delete** - Delete operation sets `isActive` to false

---

## ✨ Features

✅ **Complete CRUD** - Create, Read (All & By ID), Update, Delete
✅ **Pagination** - Get all announcements with pagination support
✅ **Validation** - Full input validation with error messages
✅ **Audit Trail** - All changes tracked in audit logs
✅ **Soft Delete** - Non-destructive deletion
✅ **Flexible Updates** - Update only specific fields
✅ **Multiple Audiences** - Target specific user groups
✅ **Priority Levels** - Mark announcement importance
✅ **Scheduling** - Set start and end dates
✅ **Active Status** - Control announcement visibility

---

## 📁 Files Created/Modified

1. **Created:** `UpdateAnnouncementRequest.java` - DTO for update requests
2. **Modified:** `AdminService.java` - Added getAnnouncementById() and updateAnnouncement()
3. **Modified:** `AdminController.java` - Added GET by ID and PUT endpoints

---

## 🚀 Ready for Production

✅ All CRUD operations implemented
✅ Full validation in place
✅ Error handling configured
✅ Audit logging integrated
✅ API documentation complete
✅ No compilation errors

**Status: ✅ COMPLETE AND TESTED**

