package com.github.phillipkruger.apiee;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.extern.java.Log;

/**
 * Ping service for all REST services (to check that REST has been activated)
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@Path("/ping")
@Api(value = "Available check")
@Log
public class PingService {
    
    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @ApiOperation ("Just return pong")
    public String ping(){
        log.info(PONG);
        return PONG;
    }
    private static final String PONG = "pong";
}
