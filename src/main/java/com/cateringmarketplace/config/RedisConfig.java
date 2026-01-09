package com.cateringmarketplace.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis configuration class.
 * Configures Redis connection, caching, and serialization.
 */
@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${app.cache.vendor-list-ttl:900}")
    private long vendorListTtl;

    @Value("${app.cache.menu-items-ttl:1800}")
    private long menuItemsTtl;

    @Value("${app.cache.platform-config-ttl:3600}")
    private long platformConfigTtl;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        if (redisPassword != null && !redisPassword.isEmpty()) {
            config.setPassword(redisPassword);
        }
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        try {
            factory.afterPropertiesSet();
            // Test connection using Lettuce client to log immediate connectivity
            String redisUri = String.format("redis://%s:%d", redisHost, redisPort);
            if (redisPassword != null && !redisPassword.isEmpty()) {
                redisUri = String.format("redis://default:%s@%s:%d", redisPassword, redisHost, redisPort);
            }
            try (RedisClient client = RedisClient.create(redisUri)) {
                var conn = client.connect();
                RedisCommands<String, String> commands = conn.sync();
                String pong = commands.ping();
                log.info("Redis ping response during startup: {}", pong);
                conn.close();
            } catch (Exception e) {
                log.warn("Redis ping failed during startup (ping may still work later): {}", e.getMessage());
            }
        } catch (Exception e) {
            log.warn("Failed to initialize LettuceConnectionFactory: {}", e.getMessage());
        }
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Vendor list cache - 15 minutes
        cacheConfigurations.put("vendors", defaultConfig.entryTtl(Duration.ofSeconds(vendorListTtl)));

        // Menu items cache - 30 minutes
        cacheConfigurations.put("menuItems", defaultConfig.entryTtl(Duration.ofSeconds(menuItemsTtl)));
        cacheConfigurations.put("categories", defaultConfig.entryTtl(Duration.ofSeconds(menuItemsTtl)));

        // Platform config cache - 1 hour
        cacheConfigurations.put("platformConfig", defaultConfig.entryTtl(Duration.ofSeconds(platformConfigTtl)));

        // User sessions cache - 24 hours
        cacheConfigurations.put("userSessions", defaultConfig.entryTtl(Duration.ofHours(24)));

        // Rate limiting cache - 1 minute
        cacheConfigurations.put("rateLimits", defaultConfig.entryTtl(Duration.ofMinutes(1)));

        // OTP cache - 10 minutes
        cacheConfigurations.put("otpCache", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
