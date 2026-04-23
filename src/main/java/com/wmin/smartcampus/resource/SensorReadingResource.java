package com.wmin.smartcampus.resource;

import com.wmin.smartcampus.data.DataStore;
import com.wmin.smartcampus.exception.SensorUnavailableException;
import com.wmin.smartcampus.model.ErrorResponse;
import com.wmin.smartcampus.model.Sensor;
import com.wmin.smartcampus.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.UUID;

/**
 * Part 4 – Sub-Resource for sensor readings.
 * Returned by the sub-resource locator in SensorResource.
 * Handles /api/v1/sensors/{sensorId}/readings
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // ---- 4.2 GET /sensors/{sensorId}/readings  (history) -------------------
    @GET
    public Response getHistory() {
        return Response.ok(DataStore.getReadings(sensorId)).build();
    }

    // ---- 4.2 POST /sensors/{sensorId}/readings  (new reading) --------------
    @POST
    public Response addReading(SensorReading reading, @Context UriInfo uriInfo) {

        // FIX: null check FIRST — before any access on the reading object
        if (reading == null) {
            ErrorResponse err = new ErrorResponse(400, "Bad Request",
                    "Reading body is required.", uriInfo.getPath());
            return Response.status(400).entity(err).type(MediaType.APPLICATION_JSON).build();
        }

        Sensor sensor = DataStore.getSensor(sensorId);
        if (sensor == null) {
            ErrorResponse err = new ErrorResponse(404, "Not Found",
                    "Sensor '" + sensorId + "' was not found.", uriInfo.getPath());
            return Response.status(404).entity(err).type(MediaType.APPLICATION_JSON).build();
        }

        // Block if sensor is in MAINTENANCE status (returns 403 Forbidden)
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId);
        }

        // Auto-fill fields if missing
        if (reading.getReadingId() == null || reading.getReadingId().isBlank()) {
            reading.setReadingId("RD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        reading.setSensorId(sensorId);
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }
        if (reading.getUnit() == null || reading.getUnit().isEmpty()) {
            reading.setUnit(sensor.getUnit());
        }

        // Persist reading
        DataStore.addReading(sensorId, reading);

        // Side-effect: update parent sensor's currentValue for data consistency
        sensor.setCurrentValue(reading.getValue());

        return Response.status(201).entity(reading).build();
    }
}
