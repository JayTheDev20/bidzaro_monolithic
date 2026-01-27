package com.cateringmarketplace.module.user.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.auth.dto.response.UserResponse;
import com.cateringmarketplace.module.auth.model.NotificationPreferences;
import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.model.enums.Gender;
import com.cateringmarketplace.module.auth.model.enums.UserType;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.user.dto.request.AddressRequest;
import com.cateringmarketplace.module.user.dto.request.UpdateProfileRequest;
import com.cateringmarketplace.module.user.dto.response.AddressResponse;
import com.cateringmarketplace.module.user.model.Address;
import com.cateringmarketplace.module.user.model.Address.AddressType;
import com.cateringmarketplace.module.user.repository.AddressRepository;

/**
 * Service class for user profile and address operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    // =========================================================
    // PROFILE OPERATIONS
    // =========================================================

    public UserResponse getProfile(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.fromEntity(user);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable, String role) {
        Page<User> users;
        if (role != null && !role.isEmpty()) {
            try {
                UserType userType = UserType.valueOf(role.toUpperCase());
                users = userRepository.findByUserType(userType, pageable);
            } catch (IllegalArgumentException e) {
                users = userRepository.findAll(pageable);
            }
        } else {
            users = userRepository.findAll(pageable);
        }
        return users.map(UserResponse::fromEntity);
    }

    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request, String userId) {

        log.info("Updating profile for user: {}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());

        if (request.getGender() != null) {
            try {
                user.setGender(Gender.valueOf(request.getGender().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        if (request.getPreferredLanguage() != null) user.setPreferredLanguage(request.getPreferredLanguage());
        if (request.getPreferredCurrency() != null) user.setPreferredCurrency(request.getPreferredCurrency());
        if (request.getCountry() != null) user.setCountry(request.getCountry());

        user = userRepository.save(user);
        log.info("Profile updated for user: {}", userId);

        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateProfilePicture(String imageUrl, String userId) {

        log.info("Updating profile picture for user: {}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setProfilePictureUrl(imageUrl);
        user = userRepository.save(user);

        return UserResponse.fromEntity(user);
    }

    // =========================================================
    // NOTIFICATION PREFERENCES
    // =========================================================

    public NotificationPreferences getNotificationPreferences(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getNotificationPreferences();
    }

    @Transactional
    public NotificationPreferences updateNotificationPreferences(
            NotificationPreferences preferences, String userId) {

        log.info("Updating notification preferences for user: {}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setNotificationPreferences(preferences);
        userRepository.save(user);

        return preferences;
    }

    // =========================================================
    // ADDRESS OPERATIONS
    // =========================================================

    public List<AddressResponse> getAddresses(String userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDesc(userId)
                .stream()
                .map(AddressResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public AddressResponse getAddress(String addressId, String userId) {
        Address address = addressRepository.findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        return AddressResponse.fromEntity(address);
    }

    @Transactional
    public AddressResponse addAddress(AddressRequest request, String userId) {

        log.info("Adding address for user: {}", userId);

        Address address = Address.builder()
                .addressId(UUID.randomUUID().toString())
                .userId(userId)
                .label(request.getLabel())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .streetAddress(request.getStreetAddress())
                .apartment(request.getApartment())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry() != null ? request.getCountry() : "India")
                .landmark(request.getLandmark())
                .isDefault(false)
                .build();

        // Parse address type
        if (request.getAddressType() != null) {
            try {
                address.setAddressType(
                        AddressType.valueOf(request.getAddressType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                address.setAddressType(AddressType.OTHER);
            }
        }

        // GPS
        if (request.getLatitude() != null && request.getLongitude() != null) {
            address.setGpsCoordinates(
                    new GeoJsonPoint(request.getLongitude(), request.getLatitude()));
        }

        // Handle default address
        List<Address> existingAddresses = addressRepository.findByUserId(userId);
        if (existingAddresses.isEmpty()
                || (request.getIsDefault() != null && request.getIsDefault())) {

            for (Address addr : existingAddresses) {
                if (addr.getIsDefault()) {
                    addr.setIsDefault(false);
                    addressRepository.save(addr);
                }
            }
            address.setIsDefault(true);
        }

        address = addressRepository.save(address);
        log.info("Address added: {}", address.getAddressId());

        return AddressResponse.fromEntity(address);
    }

    @Transactional
    public AddressResponse updateAddress(
            String addressId, AddressRequest request, String userId) {

        log.info("Updating address: {} for user: {}", addressId, userId);

        Address address = addressRepository.findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (request.getLabel() != null) address.setLabel(request.getLabel());
        if (request.getFullName() != null) address.setFullName(request.getFullName());
        if (request.getPhone() != null) address.setPhone(request.getPhone());
        if (request.getStreetAddress() != null) address.setStreetAddress(request.getStreetAddress());
        if (request.getApartment() != null) address.setApartment(request.getApartment());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getState() != null) address.setState(request.getState());
        if (request.getPostalCode() != null) address.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null) address.setCountry(request.getCountry());
        if (request.getLandmark() != null) address.setLandmark(request.getLandmark());

        if (request.getAddressType() != null) {
            try {
                address.setAddressType(
                        AddressType.valueOf(request.getAddressType().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        if (request.getLatitude() != null && request.getLongitude() != null) {
            address.setGpsCoordinates(
                    new GeoJsonPoint(request.getLongitude(), request.getLatitude()));
        }

        address = addressRepository.save(address);
        return AddressResponse.fromEntity(address);
    }

    @Transactional
    public void deleteAddress(String addressId, String userId) {

        log.info("Deleting address: {} for user: {}", addressId, userId);

        Address address = addressRepository.findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (Boolean.TRUE.equals(address.getIsDefault())) {
            throw new BadRequestException(
                    "CANNOT_DELETE_DEFAULT",
                    "Cannot delete default address. Set another address as default first.");
        }

        addressRepository.delete(address);
    }

    @Transactional
    public AddressResponse setDefaultAddress(String addressId, String userId) {

        log.info("Setting default address: {} for user: {}", addressId, userId);

        Address address = addressRepository.findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        // Unset other defaults
        List<Address> allAddresses = addressRepository.findByUserId(userId);
        for (Address addr : allAddresses) {
            if (Boolean.TRUE.equals(addr.getIsDefault())
                    && !addr.getAddressId().equals(addressId)) {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            }
        }

        address.setIsDefault(true);
        address = addressRepository.save(address);

        return AddressResponse.fromEntity(address);
    }
}
