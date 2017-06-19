package com.github.phillipkruger.apiee.providers;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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