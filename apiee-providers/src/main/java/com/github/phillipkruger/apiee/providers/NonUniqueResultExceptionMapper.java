package com.github.phillipkruger.apiee.providers;

import jakarta.persistence.NonUniqueResultException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Translate Java non unique exceptions to HTTP response
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@Provider
public class NonUniqueResultExceptionMapper implements ExceptionMapper<NonUniqueResultException> {
    
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(NonUniqueResultException nonUniqueResultException) {
        return Response.status(Response.Status.CONFLICT).header(REASON,nonUniqueResultException.getMessage()).build();
    }
    
    private static final String REASON = "reason";
}