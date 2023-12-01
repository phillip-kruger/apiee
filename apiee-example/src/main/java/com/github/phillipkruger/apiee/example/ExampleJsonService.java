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
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
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