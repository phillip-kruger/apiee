package com.github.phillipkruger.apiee.example;

import io.swagger.annotations.SwaggerDefinition;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;

import javax.ws.rs.core.Application;

/**
 * Enable REST
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@SwaggerDefinition(basePath = "/api")
@ApplicationPath("/api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(ExampleService.class);
        return classes;
    }

}
