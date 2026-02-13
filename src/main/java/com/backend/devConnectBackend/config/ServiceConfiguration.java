package com.backend.devConnectBackend.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for service layer beans.
 * Circular dependency between PostService and ReactionService is resolved
 * using @Lazy annotation.
 */
@Configuration
public class ServiceConfiguration {
    // Circular dependency resolved using @Lazy in PostService constructor
}
