package com.wmin.smartcampus.exception.mapper;

import com.wmin.smartcampus.exception.LinkedResourceNotFoundException;
import com.wmin.smartcampus.model.ErrorResponse;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

/**
 * Maps LinkedResourceNotFoundException → 422 Unprocessable Entity.
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper
        implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        ErrorResponse body = new ErrorResponse(
            422,
            "Unprocessable Entity",
            ex.getMessage(),
            uriInfo != null ? uriInfo.getPath() : "N/A"
        );
        return Response.status(422)
                       .entity(body)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
