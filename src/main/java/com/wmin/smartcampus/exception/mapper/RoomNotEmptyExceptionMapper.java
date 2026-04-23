package com.wmin.smartcampus.exception.mapper;

import com.wmin.smartcampus.exception.RoomNotEmptyException;
import com.wmin.smartcampus.model.ErrorResponse;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

/**
 * Maps RoomNotEmptyException to 409 Conflict with JSON body.
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        ErrorResponse body = new ErrorResponse(
            409,
            "Conflict",
            ex.getMessage(),
            uriInfo != null ? uriInfo.getPath() : "N/A"
        );
        return Response.status(Response.Status.CONFLICT)
                       .entity(body)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
