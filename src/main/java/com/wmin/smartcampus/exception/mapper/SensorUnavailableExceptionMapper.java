package com.wmin.smartcampus.exception.mapper;

import com.wmin.smartcampus.exception.SensorUnavailableException;
import com.wmin.smartcampus.model.ErrorResponse;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

/**
 * Maps SensorUnavailableException → 403 Forbidden.
 */
@Provider
public class SensorUnavailableExceptionMapper
        implements ExceptionMapper<SensorUnavailableException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(SensorUnavailableException ex) {
        ErrorResponse body = new ErrorResponse(
            403,
            "Forbidden",
            ex.getMessage(),
            uriInfo != null ? uriInfo.getPath() : "N/A"
        );
        return Response.status(Response.Status.FORBIDDEN)
                       .entity(body)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
