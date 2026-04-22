package com.wmin.smartcampus.exception.mapper;

import com.wmin.smartcampus.model.ErrorResponse;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import java.util.logging.*;

/**
 * Global safety-net mapper.
 * Catches ALL Throwable instances so no raw stack-trace ever reaches the client.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable t) {
        // Let JAX-RS handle known HTTP exceptions natively (404, 405, etc.)
        if (t instanceof WebApplicationException) {
            return ((WebApplicationException) t).getResponse();
        }

        LOG.log(Level.SEVERE, "Unhandled exception at " +
                (uriInfo != null ? uriInfo.getPath() : "?"), t);

        ErrorResponse body = new ErrorResponse(
            500,
            "Internal Server Error",
            "An unexpected error occurred. The incident has been logged.",
            uriInfo != null ? uriInfo.getPath() : "N/A"
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(body)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
