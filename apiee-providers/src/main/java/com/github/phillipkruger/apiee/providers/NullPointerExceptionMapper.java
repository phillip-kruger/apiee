package com.github.phillipkruger.apiee.providers;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Translate Java null pointer exceptions to HTTP response
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 * TODO: Should this rather do a 204 ?? (No content)
 */
@Provider
public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException> {
    
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(NullPointerException nullPointerException) {
        return Response.status(Response.Status.NOT_FOUND).header(REASON,nullPointerException.getMessage()).build();
    }
    
    private static final String REASON = "reason";
}