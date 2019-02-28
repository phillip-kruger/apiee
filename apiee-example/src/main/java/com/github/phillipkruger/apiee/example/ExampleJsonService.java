package com.github.phillipkruger.apiee.example;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Scanner;
import java.util.logging.Level;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import lombok.extern.java.Log;

@Path("/example/json")
@Produces({MediaType.APPLICATION_JSON}) 
@Consumes({MediaType.APPLICATION_JSON})
@Log
public class ExampleJsonService {
    
    @Context
    private UriInfo uriInfo;  
    
    @Context
    private HttpServletRequest request;
    
    @POST
    @Operation(description = "Post some example content", summary = "This will post some json to the server")
    @ApiResponses({
        @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_CREATED, description = "Successfully done something"),
        @ApiResponse(responseCode = "" + HttpURLConnection.HTTP_PRECON_FAILED, description = "Input validation failed, see reason header",headers = @Header(name = "reason"))
    })
    public Response postExample(JsonObject jsonObject) {
        log.log(Level.INFO, "POST: {0}", jsonObject);
        return Response.created(uriInfo.getRequestUri()).build();
    }
    
    @GET
    @Operation(description = "Retrieve some example content", summary = "This will return some json to the client")
    public Response getExample(){
        JsonObject jsonObject = Json.createObjectBuilder().add("name", "apiee example").add("url", "https://github.com/phillip-kruger/apiee-example").build();
        log.log(Level.INFO, "GET: {0}", jsonObject);
        return Response.ok(jsonObject).build();
    }
    
    @DELETE
    @Operation(description = "Delete some example content", summary = "This will delete some data")
    public void deleteExample(){
        log.log(Level.INFO, "DELETE");
    }
    
    
    @GET
    @Path("/header")
    @Operation(description = "Pass header value in", summary = "Some header info")
//    @ApiImplicitParams({ @ApiImplicitParam(name = "authorization",
//                                         value = "Some info.",
//                                         dataType = "string",
//                                         paramType = "header") })
    public String headerExample(){
        String header = request.getHeader("authorization");
        return "You have passes [" + header + "] in the authorization header";
    }
    
    @POST
    @Path("/upload")
    @Operation(description = "Upload some json file and print to string", summary = "Upload example")
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