package com.github.phillipkruger.apiee;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import lombok.extern.java.Log;

/**
 * This service creates a swagger document and enhance it with some context
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@Log
@Path("/apiee/")
public class ApieeService {  
    
    @Context
    private Application application;
    
    @Inject 
    private SwaggerCache swaggerCache;
    
    @Inject
    private Templates templates;
    
    @GET
    @Produces("image/png")
    @Path("favicon-{size}.png")
    public byte[] getFavicon(@Context HttpServletRequest request,@PathParam("size") int size){
        return templates.getFavicon(size);
    }
    
    @GET
    @Produces("image/png")
    @Path("logo.png")
    public byte[] getLogo(@Context HttpServletRequest request){
        return templates.getOriginalLogo();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("index.html")
    public Response getSwaggerUI(@Context HttpServletRequest request){
        String swaggerUI = templates.getSwaggerUIHtml(request);
        return Response.ok(swaggerUI, MediaType.TEXT_HTML).build();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getSwaggerUINaked(@Context UriInfo info){
        URI fw = info.getRequestUriBuilder().path("index.html").build();
        return Response.temporaryRedirect(fw).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("swagger.json")
    public String getSwaggerJson(@Context HttpServletRequest request) {  
        log.severe("swagger.json");
        URL url = getOriginalRequestURL(request);
        if(url!=null){
            Set<Class<?>> classes = application.getClasses();
            String json = swaggerCache.getSwaggerJson(classes,url);
            return json;
        }
        return null;
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("swagger.yaml")
    public String getSwaggerYaml(@Context HttpServletRequest request) {  
        log.severe("swagger.yaml");
        URL url = getOriginalRequestURL(request);
        if(url!=null){
            Set<Class<?>> classes = application.getClasses();
            return swaggerCache.getSwaggerYaml(classes, url);
        }
        return null;
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