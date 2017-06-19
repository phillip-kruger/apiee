package com.github.phillipkruger.apiee;

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
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@Log
@ApplicationScoped
public class SwaggerCache {  
    private final Map<Integer,String> swaggerMap = new ConcurrentHashMap<>();
    
    public String getSwaggerJson(final Set<Class<?>> classes,@NotNull final URL url) {
        int hash = url.toExternalForm().hashCode();
        if(swaggerMap.containsKey(hash)){
            // Load from cache
            log.log(Level.INFO, "Loading {0} from cache", url);
            return swaggerMap.get(hash);
        }else{
            return generateJson(hash,classes,url);
        }
    }
    
    public String getSwaggerYaml(final Set<Class<?>> classes,@NotNull final URL url) {  
        int hash = url.toExternalForm().hashCode();
        if(swaggerMap.containsKey(hash)){
            // Load from cache
            log.log(Level.INFO, "Loading {0} from cache", url);
            return swaggerMap.get(hash);
        }else{
            return generateYaml(hash,classes,url);
        }
    }
    
    private String generateJson(int hash,final Set<Class<?>> classes,URL url){
        log.log(Level.INFO, "Generating {0} response from context", url);
        Swagger swagger = createSwagger(classes,url);
        log.severe("swagger basepath = " + swagger.getBasePath());
        log.severe("swagger host = " + swagger.getHost());
        log.severe("swagger = " + swagger.getSwagger());
        try {
            String swaggerJson = Json.pretty().writeValueAsString(swagger);
            swaggerMap.put(hash, swaggerJson);
            return swaggerJson;
        } catch (JsonProcessingException ex) {
            log.log(Level.INFO, "Could not generate {0} - {1}", new Object[]{url.toString(), ex.getMessage()});
        }
        log.log(Level.INFO, "Could not generate {0} - null", new Object[]{url.toString()});
        return null;
    }
    
    private String generateYaml(int hash,final Set<Class<?>> classes,URL url){
        log.log(Level.INFO, "Generating {0} response for context", url);
        Swagger swagger = createSwagger(classes,url);
        try {
            String swaggerYaml = Yaml.pretty().writeValueAsString(swagger);
            swaggerMap.put(hash, swaggerYaml);
            return swaggerYaml;
        } catch (JsonProcessingException ex) {
            log.log(Level.INFO, "Could not generate {0} - {1}", new Object[]{url.toString(), ex.getMessage()});
        }
        log.log(Level.INFO, "Could not generate {0} - null", new Object[]{url.toString()});
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
        log.severe(">>> existingBasePath = [" + existingBasePath + "]");
        log.severe(">>> url = [" + url + "]");
        
        String path = url.getPath();
        log.severe(">>> path = [" + path + "]");
        if(existingBasePath!=null && !existingBasePath.isEmpty()){
            int i = path.indexOf(existingBasePath + SLASH);
            log.severe(">> i = ["  + i + "]");
            String rp = path.substring(0, i) + existingBasePath;
            log.severe(">> Option 1 = ["  + rp + "]");
            return rp;
        }else{
            int i = path.indexOf(APIEE_CONTEXT);
            log.severe(">> i = ["  + i + "]");
            String rp = path.substring(0, i);
            log.severe(">> Option 2 = ["  + rp + "]");
            return rp;
            
        }
    }
    
    private static final String APIEE_CONTEXT = "/apiee/";
    private static final String DOUBLE_POINT = ":";
    private static final String SLASH = "/";
    
}