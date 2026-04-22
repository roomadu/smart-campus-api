package com.wmin.smartcampus.app;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Part 1.1 – Application configuration.
 * Subclasses javax.ws.rs.core.Application via ResourceConfig.
 * Sets the versioned base path and enables package scanning.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {

    public SmartCampusApplication() {
        // Automatically discovers all @Provider and @Path classes in this package tree
        packages("com.wmin.smartcampus");
        // Enable Jackson JSON serialisation
        register(JacksonFeature.class);
    }
}
