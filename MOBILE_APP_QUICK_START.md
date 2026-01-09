# Bidzaro Mobile App - Quick Start Guide

## 🚀 Getting Started in 5 Minutes

This guide will help you set up and run the Bidzaro mobile app for development.

---

## Prerequisites

Before you begin, ensure you have the following installed:

- **Node.js** 18.x or higher ([Download](https://nodejs.org/))
- **npm** or **yarn**
- **Expo CLI** (`npm install -g expo-cli`)
- **Git**
- **iOS Simulator** (Mac only) or **Android Studio** (for Android emulator)
- **VS Code** (recommended) with extensions:
  - ES7+ React/Redux/React-Native snippets
  - Tailwind CSS IntelliSense
  - TypeScript

---

## Step 1: Create the Project

```bash
# Create new Expo app with TypeScript template
npx create-expo-app bidzaro-mobile --template

# Navigate to project directory
cd bidzaro-mobile
```

---

## Step 2: Install Dependencies

```bash
# Install core dependencies
npm install nativewind tailwindcss@3.3.2

# Install navigation
npm install @react-navigation/native
npx expo install react-native-screens react-native-safe-area-context

# Install Expo Router
npx expo install expo-router react-native-safe-area-context react-native-screens expo-linking expo-constants expo-status-bar

# Install Redux Toolkit
npm install @reduxjs/toolkit react-redux

# Install form handling
npm install react-hook-form @hookform/resolvers zod

# Install API client
npm install axios

# Install storage
npx expo install @react-native-async-storage/async-storage expo-secure-store

# Install UI dependencies
npx expo install expo-image expo-image-picker
npm install @expo/vector-icons

# Install notifications
npx expo install expo-notifications expo-device

# Install location
npx expo install expo-location react-native-maps

# Install date utilities
npm install date-fns

# Install gesture handler and reanimated
npx expo install react-native-gesture-handler react-native-reanimated
```

---

## Step 3: Configure NativeWind

### 3.1 Create `tailwind.config.js`

```javascript
/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./app/**/*.{js,jsx,ts,tsx}",
    "./src/**/*.{js,jsx,ts,tsx}",
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
    },
  },
  plugins: [],
};
```

### 3.2 Create `nativewind-env.d.ts`

```typescript
/// <reference types="nativewind/types" />
```

### 3.3 Update `babel.config.js`

```javascript
module.exports = function(api) {
  api.cache(true);
  return {
    presets: ['babel-preset-expo'],
    plugins: [
      'nativewind/babel',
      'react-native-reanimated/plugin',
    ],
  };
};
```

---

## Step 4: Setup Project Structure

```bash
# Create directory structure
mkdir -p src/{components/{ui,auth,vendor,order,bid,cart,profile,shared},store/{slices,api},services,hooks,utils,types,config}
mkdir -p assets/{images,icons,fonts}
```

---

## Step 5: Create Configuration Files

### 5.1 Create `src/config/env.ts`

```typescript
export const API_BASE_URL = __DEV__
  ? 'http://localhost:8080/api/v1'
  : 'https://api.bidzaro.com/api/v1';

export const RAZORPAY_KEY_ID = __DEV__
  ? 'rzp_test_xxxxx'
  : 'rzp_live_xxxxx';
```

### 5.2 Create `src/config/theme.ts`

```typescript
export const colors = {
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
  // ... rest of colors
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
```

---

## Step 6: Setup Redux Store

### 6.1 Create `src/store/index.ts`

```typescript
import { configureStore } from '@reduxjs/toolkit';
import { setupListeners } from '@reduxjs/toolkit/query';

export const store = configureStore({
  reducer: {
    // Add reducers here
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware(),
});

setupListeners(store.dispatch);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
```

---

## Step 7: Setup Expo Router

### 7.1 Update `app.json`

```json
{
  "expo": {
    "name": "Bidzaro",
    "slug": "bidzaro-mobile",
    "version": "1.0.0",
    "orientation": "portrait",
    "icon": "./assets/icon.png",
    "userInterfaceStyle": "light",
    "splash": {
      "image": "./assets/splash.png",
      "resizeMode": "contain",
      "backgroundColor": "#ffffff"
    },
    "assetBundlePatterns": [
      "**/*"
    ],
    "ios": {
      "supportsTablet": true,
      "bundleIdentifier": "com.bidzaro.app"
    },
    "android": {
      "adaptiveIcon": {
        "foregroundImage": "./assets/adaptive-icon.png",
        "backgroundColor": "#ffffff"
      },
      "package": "com.bidzaro.app"
    },
    "web": {
      "favicon": "./assets/favicon.png"
    },
    "scheme": "bidzaro",
    "plugins": [
      "expo-router"
    ]
  }
}
```

### 7.2 Create `app/_layout.tsx`

```typescript
import { Stack } from 'expo-router';
import { Provider } from 'react-redux';
import { store } from '../src/store';

export default function RootLayout() {
  return (
    <Provider store={store}>
      <Stack screenOptions={{ headerShown: false }}>
        <Stack.Screen name="(auth)" />
        <Stack.Screen name="(tabs)" />
      </Stack>
    </Provider>
  );
}
```

### 7.3 Create `app/(auth)/_layout.tsx`

```typescript
import { Stack } from 'expo-router';

export default function AuthLayout() {
  return (
    <Stack screenOptions={{ headerShown: false }}>
      <Stack.Screen name="login" />
      <Stack.Screen name="register" />
      <Stack.Screen name="forgot-password" />
    </Stack>
  );
}
```

### 7.4 Create `app/(tabs)/_layout.tsx`

```typescript
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

## Step 8: Create Basic UI Components

### 8.1 Create `src/components/ui/Button.tsx`

```typescript
import React from 'react';
import { TouchableOpacity, Text, ActivityIndicator } from 'react-native';

interface ButtonProps {
  title: string;
  onPress: () => void;
  loading?: boolean;
  disabled?: boolean;
  variant?: 'primary' | 'secondary' | 'outline';
  className?: string;
}

export const Button: React.FC<ButtonProps> = ({
  title,
  onPress,
  loading = false,
  disabled = false,
  variant = 'primary',
  className = '',
}) => {
  const variants = {
    primary: 'bg-primary-600',
    secondary: 'bg-neutral-600',
    outline: 'bg-transparent border-2 border-primary-600',
  };

  const textVariants = {
    primary: 'text-white',
    secondary: 'text-white',
    outline: 'text-primary-600',
  };

  return (
    <TouchableOpacity
      onPress={onPress}
      disabled={disabled || loading}
      className={`
        rounded-xl py-3 px-6 items-center justify-center
        ${variants[variant]}
        ${disabled || loading ? 'opacity-50' : ''}
        ${className}
      `}
    >
      {loading ? (
        <ActivityIndicator color={variant === 'outline' ? '#FF8C42' : '#FFFFFF'} />
      ) : (
        <Text className={`font-semibold text-base ${textVariants[variant]}`}>
          {title}
        </Text>
      )}
    </TouchableOpacity>
  );
};
```

### 8.2 Create `src/components/ui/Input.tsx`

```typescript
import React from 'react';
import { View, Text, TextInput, TextInputProps } from 'react-native';

interface InputProps extends TextInputProps {
  label?: string;
  error?: string;
}

export const Input: React.FC<InputProps> = ({
  label,
  error,
  className = '',
  ...props
}) => {
  return (
    <View className="mb-4">
      {label && (
        <Text className="text-neutral-700 font-medium mb-2">{label}</Text>
      )}
      <TextInput
        className={`
          bg-neutral-50 border rounded-xl px-4 py-3 text-neutral-900
          ${error ? 'border-red-500' : 'border-neutral-200'}
          ${className}
        `}
        placeholderTextColor="#9CA3AF"
        {...props}
      />
      {error && (
        <Text className="text-red-500 text-sm mt-1">{error}</Text>
      )}
    </View>
  );
};
```

---

## Step 9: Create Login Screen

### Create `app/(auth)/login.tsx`

```typescript
import React from 'react';
import { View, Text, TouchableOpacity, KeyboardAvoidingView, Platform } from 'react-native';
import { useRouter } from 'expo-router';
import { Input } from '../../../src/components/ui/Input';
import { Button } from '../../../src/components/ui/Button';

export default function LoginScreen() {
  const router = useRouter();
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');

  const handleLogin = () => {
    // Implement login logic
    console.log('Login:', { email, password });
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      className="flex-1 bg-white"
    >
      <View className="flex-1 px-6 justify-center">
        {/* Logo */}
        <View className="items-center mb-12">
          <Text className="text-4xl font-bold text-primary-600">Bidzaro</Text>
          <Text className="text-neutral-500 mt-2">Find your perfect catering</Text>
        </View>

        {/* Form */}
        <Input
          label="Email or Phone"
          placeholder="Enter your email or phone"
          value={email}
          onChangeText={setEmail}
          autoCapitalize="none"
          keyboardType="email-address"
        />

        <Input
          label="Password"
          placeholder="Enter your password"
          value={password}
          onChangeText={setPassword}
          secureTextEntry
        />

        <TouchableOpacity
          onPress={() => router.push('/(auth)/forgot-password')}
          className="self-end mb-6"
        >
          <Text className="text-primary-600 font-medium">Forgot Password?</Text>
        </TouchableOpacity>

        <Button
          title="Login"
          onPress={handleLogin}
          className="mb-4"
        />

        <View className="flex-row justify-center mt-4">
          <Text className="text-neutral-600">Don't have an account? </Text>
          <TouchableOpacity onPress={() => router.push('/(auth)/register')}>
            <Text className="text-primary-600 font-semibold">Sign Up</Text>
          </TouchableOpacity>
        </View>
      </View>
    </KeyboardAvoidingView>
  );
}
```

---

## Step 10: Create Home Screen

### Create `app/(tabs)/index.tsx`

```typescript
import React from 'react';
import { View, Text, ScrollView, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

export default function HomeScreen() {
  return (
    <ScrollView className="flex-1 bg-white">
      {/* Header */}
      <View className="px-6 pt-12 pb-6 bg-primary-600">
        <View className="flex-row justify-between items-center">
          <View>
            <Text className="text-white text-lg">Hello,</Text>
            <Text className="text-white text-2xl font-bold">John Doe</Text>
          </View>
          <TouchableOpacity className="bg-white/20 p-3 rounded-full">
            <Ionicons name="notifications-outline" size={24} color="white" />
          </TouchableOpacity>
        </View>
      </View>

      {/* Search Bar */}
      <View className="px-6 -mt-6 mb-6">
        <TouchableOpacity className="bg-white rounded-xl p-4 flex-row items-center shadow-md">
          <Ionicons name="search" size={20} color="#9CA3AF" />
          <Text className="ml-3 text-neutral-400">Search vendors, cuisines...</Text>
        </TouchableOpacity>
      </View>

      {/* Quick Actions */}
      <View className="px-6 mb-6">
        <Text className="text-xl font-bold text-neutral-900 mb-4">Quick Actions</Text>
        <View className="flex-row justify-between">
          <TouchableOpacity className="bg-orange-50 rounded-xl p-4 flex-1 mr-3 items-center">
            <Ionicons name="document-text" size={32} color="#FF8C42" />
            <Text className="text-neutral-700 font-medium mt-2">Create Bid</Text>
          </TouchableOpacity>
          <TouchableOpacity className="bg-orange-50 rounded-xl p-4 flex-1 ml-3 items-center">
            <Ionicons name="search" size={32} color="#FF8C42" />
            <Text className="text-neutral-700 font-medium mt-2">Browse Menu</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* Featured Vendors */}
      <View className="px-6 mb-6">
        <Text className="text-xl font-bold text-neutral-900 mb-4">Featured Vendors</Text>
        <View className="bg-neutral-100 rounded-xl p-8 items-center">
          <Text className="text-neutral-500">No featured vendors yet</Text>
        </View>
      </View>
    </ScrollView>
  );
}
```

---

## Step 11: Run the App

```bash
# Start the development server
npx expo start

# Then:
# - Press 'i' for iOS simulator
# - Press 'a' for Android emulator
# - Scan QR code with Expo Go app on your device
```

---

## Environment Variables

Create `.env` file in root:

```env
API_BASE_URL=http://localhost:8080/api/v1
RAZORPAY_KEY_ID=rzp_test_xxxxx
```

---

## Common Commands

```bash
# Start development server
npx expo start

# Clear cache and start
npx expo start -c

# Run on iOS
npx expo start --ios

# Run on Android
npx expo start --android

# Build for production (iOS)
eas build --platform ios

# Build for production (Android)
eas build --platform android

# Update app
eas update
```

---

## Troubleshooting

### Issue: NativeWind not working
**Solution:** Make sure you've installed NativeWind correctly and configured `babel.config.js`

### Issue: Metro bundler errors
**Solution:** Clear cache with `npx expo start -c`

### Issue: iOS simulator not launching
**Solution:** Make sure Xcode is installed and iOS simulator is set up

### Issue: Android emulator not launching
**Solution:** Make sure Android Studio is installed with an AVD

---

## Next Steps

1. ✅ Project setup complete
2. 📱 Implement authentication flow
3. 🏠 Build home screen with vendors
4. 🛒 Add cart and wishlist functionality
5. 📝 Create bid request feature
6. 📦 Implement order management
7. 💳 Integrate payment gateway
8. 👤 Build user profile
9. 🔔 Add notifications
10. 🎨 Polish UI/UX

---

## Resources

- [Expo Documentation](https://docs.expo.dev/)
- [React Native Documentation](https://reactnative.dev/)
- [NativeWind Documentation](https://www.nativewind.dev/)
- [Redux Toolkit Documentation](https://redux-toolkit.js.org/)
- [React Hook Form](https://react-hook-form.com/)
- [API Documentation](./API_DOCUMENTATION.md)
- [Full Development Guide](./MOBILE_APP_DEVELOPMENT_GUIDE.md)

---

## Support

For issues or questions:
- Check the API documentation
- Review the main development guide
- Contact the development team

---

**Happy Coding! 🚀**

**Remember:** The primary color is **Orange (#FF8C42)** - use it consistently throughout the app!

