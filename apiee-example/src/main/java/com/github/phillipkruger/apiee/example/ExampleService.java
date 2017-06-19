package com.github.phillipkruger.apiee.example;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import java.util.logging.Level;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.extern.java.Log;

@Path("/example")
@Produces({MediaType.APPLICATION_JSON}) 
@Consumes({MediaType.APPLICATION_JSON})
@SwaggerDefinition (info = @Info (
                        title = "Example Service",
                        description = "A simple example of apiee",
                        version = "1.0-SNAPSHOT",
                        contact = @Contact (
                            name = "Phillip Kruger", 
                            email = "phillip.kruger@gmail.com", 
                            url = "http://phillip-kruger.com"
                        )
                    )
                )
@Api(value = "Example service")
@Log
public class ExampleService {
    
    @POST
    @ApiOperation(value = "Post some example content", notes = "This will post some json to the server")
    public JsonObject postExample(JsonObject jsonObject) {
        log.log(Level.INFO, "POST: {0}", jsonObject);
        return jsonObject;
    }
    
    @GET
    @ApiOperation(value = "Retrieve some example content", notes = "This will return some json to the client",code = 200)
    public JsonObject getExample(){
        JsonObject jsonObject = Json.createObjectBuilder().add("name", "apiee example").add("url", "https://github.com/phillip-kruger/apiee-example").build();
        log.log(Level.INFO, "GET: {0}", jsonObject);
        return jsonObject;
    }
    
}