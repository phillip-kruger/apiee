package com.github.phillipkruger.apiee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
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
    
    @Inject @ApieeClasses
    private Set<Class> apieeClasses;
    
    @Inject 
    private SwaggerCache swaggerCache;
    
    @Inject
    private Templates templates;
    
    @Context
    private UriInfo uriInfo;
    
    @Context 
    private HttpServletRequest request;
            
    @GET
    @Produces("image/png")
    @Path("favicon-{size}.png")
    public byte[] getFavicon(@PathParam("size") int size){
        return templates.getFavicon(size);
    }
    
    @GET
    @Produces("image/png")
    @Path("logo.png")
    public byte[] getLogo(){
        return templates.getOriginalLogo();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(INDEX_HTML)
    public Response getSwaggerUI(@QueryParam("clearCache") @DefaultValue("false") boolean clearCache){
        if(clearCache)swaggerCache.clearCache();
        String swaggerUI = templates.getSwaggerUIHtml(uriInfo,request);
        return Response.ok(swaggerUI, MediaType.TEXT_HTML).build();
    }
    
    @GET
    @Produces("text/css")
    @Path("apiee.css")
    public Response getCss(){
        String css = templates.getStyle();
        return Response.ok(css, "text/css").build();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getSwaggerUINaked(){
        URI fw = uriInfo.getRequestUriBuilder().path(INDEX_HTML).build();
        return Response.temporaryRedirect(fw).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("swagger.json")
    public String getSwaggerJson() {  
        URL url = getOriginalRequestURL(request);
        if(url!=null){
            String json = swaggerCache.getSwaggerJson(getClasses(),url);
            return json;
        }
        return null;
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("swagger.yaml")
    public String getSwaggerYaml() {  
        URL url = getOriginalRequestURL(request);
        if(url!=null){
            return swaggerCache.getSwaggerYaml(getClasses(), url);
        }
        return null;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("generatedOn.json")
    public String getGeneratedOn() {  
        URL url = getSwaggerJsonUrl(getOriginalRequestURL(request));
        if(url!=null){
            Date date = swaggerCache.getGeneratedDate(url);
            if(date!=null)return toJson(date);
        }
        return null;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("cacheMap.json")
    public String getCacheMap(){
        Map<Integer, CachedDocument> swaggerMap = swaggerCache.getSwaggerMap();
        return toJson(swaggerMap);
    }
    
    @DELETE
    public void clearCache(){
        swaggerCache.clearCache();
    }
    
    private Set<Class<?>> getClasses(){
        
        Set<Class<?>> classes = application.getClasses();
        if(classes!=null && !classes.isEmpty())return classes;    
        
        // Else, let's see what we discovered with Apiee Auto register.
        Set<Class<?>> applicationClasses = new HashSet<>();
        apieeClasses.forEach((c) -> {
            //applicationClasses.add(c);
        });
        
        return applicationClasses;
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
    
    private int getOriginalRequestPort(HttpServletRequest request, String scheme){

        // Try headers
        int portFromHeader = request.getIntHeader(X_CUSTOM_PORT);
        if(portFromHeader!=-1 && portFromHeader>0)return portFromHeader;

        portFromHeader = request.getIntHeader(X_FORWARDED_PORT);
        if(portFromHeader!=-1 && portFromHeader>0 && portFromHeader!=DEFAULT_HTTP_PORT && portFromHeader!=DEFAULT_HTTPS_PORT)return portFromHeader;

        // Try serverPort
        int original = request.getServerPort();
        if(original!=-1 && original>0 && original!=DEFAULT_HTTP_PORT && original!=DEFAULT_HTTPS_PORT)return original;

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
    
    private String getOriginalRequestHost(HttpServletRequest request){
        String original = request.getHeader(X_CUSTOM_HOST);
        if(original!=null && !original.isEmpty())return original;
        
        original = request.getHeader(X_FORWARDED_HOST);
        if(original!=null && !original.isEmpty())return original;
        
        original = request.getServerName();
        if(original!=null && !original.isEmpty())return original;        
        
        try {
            return new URL(request.getRequestURL().toString()).getHost();
        } catch (MalformedURLException ex) {
            log.log(Level.WARNING, "Can not determine URL host - {0}", ex.getMessage());
            return LOCALHOST; // default
        }
    }
    
    private String getOriginalRequestScheme(HttpServletRequest request){
        String original = request.getHeader(X_CUSTOM_PROTO);
        if(original!=null && !original.isEmpty())return original;

        original = request.getHeader(X_FORWARDED_PROTO);
        if(original!=null && !original.isEmpty())return original;
        
        try {
            return new URL(request.getRequestURL().toString()).getProtocol();
        } catch (MalformedURLException ex) {
            log.log(Level.WARNING, "Can not determine URL scheme - {0}", ex.getMessage());
            return HTTP; // default
        }
    }
    
    private String getOriginalPath(HttpServletRequest request){
        String original = request.getHeader(X_REQUEST_URI);
        if(original!=null && !original.isEmpty())return original;
        return request.getRequestURI();
    }   
    
    private String toJson(Map<Integer,CachedDocument> cache){
        ObjectMapper mapper = new ObjectMapper();
 
        ArrayNode arrayNode = mapper.createArrayNode();
 
        Collection<CachedDocument> cachedDocuments = cache.values();
        
        for(CachedDocument cachedDocument : cachedDocuments){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("hash", cachedDocument.getHash());
            objectNode.put("generatedOn", cachedDocument.getGeneratedOn().toString());
            objectNode.put("url", cachedDocument.getUrl().toExternalForm());
            arrayNode.add(objectNode);
        }
        return arrayNode.toString();
 
    }
    
    private String toJson(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("date", date.toString());
        objectNode.put("formattedDate", sdf.format(date));
        return objectNode.toString();
 
    }
    
    private URL getSwaggerJsonUrl(URL sibling){
        try {
            return new URL(sibling,"swagger.json");
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static final String HTTP = "http";
    private static final String INDEX_HTML = "index.html";
    private static final String X_REQUEST_URI = "x-request-uri";
    private static final String X_FORWARDED_PORT = "x-forwarded-port";
    private static final String X_FORWARDED_PROTO = "x-forwarded-proto";
    private static final String X_FORWARDED_HOST = "x-forwarded-host";
    
    private static final String LOCALHOST = "localhost";
    private static final String X_CUSTOM_PORT = "x-custom-port";
    private static final String X_CUSTOM_PROTO = "x-custom-proto";
    private static final String X_CUSTOM_HOST = "x-custom-host";
    
}