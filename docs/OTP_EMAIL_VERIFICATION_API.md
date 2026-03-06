# 🔐 OTP Email Verification API
**Bidzaro Catering Platform** | Base URL: `http://localhost:8080/api/v1/auth`

> All endpoints are **PUBLIC** (no authentication required for OTP operations).
> ✅ = Required field | ⬜ = Optional field

---

## 📋 Table of Contents
1. [Enums Reference](#-enums-reference)
2. [Step 1 — Send OTP](#1️⃣-send-otp-request-email-verification-code)
3. [Step 2 — Verify OTP](#2️⃣-verify-otp-submit-code)
4. [Error Reference](#-error-reference)
5. [Complete Flow Example](#-complete-verification-flow-example)
6. [Dev Mode Fallback](#-dev-mode-fallback-otp-logging)

---

## 🔢 Enums Reference

### VerificationType
Specifies the purpose of the OTP. Used in both send and verify endpoints.

| Value | Description | Use Case |
|-------|-------------|----------|
| `EMAIL` | Email address verification | User signup, email change |
| `PHONE` | Phone number verification | Phone signup, phone change |
| `PASSWORD_RESET` | Password reset flow | Forgot password |
| `TWO_FACTOR` | Two-factor authentication | Login 2FA |
| `BUSINESS_EMAIL` | Business email verification | Vendor email verification |
| `BUSINESS_PHONE` | Business phone verification | Vendor phone verification |

### Channel (Optional, for phone only)
Specifies how to deliver the OTP for phone-based requests.

| Value | Description | Use Case |
|-------|-------------|----------|
| `WHATSAPP` | Send via WhatsApp only | WhatsApp-preferred users |
| `SMS` | Send via SMS only | SMS-preferred users |
| `AUTO` | Try WhatsApp first, fallback to SMS | Default (recommended) |

---

## 1️⃣ Send OTP — Request Email Verification Code

Generate and send a One-Time Password (OTP) to the user's email or phone.

**`POST /api/v1/auth/send-otp`**

### Request Body

```json
{
  "identifier":  "string",  // ✅ Required – email or phone number
  "type":        "string",  // ✅ Required – VerificationType enum
  "channel":     "string"   // ⬜ Optional – Channel enum (phone only; default: AUTO)
}
```

### Field Validation

| Field | Type | Required | Validation | Example |
|-------|------|----------|------------|---------|
| `identifier` | `String` | ✅ Yes | Valid email OR 10-digit phone (no spaces/dashes) | `user@example.com` or `9876543210` |
| `type` | `String` | ✅ Yes | One of: `EMAIL`, `PHONE`, `PASSWORD_RESET`, `TWO_FACTOR`, `BUSINESS_EMAIL`, `BUSINESS_PHONE` | `EMAIL` |
| `channel` | `String` | ⬜ No | One of: `WHATSAPP`, `SMS`, `AUTO` (ignored if identifier is email) | `WHATSAPP` |

### Request Examples

#### Email OTP (Signup Verification)
```json
{
  "identifier": "dhanunjay@hiresoftsolutions.com",
  "type": "EMAIL"
}
```

#### Email OTP (Password Reset)
```json
{
  "identifier": "dhanunjay@hiresoftsolutions.com",
  "type": "PASSWORD_RESET"
}
```

#### Phone OTP (WhatsApp Channel)
```json
{
  "identifier": "919876543210",
  "type": "PHONE",
  "channel": "WHATSAPP"
}
```

#### Phone OTP (Auto Channel — Twilio required)
```json
{
  "identifier": "919876543210",
  "type": "PHONE",
  "channel": "AUTO"
}
```

### Response — 200 OK

```json
{
  "success": true,
  "status": 200,
  "message": "OTP sent successfully",
  "data": {
    "verificationId": "78b9d282-bb3c-4847-a9d2-a5ba7575fc5b",
    "expiresIn": 600
  },
  "error": null,
  "timestamp": "2026-03-06T18:46:34.663Z"
}
```

### Response Field Reference

| Field | Type | Description |
|-------|------|-------------|
| `verificationId` | `String` | Unique ID for this OTP verification session. Must be passed to `/verify-otp` |
| `expiresIn` | `Integer` | OTP validity duration in **seconds** (typically 600 = 10 minutes) |

### Error Responses

| Status | Code | Message | Cause |
|--------|------|---------|-------|
| 400 | `BAD_REQUEST` | `"Invalid email format"` | `identifier` is not a valid email address |
| 400 | `BAD_REQUEST` | `"Invalid phone number"` | `identifier` is not a valid phone number |
| 400 | `BAD_REQUEST` | `"Invalid verification type"` | `type` enum value is invalid |
| 400 | `BAD_REQUEST` | `"Invalid channel"` | `channel` enum value is invalid (phone only) |
| 429 | `RATE_LIMITED` | `"Too many OTP requests. Try again later"` | User requested OTP too many times in short period |
| 500 | `SERVER_ERROR` | `"Failed to send OTP"` | SMTP/Twilio service failure (OTP still saved, see Dev Fallback) |

#### 400 Error Response Example
```json
{
  "success": false,
  "status": 400,
  "message": "Invalid email format",
  "data": null,
  "error": {
    "status": 400,
    "message": "Invalid email format",
    "path": "/api/v1/auth/send-otp",
    "timestamp": "2026-03-06T18:46:34Z"
  }
}
```

---

## 2️⃣ Verify OTP — Submit Code

Validate the OTP code provided by the user.

**`POST /api/v1/auth/verify-otp`**

### Request Body

```json
{
  "verificationId": "string",  // ✅ Required – from send-otp response
  "type":           "string",  // ✅ Required – VerificationType enum (must match send-otp)
  "identifier":     "string",  // ✅ Required – email or phone (must match send-otp)
  "otp":            "string"   // ✅ Required – 6-digit OTP code (no spaces)
}
```

### Field Validation

| Field | Type | Required | Validation | Example |
|-------|------|----------|------------|---------|
| `verificationId` | `String` | ✅ Yes | Must match the `verificationId` from send-otp response | `78b9d282-bb3c-4847-a9d2-a5ba7575fc5b` |
| `type` | `String` | ✅ Yes | Must match the `type` from send-otp request | `EMAIL` |
| `identifier` | `String` | ✅ Yes | Must match the `identifier` from send-otp request | `dhanunjay@hiresoftsolutions.com` |
| `otp` | `String` | ✅ Yes | 6 digits only; no spaces or special characters | `123456` |

### Request Example

```json
{
  "verificationId": "78b9d282-bb3c-4847-a9d2-a5ba7575fc5b",
  "type": "EMAIL",
  "identifier": "dhanunjay@hiresoftsolutions.com",
  "otp": "123456"
}
```

### Response — 200 OK (Verification Successful)

```json
{
  "success": true,
  "status": 200,
  "message": "OTP verified successfully",
  "data": {
    "verificationId": "78b9d282-bb3c-4847-a9d2-a5ba7575fc5b",
    "verified": true,
    "identifier": "dhanunjay@hiresoftsolutions.com",
    "type": "EMAIL"
  },
  "error": null,
  "timestamp": "2026-03-06T18:46:45.123Z"
}
```

### Response Field Reference

| Field | Type | Description |
|-------|------|-------------|
| `verificationId` | `String` | Same verification session ID |
| `verified` | `Boolean` | Always `true` on 200 status |
| `identifier` | `String` | The email/phone that was verified |
| `type` | `String` | Verification type (EMAIL, PHONE, etc.) |

### Error Responses

| Status | Code | Message | Cause |
|--------|------|---------|-------|
| 400 | `OTP_NOT_FOUND` | `"No valid OTP found"` | `verificationId` doesn't exist or OTP expired (>10 min old) |
| 400 | `OTP_EXPIRED` | `"OTP has expired or exceeded maximum attempts"` | OTP is too old OR user exceeded max attempts (typically 5) |
| 400 | `OTP_MISMATCH` | `"Invalid OTP"` | Entered OTP doesn't match saved hash |
| 400 | `MAX_ATTEMPTS_EXCEEDED` | `"Maximum OTP attempts exceeded. Request a new OTP"` | User tried wrong OTP too many times |
| 400 | `VERIFICATION_MISMATCH` | `"Verification details do not match"` | `identifier` or `type` doesn't match the send-otp request |
| 429 | `RATE_LIMITED` | `"Too many verification attempts. Try again later"` | User tried to verify too many times in short period |
| 500 | `SERVER_ERROR` | `"Failed to verify OTP"` | Database or internal service error |

#### 400 Error Response Example (OTP Mismatch)
```json
{
  "success": false,
  "status": 400,
  "message": "Invalid OTP",
  "data": null,
  "error": {
    "status": 400,
    "code": "OTP_MISMATCH",
    "message": "Invalid OTP",
    "path": "/api/v1/auth/verify-otp",
    "timestamp": "2026-03-06T18:46:45Z"
  }
}
```

#### 400 Error Response Example (OTP Expired)
```json
{
  "success": false,
  "status": 400,
  "message": "OTP has expired or exceeded maximum attempts",
  "data": null,
  "error": {
    "status": 400,
    "code": "OTP_EXPIRED",
    "message": "OTP has expired or exceeded maximum attempts",
    "path": "/api/v1/auth/verify-otp",
    "timestamp": "2026-03-06T18:46:45Z"
  }
}
```

---

## ❌ Error Reference

### Standard Error Response Format
```json
{
  "success": false,
  "status": 400,
  "message": "Human-readable error message",
  "data": null,
  "error": {
    "status": 400,
    "code": "ERROR_CODE",
    "message": "Human-readable error message",
    "path": "/api/v1/auth/send-otp",
    "timestamp": "2026-03-06T18:46:34Z"
  }
}
```

### HTTP Status Codes

| Status | Scenario |
|--------|----------|
| `200` | OTP sent successfully OR OTP verified successfully |
| `400` | Validation failed, OTP expired, OTP mismatch, or verification details don't match |
| `429` | Too many requests (rate limiting) |
| `500` | Server error (SMTP failure, database error, etc.) |

---

## 🔄 Complete Verification Flow Example

### Scenario: User signing up and verifying email

**Step 1 — User enters email and requests OTP**

```
POST /api/v1/auth/send-otp
Content-Type: application/json

{
  "identifier": "newuser@example.com",
  "type": "EMAIL"
}
```

**Step 1 Response**
```json
{
  "success": true,
  "status": 200,
  "message": "OTP sent successfully",
  "data": {
    "verificationId": "abc123def456",
    "expiresIn": 600
  }
}
```

**Step 2 — User receives email with OTP (e.g., `123456`), enters it in the app**

```
POST /api/v1/auth/verify-otp
Content-Type: application/json

{
  "verificationId": "abc123def456",
  "type": "EMAIL",
  "identifier": "newuser@example.com",
  "otp": "123456"
}
```

**Step 2 Response**
```json
{
  "success": true,
  "status": 200,
  "message": "OTP verified successfully",
  "data": {
    "verificationId": "abc123def456",
    "verified": true,
    "identifier": "newuser@example.com",
    "type": "EMAIL"
  }
}
```

**Step 3 — Frontend can now proceed with signup (email is verified)**

---

## 🛠️ Dev Mode Fallback — OTP Logging

### Problem
If SMTP email service is disabled (e.g., hPanel blocks the account), users can't receive OTP emails. The API still returns 200 (OTP saved), but **the user never receives the email**.

### Solution
Enable dev-mode OTP logging in `application.yml` to print OTPs to the console.

### Configuration

**`application.yml`** — Enable for local/dev environments ONLY:
```yaml
app:
  otp:
    log-plain: true  # Set to false in production!
```

### Dev Mode Behavior

When `app.otp.log-plain=true`:

1. **Email OTP Request**
```
POST /api/v1/auth/send-otp
{ "identifier": "user@example.com", "type": "EMAIL" }
```

**Console Output** (appears in application logs):
```
2026-03-06 18:46:31 WARN [http-nio-8080-exec-4]
[DEV-OTP] Plain OTP for user@example.com: 123456 (expires in 10 minutes).
Set app.otp.log-plain=false in production.
```

2. **Dev Fallback for Failed Email Delivery**

If SMTP fails (e.g., `554 5.7.1 Disabled by user from hPanel`):

**Console Output**:
```
2026-03-06 18:46:34 WARN [http-nio-8080-exec-4]
⚠️ Failed to send email to user@example.com: MailSendException | Cause: Failed messages:
org.eclipse.angus.mail.smtp.SMTPSendFailedException: 554 5.7.1 Disabled by user from hPanel

2026-03-06 18:46:34 WARN [http-nio-8080-exec-4]
[DEV-EMAIL-FALLBACK] SMTP failed — printing email to console
TO      : user@example.com
SUBJECT : Your OTP Code - Catering Platform
BODY    : Hello, We received a request for Email Verification.
Please use the following One-Time Password (OTP) to proceed:
123456
This code is valid for 10 minutes. Do not share this code with anyone.
If you didn't request this code, please ignore this email.
============================================================
```

### Using Dev OTPs in Testing

1. Make OTP request → check console for `[DEV-OTP]` message
2. Copy the OTP code from logs
3. Submit via verify-otp endpoint
4. Verification succeeds (OTP was saved even if email failed)

### ⚠️ Production Safety

- **Always set `app.otp.log-plain=false` in production** — OTPs in logs = security risk
- **Always set `smtp.dev-fallback-log=false` in production** — email content in logs = privacy risk
- **Fix SMTP configuration** — use Gmail, AWS SES, SendGrid, etc. instead of hPanel

---

## 🌍 Complete API Summary

| Endpoint | Method | Auth | Purpose |
|----------|--------|------|---------|
| `/api/v1/auth/send-otp` | `POST` | ❌ Public | Send OTP to email/phone |
| `/api/v1/auth/verify-otp` | `POST` | ❌ Public | Verify submitted OTP code |

### Typical Integration Flow

```
User Signup/Login
  ↓
Send OTP (/send-otp)
  ↓
Receive OTP via Email/SMS
  ↓
User enters OTP in UI
  ↓
Verify OTP (/verify-otp)
  ↓
Verification successful
  ↓
Proceed with Registration/Password Reset
```

---

**Version:** 1.0 | **Last Updated:** March 6, 2026 | **Status:** ✅ Production Ready

