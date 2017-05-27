package com.phillipkruger.library.apiee;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Properties;
import javax.ejb.Asynchronous;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;

/**
 * Expose System properties as a REST endpoint
 * @author Phillip Kruger (phillip.kruger@gmail.com)
  */
@Path("/properties")
@Api(value = "System properties")
public class SystemPropertiesService {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation ("Get all properties as a JSON Document")
    public Properties getProperties() {
        return System.getProperties();
    }
    
    @GET
    @Path("/{key}")
    @ApiOperation ("Get value of a certain property")
    @Produces(MediaType.TEXT_PLAIN)
    public String getProperty(@NotNull @PathParam("key") String key) {
        Properties properties = System.getProperties();
        if(properties.containsKey(key))return properties.getProperty(key);
        return null;
    }
    
    @PUT
    @Path("/{key}")
    @ApiOperation ("Set the value of a certain property")
    @Consumes(MediaType.TEXT_PLAIN)
    @Asynchronous
    public void setProperty(@ApiParam(value = "The property key", required = true) @NotNull @PathParam("key") String key,
            @ApiParam(value = "The property value", required = true) @NotNull final String value) {
        System.setProperty(key, value);
    }
    
}