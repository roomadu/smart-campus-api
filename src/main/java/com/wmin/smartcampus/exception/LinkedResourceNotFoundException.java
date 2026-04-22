package com.wmin.smartcampus.exception;

/**
 * Thrown when a sensor POST references a roomId that does not exist.
 * Mapped → HTTP 422 Unprocessable Entity
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    private final String resourceType;
    private final String resourceId;

    public LinkedResourceNotFoundException(String resourceType, String resourceId) {
        super(resourceType + " with id '" + resourceId + "' was not found in the system.");
        this.resourceType = resourceType;
        this.resourceId   = resourceId;
    }

    public String getResourceType() { return resourceType; }
    public String getResourceId()   { return resourceId;   }
}
