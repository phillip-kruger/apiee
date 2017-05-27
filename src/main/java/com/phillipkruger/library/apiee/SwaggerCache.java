package com.phillipkruger.library.apiee;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.jaxrs.Reader;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import lombok.extern.java.Log;

/**
 * Caching already created swagger docs.
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 * @see http://swagger.io/specification/
 */
@Log
@ApplicationScoped
public class SwaggerCache {  
    private final Map<Integer,String> swaggerMap = new ConcurrentHashMap<>();
    
    public String getSwaggerJson(final Set<Class<?>> classes,@NotNull final URL url) {
        int hash = url.toExternalForm().hashCode();
        if(swaggerMap.containsKey(hash)){
            // Load from cache
            log.log(Level.FINEST, "Loading {0} from cache", url);
            return swaggerMap.get(hash);
        }else{
            return generateJson(hash,classes,url);
        }
    }
    
    public String getSwaggerYaml(final Set<Class<?>> classes,@NotNull final URL url) {  
        int hash = url.toExternalForm().hashCode();
        if(swaggerMap.containsKey(hash)){
            // Load from cache
            log.log(Level.FINEST, "Loading {0} from cache", url);
            return swaggerMap.get(hash);
        }else{
            return generateYaml(hash,classes,url);
        }
    }
    
    private String generateJson(int hash,final Set<Class<?>> classes,URL url){
        log.log(Level.FINEST, "Generating {0} response for context", url);
        Swagger swagger = createSwagger(classes,url);
        try {
            String swaggerJson = Json.pretty().writeValueAsString(swagger);
            swaggerMap.put(hash, swaggerJson);
            return swaggerJson;
        } catch (JsonProcessingException ex) {
            log.log(Level.WARNING, "Could not generate {0} - {1}", new Object[]{url.toString(), ex.getMessage()});
        }
        log.log(Level.WARNING, "Could not generate {0} - null", new Object[]{url.toString()});
        return null;
    }
    
    private String generateYaml(int hash,final Set<Class<?>> classes,URL url){
        log.log(Level.FINEST, "Generating {0} response for context", url);
        Swagger swagger = createSwagger(classes,url);
        try {
            String swaggerYaml = Yaml.pretty().writeValueAsString(swagger);
            swaggerMap.put(hash, swaggerYaml);
            return swaggerYaml;
        } catch (JsonProcessingException ex) {
            log.log(Level.WARNING, "Could not generate {0} - {1}", new Object[]{url.toString(), ex.getMessage()});
        }
        log.log(Level.WARNING, "Could not generate {0} - null", new Object[]{url.toString()});
        return null;
    }
    
    private Swagger createSwagger(final Set<Class<?>> classes,final URL url){
        Swagger swagger = new Reader(new Swagger()).read(classes);
        swagger.addScheme(Scheme.forValue(url.getProtocol().toUpperCase()));
        swagger.setHost(url.getHost() + DOUBLE_POINT + url.getPort());
        swagger.setBasePath(getBasePath(swagger.getBasePath(), url));
        return swagger;
    }
    
    private String getBasePath(final String existingBasePath,final URL url){
        String path = url.getPath();
        if(existingBasePath!=null && !existingBasePath.isEmpty()){
            return path.substring(0, path.indexOf(existingBasePath)) + existingBasePath;
        }else{
            String filename = getFilename(url);
            return path.substring(0, path.indexOf(filename));
        }
    }
    
    private String getFilename(URL u){
        String url = u.toString();
        return url.substring( url.lastIndexOf(SLASH)+1, url.length() );
    }
    
    private static final String DOUBLE_POINT = ":";
    private static final String SLASH = "/";
    
}