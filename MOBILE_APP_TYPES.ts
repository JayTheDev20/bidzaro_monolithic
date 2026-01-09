/**
 * BIDZARO MOBILE APP - TYPESCRIPT TYPE DEFINITIONS
 * Complete type definitions for all user-related features
 */

// ============================================
// AUTH TYPES
// ============================================

export interface LoginRequest {
  identifier: string; // email or phone
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  country?: string;
  referralCode?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserResponse;
}

export interface OtpRequest {
  identifier: string;
  type: 'EMAIL' | 'SMS';
}

export interface OtpVerifyRequest {
  identifier: string;
  otp: string;
  type: 'EMAIL' | 'SMS';
}

// ============================================
// USER TYPES
// ============================================

export interface UserResponse {
  userId: string;
  email: string;
  phone: string;
  userType: UserType;
  firstName: string;
  lastName: string;
  fullName?: string;
  profilePictureUrl?: string;
  dateOfBirth?: string;
  gender?: Gender;
  emailVerified: boolean;
  phoneVerified: boolean;
  twoFactorEnabled: boolean;
  preferredLanguage: string;
  preferredCurrency: string;
  country: string;
  status: UserStatus;
  notificationPreferences?: NotificationPreferences;
  lastLoginAt?: string;
  createdAt: string;
}

export enum UserType {
  USER = 'USER',
  VENDOR = 'VENDOR',
  ADMIN = 'ADMIN',
  SUPPORT = 'SUPPORT',
}

export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  OTHER = 'OTHER',
  PREFER_NOT_TO_SAY = 'PREFER_NOT_TO_SAY',
}

export enum UserStatus {
  PENDING_VERIFICATION = 'PENDING_VERIFICATION',
  ACTIVE = 'ACTIVE',
  SUSPENDED = 'SUSPENDED',
  DEACTIVATED = 'DEACTIVATED',
}

export interface UpdateProfileRequest {
  firstName?: string;
  lastName?: string;
  dateOfBirth?: string;
  gender?: string;
  preferredLanguage?: string;
  preferredCurrency?: string;
  country?: string;
}

export interface NotificationPreferences {
  emailNotifications: EmailNotificationSettings;
  smsNotifications: SmsNotificationSettings;
  pushNotifications: PushNotificationSettings;
  whatsappNotifications: WhatsappNotificationSettings;
}

export interface EmailNotificationSettings {
  orderUpdates: boolean;
  bidUpdates: boolean;
  promotional: boolean;
  newsletter: boolean;
  paymentReminders: boolean;
  securityAlerts: boolean;
}

export interface SmsNotificationSettings {
  orderUpdates: boolean;
  bidUpdates: boolean;
  promotional: boolean;
  otpAlerts: boolean;
}

export interface PushNotificationSettings {
  orderUpdates: boolean;
  bidUpdates: boolean;
  chatMessages: boolean;
  promotional: boolean;
}

export interface WhatsappNotificationSettings {
  orderUpdates: boolean;
  bidUpdates: boolean;
  promotional: boolean;
}

// ============================================
// ADDRESS TYPES
// ============================================

export interface AddressResponse {
  addressId: string;
  userId: string;
  addressType: AddressType;
  label?: string;
  fullName: string;
  phone: string;
  streetAddress: string;
  apartment?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  gpsCoordinates?: GpsCoordinates;
  landmark?: string;
  isDefault: boolean;
  createdAt: string;
  updatedAt: string;
}

export enum AddressType {
  HOME = 'HOME',
  OFFICE = 'OFFICE',
  EVENT_VENUE = 'EVENT_VENUE',
  OTHER = 'OTHER',
}

export interface AddressRequest {
  addressType: AddressType;
  label?: string;
  fullName: string;
  phone: string;
  streetAddress: string;
  apartment?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  latitude?: number;
  longitude?: number;
  landmark?: string;
}

export interface GpsCoordinates {
  latitude: number;
  longitude: number;
}

// ============================================
// VENDOR TYPES
// ============================================

export interface VendorResponse {
  vendorId: string;
  userId: string;
  businessName: string;
  description?: string;
  logoUrl?: string;
  bannerImageUrl?: string;
  cuisineTypes: string[];
  serviceTypes: string[];
  specialties?: string[];
  rating: number;
  reviewCount: number;
  city: string;
  state: string;
  country: string;
  minOrderAmount?: number;
  maxCapacity?: number;
  averageDeliveryTime?: number;
  status: VendorStatus;
  isVerified: boolean;
  createdAt: string;
}

export enum VendorStatus {
  PENDING_APPROVAL = 'PENDING_APPROVAL',
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
}

export interface VendorMenuItemResponse {
  vendorItemId: string;
  vendorId: string;
  masterItemId?: string;
  customName?: string;
  customDescription?: string;
  pricing: MenuItemPricing;
  availability: MenuItemAvailability;
  preparationTimeMinutes?: number;
  customizationOptions?: CustomizationOption[];
  stats?: MenuItemStats;
  status: string;
  createdAt: string;
}

export interface MenuItemPricing {
  basePrice: number;
  currency: string;
  discountPercentage?: number;
  finalPrice: number;
}

export interface MenuItemAvailability {
  isAvailable: boolean;
  unavailableReason?: string;
}

export interface CustomizationOption {
  optionName: string;
  choices: string[];
  isRequired: boolean;
}

export interface MenuItemStats {
  totalOrders: number;
  averageRating: number;
  totalReviews: number;
}

// ============================================
// BID TYPES
// ============================================

export interface BidRequestResponse {
  bidRequestId: string;
  userId: string;
  eventDetails: EventDetails;
  requirements: BidRequirements;
  budget: Budget;
  status: BidRequestStatus;
  bidCount: number;
  acceptedBidId?: string;
  createdAt: string;
  expiresAt?: string;
}

export interface EventDetails {
  eventType: EventType;
  eventDate: string;
  eventTime?: string;
  guestCount: number;
  venue: VenueAddress;
  eventName?: string;
  eventDescription?: string;
}

export enum EventType {
  WEDDING = 'WEDDING',
  BIRTHDAY = 'BIRTHDAY',
  CORPORATE = 'CORPORATE',
  ANNIVERSARY = 'ANNIVERSARY',
  PARTY = 'PARTY',
  CONFERENCE = 'CONFERENCE',
  SEMINAR = 'SEMINAR',
  OTHER = 'OTHER',
}

export interface VenueAddress {
  addressLine1?: string;
  addressLine2?: string;
  city: string;
  state?: string;
  pincode?: string;
  country?: string;
  latitude?: number;
  longitude?: number;
}

export interface BidRequirements {
  cuisinePreferences: string[];
  dietaryRestrictions?: string[];
  serviceType: ServiceType;
  specialRequests?: string;
}

export enum ServiceType {
  BUFFET = 'BUFFET',
  PLATED = 'PLATED',
  LIVE_COUNTER = 'LIVE_COUNTER',
  COCKTAIL = 'COCKTAIL',
  FAMILY_STYLE = 'FAMILY_STYLE',
}

export interface Budget {
  minBudget: number;
  maxBudget: number;
  currency?: string;
}

export enum BidRequestStatus {
  DRAFT = 'DRAFT',
  OPEN = 'OPEN',
  BID_ACCEPTED = 'BID_ACCEPTED',
  COOLING_PERIOD = 'COOLING_PERIOD',
  ORDER_PLACED = 'ORDER_PLACED',
  EXPIRED = 'EXPIRED',
  CANCELLED = 'CANCELLED',
}

export interface CreateBidRequestDTO {
  eventDetails: EventDetails;
  requirements: BidRequirements;
  budget: Budget;
}

export interface VendorBidResponse {
  bidId: string;
  bidRequestId: string;
  vendorId: string;
  vendorName: string;
  vendorRating?: number;
  quotedPrice: QuotedPrice;
  itemizedPricing?: ItemizedPrice[];
  proposal?: string;
  termsAndConditions?: string;
  deliveryDetails?: DeliveryDetails;
  status: BidStatus;
  validUntil?: string;
  submittedAt: string;
}

export interface QuotedPrice {
  currency: string;
  subtotal: number;
  serviceCharge: number;
  taxPercentage: number;
  taxAmount: number;
  totalAmount: number;
}

export interface ItemizedPrice {
  vendorItemId: string;
  itemName: string;
  quantity: number;
  pricePerPlate: number;
  totalPrice: number;
}

export interface DeliveryDetails {
  estimatedSetupTime?: string;
  foodReadyTime?: string;
  cleanupTime?: string;
}

export enum BidStatus {
  SUBMITTED = 'SUBMITTED',
  REVISED = 'REVISED',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED',
  EXPIRED = 'EXPIRED',
  WITHDRAWN = 'WITHDRAWN',
}

// ============================================
// CART TYPES
// ============================================

export interface CartItem {
  cartItemId: string;
  userId: string;
  vendorId: string;
  vendorItemId: string;
  itemName: string;
  quantity: number;
  pricePerPlate: number;
  totalPrice: number;
  customizations?: Customization[];
  addedAt: string;
  updatedAt: string;
}

export interface Customization {
  optionName: string;
  selectedChoice: string;
}

export interface CartGrouped {
  [vendorId: string]: CartItem[];
}

export interface CartSummary {
  itemCount: number;
  subtotal: number;
  total: number;
}

// ============================================
// WISHLIST TYPES
// ============================================

export interface WishlistItem {
  wishlistItemId: string;
  userId: string;
  vendorId: string;
  vendorItemId: string;
  itemName: string;
  pricePerPlate: number;
  imageUrl?: string;
  addedAt: string;
}

// ============================================
// ORDER TYPES
// ============================================

export interface OrderResponse {
  orderId: string;
  userId: string;
  bidRequestId?: string;
  eventDetails: EventDetails;
  vendorOrders: VendorOrder[];
  pricing: OrderPricing;
  paymentDetails: PaymentDetails;
  contactInfo: ContactInfo;
  specialInstructions?: string;
  status: OrderStatus;
  cancellation?: CancellationDetails;
  createdAt: string;
  confirmedAt?: string;
  deliveredAt?: string;
  completedAt?: string;
}

export interface VendorOrder {
  vendorOrderId: string;
  vendorId: string;
  vendorName: string;
  items: OrderItem[];
  subtotal: number;
  serviceCharge: number;
  taxAmount: number;
  totalAmount: number;
  vendorStatus: VendorOrderStatus;
  deliveryStatus: DeliveryStatus;
}

export interface OrderItem {
  vendorItemId: string;
  itemName: string;
  quantity: number;
  pricePerPlate: number;
  totalPrice: number;
}

export enum VendorOrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  IN_PREPARATION = 'IN_PREPARATION',
  READY = 'READY',
  DELIVERED = 'DELIVERED',
  COMPLETED = 'COMPLETED',
}

export enum DeliveryStatus {
  PENDING = 'PENDING',
  ON_THE_WAY = 'ON_THE_WAY',
  DELIVERED = 'DELIVERED',
}

export interface OrderPricing {
  subtotal: number;
  platformFee: number;
  gst: number;
  discount: number;
  totalAmount: number;
  currency: string;
}

export interface PaymentDetails {
  tokenAmount: number;
  remainingAmount: number;
  tokenPaid: boolean;
  finalPaid: boolean;
  paymentMethod?: string;
}

export interface ContactInfo {
  name: string;
  phone: string;
  email: string;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  IN_PROGRESS = 'IN_PROGRESS',
  READY = 'READY',
  IN_TRANSIT = 'IN_TRANSIT',
  DELIVERED = 'DELIVERED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED',
}

export interface CancellationDetails {
  reason: string;
  cancelledBy: string;
  cancelledAt: string;
  refundAmount: number;
  refundStatus: string;
}

// ============================================
// PAYMENT TYPES
// ============================================

export interface PaymentInitiateResponse {
  gatewayOrderId: string;
  amount: number;
  currency: string;
  keyId: string;
}

export interface PaymentVerifyRequest {
  gatewayOrderId: string;
  gatewayPaymentId: string;
  signature: string;
}

export interface PaymentTransaction {
  transactionId: string;
  orderId: string;
  amount: number;
  paymentType: PaymentType;
  status: PaymentStatus;
  paymentMethod?: string;
  gatewayOrderId?: string;
  gatewayPaymentId?: string;
  createdAt: string;
}

export enum PaymentType {
  TOKEN_PAYMENT = 'TOKEN_PAYMENT',
  FINAL_PAYMENT = 'FINAL_PAYMENT',
  REFUND = 'REFUND',
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED',
}

// ============================================
// NOTIFICATION TYPES
// ============================================

export interface NotificationResponse {
  notificationId: string;
  userId: string;
  title: string;
  message: string;
  type: NotificationType;
  data?: NotificationData;
  isRead: boolean;
  createdAt: string;
}

export enum NotificationType {
  ORDER_UPDATE = 'ORDER_UPDATE',
  BID_UPDATE = 'BID_UPDATE',
  PAYMENT_UPDATE = 'PAYMENT_UPDATE',
  PROMOTIONAL = 'PROMOTIONAL',
  SYSTEM = 'SYSTEM',
}

export interface NotificationData {
  orderId?: string;
  bidId?: string;
  vendorId?: string;
  deepLink?: string;
}

// ============================================
// LOYALTY TYPES
// ============================================

export interface LoyaltyBalanceResponse {
  pointsBalance: number;
  lifetimePoints: number;
  tier: LoyaltyTier;
  pointsToNextTier?: number;
  nextTier?: string;
  pointsValue: number;
  earnMultiplier: number;
}

export enum LoyaltyTier {
  BRONZE = 'BRONZE',
  SILVER = 'SILVER',
  GOLD = 'GOLD',
  PLATINUM = 'PLATINUM',
}

export interface LoyaltyTransaction {
  transactionId: string;
  userId: string;
  type: LoyaltyTransactionType;
  points: number;
  description: string;
  referenceId?: string;
  expiresAt?: string;
  createdAt: string;
}

export enum LoyaltyTransactionType {
  EARNED = 'EARNED',
  REDEEMED = 'REDEEMED',
  EXPIRED = 'EXPIRED',
  BONUS = 'BONUS',
  REFUNDED = 'REFUNDED',
}

// ============================================
// REFERRAL TYPES
// ============================================

export interface ReferralCodeResponse {
  code: string;
  shareLink: string;
  status: string;
  totalReferrals: number;
  successfulReferrals: number;
  pendingReferrals: number;
  totalRewardsEarned: number;
}

export interface ReferralStatsResponse {
  referralCode: string;
  totalInvites: number;
  signups: number;
  completedOrders: number;
  pendingRewards: number;
  grantedRewards: number;
  totalPointsEarned: number;
  recentReferrals: ReferralDetail[];
}

export interface ReferralDetail {
  referredUserName: string;
  eventType: string;
  rewardStatus: string;
  rewardPoints: number;
  createdAt: string;
}

// ============================================
// REVIEW TYPES
// ============================================

export interface ReviewResponse {
  reviewId: string;
  orderId: string;
  userId: string;
  userName: string;
  userProfilePicture?: string;
  vendorId: string;
  rating: number;
  comment?: string;
  images?: string[];
  isVerifiedPurchase: boolean;
  createdAt: string;
}

export interface CreateReviewRequest {
  orderId: string;
  vendorId: string;
  rating: number;
  comment?: string;
  images?: string[];
}

// ============================================
// SUPPORT TYPES
// ============================================

export interface SupportTicket {
  ticketId: string;
  ticketNumber: string;
  userId: string;
  subject: string;
  description: string;
  priority: TicketPriority;
  category: TicketCategory;
  status: TicketStatus;
  orderId?: string;
  assignedTo?: string;
  resolutionNotes?: string;
  rating?: number;
  feedback?: string;
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string;
}

export enum TicketPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  URGENT = 'URGENT',
}

export enum TicketCategory {
  ORDER_ISSUE = 'ORDER_ISSUE',
  PAYMENT = 'PAYMENT',
  TECHNICAL = 'TECHNICAL',
  ACCOUNT = 'ACCOUNT',
  FEEDBACK = 'FEEDBACK',
  OTHER = 'OTHER',
}

export enum TicketStatus {
  OPEN = 'OPEN',
  IN_PROGRESS = 'IN_PROGRESS',
  RESOLVED = 'RESOLVED',
  CLOSED = 'CLOSED',
}

export interface CreateTicketRequest {
  subject: string;
  description: string;
  priority: TicketPriority;
  category: TicketCategory;
  orderId?: string;
}

// ============================================
// API RESPONSE WRAPPER
// ============================================

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

// ============================================
// COMMON TYPES
// ============================================

export interface FileUploadResponse {
  fileUrl: string;
  fileName: string;
  fileSize: number;
  mimeType: string;
}

export interface SearchFilters {
  query?: string;
  city?: string;
  cuisineTypes?: string[];
  serviceTypes?: string[];
  minRating?: number;
  minPrice?: number;
  maxPrice?: number;
  isVerified?: boolean;
}

export interface PaginationParams {
  page: number;
  size: number;
  sort?: string;
}

