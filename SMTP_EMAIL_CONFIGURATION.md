# SMTP Email Configuration Guide

## Overview

The project has been updated to use **SMTP-based email** instead of SendGrid. This provides more flexibility and allows you to use any SMTP provider (Hostinger, Gmail, AWS SES, etc.).

## What Was Changed

### 1. Configuration Files Updated

#### `.env` File
- Replaced `SENDGRID_API_KEY` with proper SMTP credentials:
  ```env
  SMTP_HOST=smtp.hostinger.com
  SMTP_PORT=587
  SMTP_USERNAME=your_email@yourdomain.com
  SMTP_PASSWORD=your_smtp_password_here
  SMTP_FROM_EMAIL=noreply@cateringplatform.com
  SMTP_FROM_NAME=Catering Platform
  ```

#### `application.yml`
- Updated Spring Mail configuration to use environment variables:
  ```yaml
  spring:
    mail:
      host: ${SMTP_HOST:smtp.hostinger.com}
      port: ${SMTP_PORT:587}
      username: ${SMTP_USERNAME}
      password: ${SMTP_PASSWORD}
      properties:
        mail:
          smtp:
            auth: true
            starttls:
              enable: true
              required: true
            connectiontimeout: 5000
            timeout: 5000
            writetimeout: 5000
      default-encoding: UTF-8
  ```
- Replaced `sendgrid` section with `smtp` section for from-email and from-name

### 2. New EmailService Created

Created `src/main/java/com/cateringmarketplace/module/notification/service/EmailService.java` with the following features:

#### Core Methods
- `sendSimpleEmail()` - Sends plain text emails
- `sendHtmlEmail()` - Sends HTML formatted emails
- `sendEmailWithCopies()` - Sends emails with CC and BCC

#### Pre-configured Email Templates
1. **Welcome Email** - `sendWelcomeEmail()`
   - Sent when new users register
   - Welcomes them and lists platform features

2. **OTP Email** - `sendOTPEmail()`
   - Sends OTP codes for verification
   - Used for email verification, password reset, 2FA

3. **Password Reset Email** - `sendPasswordResetEmail()`
   - Sends password reset link with token

4. **Order Confirmation Email** - `sendOrderConfirmationEmail()`
   - Confirms order placement

5. **Bid Acceptance Email** - `sendBidAcceptanceEmail()`
   - Notifies when a bid is accepted

6. **Payment Confirmation Email** - `sendPaymentConfirmationEmail()`
   - Confirms successful payment

7. **Vendor Approval Email** - `sendVendorApprovalEmail()`
   - Notifies vendor when account is approved

8. **Vendor Rejection Email** - `sendVendorRejectionEmail()`
   - Notifies vendor when account is rejected with reason

9. **Generic Notification Email** - `sendNotificationEmail()`
   - For general purpose notifications

### 3. Service Integration

#### AuthService Updated
- Injects `EmailService`
- Sends welcome email after user registration
- Sends OTP via email for verification and password reset

#### VendorService Updated
- Injects `EmailService`
- Sends approval email when admin approves vendor
- Sends rejection email when admin rejects vendor with reason

#### NotificationService Updated
- Injects `EmailService`
- Uses `EmailService` instead of TODO stub for email notifications

## Setup Instructions

### Step 1: Configure SMTP Credentials

Edit `.env` file with your SMTP provider details:

#### For Hostinger SMTP:
```env
SMTP_HOST=smtp.hostinger.com
SMTP_PORT=587
SMTP_USERNAME=youremail@yourdomain.com
SMTP_PASSWORD=your_hostinger_email_password
SMTP_FROM_EMAIL=noreply@yourdomain.com
SMTP_FROM_NAME=Your Company Name
```

#### For Gmail SMTP:
```env
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=yourname@gmail.com
SMTP_PASSWORD=your_app_password  # Use App Password, not regular password
SMTP_FROM_EMAIL=yourname@gmail.com
SMTP_FROM_NAME=Your Company Name
```

#### For AWS SES:
```env
SMTP_HOST=email-smtp.us-east-1.amazonaws.com
SMTP_PORT=587
SMTP_USERNAME=your_aws_ses_smtp_username
SMTP_PASSWORD=your_aws_ses_smtp_password
SMTP_FROM_EMAIL=verified@yourdomain.com
SMTP_FROM_NAME=Your Company Name
```

#### For SendGrid SMTP (if you want to continue using SendGrid):
```env
SMTP_HOST=smtp.sendgrid.net
SMTP_PORT=587
SMTP_USERNAME=apikey
SMTP_PASSWORD=your_sendgrid_api_key
SMTP_FROM_EMAIL=noreply@yourdomain.com
SMTP_FROM_NAME=Your Company Name
```

### Step 2: Test Email Configuration

You can test the email configuration by:

1. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

2. **Register a new user** via the API:
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "email": "test@example.com",
       "phone": "+1234567890",
       "password": "TestPassword123",
       "firstName": "Test",
       "lastName": "User"
     }'
   ```

3. **Check your logs** for email sending confirmation:
   ```
   Sending simple email to: test@example.com
   Email sent successfully to: test@example.com
   ```

4. **Check the recipient's inbox** for the welcome email

### Step 3: Troubleshooting

#### Common Issues

1. **Authentication Failed**
   - **Gmail**: Enable "Less secure app access" or use App Password
   - **Hostinger**: Verify email account exists and password is correct
   - **AWS SES**: Verify domain/email and use SMTP credentials (not AWS access keys)

2. **Connection Timeout**
   - Check if port 587 is blocked by firewall
   - Try port 465 (SSL) instead of 587 (TLS)
   - Update `application.yml`:
     ```yaml
     port: ${SMTP_PORT:465}
     properties:
       mail:
         smtp:
           ssl:
             enable: true
           starttls:
             enable: false
     ```

3. **Emails Not Received**
   - Check spam/junk folder
   - Verify SMTP_FROM_EMAIL is correct
   - For production, verify domain SPF/DKIM records

4. **Enable Debug Logging**
   
   Add to `application.yml`:
   ```yaml
   logging:
     level:
       org.springframework.mail: DEBUG
   ```

## Email Features by Flow

### User Registration Flow
1. User registers → Welcome email sent
2. User requests email verification → OTP email sent
3. User verifies OTP → Account activated

### Password Reset Flow
1. User clicks "Forgot Password" → OTP email sent
2. User enters OTP → Can set new password

### Vendor Registration Flow
1. Vendor submits application → Vendor receives confirmation
2. Admin reviews → Approval or rejection email sent to vendor

### Order Flow
1. Customer places order → Order confirmation email
2. Payment made → Payment confirmation email
3. Bid accepted → Bid acceptance email to vendor

### Notification Flow
- All notifications can optionally be sent via email
- Email notification channel is integrated with NotificationService

## Email Templates

All email templates are currently plain text with a consistent format:
- Professional greeting
- Clear message body
- Call-to-action or important information
- Professional sign-off with "The Catering Platform Team"

### Future Enhancements

To add HTML email templates:

1. Add Thymeleaf dependency to `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-thymeleaf</artifactId>
   </dependency>
   ```

2. Create HTML templates in `src/main/resources/templates/emails/`

3. Use `sendHtmlEmail()` method with template engine

## Security Best Practices

### For Production Deployment

1. **Never commit `.env` with real credentials**
   - Add `.env` to `.gitignore`
   - Use `.env.example` as template

2. **Use environment variables or secrets manager**
   - AWS Parameter Store
   - HashiCorp Vault
   - Kubernetes Secrets
   - Azure Key Vault

3. **Verify sender domain**
   - Configure SPF records
   - Configure DKIM signing
   - Configure DMARC policy

4. **Rate limiting**
   - Already configured via `@Async("emailExecutor")` with thread pool
   - Current pool: 2-5 threads, queue capacity: 100

5. **Monitor email sending**
   - Check logs for failures
   - Set up alerts for high failure rates
   - Track bounce rates

## API Examples

### Send Welcome Email (Automatic on Registration)
```bash
POST /api/v1/auth/register
{
  "email": "newuser@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "password": "SecurePass123"
}
```
→ Welcome email automatically sent

### Send OTP Email (Automatic on OTP Request)
```bash
POST /api/v1/auth/send-otp
{
  "identifier": "user@example.com",
  "type": "EMAIL"
}
```
→ OTP email sent with verification code

### Vendor Approval (Admin Only)
```bash
POST /api/v1/admin/vendors/{vendorId}/approve
Authorization: Bearer {admin_token}
```
→ Approval email sent to vendor

### Vendor Rejection (Admin Only)
```bash
POST /api/v1/admin/vendors/{vendorId}/reject?reason=Incomplete%20documents
Authorization: Bearer {admin_token}
```
→ Rejection email sent with reason

## Monitoring and Logs

Email sending is logged at INFO level:
```
2026-01-09 12:00:00 - Sending simple email to: user@example.com
2026-01-09 12:00:01 - Email sent successfully to: user@example.com
```

Failed emails are logged at ERROR level:
```
2026-01-09 12:00:00 - Failed to send email to user@example.com: Authentication failed
```

## Support

For issues or questions:
1. Check logs in `logs/` directory
2. Review SMTP provider documentation
3. Test SMTP credentials using a mail client (Thunderbird, etc.)
4. Contact your SMTP provider support

---

**Last Updated:** January 9, 2026
**Version:** 1.0.0

