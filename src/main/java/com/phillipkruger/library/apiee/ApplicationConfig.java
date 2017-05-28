package com.phillipkruger.library.apiee;

import io.swagger.annotations.SwaggerDefinition;
import javax.ws.rs.ApplicationPath;

import javax.ws.rs.core.Application;

/**
 * Enable REST
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@SwaggerDefinition(basePath = "/api")
@ApplicationPath("/api")
public class ApplicationConfig extends Application {

}
