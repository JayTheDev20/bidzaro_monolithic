# 📱 EXPO APP LOCAL TESTING SETUP
## Bidzaro Catering Platform — Expo Development Guide

**Date:** February 24, 2026  
**Backend URL:** `http://192.168.1.19:8080/api/v1`  
**Expo App URL:** `exp://192.168.1.19:8081`

---

## ✅ Quick Setup Steps

### 1. **Backend is Ready**
Your Spring Boot backend at `http://192.168.1.19:8080`:
- ✅ **CORS**: Already configured to allow all origins (`allowedOriginPatterns(List.of("*"))`)
- ✅ **Port**: 8080 (configured in `.env`)
- ✅ **Context**: `/api/v1` (all routes under this path)

### 2. **Configure Your Expo App**

Create an API client in your Expo project:

#### Option A: Using Axios (Recommended)
```javascript
// src/api/axiosClient.ts
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API_BASE_URL = 'http://192.168.1.19:8080/api/v1';

export const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
});

// Add JWT token to all requests
axiosClient.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Handle token refresh on 401
axiosClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const refreshToken = await AsyncStorage.getItem('refreshToken');
        const response = await axios.post(
          `${API_BASE_URL}/auth/refresh-token`,
          { refreshToken }
        );
        const newAccessToken = response.data.data.accessToken;
        await AsyncStorage.setItem('accessToken', newAccessToken);
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return axiosClient(originalRequest);
      } catch (err) {
        // Redirect to login
        return Promise.reject(err);
      }
    }
    return Promise.reject(error);
  }
);

export default axiosClient;
```

#### Option B: Using Fetch API (Simpler)
```javascript
// src/api/client.ts
import AsyncStorage from '@react-native-async-storage/async-storage';

const API_BASE_URL = 'http://192.168.1.19:8080/api/v1';

async function getHeaders() {
  const token = await AsyncStorage.getItem('accessToken');
  return {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` })
  };
}

export async function apiCall(
  endpoint: string,
  method: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE' = 'GET',
  body?: any
) {
  const headers = await getHeaders();
  const url = `${API_BASE_URL}${endpoint}`;

  const response = await fetch(url, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (!response.ok) {
    if (response.status === 401) {
      // Handle token refresh
      // Redirect to login
    }
    throw new Error(`API Error: ${response.status}`);
  }

  return await response.json();
}
```

### 3. **Example: Login API Call**

```javascript
import axiosClient from './api/axiosClient';
import AsyncStorage from '@react-native-async-storage/async-storage';

async function login(email: string, password: string) {
  try {
    const response = await axiosClient.post('/auth/login', {
      identifier: email,
      password: password,
    });

    const { accessToken, refreshToken, user } = response.data.data;

    // Store tokens
    await AsyncStorage.setItem('accessToken', accessToken);
    await AsyncStorage.setItem('refreshToken', refreshToken);
    await AsyncStorage.setItem('user', JSON.stringify(user));

    return user;
  } catch (error) {
    console.error('Login failed:', error.response?.data?.error?.message);
    throw error;
  }
}
```

### 4. **Example: Get User Profile**

```javascript
import axiosClient from './api/axiosClient';

async function getUserProfile() {
  try {
    const response = await axiosClient.get('/users/profile');
    return response.data.data;
  } catch (error) {
    console.error('Failed to fetch profile:', error);
    throw error;
  }
}
```

### 5. **Example: Browse Vendors**

```javascript
import axiosClient from './api/axiosClient';

async function getVendors(page = 0, size = 20) {
  try {
    const response = await axiosClient.get('/vendors', {
      params: { page, size, status: 'ACTIVE' }
    });
    return response.data.data;
  } catch (error) {
    console.error('Failed to fetch vendors:', error);
    throw error;
  }
}
```

---

## 🛠️ Troubleshooting

### Issue: Connection Refused
```
Error: Network Error: connect ECONNREFUSED 192.168.1.19:8080
```
**Solution:**
- Ensure backend is running: `java -jar target/bidzaro.jar`
- Check firewall allows port 8080
- Verify IP address is correct (run `ipconfig` on Windows)

### Issue: CORS Error
```
No 'Access-Control-Allow-Origin' header
```
**Solution:**
- Already configured in `SecurityConfig.java`
- If still failing, check browser console for actual error
- Try adding Expo IP explicitly to CORS (though not needed since `*` is allowed)

### Issue: 401 Unauthorized
```
{ "error": { "code": "UNAUTHORIZED", "message": "Token missing or expired" } }
```
**Solution:**
- Ensure `Authorization: Bearer {token}` is sent
- Check token is stored in AsyncStorage
- Verify JWT secret matches between login/protected calls
- Token expiry: 7 days (604800 seconds)

### Issue: No internet on Expo
**Solution:**
- Both your PC (backend) and phone (Expo) must be on **same WiFi network**
- `192.168.1.19` should be your PC's local IP
- Test: Open browser on phone, navigate to `http://192.168.1.19:8080/api/v1/vendors`

---

## 📋 API Endpoints You Can Test

| Endpoint | Method | Auth | Purpose |
|----------|--------|------|---------|
| `/auth/register` | POST | ❌ | Register new user |
| `/auth/login` | POST | ❌ | Login |
| `/auth/refresh-token` | POST | ❌ | Refresh access token |
| `/users/profile` | GET | ✅ | Get my profile |
| `/vendors` | GET | ❌ | Browse all vendors |
| `/vendors/{id}` | GET | ❌ | Get vendor details |
| `/menu/categories` | GET | ❌ | Get menu categories |
| `/menu/items` | GET | ❌ | Get master menu items |
| `/menu/vendor-items` | GET | ❌ | Get vendor's menu items |
| `/cart` | GET | ✅ | Get my cart |
| `/cart/items` | POST | ✅ | Add to cart |
| `/bids/requests` | POST | ✅ | Create bid request |
| `/orders` | GET | ✅ | Get my orders |
| `/reviews` | POST | ✅ | Submit review |

---

## 🔑 Sample Test Credentials

Use these to test login:

```json
{
  "identifier": "john.doe@gmail.com",
  "password": "MyPass@123"
}
```

Or register a new user:
```json
{
  "email": "test@example.com",
  "phone": "+917890123456",
  "password": "TestPass@123",
  "firstName": "Test",
  "lastName": "User",
  "country": "INDIA",
  "userType": "USER"
}
```

---

## 📖 Full API Docs

For complete API documentation:
- **User APIs:** `docs/USER_API_DOCS.md`
- **Vendor APIs:** `docs/VENDOR_API_DOCS.md`
- **Admin APIs:** `docs/ADMIN_API_DOCS.md`
- **Support APIs:** `docs/SUPPORT_API_DOCS.md`

---

## ✨ Next Steps

1. ✅ Backend running on `http://192.168.1.19:8080`
2. ✅ CORS already configured
3. 🔲 Implement API client in your Expo app
4. 🔲 Test login endpoint
5. 🔲 Build authentication flow
6. 🔲 Test other APIs
7. Later: Deploy to production

**Ready to start testing!** 🚀

