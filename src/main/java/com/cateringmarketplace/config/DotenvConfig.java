package com.cateringmarketplace.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Loads environment variables from .env file into Spring Environment.
 * This runs before application.yml is processed.
 */
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            // Load .env file - it will automatically set System properties
            Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing()
                    .systemProperties()
                    .load();

            System.out.println("✓ Loaded .env file successfully");
        } catch (Exception e) {
            System.out.println("⚠ .env file not found or could not be loaded: " + e.getMessage());
            System.out.println("  Using default values from application.yml");
        }
    }
}
