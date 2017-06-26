package com.github.phillipkruger.apiee;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.jaxrs.Reader;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
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
import javax.inject.Inject;
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
    
    @Inject
    private WhiteLabel whiteLabel;
    
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
        log.log(Level.FINEST, "Generating {0} response from context", url);
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
        Info info = getSwaggerInfo();
        if(info!=null)swagger.setInfo(info);
        return swagger;
    }
    
    private String getBasePath(final String existingBasePath,final URL url){
        String path = url.getPath();
        if(existingBasePath!=null && !existingBasePath.isEmpty()){
            int i = path.indexOf(existingBasePath + SLASH);
            return path.substring(0, i) + existingBasePath;
        }else{
            int i = path.indexOf(APIEE_CONTEXT);
            return path.substring(0, i);
            
        }
    }
    
    private Info getSwaggerInfo(){
        Contact contact = getSwaggerContact();
        License license = getSwaggerLicense();
        
        String title = whiteLabel.getProperty(INFO_TITLE, null);
        String description = whiteLabel.getProperty(INFO_DESC, null);
        String version = whiteLabel.getProperty(INFO_VERSION, null);
        String terms = whiteLabel.getProperty(INFO_TERMS, null);
        
        if(isSet(title,description,version,terms) || contact!=null || license!=null){
        
            Info info = new Info();
            if(isSet(title))info.setTitle(title);
            if(isSet(description))info.setDescription(description);
            if(isSet(version))info.setVersion(version);
            if(isSet(terms))info.setTermsOfService(terms);
            if(contact!=null)info.setContact(contact);
            if(license!=null)info.setLicense(license);
        
            return info;
        }
        
        return null;
        
    }
    
    private Contact getSwaggerContact(){
        String name = whiteLabel.getProperty(INFO_CONTACT_NAME, null);
        String email = whiteLabel.getProperty(INFO_CONTACT_EMAIL, null);
        String url = whiteLabel.getProperty(INFO_CONTACT_URL, null);
        
        if(isSet(name,email,url)){
            Contact contact = new Contact();
            if(isSet(email))contact.setEmail(email);
            if(isSet(name))contact.setName(name);
            if(isSet(url))contact.setUrl(url);
            return contact;
        }
        return null;
    }
    
    private License getSwaggerLicense(){
        String name = whiteLabel.getProperty(INFO_LICENSE_NAME, null);
        String url = whiteLabel.getProperty(INFO_LICENSE_URL, null);
        
        if(isSet(name,url)){
            License license = new License();
            if(isSet(name))license.setName(name);
            if(isSet(url))license.setUrl(url);
            return license;
        }
        return null;
    }
    
    private boolean isSet(String... value){
        for(String v:value){
            if(v!=null && !v.isEmpty())return true;
        }
        return false;
    }
    
    
    private static final String APIEE_CONTEXT = "/apiee/";
    private static final String DOUBLE_POINT = ":";
    private static final String SLASH = "/";
    
    private static final String INFO_TITLE = "infoTitle";
    private static final String INFO_DESC = "infoDescription";
    private static final String INFO_VERSION = "infoVersion";
    private static final String INFO_TERMS = "infoTermsOfService";
    
    private static final String INFO_CONTACT_NAME = "infoContactName";
    private static final String INFO_CONTACT_EMAIL = "infoContactEmail";
    private static final String INFO_CONTACT_URL = "infoContactUrl";    
    private static final String INFO_LICENSE_NAME = "infoLicenseName";
    private static final String INFO_LICENSE_URL = "infoLicenseUrl";
}