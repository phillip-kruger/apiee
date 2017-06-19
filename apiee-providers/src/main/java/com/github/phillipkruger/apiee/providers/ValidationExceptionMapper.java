package com.github.phillipkruger.apiee.providers;

import java.util.Iterator;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Translate Java validation exceptions to HTTP response
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    private static final String EMPTY = "";
    
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(ConstraintViolationException constraintViolationException) {
        Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
        
        ValidationErrors errors = new ValidationErrors();
        // Real jsr validation errors
        if(violations!=null && !violations.isEmpty()){
            Iterator<ConstraintViolation<?>> i = violations.iterator();
            while (i.hasNext()) {
                ConstraintViolation<?> violation = i.next();
                if(violation!=null){
                    ValidationError ve = new ValidationError();
                    ve.setMessage(violation.getMessage());
                    if(violation.getPropertyPath()!=null)ve.setPath(violation.getPropertyPath().toString());
                    if(violation.getInvalidValue()!=null)ve.setInvalidValue(violation.getInvalidValue().toString());
                    errors.getValidationError().add(ve);
                }
            }// We throw the exception
        }else{
           String message = constraintViolationException.getMessage();
           errors.getValidationError().add(new ValidationError(message,EMPTY,EMPTY));
        }
        return Response.status(Response.Status.PRECONDITION_FAILED).header(REASON, constraintViolationException.getMessage()).entity(errors).build();
    }
    
    private static final String REASON = "reason";
}
