package com.github.phillipkruger.apiee.example;

import io.swagger.annotations.SwaggerDefinition;
import javax.ws.rs.ApplicationPath;

import javax.ws.rs.core.Application;

/**
 * Enable REST
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@SwaggerDefinition(basePath = "/api")
@ApplicationPath("/api")
public class ApplicationConfig extends Application {

}