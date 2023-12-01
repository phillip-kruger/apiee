package com.github.phillipkruger.apiee.example;

import io.swagger.v3.oas.annotations.Operation;
import java.util.logging.Level;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.extern.java.Log;

@Path("/example/xml")
@Produces({MediaType.APPLICATION_XML}) 
@Consumes({MediaType.APPLICATION_XML})
@Log
public class ExampleXmlService {
    
    @Context
    private UriInfo uriInfo;  
    
    @Context
    private HttpServletRequest request;
    
    @POST
    @Operation(description = "Post some example content", summary = "This will post some object to the server")
    public Response postExample(SomeObject someObject) {
        log.log(Level.INFO, "POST: {0}", someObject);
        return Response.created(uriInfo.getRequestUri()).build();
    }
    
    @GET
    @Operation(description = "Retrieve some example content", summary = "This will return some object to the client")
    public Response getExample(){
        SomeObject object = new SomeObject("apiee example","https://github.com/phillip-kruger/apiee-example");
        log.log(Level.INFO, "GET: {0}", object);
        return Response.ok(object).build();
    }
    
}