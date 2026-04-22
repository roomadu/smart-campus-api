package com.wmin.smartcampus.filter;

import javax.ws.rs.container.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.*;

/**
 * Cross-cutting observability filter.
 * Logs every inbound request and every outbound response status.
 * Implements BOTH ContainerRequestFilter and ContainerResponseFilter.
 */
@Provider
public class RequestResponseLoggingFilter
        implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG =
            Logger.getLogger(RequestResponseLoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext req) throws IOException {
        LOG.info(String.format("[REQUEST ] %s %s",
                req.getMethod(),
                req.getUriInfo().getRequestUri()));
    }

    @Override
    public void filter(ContainerRequestContext req,
                       ContainerResponseContext res) throws IOException {
        LOG.info(String.format("[RESPONSE] %s %s -> HTTP %d",
                req.getMethod(),
                req.getUriInfo().getRequestUri(),
                res.getStatus()));
    }
}
