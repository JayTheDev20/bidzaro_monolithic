# Environment Setup Guide

## Overview
This application uses a `.env` file to store sensitive configuration values like API keys, database credentials, and secrets.

## Quick Start

### 1. Copy the example file
```bash
cp .env.example .env
```

### 2. Update the values in `.env` with your actual credentials

### 3. Never commit `.env` to version control
The `.env` file is already added to `.gitignore` to prevent accidental commits.

---

## Environment Variables

### Database Configuration

| Variable | Description | Example |
|----------|-------------|---------|
| `MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017/catering_platform_db` |
| `REDIS_HOST` | Redis server host | `localhost` |
| `REDIS_PORT` | Redis server port | `6379` |
| `REDIS_PASSWORD` | Redis password (if any) | `your_redis_password` |

### JWT Configuration

| Variable | Description | Example |
|----------|-------------|---------|
| `JWT_SECRET` | Secret key for JWT signing | `your_very_long_random_secret_at_least_256_bits` |

**⚠️ IMPORTANT:** Generate a strong random secret for production:
```bash
# Generate a secure random secret
openssl rand -base64 64
```

### Payment Gateway (Razorpay)

| Variable | Description | Where to get |
|----------|-------------|--------------|
| `RAZORPAY_KEY_ID` | Razorpay API Key ID | [Dashboard](https://dashboard.razorpay.com/app/keys) |
| `RAZORPAY_KEY_SECRET` | Razorpay API Secret | [Dashboard](https://dashboard.razorpay.com/app/keys) |
| `RAZORPAY_WEBHOOK_SECRET` | Webhook signature secret | [Webhooks](https://dashboard.razorpay.com/app/webhooks) |

### Email Service (SendGrid)

| Variable | Description | Where to get |
|----------|-------------|--------------|
| `SENDGRID_API_KEY` | SendGrid API key | [API Keys](https://app.sendgrid.com/settings/api_keys) |
| `SENDGRID_FROM_EMAIL` | Sender email address | Your verified sender |
| `SENDGRID_FROM_NAME` | Sender name | `Catering Platform` |

### SMS Service (Twilio)

| Variable | Description | Where to get |
|----------|-------------|--------------|
| `TWILIO_ACCOUNT_SID` | Twilio Account SID | [Console](https://console.twilio.com/) |
| `TWILIO_AUTH_TOKEN` | Twilio Auth Token | [Console](https://console.twilio.com/) |
| `TWILIO_PHONE_NUMBER` | Twilio phone number | [Phone Numbers](https://console.twilio.com/us1/develop/phone-numbers/manage/incoming) |
| `TWILIO_WHATSAPP_NUMBER` | WhatsApp number | [WhatsApp](https://console.twilio.com/us1/develop/sms/try-it-out/whatsapp-learn) |

### Firebase (Push Notifications)

| Variable | Description | Where to get |
|----------|-------------|--------------|
| `FIREBASE_CREDENTIALS_PATH` | Path to service account JSON | [Firebase Console](https://console.firebase.google.com/) → Project Settings → Service Accounts |
| `FIREBASE_PROJECT_ID` | Firebase project ID | Firebase Console |

**Setup Firebase:**
1. Go to Firebase Console
2. Select your project
3. Project Settings → Service Accounts
4. Click "Generate New Private Key"
5. Save the JSON file as `firebase-credentials.json` in the project root

### Google Cloud Storage

| Variable | Description | Where to get |
|----------|-------------|--------------|
| `GCP_PROJECT_ID` | GCP Project ID | [Cloud Console](https://console.cloud.google.com/) |
| `GCP_STORAGE_BUCKET` | Bucket name | [Storage Browser](https://console.cloud.google.com/storage/browser) |
| `GCP_CREDENTIALS_PATH` | Path to service account JSON | [IAM & Admin](https://console.cloud.google.com/iam-admin/serviceaccounts) |

**Setup GCP:**
1. Create a project in Google Cloud Console
2. Enable Cloud Storage API
3. Create a service account
4. Download the JSON key file as `gcp-credentials.json`
5. Create a storage bucket

### Application Configuration

| Variable | Description | Example |
|----------|-------------|---------|
| `FRONTEND_URL` | Frontend application URL | `http://localhost:3000` |
| `CORS_ORIGINS` | Allowed CORS origins (comma-separated) | `http://localhost:3000,http://localhost:8080` |
| `UPLOAD_PATH` | Local file upload directory | `./uploads` |
| `UPLOAD_BASE_URL` | Base URL for uploaded files | `http://localhost:8080/uploads` |
| `SERVER_PORT` | Server port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` or `prod` |

---

## Environment Profiles

### Development (.env)
```env
SPRING_PROFILES_ACTIVE=dev
MONGODB_URI=mongodb://localhost:27017/catering_platform_db
FRONTEND_URL=http://localhost:3000
```

### Production (.env.production)
```env
SPRING_PROFILES_ACTIVE=prod
MONGODB_URI=mongodb+srv://user:password@cluster.mongodb.net/catering_platform_db
FRONTEND_URL=https://yourdomain.com
```

---

## Security Best Practices

### ✅ DO:
- ✓ Use `.env` for all secrets and credentials
- ✓ Add `.env` to `.gitignore`
- ✓ Commit `.env.example` with dummy values
- ✓ Use different values for dev/staging/prod
- ✓ Generate strong random secrets for JWT
- ✓ Rotate secrets regularly
- ✓ Use environment-specific `.env` files

### ❌ DON'T:
- ✗ Commit `.env` to version control
- ✗ Use the same secrets across environments
- ✗ Share `.env` file via email/chat
- ✗ Use weak or predictable secrets
- ✗ Include secrets in application.yml
- ✗ Log sensitive environment variables

---

## Troubleshooting

### .env file not loading
1. Make sure `.env` is in the project root directory
2. Check that `dotenv-java` dependency is in pom.xml
3. Verify `spring.factories` is in `src/main/resources/META-INF/`
4. Restart the application

### Environment variable not found
1. Check the variable name matches exactly (case-sensitive)
2. Ensure no spaces around `=` in `.env` file
3. Restart the application after changing `.env`

### Using system environment variables
The application will use system environment variables if they exist, 
overriding values from `.env` file. This is useful for production deployments.

---

## Deployment

### Docker
When deploying with Docker, pass environment variables using:
```bash
docker run -p 8080:8080 --env-file .env catering-platform
```

### Kubernetes
Create a ConfigMap and Secret:
```bash
kubectl create secret generic app-secrets --from-env-file=.env
kubectl create configmap app-config --from-env-file=.env
```

### Cloud Platforms

#### Heroku
```bash
heroku config:set MONGODB_URI=your_mongodb_uri
heroku config:set JWT_SECRET=your_jwt_secret
```

#### AWS Elastic Beanstalk
Add environment variables in EB Console or `.ebextensions/environment.config`

#### Google Cloud Run
```bash
gcloud run deploy --set-env-vars="MONGODB_URI=your_uri,JWT_SECRET=your_secret"
```

---

## Testing

For testing, you can create a `.env.test` file:
```env
MONGODB_URI=mongodb://localhost:27017/catering_platform_test_db
JWT_SECRET=test_secret_key_for_testing_only
SPRING_PROFILES_ACTIVE=test
```

---

## Support

If you encounter issues with environment setup:
1. Check this guide
2. Review `.env.example` for reference
3. Ensure all required services (MongoDB, Redis) are running
4. Check application logs for specific errors

