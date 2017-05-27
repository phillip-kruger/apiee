package com.phillipkruger.library.apiee.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Translate Java illegal argument exceptions to HTTP response
 * @author Phillip Kruger (phillip.kruger@gmail.com)
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