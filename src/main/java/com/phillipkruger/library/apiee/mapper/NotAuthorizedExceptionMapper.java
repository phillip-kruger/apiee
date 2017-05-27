package com.phillipkruger.library.apiee.mapper;

import javax.ejb.AccessLocalException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Translate Java entity not found exceptions to HTTP response
 * @author Phillip Kruger (phillip.kruger@gmail.com)
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