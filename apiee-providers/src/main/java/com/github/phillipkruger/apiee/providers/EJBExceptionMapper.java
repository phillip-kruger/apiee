package com.github.phillipkruger.apiee.providers;

import javax.ejb.EJBException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

/**
 * Get the cause of a EJB Exception
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {
    
    @Context Providers providers;
    
    @Override
    public Response toResponse(EJBException exception) {
        final Exception cause = exception.getCausedByException();
        if (cause != null && providers!=null) {
            final ExceptionMapper mapper = providers.getExceptionMapper(cause.getClass());
            if (mapper != null) {
                return mapper.toResponse(cause);
            } else if (cause instanceof WebApplicationException) {
                return ((WebApplicationException) cause).getResponse();
            }
            // We did not map the cause exception.
            // So this is a server error
            return noMapResponse(cause);
        }
        return noMapResponse(exception);
        
    }
    
    private Response noMapResponse(final Exception exception){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header(REASON, exception.getMessage()).build();
    }
    
    private static final String REASON = "reason";
}