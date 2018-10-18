package com.github.phillipkruger.apiee.example;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import lombok.extern.java.Log;

@Path("/example/json")
@Produces({MediaType.APPLICATION_JSON}) 
@Consumes({MediaType.APPLICATION_JSON})
@Api(value = "Example JSON service")
@Log
public class ExampleJsonService {
    
    @Context
    private UriInfo uriInfo;  
    
    @Context
    private HttpServletRequest request;
    
    @POST
    @ApiOperation(value = "Post some example content", notes = "This will post some json to the server")
    @ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_CREATED, message = "Successfully done something"),
        @ApiResponse(code = HttpURLConnection.HTTP_PRECON_FAILED, message = "Input validation failed, see reason header",responseHeaders = @ResponseHeader(name = "reason",response = String.class))
    })
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
    
    @POST
    @Path("/upload")
    @ApiOperation(value = "Upload some json file and print to string", notes = "Upload example")
    public Response uploadJson(@FormParam("file") File uploadedFile){
        
        try(FileInputStream fis = new FileInputStream(uploadedFile)){
        
            String contents = convertStreamToString(fis);
        
            return Response.accepted(contents).build();
        } catch (IOException ex) {
            ex.printStackTrace();
            return Response.serverError().build();
        } 
    }
    
    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    
}