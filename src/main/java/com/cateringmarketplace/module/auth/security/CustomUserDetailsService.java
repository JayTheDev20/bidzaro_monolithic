package com.cateringmarketplace.module.auth.security;

import com.cateringmarketplace.common.constant.ErrorMessages;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService implementation for authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "users", key = "#email")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));

        return new CustomUserDetails(user);
    }

    /**
     * Loads user by user ID.
     */
    public UserDetails loadUserByUserId(String userId) {
        log.debug("Loading user by userId: {}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        return new CustomUserDetails(user);
    }
}
