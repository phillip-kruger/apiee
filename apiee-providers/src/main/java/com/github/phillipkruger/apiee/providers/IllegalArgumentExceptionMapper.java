package com.github.phillipkruger.apiee.providers;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Translate Java illegal argument exceptions to HTTP response
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(IllegalArgumentException illegalArgumentException) {
        return Response.status(Response.Status.PRECONDITION_FAILED).header(REASON,illegalArgumentException.getMessage()).build();
    }
    
    private static final String REASON = "reason";
}