package com.github.phillipkruger.apiee.mapper;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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