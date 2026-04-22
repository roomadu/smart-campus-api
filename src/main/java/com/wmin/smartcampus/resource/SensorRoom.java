package com.wmin.smartcampus.resource;

import com.wmin.smartcampus.data.DataStore;
import com.wmin.smartcampus.exception.RoomNotEmptyException;
import com.wmin.smartcampus.model.ErrorResponse;
import com.wmin.smartcampus.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

/**
 * Part 2 – Room Management
 * SensorRoom resource at /api/v1/rooms  (spec names the class "SensorRoom Resource")
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoom {

    // ---- 2.1 GET /rooms  (list all) ----------------------------------------
    @GET
    public Response getAllRooms() {
        return Response.ok(DataStore.getAllRooms().values()).build();
    }

    // ---- 2.1 POST /rooms  (create) -----------------------------------------
    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null || room.getRoomId() == null || room.getRoomId().isBlank()) {
            ErrorResponse err = new ErrorResponse(400, "Bad Request",
                    "roomId is required.", uriInfo.getPath());
            return Response.status(400).entity(err).type(MediaType.APPLICATION_JSON).build();
        }
        if (DataStore.roomExists(room.getRoomId())) {
            ErrorResponse err = new ErrorResponse(409, "Conflict",
                    "A room with id '" + room.getRoomId() + "' already exists.", uriInfo.getPath());
            return Response.status(409).entity(err).type(MediaType.APPLICATION_JSON).build();
        }
        DataStore.putRoom(room);
        URI location = uriInfo.getAbsolutePathBuilder().path(room.getRoomId()).build();
        return Response.status(201).entity(room).location(location).build();
    }

    // ---- 2.1 GET /rooms/{roomId}  (detail) ---------------------------------
    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId, @Context UriInfo uriInfo) {
        Room room = DataStore.getRoom(roomId);
        if (room == null) {
            ErrorResponse err = new ErrorResponse(404, "Not Found",
                    "Room '" + roomId + "' does not exist.", uriInfo.getPath());
            return Response.status(404).entity(err).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(room).build();
    }

    // ---- 2.2 DELETE /rooms/{roomId}  (decommission) ------------------------
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId, @Context UriInfo uriInfo) {
        if (!DataStore.roomExists(roomId)) {
            ErrorResponse err = new ErrorResponse(404, "Not Found",
                    "Room '" + roomId + "' does not exist.", uriInfo.getPath());
            return Response.status(404).entity(err).type(MediaType.APPLICATION_JSON).build();
        }
        // Business constraint: block deletion if sensors are still linked (returns 409)
        if (DataStore.roomHasSensors(roomId)) {
            throw new RoomNotEmptyException(roomId);
        }
        DataStore.deleteRoom(roomId);
        return Response.noContent().build();  // 204
    }
}
