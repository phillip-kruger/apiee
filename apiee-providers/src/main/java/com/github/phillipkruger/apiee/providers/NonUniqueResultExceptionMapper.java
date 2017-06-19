package com.github.phillipkruger.apiee.providers;

import javax.persistence.NonUniqueResultException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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