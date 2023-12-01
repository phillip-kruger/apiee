package com.github.phillipkruger.apiee.providers;

import jakarta.persistence.EntityNotFoundException;
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
public class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {
    
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(EntityNotFoundException entityNotFoundException) {
        return Response.status(Response.Status.NOT_FOUND).header(REASON,entityNotFoundException.getMessage()).build();
    }
    
    private static final String REASON = "reason";
}