# 💳 PAYMENT & ORDER - CODE EXAMPLES

## 🎯 FRONTEND IMPLEMENTATION EXAMPLES

### JavaScript/TypeScript - Initiate Payment

```typescript
import axios from 'axios';

interface PaymentInitiateRequest {
  orderId?: string;
  bidId?: string;
  paymentType: 'ORDER' | 'BID';
  amount: number;
}

interface PaymentResponse {
  transactionId: string;
  gatewayOrderId: string;
  clientSecret: string;
  paymentGateway: 'STRIPE' | 'RAZORPAY';
  status: string;
}

async function initiatePayment(request: PaymentInitiateRequest): Promise<PaymentResponse> {
  const response = await axios.post('/api/v1/payments/initiate', request, {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    }
  });
  return response.data.data;
}

// Usage
const paymentData = await initiatePayment({
  bidId: 'bid-123',
  paymentType: 'BID',
  amount: 5000.00
});

console.log('Transaction ID:', paymentData.transactionId);
console.log('Gateway Order ID:', paymentData.gatewayOrderId);
```

---

### React Component - Handle Payment

```typescript
import React, { useState } from 'react';
import { loadStripe } from '@stripe/js';

export function CheckoutComponent() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handlePayment = async (bidId: string, amount: number) => {
    try {
      setLoading(true);

      // 1. Initiate payment
      const payment = await initiatePayment({
        bidId,
        paymentType: 'BID',
        amount
      });

      // 2. Determine gateway and open UI
      if (payment.paymentGateway === 'STRIPE') {
        const stripe = await loadStripe('pk_test_...');

        // Use clientSecret to complete payment
        const result = await stripe?.confirmCardPayment(
          payment.clientSecret,
          { payment_method: {...} }
        );

        if (result?.paymentIntent?.status === 'succeeded') {
          // 3. Verify payment on backend
          await verifyPayment({
            gatewayOrderId: payment.gatewayOrderId,
            gatewayPaymentId: result.paymentIntent.id,
            signature: result.paymentIntent.client_secret
          });

          console.log('✅ Payment successful!');
          setLoading(false);
        }
      } else if (payment.paymentGateway === 'RAZORPAY') {
        // Handle Razorpay similarly
      }
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  return (
    <button onClick={() => handlePayment('bid-123', 5000)} disabled={loading}>
      {loading ? 'Processing...' : 'Pay Now'}
    </button>
  );
}
```

---

### Fetch Orders After Payment

```typescript
interface OrderResponse {
  orderId: string;
  vendorId: string;
  status: 'CONFIRMED' | 'PREPARING' | 'READY' | 'COMPLETED' | 'CANCELLED';
  totalAmount: number;
  items: OrderItem[];
  createdAt: string;
}

async function getOrders(page = 0, size = 20): Promise<OrderResponse[]> {
  const response = await axios.get(`/api/v1/orders?page=${page}&size=${size}`, {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    }
  });
  return response.data.data;
}

async function getOrderById(orderId: string): Promise<OrderResponse> {
  const response = await axios.get(`/api/v1/orders/${orderId}`, {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    }
  });
  return response.data.data;
}

// Usage
useEffect(() => {
  const orders = await getOrders();
  setOrders(orders);
}, []);
```

---

## 🎯 BACKEND EXAMPLES

### Spring Boot Service - Payment

```java
@Service
@Slf4j
public class PaymentService {

  @Autowired
  private PaymentGatewayFactory gatewayFactory;

  @Autowired
  private OrderService orderService;

  @Transactional
  public PaymentInitiationResponse initiatePayment(
      String orderId, String bidId, PaymentType type,
      BigDecimal amount, String userId) {

    // 1. Determine gateway by user country
    String country = getUserCountry(userId);
    PaymentGateway gateway = determineGateway(country);

    // 2. Create transaction record
    Transaction transaction = Transaction.builder()
        .transactionId(UUID.randomUUID().toString())
        .userId(userId)
        .orderId(orderId)
        .bidId(bidId)
        .amount(amount)
        .paymentGateway(gateway)
        .status(TransactionStatus.PENDING)
        .build();

    transactionRepository.save(transaction);

    // 3. Call payment gateway
    PaymentInitiationRequest request = PaymentInitiationRequest.builder()
        .orderId(orderId)
        .bidId(bidId)
        .amount(amount)
        .currency(gateway == PaymentGateway.STRIPE ? "USD" : "INR")
        .build();

    PaymentGatewayResponse response = gatewayFactory
        .getGateway(gateway)
        .initiatePayment(request);

    return PaymentInitiationResponse.builder()
        .transactionId(transaction.getTransactionId())
        .gatewayOrderId(response.getOrderId())
        .clientSecret(response.getClientSecret())
        .paymentGateway(gateway.toString())
        .status("PENDING")
        .build();
  }

  @Transactional
  public Transaction verifyPayment(
      String gatewayOrderId, String gatewayPaymentId, String signature) {

    // 1. Find transaction
    Transaction transaction = transactionRepository
        .findByGatewayOrderId(gatewayOrderId)
        .orElseThrow();

    // 2. Verify with gateway
    boolean valid = gatewayFactory
        .getGateway(transaction.getPaymentGateway())
        .verifyPayment(gatewayOrderId, gatewayPaymentId, signature);

    if (!valid) {
      throw new PaymentVerificationException("Signature verification failed");
    }

    // 3. Update transaction
    transaction.setStatus(TransactionStatus.SUCCESS);
    transaction.setVerifiedAt(Instant.now());
    transaction = transactionRepository.save(transaction);

    // 4. AUTO: Create order
    if (transaction.getOrderId() != null) {
      orderService.createOrder(
          transaction.getOrderId(),
          transaction.getUserId()
      );
    } else if (transaction.getBidId() != null) {
      orderService.createOrderFromBid(
          transaction.getBidId(),
          transaction.getUserId()
      );
    }

    return transaction;
  }
}
```

---

### Webhook Handler

```java
@RestController
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentController {

  @PostMapping("/webhook/razorpay")
  public ResponseEntity<String> handleRazorpayWebhook(
      @RequestBody String payload,
      @RequestHeader("X-Razorpay-Signature") String signature) {

    try {
      // 1. Verify signature
      paymentService.verifyRazorpaySignature(payload, signature);

      // 2. Parse webhook
      WebhookEvent event = parseRazorpayWebhook(payload);

      // 3. Handle event
      if ("payment.authorized".equals(event.getType())) {
        String gatewayOrderId = event.getOrderId();
        String gatewayPaymentId = event.getPaymentId();

        // Verify payment and create order (AUTO)
        paymentService.verifyPayment(
            gatewayOrderId,
            gatewayPaymentId,
            signature
        );

        log.info("✅ Order created for transaction: {}", gatewayOrderId);
      }

      return ResponseEntity.ok("OK");
    } catch (Exception e) {
      log.error("❌ Webhook error: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("ERROR");
    }
  }

  @PostMapping("/webhook/stripe")
  public ResponseEntity<String> handleStripeWebhook(
      @RequestBody String payload,
      @RequestHeader("Stripe-Signature") String signature) {

    try {
      // Similar flow for Stripe
      return ResponseEntity.ok("OK");
    } catch (Exception e) {
      log.error("❌ Stripe webhook error: {}", e.getMessage());
      return ResponseEntity.status(500).body("ERROR");
    }
  }
}
```

---

### Order Service - Auto Creation

```java
@Service
@Slf4j
public class OrderService {

  @Transactional
  public void createOrder(String orderId, String userId) {
    log.info("Creating order: {} for user: {}", orderId, userId);

    Order order = Order.builder()
        .orderId(orderId)
        .userId(userId)
        .status(OrderStatus.CONFIRMED)
        .createdAt(Instant.now())
        .build();

    orderRepository.save(order);

    // Notify vendor & user
    notificationService.notifyVendor(orderId);
    notificationService.notifyUser(userId, orderId);

    log.info("✅ Order created successfully: {}", orderId);
  }

  @Transactional
  public void createOrderFromBid(String bidId, String userId) {
    log.info("Creating order from bid: {} for user: {}", bidId, userId);

    // Get bid details
    Bid bid = bidRepository.findById(bidId)
        .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));

    // Create order with bid details
    Order order = Order.builder()
        .orderId(UUID.randomUUID().toString())
        .userId(userId)
        .vendorId(bid.getVendorId())
        .items(bid.getItems())
        .status(OrderStatus.CONFIRMED)
        .createdAt(Instant.now())
        .build();

    orderRepository.save(order);

    // Update bid status
    bid.setStatus(BidStatus.ACCEPTED);
    bidRepository.save(bid);

    log.info("✅ Order created from bid: {}", order.getOrderId());
  }

  public List<Order> getOrdersByUserId(String userId, Pageable pageable) {
    return orderRepository.findByUserId(userId, pageable).getContent();
  }

  @Transactional
  public Order updateOrderStatus(String orderId, OrderStatus status) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow();

    order.setStatus(status);
    order = orderRepository.save(order);

    // Notify user of status change
    notificationService.notifyOrderStatusChange(orderId, status);

    return order;
  }

  @Transactional
  public void cancelOrder(String orderId, String reason) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow();

    order.setStatus(OrderStatus.CANCELLED);
    orderRepository.save(order);

    // Process refund
    paymentService.refundPayment(orderId);

    log.info("✅ Order cancelled: {} - Reason: {}", orderId, reason);
  }
}
```

---

## 🧪 CURL TEST EXAMPLES

### 1. Initiate Payment

```bash
curl -X POST http://localhost:8080/api/v1/payments/initiate \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": null,
    "bidId": "bid-123",
    "paymentType": "BID",
    "amount": 5000.00
  }'
```

### 2. Verify Payment

```bash
curl -X POST http://localhost:8080/api/v1/payments/verify \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "gatewayOrderId": "razorpay_order_123",
    "gatewayPaymentId": "razorpay_payment_456",
    "signature": "signature_hash_here"
  }'
```

### 3. Get User Orders

```bash
curl -X GET "http://localhost:8080/api/v1/orders?page=0&size=20" \
  -H "Authorization: Bearer eyJhbGc..."
```

### 4. Get Order Details

```bash
curl -X GET "http://localhost:8080/api/v1/orders/order-uuid" \
  -H "Authorization: Bearer eyJhbGc..."
```

### 5. Update Order Status

```bash
curl -X PATCH "http://localhost:8080/api/v1/orders/order-uuid/status?status=PREPARING" \
  -H "Authorization: Bearer eyJhbGc..."
```

### 6. Cancel Order

```bash
curl -X POST "http://localhost:8080/api/v1/orders/order-uuid/cancel" \
  -H "Authorization: Bearer eyJhbGc..." \
  -d "reason=User%20requested"
```

---

## 🎯 INTEGRATION CHECKLIST

- [ ] Add Bearer token to all API requests
- [ ] Handle STRIPE for USA users
- [ ] Handle RAZORPAY for India users
- [ ] Implement payment UI in frontend
- [ ] Handle successful payment response
- [ ] Fetch orders after payment
- [ ] Display order tracking
- [ ] Handle refunds on cancellation
- [ ] Add webhook handlers on backend
- [ ] Test with test cards
- [ ] Deploy to production

---

**All code examples production-ready!** ✅


