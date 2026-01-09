# Bidzaro - Complete Mobile App Development Guide
## User-Focused Catering Platform | Expo + React Native + NativeWind

> **Production-Ready Mobile Application Development Specification**  
> **Primary Color:** Orange (#FF6B35, #FF8C42, #FFA500)  
> **Project:** Bidzaro - Catering Marketplace  
> **Platform:** iOS & Android  
> **Tech Stack:** Expo, React Native, TypeScript, NativeWind (Tailwind CSS), Redux Toolkit

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Technical Architecture](#technical-architecture)
3. [Setup & Installation](#setup--installation)
4. [Design System](#design-system)
5. [Feature Modules](#feature-modules)
6. [API Integration](#api-integration)
7. [State Management](#state-management)
8. [Navigation Structure](#navigation-structure)
9. [Component Library](#component-library)
10. [Implementation Checklist](#implementation-checklist)

---

## 🎯 Project Overview

### Application Purpose
Bidzaro is a comprehensive catering marketplace mobile application that connects users with catering vendors. Users can browse menus, create bid requests for events, manage orders, earn loyalty points, and track their complete catering experience.

### Target Users
- **Primary:** Event organizers, individuals planning weddings, corporate events, parties
- **Demographics:** 25-55 years old, tech-savvy, middle to upper-middle class
- **Platforms:** iOS 13+, Android 8.0+

### Core Value Propositions
1. **Competitive Bidding:** Get best prices through vendor bidding
2. **Comprehensive Menu:** Browse extensive catering options
3. **Easy Management:** Track orders, payments, and events in one place
4. **Rewards Program:** Earn loyalty points on every order
5. **Personalization:** Saved addresses, wishlists, preferences

---

## 🏗️ Technical Architecture

### Technology Stack

```typescript
// Core Technologies
- Framework: Expo SDK 50+
- Language: TypeScript 5.x
- UI Library: React Native 0.73+
- Styling: NativeWind (Tailwind CSS for React Native)
- State Management: Redux Toolkit + RTK Query
- Navigation: React Navigation 6.x
- Forms: React Hook Form + Zod
- API Client: Axios with interceptors
- Image Handling: Expo Image, Image Picker
- Notifications: Expo Notifications, FCM
- Storage: AsyncStorage, Secure Store
- Maps: React Native Maps
- Payments: Razorpay SDK
- Analytics: Expo Analytics / Firebase Analytics
```

### Project Structure

```
bidzaro-mobile/
├── app/                          # Expo Router (app directory)
│   ├── (auth)/                   # Auth group
│   │   ├── login.tsx
│   │   ├── register.tsx
│   │   └── forgot-password.tsx
│   ├── (tabs)/                   # Main app tabs
│   │   ├── index.tsx             # Home
│   │   ├── explore.tsx           # Browse vendors
│   │   ├── bids.tsx              # My bid requests
│   │   ├── orders.tsx            # My orders
│   │   └── profile.tsx           # User profile
│   ├── vendor/
│   │   ├── [id].tsx              # Vendor details
│   │   └── menu/[id].tsx         # Menu item details
│   ├── order/
│   │   └── [id].tsx              # Order details
│   ├── bid/
│   │   ├── create.tsx            # Create bid request
│   │   └── [id].tsx              # Bid details
│   ├── checkout/
│   │   └── index.tsx             # Checkout flow
│   ├── _layout.tsx               # Root layout
│   └── +not-found.tsx
├── src/
│   ├── components/               # Reusable components
│   │   ├── ui/                   # Base UI components
│   │   │   ├── Button.tsx
│   │   │   ├── Input.tsx
│   │   │   ├── Card.tsx
│   │   │   ├── Badge.tsx
│   │   │   ├── Avatar.tsx
│   │   │   ├── Tabs.tsx
│   │   │   ├── Modal.tsx
│   │   │   └── LoadingSpinner.tsx
│   │   ├── auth/                 # Auth components
│   │   │   ├── LoginForm.tsx
│   │   │   └── RegisterForm.tsx
│   │   ├── vendor/               # Vendor components
│   │   │   ├── VendorCard.tsx
│   │   │   ├── VendorList.tsx
│   │   │   └── MenuItemCard.tsx
│   │   ├── order/                # Order components
│   │   │   ├── OrderCard.tsx
│   │   │   ├── OrderTimeline.tsx
│   │   │   └── OrderSummary.tsx
│   │   ├── bid/                  # Bid components
│   │   │   ├── BidRequestCard.tsx
│   │   │   ├── BidCard.tsx
│   │   │   └── CreateBidForm.tsx
│   │   ├── cart/                 # Cart components
│   │   │   ├── CartItem.tsx
│   │   │   └── CartSummary.tsx
│   │   ├── profile/              # Profile components
│   │   │   ├── ProfileHeader.tsx
│   │   │   ├── AddressCard.tsx
│   │   │   └── LoyaltyCard.tsx
│   │   └── shared/               # Shared components
│   │       ├── Header.tsx
│   │       ├── SearchBar.tsx
│   │       ├── FilterSheet.tsx
│   │       └── EmptyState.tsx
│   ├── store/                    # Redux store
│   │   ├── index.ts              # Store configuration
│   │   ├── slices/
│   │   │   ├── authSlice.ts
│   │   │   ├── userSlice.ts
│   │   │   ├── cartSlice.ts
│   │   │   ├── wishlistSlice.ts
│   │   │   └── appSlice.ts
│   │   └── api/                  # RTK Query APIs
│   │       ├── authApi.ts
│   │       ├── userApi.ts
│   │       ├── vendorApi.ts
│   │       ├── bidApi.ts
│   │       ├── orderApi.ts
│   │       ├── cartApi.ts
│   │       ├── wishlistApi.ts
│   │       ├── notificationApi.ts
│   │       ├── loyaltyApi.ts
│   │       └── referralApi.ts
│   ├── services/                 # Service layer
│   │   ├── api.ts                # Axios configuration
│   │   ├── storage.ts            # AsyncStorage wrapper
│   │   ├── notifications.ts      # Push notifications
│   │   ├── location.ts           # Location services
│   │   └── analytics.ts          # Analytics tracking
│   ├── hooks/                    # Custom hooks
│   │   ├── useAuth.ts
│   │   ├── useCart.ts
│   │   ├── useLocation.ts
│   │   ├── useDebounce.ts
│   │   └── useAppState.ts
│   ├── utils/                    # Utility functions
│   │   ├── validation.ts
│   │   ├── formatters.ts
│   │   ├── dateUtils.ts
│   │   └── constants.ts
│   ├── types/                    # TypeScript types
│   │   ├── auth.types.ts
│   │   ├── user.types.ts
│   │   ├── vendor.types.ts
│   │   ├── order.types.ts
│   │   ├── bid.types.ts
│   │   └── api.types.ts
│   └── config/                   # Configuration
│       ├── theme.ts              # Theme configuration
│       ├── env.ts                # Environment variables
│       └── navigation.ts         # Navigation config
├── assets/
│   ├── images/
│   ├── icons/
│   └── fonts/
├── tailwind.config.js            # NativeWind configuration
├── app.json                      # Expo configuration
├── package.json
└── tsconfig.json
```

---

## 🎨 Design System

### Color Palette (Orange-Centric)

```typescript
// src/config/theme.ts

export const colors = {
  // Primary Orange Shades
  primary: {
    50: '#FFF5F0',
    100: '#FFE8DB',
    200: '#FFD1B8',
    300: '#FFB894',
    400: '#FFA071',
    500: '#FF8C42',  // Main Orange
    600: '#FF6B35',  // Dark Orange
    700: '#E85A28',
    800: '#CC4A1D',
    900: '#A63B15',
  },
  
  // Accent Colors
  accent: {
    gold: '#FFD700',
    peach: '#FFCBA4',
    coral: '#FF7F50',
  },
  
  // Neutral Colors
  neutral: {
    50: '#F9FAFB',
    100: '#F3F4F6',
    200: '#E5E7EB',
    300: '#D1D5DB',
    400: '#9CA3AF',
    500: '#6B7280',
    600: '#4B5563',
    700: '#374151',
    800: '#1F2937',
    900: '#111827',
  },
  
  // Semantic Colors
  success: {
    light: '#D1FAE5',
    main: '#10B981',
    dark: '#065F46',
  },
  error: {
    light: '#FEE2E2',
    main: '#EF4444',
    dark: '#991B1B',
  },
  warning: {
    light: '#FEF3C7',
    main: '#F59E0B',
    dark: '#92400E',
  },
  info: {
    light: '#DBEAFE',
    main: '#3B82F6',
    dark: '#1E40AF',
  },
  
  // Background
  background: {
    primary: '#FFFFFF',
    secondary: '#F9FAFB',
    tertiary: '#F3F4F6',
  },
  
  // Text
  text: {
    primary: '#111827',
    secondary: '#4B5563',
    tertiary: '#9CA3AF',
    inverse: '#FFFFFF',
  },
  
  // Border
  border: {
    light: '#E5E7EB',
    main: '#D1D5DB',
    dark: '#9CA3AF',
  },
};

export const spacing = {
  xs: 4,
  sm: 8,
  md: 16,
  lg: 24,
  xl: 32,
  '2xl': 48,
  '3xl': 64,
};

export const borderRadius = {
  sm: 4,
  md: 8,
  lg: 12,
  xl: 16,
  '2xl': 24,
  full: 9999,
};

export const typography = {
  fontFamily: {
    regular: 'Inter-Regular',
    medium: 'Inter-Medium',
    semibold: 'Inter-SemiBold',
    bold: 'Inter-Bold',
  },
  fontSize: {
    xs: 12,
    sm: 14,
    base: 16,
    lg: 18,
    xl: 20,
    '2xl': 24,
    '3xl': 30,
    '4xl': 36,
  },
};

export const shadows = {
  sm: {
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 2,
    elevation: 2,
  },
  md: {
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 4,
  },
  lg: {
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 8,
    elevation: 8,
  },
};
```

### NativeWind Configuration

```javascript
// tailwind.config.js

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './app/**/*.{js,jsx,ts,tsx}',
    './src/**/*.{js,jsx,ts,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#FFF5F0',
          100: '#FFE8DB',
          200: '#FFD1B8',
          300: '#FFB894',
          400: '#FFA071',
          500: '#FF8C42',
          600: '#FF6B35',
          700: '#E85A28',
          800: '#CC4A1D',
          900: '#A63B15',
        },
        orange: {
          DEFAULT: '#FF8C42',
          light: '#FFA071',
          dark: '#FF6B35',
        },
      },
      fontFamily: {
        regular: ['Inter-Regular'],
        medium: ['Inter-Medium'],
        semibold: ['Inter-SemiBold'],
        bold: ['Inter-Bold'],
      },
    },
  },
  plugins: [],
};
```

---

## 📦 Feature Modules

### 1. Authentication Module

#### Screens
- **Login Screen** (`app/(auth)/login.tsx`)
- **Registration Screen** (`app/(auth)/register.tsx`)
- **Forgot Password** (`app/(auth)/forgot-password.tsx`)
- **OTP Verification** (`app/(auth)/verify-otp.tsx`)

#### Features
- Email/Phone login
- Social authentication (Google, Apple)
- Biometric login (Face ID, Touch ID)
- Password reset flow
- Email/Phone verification
- Referral code during signup

#### API Endpoints
```typescript
POST /auth/register
POST /auth/login
POST /auth/refresh-token
POST /auth/logout
POST /auth/send-otp
POST /auth/verify-otp
POST /auth/forgot-password
POST /auth/reset-password
GET  /auth/me
```

#### Implementation Example

```typescript
// src/components/auth/LoginForm.tsx
import React, { useState } from 'react';
import { View, Text, TouchableOpacity, Alert } from 'react-native';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useLoginMutation } from '@/store/api/authApi';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { useRouter } from 'expo-router';

const loginSchema = z.object({
  identifier: z.string().min(1, 'Email or phone required'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginFormData = z.infer<typeof loginSchema>;

export const LoginForm = () => {
  const router = useRouter();
  const [login, { isLoading }] = useLoginMutation();
  const { control, handleSubmit, formState: { errors } } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      await login(data).unwrap();
      router.replace('/(tabs)');
    } catch (error: any) {
      Alert.alert('Login Failed', error?.data?.message || 'Something went wrong');
    }
  };

  return (
    <View className="flex-1 bg-white px-6 justify-center">
      {/* Logo */}
      <View className="items-center mb-12">
        <Text className="text-4xl font-bold text-primary-600">Bidzaro</Text>
        <Text className="text-neutral-500 mt-2">Find your perfect catering</Text>
      </View>

      {/* Form */}
      <Controller
        control={control}
        name="identifier"
        render={({ field: { onChange, value } }) => (
          <Input
            label="Email or Phone"
            placeholder="Enter your email or phone"
            value={value}
            onChangeText={onChange}
            error={errors.identifier?.message}
            autoCapitalize="none"
            keyboardType="email-address"
          />
        )}
      />

      <Controller
        control={control}
        name="password"
        render={({ field: { onChange, value } }) => (
          <Input
            label="Password"
            placeholder="Enter your password"
            value={value}
            onChangeText={onChange}
            error={errors.password?.message}
            secureTextEntry
          />
        )}
      />

      <TouchableOpacity 
        onPress={() => router.push('/(auth)/forgot-password')}
        className="self-end mb-6"
      >
        <Text className="text-primary-600 font-medium">Forgot Password?</Text>
      </TouchableOpacity>

      <Button
        title="Login"
        onPress={handleSubmit(onSubmit)}
        loading={isLoading}
        className="mb-4"
      />

      <View className="flex-row justify-center mt-4">
        <Text className="text-neutral-600">Don't have an account? </Text>
        <TouchableOpacity onPress={() => router.push('/(auth)/register')}>
          <Text className="text-primary-600 font-semibold">Sign Up</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};
```

---

### 2. Home & Discovery Module

#### Screens
- **Home Screen** (`app/(tabs)/index.tsx`)
- **Explore/Browse Vendors** (`app/(tabs)/explore.tsx`)
- **Vendor Details** (`app/vendor/[id].tsx`)
- **Menu Item Details** (`app/vendor/menu/[id].tsx`)

#### Features
- Featured vendors carousel
- Categories browsing
- Search with filters (cuisine, location, rating, price)
- Vendor listings with ratings
- Menu browsing
- Add to cart/wishlist
- Vendor reviews and ratings
- Real-time availability

#### Key Components

```typescript
// src/components/vendor/VendorCard.tsx
import React from 'react';
import { View, Text, Image, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { VendorResponse } from '@/types/vendor.types';
import { useRouter } from 'expo-router';

interface VendorCardProps {
  vendor: VendorResponse;
}

export const VendorCard: React.FC<VendorCardProps> = ({ vendor }) => {
  const router = useRouter();

  return (
    <TouchableOpacity
      onPress={() => router.push(`/vendor/${vendor.vendorId}`)}
      className="bg-white rounded-xl mb-4 overflow-hidden shadow-md"
    >
      <Image
        source={{ uri: vendor.bannerImageUrl || 'https://via.placeholder.com/400x200' }}
        className="w-full h-48"
      />
      
      <View className="p-4">
        <Text className="text-lg font-bold text-neutral-900">{vendor.businessName}</Text>
        
        <View className="flex-row items-center mt-2">
          <Ionicons name="star" size={16} color="#FF8C42" />
          <Text className="ml-1 text-neutral-700 font-medium">
            {vendor.rating?.toFixed(1)} ({vendor.reviewCount} reviews)
          </Text>
        </View>

        <View className="flex-row flex-wrap mt-2">
          {vendor.cuisineTypes?.slice(0, 3).map((cuisine, index) => (
            <View key={index} className="bg-orange-50 rounded-full px-3 py-1 mr-2 mb-2">
              <Text className="text-primary-600 text-xs font-medium">{cuisine}</Text>
            </View>
          ))}
        </View>

        <View className="flex-row items-center justify-between mt-3">
          <View className="flex-row items-center">
            <Ionicons name="location-outline" size={16} color="#6B7280" />
            <Text className="ml-1 text-neutral-600 text-sm">{vendor.city}</Text>
          </View>
          
          <Text className="text-neutral-600 text-sm">
            Min ₹{vendor.minOrderAmount?.toLocaleString()}
          </Text>
        </View>
      </View>
    </TouchableOpacity>
  );
};
```

---

### 3. Bid Request Module

#### Screens
- **My Bid Requests** (`app/(tabs)/bids.tsx`)
- **Create Bid Request** (`app/bid/create.tsx`)
- **Bid Details** (`app/bid/[id].tsx`)
- **View Bids** (`app/bid/[id]/bids.tsx`)

#### Features
- Create custom bid requests for events
- Event details (type, date, guest count, venue)
- Requirements specification (cuisine, dietary, service type)
- Budget range
- View submitted bids from vendors
- Accept/reject bids
- Bid comparison
- Cooling period handling

#### API Endpoints
```typescript
POST   /bids/requests
GET    /bids/requests?page=0&size=20
GET    /bids/requests/{bidRequestId}
PUT    /bids/requests/{bidRequestId}
DELETE /bids/requests/{bidRequestId}
GET    /bids/requests/{bidRequestId}/bids
POST   /bids/{bidId}/accept
```

#### Implementation Example

```typescript
// src/components/bid/CreateBidForm.tsx
import React from 'react';
import { View, Text, ScrollView, Alert } from 'react-native';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useCreateBidRequestMutation } from '@/store/api/bidApi';
import DateTimePicker from '@react-native-community/datetimepicker';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { Dropdown } from '@/components/ui/Dropdown';

const bidRequestSchema = z.object({
  eventType: z.string().min(1, 'Event type required'),
  eventDate: z.date(),
  guestCount: z.number().min(10, 'Minimum 10 guests'),
  city: z.string().min(1, 'City required'),
  cuisinePreferences: z.array(z.string()).min(1, 'Select at least one cuisine'),
  serviceType: z.string().min(1, 'Service type required'),
  minBudget: z.number().positive(),
  maxBudget: z.number().positive(),
});

type BidRequestFormData = z.infer<typeof bidRequestSchema>;

export const CreateBidForm = () => {
  const [createBidRequest, { isLoading }] = useCreateBidRequestMutation();
  const { control, handleSubmit, formState: { errors } } = useForm<BidRequestFormData>({
    resolver: zodResolver(bidRequestSchema),
  });

  const onSubmit = async (data: BidRequestFormData) => {
    try {
      await createBidRequest({
        eventDetails: {
          eventType: data.eventType,
          eventDate: data.eventDate.toISOString(),
          guestCount: data.guestCount,
          venue: { city: data.city },
        },
        requirements: {
          cuisinePreferences: data.cuisinePreferences,
          serviceType: data.serviceType,
        },
        budget: {
          minBudget: data.minBudget,
          maxBudget: data.maxBudget,
        },
      }).unwrap();
      
      Alert.alert('Success', 'Bid request created successfully!');
    } catch (error: any) {
      Alert.alert('Error', error?.data?.message || 'Failed to create bid request');
    }
  };

  return (
    <ScrollView className="flex-1 bg-white px-6 py-4">
      <Text className="text-2xl font-bold text-neutral-900 mb-6">Create Bid Request</Text>

      {/* Event Type */}
      <Controller
        control={control}
        name="eventType"
        render={({ field: { onChange, value } }) => (
          <Dropdown
            label="Event Type"
            options={['WEDDING', 'BIRTHDAY', 'CORPORATE', 'ANNIVERSARY', 'PARTY', 'OTHER']}
            value={value}
            onChange={onChange}
            error={errors.eventType?.message}
          />
        )}
      />

      {/* Guest Count */}
      <Controller
        control={control}
        name="guestCount"
        render={({ field: { onChange, value } }) => (
          <Input
            label="Number of Guests"
            placeholder="Enter guest count"
            value={value?.toString()}
            onChangeText={(text) => onChange(parseInt(text) || 0)}
            error={errors.guestCount?.message}
            keyboardType="numeric"
          />
        )}
      />

      {/* More form fields... */}

      <Button
        title="Create Bid Request"
        onPress={handleSubmit(onSubmit)}
        loading={isLoading}
        className="mt-6"
      />
    </ScrollView>
  );
};
```

---

### 4. Cart & Wishlist Module

#### Screens
- **Cart Screen** (`app/cart/index.tsx`)
- **Wishlist Screen** (`app/wishlist/index.tsx`)

#### Features
- Add/remove items
- Update quantities
- View cart total
- Grouped by vendor
- Quick add to cart from menu
- Move items between cart and wishlist
- Saved for later

#### API Endpoints
```typescript
// Cart
GET    /cart
GET    /cart/grouped
POST   /cart/items?vendorItemId={id}&quantity={qty}
PUT    /cart/items/{cartItemId}?quantity={qty}
DELETE /cart/items/{cartItemId}
DELETE /cart
GET    /cart/count
GET    /cart/total

// Wishlist
GET    /wishlist?page=0&size=20
POST   /wishlist?vendorItemId={id}
DELETE /wishlist/{wishlistItemId}
DELETE /wishlist/vendor-item/{vendorItemId}
GET    /wishlist/count
```

---

### 5. Orders Module

#### Screens
- **My Orders** (`app/(tabs)/orders.tsx`)
- **Order Details** (`app/order/[id].tsx`)
- **Order Tracking** (`app/order/[id]/tracking.tsx`)

#### Features
- Order history with filters
- Order status tracking
- Real-time updates
- Order timeline
- Vendor contact
- Cancel orders
- Reorder functionality
- Download invoices

#### API Endpoints
```typescript
POST   /orders/from-bid?bidRequestId={id}
GET    /orders/user?page=0&size=20
GET    /orders/{orderId}
PATCH  /orders/{orderId}/status?status={status}
POST   /orders/{orderId}/cancel?reason={reason}
```

#### Order Status Flow
```
PENDING → CONFIRMED → IN_PREPARATION → READY → 
IN_TRANSIT → DELIVERED → COMPLETED
```

---

### 6. User Profile Module

#### Screens
- **Profile Screen** (`app/(tabs)/profile.tsx`)
- **Edit Profile** (`app/profile/edit.tsx`)
- **Addresses** (`app/profile/addresses.tsx`)
- **Add/Edit Address** (`app/profile/address/[id].tsx`)
- **Notification Settings** (`app/profile/notifications.tsx`)
- **Loyalty Points** (`app/profile/loyalty.tsx`)
- **Referral** (`app/profile/referral.tsx`)

#### Features
- View/edit profile
- Profile picture upload
- Manage addresses (add, edit, delete, set default)
- Notification preferences
- Loyalty points balance and history
- Referral code sharing
- Language & currency preferences
- Account settings

#### API Endpoints
```typescript
// Profile
GET    /users/profile
PUT    /users/profile
PATCH  /users/profile-picture?imageUrl={url}

// Addresses
GET    /users/addresses
GET    /users/addresses/{addressId}
POST   /users/addresses
PUT    /users/addresses/{addressId}
DELETE /users/addresses/{addressId}
PATCH  /users/addresses/{addressId}/default

// Notification Preferences
GET    /users/notification-preferences
PUT    /users/notification-preferences

// Loyalty
GET    /loyalty/balance
GET    /loyalty/transactions?page=0&size=20
GET    /loyalty/calculate?orderAmount={amount}

// Referral
GET    /referral/code
GET    /referral/stats
GET    /referral/validate/{code}
```

#### Implementation Example

```typescript
// src/components/profile/AddressCard.tsx
import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { AddressResponse } from '@/types/user.types';

interface AddressCardProps {
  address: AddressResponse;
  onEdit: () => void;
  onDelete: () => void;
  onSetDefault: () => void;
}

export const AddressCard: React.FC<AddressCardProps> = ({
  address,
  onEdit,
  onDelete,
  onSetDefault,
}) => {
  return (
    <View className="bg-white rounded-xl p-4 mb-4 border border-neutral-200">
      {address.isDefault && (
        <View className="bg-primary-50 rounded-full px-3 py-1 self-start mb-2">
          <Text className="text-primary-600 text-xs font-semibold">DEFAULT</Text>
        </View>
      )}

      <View className="flex-row justify-between items-start">
        <View className="flex-1">
          <Text className="text-base font-semibold text-neutral-900">{address.label}</Text>
          <Text className="text-sm text-neutral-600 mt-1">{address.fullName}</Text>
          <Text className="text-sm text-neutral-600">{address.phone}</Text>
          <Text className="text-sm text-neutral-600 mt-2">
            {address.streetAddress}, {address.city}, {address.state} - {address.postalCode}
          </Text>
        </View>

        <View className="flex-row">
          <TouchableOpacity onPress={onEdit} className="p-2">
            <Ionicons name="pencil" size={20} color="#FF8C42" />
          </TouchableOpacity>
          <TouchableOpacity onPress={onDelete} className="p-2">
            <Ionicons name="trash-outline" size={20} color="#EF4444" />
          </TouchableOpacity>
        </View>
      </View>

      {!address.isDefault && (
        <TouchableOpacity
          onPress={onSetDefault}
          className="mt-3 border border-primary-600 rounded-lg py-2 items-center"
        >
          <Text className="text-primary-600 font-medium">Set as Default</Text>
        </TouchableOpacity>
      )}
    </View>
  );
};
```

---

### 7. Notifications Module

#### Screens
- **Notifications** (`app/notifications/index.tsx`)

#### Features
- In-app notifications
- Push notifications
- Real-time updates
- Mark as read
- Mark all as read
- Notification badges
- Deep linking to relevant screens

#### API Endpoints
```typescript
GET   /notifications?page=0&size=20
GET   /notifications/unread?page=0&size=20
GET   /notifications/count
PATCH /notifications/{notificationId}/read
PATCH /notifications/read-all
```

---

### 8. Payments Module

#### Screens
- **Payment Screen** (`app/payment/index.tsx`)
- **Payment Success** (`app/payment/success.tsx`)
- **Payment Failed** (`app/payment/failed.tsx`)

#### Features
- Razorpay integration
- Multiple payment methods
- Token payment (25%)
- Final payment
- Payment history
- Refund tracking
- Apply loyalty points
- Promo codes

#### API Endpoints
```typescript
POST  /payments/initiate?orderId={id}&paymentType={type}&amount={amt}
POST  /payments/verify?gatewayOrderId={id}&gatewayPaymentId={id}&signature={sig}
GET   /payments/transactions?page=0&size=20
```

---

### 9. Support Module

#### Screens
- **Help Center** (`app/support/index.tsx`)
- **Create Ticket** (`app/support/create.tsx`)
- **Ticket Details** (`app/support/ticket/[id].tsx`)
- **FAQ** (`app/support/faq.tsx`)

#### Features
- Create support tickets
- View ticket status
- Chat with support
- FAQ section
- Order-specific issues
- Rate support experience

#### API Endpoints
```typescript
POST  /support/tickets
GET   /support/tickets?page=0&size=20
GET   /support/tickets/{ticketId}
POST  /support/tickets/{ticketId}/rate?rating={rating}&feedback={text}
```

---

## 🔌 API Integration

### Axios Configuration

```typescript
// src/services/api.ts
import axios from 'axios';
import { storage } from './storage';
import { API_BASE_URL } from '@/config/env';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use(
  async (config) => {
    const token = await storage.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response.data,
  async (error) => {
    const originalRequest = error.config;

    // Handle 401 - Token expired
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = await storage.getRefreshToken();
        const response = await axios.post(`${API_BASE_URL}/auth/refresh-token`, {
          refreshToken,
        });

        const { accessToken } = response.data.data;
        await storage.setAccessToken(accessToken);

        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return apiClient(originalRequest);
      } catch (refreshError) {
        await storage.clearTokens();
        // Navigate to login
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);
```

### RTK Query API Setup

```typescript
// src/store/api/authApi.ts
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { API_BASE_URL } from '@/config/env';
import { storage } from '@/services/storage';

export const authApi = createApi({
  reducerPath: 'authApi',
  baseQuery: fetchBaseQuery({
    baseUrl: `${API_BASE_URL}/auth`,
    prepareHeaders: async (headers) => {
      const token = await storage.getAccessToken();
      if (token) {
        headers.set('Authorization', `Bearer ${token}`);
      }
      return headers;
    },
  }),
  endpoints: (builder) => ({
    login: builder.mutation({
      query: (credentials) => ({
        url: '/login',
        method: 'POST',
        body: credentials,
      }),
    }),
    register: builder.mutation({
      query: (userData) => ({
        url: '/register',
        method: 'POST',
        body: userData,
      }),
    }),
    logout: builder.mutation({
      query: () => ({
        url: '/logout',
        method: 'POST',
      }),
    }),
    sendOtp: builder.mutation({
      query: (data) => ({
        url: '/send-otp',
        method: 'POST',
        body: data,
      }),
    }),
    verifyOtp: builder.mutation({
      query: (data) => ({
        url: '/verify-otp',
        method: 'POST',
        body: data,
      }),
    }),
  }),
});

export const {
  useLoginMutation,
  useRegisterMutation,
  useLogoutMutation,
  useSendOtpMutation,
  useVerifyOtpMutation,
} = authApi;
```

---

## 🗄️ State Management

### Redux Store Setup

```typescript
// src/store/index.ts
import { configureStore } from '@reduxjs/toolkit';
import { setupListeners } from '@reduxjs/toolkit/query';
import authReducer from './slices/authSlice';
import userReducer from './slices/userSlice';
import cartReducer from './slices/cartSlice';
import wishlistReducer from './slices/wishlistSlice';
import appReducer from './slices/appSlice';
import { authApi } from './api/authApi';
import { userApi } from './api/userApi';
import { vendorApi } from './api/vendorApi';
import { bidApi } from './api/bidApi';
import { orderApi } from './api/orderApi';
import { cartApi } from './api/cartApi';
import { wishlistApi } from './api/wishlistApi';
import { notificationApi } from './api/notificationApi';
import { loyaltyApi } from './api/loyaltyApi';
import { referralApi } from './api/referralApi';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    user: userReducer,
    cart: cartReducer,
    wishlist: wishlistReducer,
    app: appReducer,
    [authApi.reducerPath]: authApi.reducer,
    [userApi.reducerPath]: userApi.reducer,
    [vendorApi.reducerPath]: vendorApi.reducer,
    [bidApi.reducerPath]: bidApi.reducer,
    [orderApi.reducerPath]: orderApi.reducer,
    [cartApi.reducerPath]: cartApi.reducer,
    [wishlistApi.reducerPath]: wishlistApi.reducer,
    [notificationApi.reducerPath]: notificationApi.reducer,
    [loyaltyApi.reducerPath]: loyaltyApi.reducer,
    [referralApi.reducerPath]: referralApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      authApi.middleware,
      userApi.middleware,
      vendorApi.middleware,
      bidApi.middleware,
      orderApi.middleware,
      cartApi.middleware,
      wishlistApi.middleware,
      notificationApi.middleware,
      loyaltyApi.middleware,
      referralApi.middleware
    ),
});

setupListeners(store.dispatch);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
```

### Auth Slice Example

```typescript
// src/store/slices/authSlice.ts
import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { UserResponse } from '@/types/user.types';

interface AuthState {
  user: UserResponse | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

const initialState: AuthState = {
  user: null,
  accessToken: null,
  refreshToken: null,
  isAuthenticated: false,
  isLoading: true,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials: (
      state,
      action: PayloadAction<{
        user: UserResponse;
        accessToken: string;
        refreshToken: string;
      }>
    ) => {
      state.user = action.payload.user;
      state.accessToken = action.payload.accessToken;
      state.refreshToken = action.payload.refreshToken;
      state.isAuthenticated = true;
      state.isLoading = false;
    },
    clearCredentials: (state) => {
      state.user = null;
      state.accessToken = null;
      state.refreshToken = null;
      state.isAuthenticated = false;
      state.isLoading = false;
    },
    updateUser: (state, action: PayloadAction<Partial<UserResponse>>) => {
      if (state.user) {
        state.user = { ...state.user, ...action.payload };
      }
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
  },
});

export const { setCredentials, clearCredentials, updateUser, setLoading } = authSlice.actions;
export default authSlice.reducer;
```

---

## 🧭 Navigation Structure

### Tab Navigation

```typescript
// app/(tabs)/_layout.tsx
import { Tabs } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: '#FF8C42',
        tabBarInactiveTintColor: '#9CA3AF',
        headerShown: false,
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: 'Home',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="home" size={size} color={color} />
          ),
        }}
      />
      <Tabs.Screen
        name="explore"
        options={{
          title: 'Explore',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="search" size={size} color={color} />
          ),
        }}
      />
      <Tabs.Screen
        name="bids"
        options={{
          title: 'My Bids',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="document-text" size={size} color={color} />
          ),
        }}
      />
      <Tabs.Screen
        name="orders"
        options={{
          title: 'Orders',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="bag-handle" size={size} color={color} />
          ),
        }}
      />
      <Tabs.Screen
        name="profile"
        options={{
          title: 'Profile',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="person" size={size} color={color} />
          ),
        }}
      />
    </Tabs>
  );
}
```

---

## 🎨 Component Library

### Button Component

```typescript
// src/components/ui/Button.tsx
import React from 'react';
import { TouchableOpacity, Text, ActivityIndicator, ViewStyle } from 'react-native';

interface ButtonProps {
  title: string;
  onPress: () => void;
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  loading?: boolean;
  disabled?: boolean;
  className?: string;
  icon?: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({
  title,
  onPress,
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled = false,
  className = '',
  icon,
}) => {
  const variantClasses = {
    primary: 'bg-primary-600 active:bg-primary-700',
    secondary: 'bg-neutral-600 active:bg-neutral-700',
    outline: 'bg-transparent border-2 border-primary-600',
    ghost: 'bg-transparent',
  };

  const sizeClasses = {
    sm: 'py-2 px-4',
    md: 'py-3 px-6',
    lg: 'py-4 px-8',
  };

  const textVariantClasses = {
    primary: 'text-white',
    secondary: 'text-white',
    outline: 'text-primary-600',
    ghost: 'text-primary-600',
  };

  const textSizeClasses = {
    sm: 'text-sm',
    md: 'text-base',
    lg: 'text-lg',
  };

  const isDisabled = disabled || loading;

  return (
    <TouchableOpacity
      onPress={onPress}
      disabled={isDisabled}
      className={`
        rounded-xl items-center justify-center flex-row
        ${variantClasses[variant]}
        ${sizeClasses[size]}
        ${isDisabled ? 'opacity-50' : ''}
        ${className}
      `}
    >
      {loading ? (
        <ActivityIndicator color={variant === 'outline' || variant === 'ghost' ? '#FF8C42' : '#FFFFFF'} />
      ) : (
        <>
          {icon && <>{icon}</>}
          <Text
            className={`
              font-semibold
              ${textVariantClasses[variant]}
              ${textSizeClasses[size]}
              ${icon ? 'ml-2' : ''}
            `}
          >
            {title}
          </Text>
        </>
      )}
    </TouchableOpacity>
  );
};
```

### Input Component

```typescript
// src/components/ui/Input.tsx
import React from 'react';
import { View, Text, TextInput, TextInputProps } from 'react-native';

interface InputProps extends TextInputProps {
  label?: string;
  error?: string;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
}

export const Input: React.FC<InputProps> = ({
  label,
  error,
  leftIcon,
  rightIcon,
  className = '',
  ...props
}) => {
  return (
    <View className="mb-4">
      {label && (
        <Text className="text-neutral-700 font-medium mb-2">{label}</Text>
      )}
      
      <View
        className={`
          flex-row items-center
          bg-neutral-50 border rounded-xl px-4
          ${error ? 'border-error-main' : 'border-neutral-200'}
        `}
      >
        {leftIcon && <View className="mr-2">{leftIcon}</View>}
        
        <TextInput
          className={`flex-1 py-3 text-neutral-900 ${className}`}
          placeholderTextColor="#9CA3AF"
          {...props}
        />
        
        {rightIcon && <View className="ml-2">{rightIcon}</View>}
      </View>
      
      {error && (
        <Text className="text-error-main text-sm mt-1">{error}</Text>
      )}
    </View>
  );
};
```

### Card Component

```typescript
// src/components/ui/Card.tsx
import React from 'react';
import { View, ViewStyle } from 'react-native';

interface CardProps {
  children: React.ReactNode;
  className?: string;
  shadow?: 'sm' | 'md' | 'lg';
}

export const Card: React.FC<CardProps> = ({
  children,
  className = '',
  shadow = 'md',
}) => {
  const shadowClasses = {
    sm: 'shadow-sm',
    md: 'shadow-md',
    lg: 'shadow-lg',
  };

  return (
    <View
      className={`
        bg-white rounded-xl p-4
        ${shadowClasses[shadow]}
        ${className}
      `}
    >
      {children}
    </View>
  );
};
```

---

## ✅ Implementation Checklist

### Phase 1: Setup & Foundation (Week 1-2)

- [ ] Initialize Expo project with TypeScript
- [ ] Configure NativeWind and Tailwind CSS
- [ ] Setup folder structure
- [ ] Install all dependencies
- [ ] Configure environment variables
- [ ] Setup Redux store and RTK Query
- [ ] Create base theme configuration
- [ ] Setup navigation structure
- [ ] Create reusable UI components (Button, Input, Card, etc.)
- [ ] Setup API client with interceptors
- [ ] Configure AsyncStorage and Secure Store

### Phase 2: Authentication (Week 3)

- [ ] Login screen with form validation
- [ ] Registration screen with referral code
- [ ] OTP verification flow
- [ ] Forgot password flow
- [ ] Biometric authentication
- [ ] Token management and refresh
- [ ] Auth state persistence
- [ ] Protected route handling

### Phase 3: Core Features (Week 4-6)

- [ ] Home screen with featured vendors
- [ ] Vendor listing and search
- [ ] Vendor details and menu
- [ ] Cart functionality
- [ ] Wishlist functionality
- [ ] Bid request creation
- [ ] Bid listing and details
- [ ] Accept bid flow

### Phase 4: Orders & Payments (Week 7-8)

- [ ] Order creation from bid
- [ ] Order listing
- [ ] Order details and tracking
- [ ] Razorpay payment integration
- [ ] Payment success/failure handling
- [ ] Apply loyalty points
- [ ] Promo code application

### Phase 5: Profile & Settings (Week 9)

- [ ] User profile display
- [ ] Edit profile
- [ ] Profile picture upload
- [ ] Address management (CRUD)
- [ ] Notification preferences
- [ ] Loyalty points display
- [ ] Referral code sharing

### Phase 6: Notifications & Support (Week 10)

- [ ] Push notifications setup
- [ ] Notification listing
- [ ] Mark as read functionality
- [ ] Deep linking from notifications
- [ ] Support ticket creation
- [ ] Ticket listing and details
- [ ] FAQ section

### Phase 7: Polish & Optimization (Week 11-12)

- [ ] Loading states and skeletons
- [ ] Error handling and retry
- [ ] Empty states
- [ ] Offline mode handling
- [ ] Image optimization
- [ ] Performance optimization
- [ ] Accessibility improvements
- [ ] Analytics integration
- [ ] Crash reporting

### Phase 8: Testing & Deployment (Week 13-14)

- [ ] Unit tests for utilities
- [ ] Integration tests for API calls
- [ ] E2E tests for critical flows
- [ ] QA testing on iOS
- [ ] QA testing on Android
- [ ] Bug fixes
- [ ] App Store preparation
- [ ] Play Store preparation
- [ ] Production build
- [ ] Deployment

---

## 📱 Screen Flow Diagram

```
┌─────────────────┐
│   Splash Screen │
└────────┬────────┘
         │
         ▼
    ┌────────┐
    │ Is Auth?│
    └─┬────┬─┘
      │    │
   NO │    │ YES
      │    │
      ▼    ▼
┌──────────┐  ┌──────────────┐
│  Login/  │  │  Home (Tabs) │
│ Register │  └──────────────┘
└──────────┘         │
                     ├─── Home
                     ├─── Explore
                     ├─── My Bids
                     ├─── Orders
                     └─── Profile
```

---

## 🎯 Key Performance Indicators (KPIs)

### Technical KPIs
- App launch time < 2 seconds
- API response handling < 500ms
- Smooth 60fps scrolling
- Bundle size < 50MB
- Crash-free rate > 99.5%

### User Experience KPIs
- Registration completion rate > 70%
- Cart abandonment rate < 40%
- Bid request creation rate > 50%
- Order completion rate > 80%
- User retention (Day 7) > 40%

---

## 🔐 Security Considerations

1. **Token Security**
   - Store tokens in Expo Secure Store
   - Implement token refresh mechanism
   - Clear tokens on logout

2. **API Security**
   - SSL pinning for production
   - Request/response encryption
   - Rate limiting handling

3. **Data Privacy**
   - GDPR compliance
   - User data encryption
   - Secure payment handling

4. **Authentication**
   - Biometric authentication
   - 2FA support
   - Session timeout

---

## 📊 Analytics & Tracking

### Events to Track

```typescript
// User Events
- User Registration
- User Login
- Profile Update
- Address Added

// Vendor Events
- Vendor Viewed
- Menu Browsed
- Item Added to Cart
- Item Added to Wishlist

// Bid Events
- Bid Request Created
- Bid Viewed
- Bid Accepted

// Order Events
- Order Created
- Payment Initiated
- Payment Completed
- Order Cancelled

// Engagement Events
- Screen View
- Button Click
- Search Performed
- Filter Applied
```

---

## 🚀 Deployment Checklist

### iOS (App Store)

- [ ] Configure app.json for iOS
- [ ] Generate app icons (1024x1024)
- [ ] Create screenshots for all devices
- [ ] Setup Apple Developer account
- [ ] Create App Store Connect listing
- [ ] Configure push notifications
- [ ] Setup In-App Purchases (if applicable)
- [ ] Submit for review
- [ ] Handle review feedback

### Android (Play Store)

- [ ] Configure app.json for Android
- [ ] Generate app icons and adaptive icons
- [ ] Create screenshots for all devices
- [ ] Setup Google Play Console
- [ ] Create Play Store listing
- [ ] Configure Firebase for push notifications
- [ ] Setup In-App Billing (if applicable)
- [ ] Generate signed APK/AAB
- [ ] Submit for review

---

## 📝 API Base URLs

```typescript
// src/config/env.ts
export const API_BASE_URL = __DEV__
  ? 'http://localhost:8080/api/v1'
  : 'https://api.bidzaro.com/api/v1';

export const RAZORPAY_KEY_ID = __DEV__
  ? 'rzp_test_xxxxx'
  : 'rzp_live_xxxxx';
```

---

## 🎨 Brand Assets Required

### Images
- App icon (1024x1024)
- Splash screen logo
- Placeholder images (vendor, menu items, user avatar)
- Empty state illustrations
- Onboarding illustrations

### Icons
- Tab bar icons
- Feature icons
- Category icons
- Payment method icons

### Colors
- Primary orange variations (#FF6B35, #FF8C42, #FFA500)
- Success, error, warning, info states
- Neutral grayscale palette

---

## 📚 Dependencies

```json
{
  "dependencies": {
    "expo": "~50.0.0",
    "expo-router": "~3.0.0",
    "react": "18.2.0",
    "react-native": "0.73.0",
    "nativewind": "^4.0.0",
    "tailwindcss": "^3.4.0",
    "@reduxjs/toolkit": "^2.0.0",
    "react-redux": "^9.0.0",
    "axios": "^1.6.0",
    "react-hook-form": "^7.49.0",
    "@hookform/resolvers": "^3.3.0",
    "zod": "^3.22.0",
    "expo-image": "~1.10.0",
    "expo-image-picker": "~14.7.0",
    "expo-secure-store": "~12.8.0",
    "@react-native-async-storage/async-storage": "1.21.0",
    "expo-notifications": "~0.27.0",
    "react-native-maps": "1.10.0",
    "expo-location": "~16.5.0",
    "@expo/vector-icons": "^14.0.0",
    "expo-linking": "~6.2.0",
    "expo-constants": "~15.4.0",
    "expo-device": "~5.9.0",
    "date-fns": "^3.0.0",
    "react-native-reanimated": "~3.6.0",
    "react-native-gesture-handler": "~2.14.0",
    "react-native-safe-area-context": "4.8.2",
    "react-native-screens": "~3.29.0"
  },
  "devDependencies": {
    "@types/react": "~18.2.45",
    "@types/react-native": "~0.73.0",
    "typescript": "^5.3.0"
  }
}
```

---

## 🎓 Development Best Practices

1. **Code Organization**
   - Follow folder structure strictly
   - Use TypeScript for type safety
   - Create reusable components
   - Implement proper error boundaries

2. **Performance**
   - Use React.memo for expensive components
   - Implement proper list virtualization
   - Optimize images with Expo Image
   - Lazy load screens and components

3. **State Management**
   - Use Redux for global state
   - Use local state for UI-specific state
   - Implement proper loading and error states
   - Cache API responses with RTK Query

4. **UI/UX**
   - Follow platform-specific guidelines
   - Implement proper loading indicators
   - Show meaningful error messages
   - Provide haptic feedback
   - Support dark mode (optional)

5. **Testing**
   - Write unit tests for utilities
   - Test API integration
   - Perform manual testing on both platforms
   - Test on different screen sizes

---

## 📞 Support & Resources

- **API Documentation:** `API_DOCUMENTATION.md`
- **Backend Repository:** Main monolithic backend
- **Design System:** Figma (to be provided)
- **Project Management:** Jira/Trello (to be setup)

---

## 🎉 Conclusion

This comprehensive guide provides everything needed to develop a production-ready mobile application for Bidzaro catering platform. Follow the implementation checklist, maintain code quality, and ensure thorough testing before deployment.

**Primary Color: Orange (#FF8C42)** - Use consistently throughout the app for branding and user recognition.

**Next Steps:**
1. Initialize the Expo project
2. Setup the development environment
3. Begin with Phase 1 implementation
4. Follow the checklist systematically

Good luck with the development! 🚀

