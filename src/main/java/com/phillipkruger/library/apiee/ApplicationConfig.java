package com.phillipkruger.library.apiee;

import io.swagger.annotations.SwaggerDefinition;
import lombok.extern.java.Log;

import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;

/**
 * Enable REST
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@Log
@SwaggerDefinition(basePath = "/api")
@ApplicationPath("/api")
public class ApplicationConfig extends Application {

}