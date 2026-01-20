package com.cateringmarketplace.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.concurrent.TimeUnit;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration that builds a MongoClient with configurable timeouts and
 * optional startup ping. Also configures a MongoTemplate with the custom
 * converter that removes the _class field and disables automatic index creation
 * on the mapping context to avoid blocking startup when MongoDB is unreachable.
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.cateringmarketplace")
@EnableMongoAuditing
public class MongoConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${SPRING_DATA_MONGODB_URI:}")
    private String mongoUriEnv;

    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017/catering_platform_db}")
    private String mongoUriDefault;

    @Value("${SPRING_DATA_MONGODB_SERVER_SELECTION_TIMEOUT_MS:10000}")
    private int serverSelectionTimeoutMs;

    @Value("${SPRING_DATA_MONGODB_SOCKET_CONNECT_TIMEOUT_MS:10000}")
    private int socketConnectTimeoutMs;

    @Value("${app.mongodb.failOnStartup:false}")
    private boolean failOnStartup;

    @Value("${app.mongodb.pingOnStartup:false}")
    private boolean pingOnStartup;

    /**
     * Configures a MongoClient bean with custom settings.
     *
     * @return Configured MongoClient instance.
     */
    @Bean
    public MongoClient mongoClient() {
        String connectionString = determineConnectionString();
        log.info("Using MongoDB connection string: {}", maskConnectionString(connectionString));

        ConnectionString cs;
        try {
            cs = new ConnectionString(connectionString);
        } catch (Exception e) {
            log.error("Failed to parse MongoDB connection string: {}", e.getMessage());
            throw new RuntimeException("Invalid MongoDB connection string", e);
        }

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(cs)
                .applyToClusterSettings(b -> b.serverSelectionTimeout(serverSelectionTimeoutMs, TimeUnit.MILLISECONDS))
                .applyToSocketSettings(b -> b.connectTimeout(socketConnectTimeoutMs, TimeUnit.MILLISECONDS)
                        .readTimeout(30000, TimeUnit.MILLISECONDS))
                .build();

        MongoClient client = MongoClients.create(settings);

        if (pingOnStartup) {
            try {
                log.info("Pinging MongoDB to verify connectivity (timeout {} ms)...", serverSelectionTimeoutMs);
                Document ping = new Document("ping", 1);
                client.getDatabase("admin").runCommand(ping);
                log.info("Successfully connected to MongoDB (ping OK)");
            } catch (Exception e) {
                log.warn("Unable to ping MongoDB during startup: {}", e.toString());
                if (failOnStartup) {
                    log.error("app.mongodb.failOnStartup is true — failing application startup due to Mongo connectivity");
                    try { client.close(); } catch (Exception ignore) {}
                    throw new RuntimeException("Failed to connect to MongoDB during startup", e);
                } else {
                    log.warn("Continuing startup in degraded mode; Mongo operations will retry when first used.");
                }
            }
        } else {
            log.debug("app.mongodb.pingOnStartup is false — skipping startup ping to avoid blocking startup.");
        }

        return client;
    }

    /**
     * Configures MongoTemplate with custom converter that removes _class field.
     */
    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory,
                                       MongoMappingContext context) {
        // Ensure mapping context does not try to create indexes at startup.
        try {
            context.setAutoIndexCreation(false);
        } catch (Exception e) {
            log.warn("Could not set auto index creation on MongoMappingContext: {}", e.getMessage());
        }

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, context);
        // Remove _class field from documents
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        
        // Initialize the converter to register default conversions (including GeoJson)
        converter.afterPropertiesSet();
        
        return new MongoTemplate(mongoDbFactory, converter);
    }

    private String determineConnectionString() {
        if (mongoUriEnv != null && !mongoUriEnv.isBlank()) {
            String candidate = mongoUriEnv.trim();
            if (isValidPrefix(candidate)) {
                return candidate;
            } else {
                log.warn("Environment Mongo URI does not start with 'mongodb://' or 'mongodb+srv://', ignoring and falling back to default. Value: {}", maskConnectionString(candidate));
            }
        }
        if (mongoUriDefault != null && !mongoUriDefault.isBlank()) {
            if (isValidPrefix(mongoUriDefault.trim())) {
                return mongoUriDefault.trim();
            }
        }
        return "mongodb://localhost:27017/catering_platform_db";
    }

    private boolean isValidPrefix(String uri) {
        String lower = uri.toLowerCase();
        return lower.startsWith("mongodb://") || lower.startsWith("mongodb+srv://");
    }

    private String maskConnectionString(String uri) {
        if (uri == null) return "(null)";
        try {
            int at = uri.indexOf('@');
            if (at > 0) {
                int schemeEnd = uri.indexOf("://");
                if (schemeEnd >= 0) {
                    String prefix = uri.substring(0, schemeEnd + 3);
                    String suffix = uri.substring(at + 1);
                    return prefix + "***@" + suffix;
                }
            }
            return uri;
        } catch (Exception e) {
            return "(unparsable)";
        }
    }
}
