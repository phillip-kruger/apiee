package com.github.phillipkruger.apiee;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import lombok.extern.java.Log;

/**
 * This service creates a swagger document and enhance it with some context
 * (Mostly making it relative to the URL)
 
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 * @see http://swagger.io/specification/
 * @see http://stackoverflow.com/questions/20937362/what-objects-can-i-inject-using-the-context-annotation
 */
@Log
@Path("/")
@Api(value = "Open API Service")
@SwaggerDefinition (info = @Info (
                        title = "Apiee",
                        description = "OpenAPI for Java EE",
                        version = "1.0-0-SNAPSHOT",
                        contact = @Contact (
                            name = "Phillip Kruger", 
                            email = "apiee@phillip-kruger.com", 
                            url = "https://github.com/phillip-kruger/apiee"
                        )
                    )
                )
public class OpenAPIService {  
    
    @Context
    private Application application;
    
    @Inject 
    private SwaggerCache swaggerCache;
    
    @GET
    @ApiOperation ("Creating apiee swagger.json file")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("apiee/swagger.json")
    public String getApieeSwaggerJson(@Context HttpServletRequest request) {
        log.info("apiee/swagger.json");
        URL url = getOriginalRequestURL(request);
        if(url!=null){
            return swaggerCache.getSwaggerJson(getApieeClasses(),url);
        }
        return null;
    }
    
    @GET
    @ApiOperation ("Creating apiee swagger.yaml file")
    @Produces(MediaType.TEXT_PLAIN)
    @Path("apiee/swagger.yaml")
    public String getApieeSwaggerYaml(@Context HttpServletRequest request) {  
        log.info("apiee/swagger.yaml");
        URL url = getOriginalRequestURL(request);
        if(url!=null){
            return swaggerCache.getSwaggerYaml(getApieeClasses(), url);
        }
        return null;
    }
    
    @GET
    @ApiOperation ("Creating system swagger.json file")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("swagger.json")
    public String getSwaggerJson(@Context HttpServletRequest request) {  
        log.info("swagger.json");
        URL url = getOriginalRequestURL(request);
        log.severe("url = " + url);
        if(url!=null){
            String json = swaggerCache.getSwaggerJson(getClasses(),url);
            log.severe("json = " + json);
            return json;
        }
        return null;
    }
    
    @GET
    @ApiOperation ("Creating system swagger.yaml file")
    @Produces(MediaType.TEXT_PLAIN)
    @Path("swagger.yaml")
    public String getSwaggerYaml(@Context HttpServletRequest request) {  
        log.info("swagger.yaml");
        URL url = getOriginalRequestURL(request);
        if(url!=null){
            return swaggerCache.getSwaggerYaml(getClasses(), url);
        }
        return null;
    }
    
    private Set<Class<?>> getClasses() {
        Set<Class<?>> apieeClasses = getApieeClasses();
        Set<Class<?>> appClasses = new HashSet<>();
        // Remove all apiee classes from this swagger doc
        application.getClasses().stream().filter((c) -> (!apieeClasses.contains(c))).forEachOrdered((c) -> {
            appClasses.add(c);
        });
        return appClasses;
    }
    
    // Known rest-lib classes
    private Set<Class<?>> getApieeClasses(){        
        Set<Class<?>> apieeClasses = new HashSet<>();
        apieeClasses.add(ApplicationConfig.class);
        apieeClasses.add(PingService.class);
        apieeClasses.add(OpenAPIService.class);
        return apieeClasses;
    }
    
    private URL getOriginalRequestURL(HttpServletRequest request){
        try {
            String path = getOriginalPath(request);
            String scheme = getOriginalRequestScheme(request);
            String host = getOriginalRequestHost(request);
            int port = getOriginalRequestPort(request,scheme);
            URL u = new URL(scheme,host,port,path);
            return u;
        } catch (MalformedURLException ex) {
            log.warning("Could not determine URL for swagger.json");
        }
        return null;
    }
    
    private static final String X_FORWARDED_PORT = "x-forwarded-port";
    private int getOriginalRequestPort(HttpServletRequest request, String scheme){
        // Try serverPort
        int original = request.getServerPort();
        if(original!=-1 && original>0 && original!=DEFAULT_HTTP_PORT && original!=DEFAULT_HTTPS_PORT)return original;        
        
        // Try header 
        int portFromHeader = request.getIntHeader(X_FORWARDED_PORT);
        if(portFromHeader!=-1 && portFromHeader>0 && portFromHeader!=DEFAULT_HTTP_PORT && portFromHeader!=DEFAULT_HTTPS_PORT)return portFromHeader;
        
        // Try Url
        try {
            int portFromUrl = new URL(request.getRequestURL().toString()).getPort();
            if(portFromUrl!=-1 && portFromUrl>0 && portFromUrl!=DEFAULT_HTTP_PORT && portFromUrl!=DEFAULT_HTTPS_PORT)return portFromUrl;        
            
            return getDefaultPort(scheme);
        } catch (MalformedURLException ex) {
            log.log(Level.WARNING, "Can not determine URL port - {0}", ex.getMessage());
            return getDefaultPort(scheme); // default
        }
    }
    
    
    private static final int DEFAULT_HTTP_PORT = 80;
    private static final int DEFAULT_HTTPS_PORT = 443;
    private int getDefaultPort(String scheme){
        if(scheme==null || scheme.isEmpty() || scheme.equalsIgnoreCase(HTTP))return DEFAULT_HTTP_PORT;
        return DEFAULT_HTTPS_PORT;
    }
    
    private static final String LOCALHOST = "localhost";
    private String getOriginalRequestHost(HttpServletRequest request){
        String original = request.getServerName();
        if(original!=null && !original.isEmpty())return original;        
        try {
            return new URL(request.getRequestURL().toString()).getHost();
        } catch (MalformedURLException ex) {
            log.log(Level.WARNING, "Can not determine URL host - {0}", ex.getMessage());
            return LOCALHOST; // default
        }
    }
    
    private static final String X_FORWARDED_PROTO = "x-forwarded-proto";
    private static final String HTTP = "http";
    private String getOriginalRequestScheme(HttpServletRequest request){
        String original = request.getHeader(X_FORWARDED_PROTO);
        if(original!=null && !original.isEmpty())return original;
        try {
            return new URL(request.getRequestURL().toString()).getProtocol();
        } catch (MalformedURLException ex) {
            log.log(Level.WARNING, "Can not determine URL scheme - {0}", ex.getMessage());
            return HTTP; // default
        }
    }
    
    private static final String X_REQUEST_URI = "x-request-uri";
    private String getOriginalPath(HttpServletRequest request){
        String original = request.getHeader(X_REQUEST_URI);
        if(original!=null && !original.isEmpty())return original;
        return request.getRequestURI();
    }
}