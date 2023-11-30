package com.github.phillipkruger.apiee.providers;

import jakarta.ejb.AccessLocalException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Translate Java entity not found exceptions to HTTP response
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<AccessLocalException> {
    
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(AccessLocalException accessLocalException) {
        return Response.status(Response.Status.UNAUTHORIZED).header(REASON,accessLocalException.getMessage()).build();
    }
    
    private static final String REASON = "reason";
}