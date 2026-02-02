package com.cateringmarketplace.config;

import com.cateringmarketplace.common.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the application.
 * Configures JWT-based authentication, CORS, and endpoint security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/auth/register",
            "/auth/login",
            "/auth/refresh-token",
            "/auth/forgot-password",
            "/auth/reset-password",
            "/auth/verify-email",
            "/auth/resend-verification",
            "/auth/send-otp",
            "/auth/verify-otp",
            "/auth/recover/**",
            "/vendors/auth/**",
            "/vendors/{id}",
            "/vendors/search",
            "/vendors/nearby",
            "/vendors/{id}/menu",
            "/vendors/{id}/reviews",
            "/menu/items",
            "/menu/items/{id}",
            "/menu/categories",
            "/menu/search",
            "/reviews",
            "/reviews/{id}",
            "/reviews/vendor/{vendorId}",
            "/payments/webhook/**",
            "/ws/**",
            "/actuator/**",
            "/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    private static final String[] ADMIN_ENDPOINTS = {
            "/admin/**",
            "/api/v1/admin/**"
    };

    private static final String[] VENDOR_ENDPOINTS = {
            "/vendors/{id}/analytics",
            "/menu/vendor-items/**",
            "/analytics/vendor/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow preflight requests (OPTIONS) through security so browser CORS works
                        .requestMatchers(HttpMethod.OPTIONS, "**").permitAll()
                        // Allow public GET access to vendor listing and search endpoints
                        .requestMatchers(HttpMethod.GET,
                                "/vendors",
                                "/vendors/*",
                                "/vendors/search",
                                "/vendors/nearby",
                                "/vendors/*/menu",
                                "/vendors/*/reviews"
                        ).permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/menu/**").permitAll()
                        //.requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                        //.requestMatchers("/support/tickets/{id}/assign").hasAnyRole("ADMIN", "SUPPORT_AGENT")
                        .anyRequest().authenticated() // Changed from permitAll() to authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // If allowedOrigins is set to '*' in env/config, allow all origins via origin patterns
        // For development, we can be permissive
        configuration.setAllowedOriginPatterns(List.of("*"));

        // Allow all methods and headers from frontend
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(maxAge);
        // Expose common headers including Authorization so frontend can read them
        configuration.setExposedHeaders(List.of("Authorization", "X-Total-Count", "X-Page-Number", "X-Page-Size"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply to all API paths
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/", configuration);
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
