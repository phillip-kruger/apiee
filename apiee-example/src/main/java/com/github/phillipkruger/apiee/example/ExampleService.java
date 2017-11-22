package com.github.phillipkruger.apiee.example;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.logging.Level;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import lombok.extern.java.Log;


//You can define the definition here or in apiee.properties.
//@SwaggerDefinition (info = @Info (
//                        title = "Example Service",
//                        description = "A simple example of apiee",
//                        version = "1.0.0",
//                        contact = @Contact (
//                            name = "Phillip Kruger", 
//                            email = "apiee@phillip-kruger.com", 
//                            url = "http://phillip-kruger.com"
//                        )
//                    )
//                )
@Path("/example")
@Produces({MediaType.APPLICATION_JSON}) 
@Consumes({MediaType.APPLICATION_JSON})
@Api(value = "Example service")
@Log
public class ExampleService {
    
    @Context
    private UriInfo uriInfo;  
    
    @Context
    private HttpServletRequest request;
    
    @POST
    @ApiOperation(value = "Post some example content", notes = "This will post some json to the server")
    public Response postExample(JsonObject jsonObject) {
        log.log(Level.INFO, "POST: {0}", jsonObject);
        return Response.created(uriInfo.getRequestUri()).build();
    }
    
    @GET
    @ApiOperation(value = "Retrieve some example content", notes = "This will return some json to the client",response = JsonObject.class)
    public Response getExample(){
        JsonObject jsonObject = Json.createObjectBuilder().add("name", "apiee example").add("url", "https://github.com/phillip-kruger/apiee-example").build();
        log.log(Level.INFO, "GET: {0}", jsonObject);
        return Response.ok(jsonObject).build();
    }
    
    @DELETE
    @ApiOperation(value = "Delete some example content", notes = "This will delete some data")
    public void deleteExample(){
        log.log(Level.INFO, "DELETE");
    }
    
    
    @GET
    @Path("/header")
    @ApiOperation(value = "Pass header value in", notes = "Some header info")
    @ApiImplicitParams({ @ApiImplicitParam(name = "authorization",
                                         value = "Some info.",
                                         dataType = "string",
                                         paramType = "header") })
    public String headerExample(){
        String header = request.getHeader("authorization");
        return "You have passes [" + header + "] in the authorization header";
    }
}