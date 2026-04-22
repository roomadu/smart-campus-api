package com.wmin.smartcampus.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

/**
 * Part 1.2 – Discovery Endpoint
 * GET /api/v1  returns API metadata including versioning, contact, and resource links.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response discover(@Context UriInfo uriInfo) {
        String base = uriInfo.getBaseUri().toString();

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("service",     "Smart Campus Sensor & Room Management API");
        meta.put("version",     "1.0.0");
        meta.put("description", "RESTful API for monitoring IoT sensors and managing campus facilities.");
        meta.put("contact",     "admin@wmin.ac.uk");
        meta.put("basePath",    "/api/v1");

        Map<String, String> links = new LinkedHashMap<>();
        links.put("rooms",   base + "rooms");
        links.put("sensors", base + "sensors");
        meta.put("resources", links);

        return Response.ok(meta).build();
    }
}
