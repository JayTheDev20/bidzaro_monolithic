# Postman End-to-End Testing Guide

This guide provides a step-by-step flow to test the entire application using Postman. It covers the lifecycle of a user, a vendor, and an order.

---

## **Phase 1: User & Vendor Registration**

### **Step 1: Register a Customer**
*   **Endpoint:** `POST {{baseUrl}}/auth/register`
*   **Description:** Create a standard user account.
*   **Payload:**
    ```json
    {
      "email": "customer@test.com",
      "phone": "+919876543210",
      "password": "Password@123",
      "firstName": "Rahul",
      "lastName": "Sharma",
      "userType": "USER",
      "country": "India"
    }
    ```
*   **Action:** Save the `accessToken` from the response as `{{customerToken}}`.

### **Step 2: Register a Vendor User**
*   **Endpoint:** `POST {{baseUrl}}/auth/register`
*   **Description:** Create a user account intended for a vendor.
*   **Payload:**
    ```json
    {
      "email": "vendor@test.com",
      "phone": "+919876543211",
      "password": "Password@123",
      "firstName": "Suresh",
      "lastName": "Verma",
      "userType": "VENDOR",
      "country": "India"
    }
    ```
*   **Action:** Save the `accessToken` from the response as `{{vendorToken}}`.

### **Step 3: Create Vendor Profile**
*   **Endpoint:** `POST {{baseUrl}}/vendors`
*   **Header:** `Authorization: Bearer {{vendorToken}}`
*   **Description:** Complete the vendor profile setup.
*   **Payload:**
    ```json
    {
      "businessName": "Royal Catering Services",
      "businessEmail": "contact@royalcatering.com",
      "businessPhone": "+919876543211",
      "businessType": "CATERING",
      "description": "Premium wedding catering",
      "cuisinesOffered": ["North Indian", "Chinese"],
      "businessAddress": {
        "streetAddress": "12 MG Road",
        "city": "Bangalore",
        "state": "Karnataka",
        "postalCode": "560001",
        "country": "India"
      },
      "ownerInfo": {
        "firstName": "Suresh",
        "lastName": "Verma",
        "phone": "+919876543211",
        "email": "vendor@test.com"
      },
      "pricing": {
        "currency": "INR",
        "startingPricePerPlate": 500.00
      }
    }
    ```
*   **Response:** Note the `vendorId`.

---

## **Phase 2: Admin Approval**

### **Step 4: Admin Login**
*   **Endpoint:** `POST {{baseUrl}}/auth/login`
*   **Payload:**
    ```json
    {
      "identifier": "admin@bidzaro.com",
      "password": "AdminPassword@123"
    }
    ```
*   **Action:** Save `accessToken` as `{{adminToken}}`.

### **Step 5: Approve Vendor**
*   **Endpoint:** `POST {{baseUrl}}/vendors/{vendorId}/approve`
*   **Header:** `Authorization: Bearer {{adminToken}}`
*   **Description:** Approve the vendor created in Step 3.

---

## **Phase 3: Vendor Menu Setup**

### **Step 6: Add Menu Item**
*   **Endpoint:** `POST {{baseUrl}}/menu/vendor-items`
*   **Header:** `Authorization: Bearer {{vendorToken}}`
*   **Payload:**
    ```json
    {
      "masterItemId": "item_paneer_tikka", 
      "customName": "Special Paneer Tikka",
      "pricePerPlate": 250.00,
      "minimumOrderQuantity": 20,
      "isAvailable": true,
      "preparationTimeMinutes": 45
    }
    ```
*   **Action:** Save `vendorItemId` as `{{itemId}}`.

---

## **Phase 4: Bidding Process**

### **Step 7: Customer Creates Bid Request**
*   **Endpoint:** `POST {{baseUrl}}/bids/requests`
*   **Header:** `Authorization: Bearer {{customerToken}}`
*   **Payload:**
    ```json
    {
      "eventDetails": {
        "eventType": "Wedding Reception",
        "eventName": "Rahul's Wedding",
        "eventDate": "2024-12-25T19:00:00",
        "numberOfGuests": 200,
        "venueAddress": {
          "streetAddress": "Palace Grounds",
          "city": "Bangalore",
          "state": "Karnataka",
          "postalCode": "560001",
          "country": "India"
        }
      },
      "budget": {
        "currency": "INR",
        "estimatedBudget": 100000.00
      },
      "menuItems": [
        {
          "itemName": "Paneer Tikka",
          "quantity": 200
        }
      ]
    }
    ```
*   **Action:** Save `bidRequestId` as `{{bidRequestId}}`.

### **Step 8: Vendor Views Active Bids**
*   **Endpoint:** `GET {{baseUrl}}/bids/active`
*   **Header:** `Authorization: Bearer {{vendorToken}}`
*   **Description:** Verify the new bid request is visible.

### **Step 9: Vendor Submits Bid**
*   **Endpoint:** `POST {{baseUrl}}/bids/requests/{{bidRequestId}}/submit-bid`
*   **Header:** `Authorization: Bearer {{vendorToken}}`
*   **Payload:**
    ```json
    {
      "quotedPrice": {
        "currency": "INR",
        "subtotal": 90000.00,
        "totalAmount": 94500.00
      },
      "itemizedPricing": [
        {
          "itemName": "Paneer Tikka",
          "quantity": 200,
          "pricePerPlate": 450.00,
          "totalPrice": 90000.00
        }
      ],
      "validityPeriodHours": 48
    }
    ```
*   **Action:** Save `bidId` as `{{bidId}}`.

### **Step 10: Customer Accepts Bid**
*   **Endpoint:** `POST {{baseUrl}}/bids/{{bidId}}/accept`
*   **Header:** `Authorization: Bearer {{customerToken}}`
*   **Description:** Customer accepts the vendor's quote.

---

## **Phase 5: Order & Payment**

### **Step 11: Create Order**
*   **Endpoint:** `POST {{baseUrl}}/orders?bidRequestId={{bidRequestId}}`
*   **Header:** `Authorization: Bearer {{customerToken}}`
*   **Description:** Converts the accepted bid into a formal order.
*   **Action:** Save `orderId` as `{{orderId}}`.

### **Step 12: Initiate Token Payment**
*   **Endpoint:** `POST {{baseUrl}}/payments/initiate`
*   **Header:** `Authorization: Bearer {{customerToken}}`
*   **Params:**
    *   `orderId`: `{{orderId}}`
    *   `paymentType`: `TOKEN`
    *   `amount`: `25000` (Partial amount)
*   **Response:** Returns `gatewayOrderId`.

### **Step 13: Verify Payment (Simulated)**
*   **Endpoint:** `POST {{baseUrl}}/payments/verify`
*   **Header:** `Authorization: Bearer {{customerToken}}`
*   **Params:**
    *   `gatewayOrderId`: (From Step 12)
    *   `gatewayPaymentId`: `pay_simulated_123`
    *   `signature`: `simulated_signature`

---

## **Phase 6: Order Fulfillment**

### **Step 14: Vendor Updates Status (Preparation)**
*   **Endpoint:** `PATCH {{baseUrl}}/orders/{{orderId}}/status`
*   **Header:** `Authorization: Bearer {{vendorToken}}`
*   **Params:** `status=IN_PREPARATION`

### **Step 15: Vendor Updates Status (Ready)**
*   **Endpoint:** `PATCH {{baseUrl}}/orders/{{orderId}}/status`
*   **Header:** `Authorization: Bearer {{vendorToken}}`
*   **Params:** `status=READY_FOR_DELIVERY`

### **Step 16: Vendor Updates Status (Completed)**
*   **Endpoint:** `PATCH {{baseUrl}}/orders/{{orderId}}/status`
*   **Header:** `Authorization: Bearer {{vendorToken}}`
*   **Params:** `status=COMPLETED`

---

## **Phase 7: Post-Order**

### **Step 17: Customer Reviews Vendor**
*   **Endpoint:** `POST {{baseUrl}}/reviews`
*   **Header:** `Authorization: Bearer {{customerToken}}`
*   **Payload:**
    ```json
    {
      "orderId": "{{orderId}}",
      "rating": 5,
      "reviewText": "Excellent food and service!",
      "foodQualityRating": 5,
      "serviceQualityRating": 5,
      "hygieneRating": 5,
      "valueForMoneyRating": 4,
      "punctualityRating": 5
    }
    ```

### **Step 18: Check Loyalty Points**
*   **Endpoint:** `GET {{baseUrl}}/loyalty/balance`
*   **Header:** `Authorization: Bearer {{customerToken}}`
*   **Description:** Verify points were earned from the order.
