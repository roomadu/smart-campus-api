package com.wmin.smartcampus.resource;

import com.wmin.smartcampus.data.DataStore;
import com.wmin.smartcampus.exception.LinkedResourceNotFoundException;
import com.wmin.smartcampus.model.ErrorResponse;
import com.wmin.smartcampus.model.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Part 3 – Sensor Operations & Linking
 * Manages /api/v1/sensors
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    // ---- 3.2 GET /sensors  (optional ?type= filter) -------------------------
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        Collection<Sensor> all = DataStore.getAllSensors().values();
        if (type == null || type.isBlank()) {
            return Response.ok(all).build();
        }
        List<Sensor> filtered = all.stream()
                .filter(s -> type.equalsIgnoreCase(s.getType()))
                .collect(Collectors.toList());
        return Response.ok(filtered).build();
    }

    // ---- 3.1 POST /sensors  (create, with roomId validation) ---------------
    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null) {
            ErrorResponse err = new ErrorResponse(400, "Bad Request",
                    "Request body is missing.", uriInfo.getPath());
            return Response.status(400).entity(err).type(MediaType.APPLICATION_JSON).build();
        }
        if (sensor.getRoomId() == null || sensor.getRoomId().isBlank()) {
            ErrorResponse err = new ErrorResponse(400, "Bad Request",
                    "roomId is required.", uriInfo.getPath());
            return Response.status(400).entity(err).type(MediaType.APPLICATION_JSON).build();
        }
        // Dependency validation: roomId must exist, otherwise throws 422
        if (!DataStore.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room", sensor.getRoomId());
        }
        if (sensor.getSensorId() == null || sensor.getSensorId().isBlank()) {
            sensor.setSensorId("SENS-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        if (sensor.getStatus() == null || sensor.getStatus().isBlank()) {
            sensor.setStatus("ACTIVE");
        }
        DataStore.putSensor(sensor);
        URI location = uriInfo.getAbsolutePathBuilder().path(sensor.getSensorId()).build();
        return Response.status(201).entity(sensor).location(location).build();
    }

    // ---- GET /sensors/{sensorId} -------------------------------------------
    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId, @Context UriInfo uriInfo) {
        Sensor s = DataStore.getSensor(sensorId);
        if (s == null) {
            ErrorResponse err = new ErrorResponse(404, "Not Found",
                    "Sensor '" + sensorId + "' was not found.", uriInfo.getPath());
            return Response.status(404).entity(err).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(s).build();
    }

    // ---- DELETE /sensors/{sensorId} ----------------------------------------
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId, @Context UriInfo uriInfo) {
        if (!DataStore.sensorExists(sensorId)) {
            ErrorResponse err = new ErrorResponse(404, "Not Found",
                    "Sensor '" + sensorId + "' was not found.", uriInfo.getPath());
            return Response.status(404).entity(err).type(MediaType.APPLICATION_JSON).build();
        }
        DataStore.deleteSensor(sensorId);
        return Response.noContent().build();
    }

    // ---- Part 4.1: Sub-Resource Locator ------------------------------------
    // Path: /sensors/{sensorId}/readings  delegates to SensorReadingResource
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        if (!DataStore.sensorExists(sensorId)) {
            throw new NotFoundException("Sensor '" + sensorId + "' was not found.");
        }
        return new SensorReadingResource(sensorId);
    }
}
