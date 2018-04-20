package com.github.phillipkruger.apiee;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.jaxrs.Reader;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import io.swagger.models.auth.BasicAuthDefinition;
import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.models.auth.OAuth2Definition;
import io.swagger.models.auth.In;
import io.swagger.models.auth.SecuritySchemeDefinition;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.java.Log;

/**
 * Caching already created swagger docs.
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@Log
@ApplicationScoped
public class SwaggerCache {
    @Getter
    private final Map<Integer,CachedDocument> swaggerMap = new ConcurrentHashMap<>();
    
    @Inject
    private WhiteLabel whiteLabel;
    
    public String getSwaggerJson(final Set<Class<?>> classes,@NotNull final URL url) {
        int hash = url.toExternalForm().hashCode();
        if(swaggerMap.containsKey(hash)){
            // Load from cache
            log.log(Level.FINEST, "Loading {0} from cache", url);
            return swaggerMap.get(hash).getDocument();
        }else{
            return generateJson(hash,classes,url);
        }
    }
    
    public String getSwaggerYaml(final Set<Class<?>> classes,@NotNull final URL url) {  
        int hash = url.toExternalForm().hashCode();
        if(swaggerMap.containsKey(hash)){
            // Load from cache
            log.log(Level.FINEST, "Loading {0} from cache", url);
            return swaggerMap.get(hash).getDocument();
        }else{
            return generateYaml(hash,classes,url);
        }
    }
    
    public Date getGeneratedDate(@NotNull final URL url) {
        int hash = url.toExternalForm().hashCode();
        if(swaggerMap.containsKey(hash)){
            return swaggerMap.get(hash).getGeneratedOn();
        }else{
            return null;
        }
    }
    
    public void clearCache(){
        log.info("Clearing Apiee cache...");
        swaggerMap.clear();
    }
    
    private String generateJson(int hash,final Set<Class<?>> classes,URL url){
        log.log(Level.FINEST, "Generating {0} response from context", url);
        Swagger swagger = createSwagger(classes,url);
        try {
            String swaggerJson = Json.pretty().writeValueAsString(swagger);
            swaggerMap.put(hash, new CachedDocument(hash,url,swaggerJson));
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
            swaggerMap.put(hash, new CachedDocument(hash,url,swaggerYaml));
            return swaggerYaml;
        } catch (JsonProcessingException ex) {
            log.log(Level.WARNING, "Could not generate {0} - {1}", new Object[]{url.toString(), ex.getMessage()});
        }
        log.log(Level.WARNING, "Could not generate {0} - null", new Object[]{url.toString()});
        return null;
    }
    
    private Swagger createSwagger(final Set<Class<?>> classes,final URL url){
        Swagger swagger = new Reader(new Swagger()).read(classes);
        
        Info info = getSwaggerInfo(swagger);
        if(info!=null)swagger.setInfo(info);
        
        Map<String, SecuritySchemeDefinition> securityDefinitions = getSecurityDefinitions(swagger);
        if(securityDefinitions!=null)swagger.setSecurityDefinitions(securityDefinitions);
        
        String consumes = whiteLabel.getProperty(CONSUMES, null);
        if(anyIsSet(consumes))swagger.setConsumes(toList(swagger.getConsumes(),consumes));
        
        String produces = whiteLabel.getProperty(PRODUCES, null);
        if(anyIsSet(produces))swagger.setProduces(toList(swagger.getProduces(),produces));
        
        String basePath = whiteLabel.getProperty(BASE_PATH, getBasePath(swagger.getBasePath(), url));
        if(anyIsSet(basePath))swagger.setBasePath(basePath);
        
        String schemes = whiteLabel.getProperty(SCHEMES, url.getProtocol().toUpperCase());
        if(anyIsSet(schemes))swagger.setSchemes(toSchemeList(swagger.getSchemes(),schemes));
                
        String host = whiteLabel.getProperty(HOST, url.getHost() + DOUBLE_POINT + url.getPort());
        if(anyIsSet(host))swagger.setHost(host);
        
        String tags = whiteLabel.getProperty(TAGS, null);
        if(anyIsSet(tags))swagger.setTags(toTagList(swagger.getTags(),tags));
        
        return swagger;
    }
    
    private List<String> toList(List<String> original,String newCommaSeperated){
        if(original!=null){
            original.addAll(toList(newCommaSeperated));
        }else{
            original = toList(newCommaSeperated);
        }
        return original;
    }
    
    private List<Scheme> toSchemeList(List<Scheme> original,String schemes) {
        List<Scheme> schemeList = new ArrayList<>();
        toList(schemes).forEach((scheme) -> {
            schemeList.add(Scheme.forValue(scheme));
        });
        
        if(original!=null){
            original.addAll(schemeList);
            return original;
        }else{
            return schemeList;
        }
    }
    
    private List<Tag> toTagList(List<Tag> original,String tags) {
        List<Tag> tagList = new ArrayList<>();
        
        for(String tag:toList(tags)){
            Tag t = new Tag();
            if(tag.contains(DOUBLE_POINT)){
                String[] nameAndDesc = tag.split(DOUBLE_POINT);
                t.setName(nameAndDesc[0]);
                t.setDescription(nameAndDesc[1]);
            }else{
                t.setName(tag);
            }
            
            tagList.add(t);
        }
        
        if(original!=null){
            original.addAll(tagList);
            return original;
        }else{
            return tagList;
        }
    }
    
    private List<String> toList(String s) {
        return Arrays.asList(s.split(COMMA));
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
    
    private Map<String, SecuritySchemeDefinition> getSecurityDefinitions(Swagger swagger) {
        
        Map<String, SecuritySchemeDefinition> securityDefinitions = swagger.getSecurityDefinitions();
        if(securityDefinitions==null)securityDefinitions= new HashMap<>();
        
        // Basic auth
        String basicAuthKey = whiteLabel.getProperty(BASIC_AUTH_KEY, null);
        if(basicAuthKey!=null && !basicAuthKey.isEmpty()){
            BasicAuthDefinition basicAuthDefinition = new BasicAuthDefinition();
            
            String basicAuthDescription = whiteLabel.getProperty(BASIC_AUTH_DESC, null);
            if(allIsSet(basicAuthDescription))basicAuthDefinition.setDescription(basicAuthDescription);
            
            securityDefinitions.put(basicAuthKey, basicAuthDefinition);
        }
        
        // Token auth
        String apiKeyKey = whiteLabel.getProperty(API_KEY_KEY, null);
        if(apiKeyKey!=null && !apiKeyKey.isEmpty()){
            ApiKeyAuthDefinition apiKeyAuthDefinition = new ApiKeyAuthDefinition();
            
            String apiKeyIn = whiteLabel.getProperty(API_KEY_IN, "HEADER");
            if(allIsSet(apiKeyIn))apiKeyAuthDefinition.setIn(In.forValue(apiKeyIn));
            
            String apiKeyDescription = whiteLabel.getProperty(API_KEY_DESC, null);
            if(allIsSet(apiKeyDescription))apiKeyAuthDefinition.setDescription(apiKeyDescription);
            
            String apiKeyName = whiteLabel.getProperty(API_KEY_NAME, "Authorization");
            if(allIsSet(apiKeyName))apiKeyAuthDefinition.setName(apiKeyName);
            
            securityDefinitions.put(apiKeyKey, apiKeyAuthDefinition);
        }
        
        // OAuth 2
        String oauth2Key = whiteLabel.getProperty(OAUTH2_KEY, null);
        if(oauth2Key!=null && !oauth2Key.isEmpty()){
            OAuth2Definition oAuth2Definition = new OAuth2Definition();
            
            String authorizationUrl = whiteLabel.getProperty(OAUTH2_AUTH_URL, null);
            if(allIsSet(authorizationUrl))oAuth2Definition.setAuthorizationUrl(authorizationUrl);
            
            String tokenUrl = whiteLabel.getProperty(OAUTH2_TOKEN_URL, null);
            if(allIsSet(tokenUrl))oAuth2Definition.setTokenUrl(tokenUrl);
            
            String oauth2Description = whiteLabel.getProperty(OAUTH2_DESC, null);
            if(allIsSet(oauth2Description))oAuth2Definition.setDescription(oauth2Description);

            String oauth2Flow = whiteLabel.getProperty(OAUTH2_FLOW, null); // ACCESS_CODE,APPLICATION,IMPLICIT,PASSWORD
            if(allIsSet(oauth2Flow))oAuth2Definition.setFlow(oauth2Flow);
            
            String oauth2Scopes = whiteLabel.getProperty(OAUTH2_SCOPES, null); // Comma-seperated with name:description
            if(allIsSet(oauth2Scopes))oAuth2Definition.setScopes(toScopesMaps(oauth2Scopes));
            
            securityDefinitions.put(oauth2Key, oAuth2Definition);
        }
        
        
        return securityDefinitions;
    }
    

    private Map<String,String> toScopesMaps(String line){
        Map<String,String> m = new HashMap<>();
        String scopes[] = line.split(COMMA);
        for(String scope:scopes){
            String[] kv = scope.split(DOUBLE_POINT);
            m.put(kv[0], kv[1]);
        }
        return m;
    }
    
    private Info getSwaggerInfo(Swagger swagger){
        Info info = swagger.getInfo();
        if(info ==null)info = new Info();
        
        Contact contact = getSwaggerContact();
        License license = getSwaggerLicense();
        
        String title = whiteLabel.getProperty(INFO_TITLE, null);
        String description = whiteLabel.getProperty(INFO_DESC, null);
        String version = whiteLabel.getProperty(INFO_VERSION, null);
        String terms = whiteLabel.getProperty(INFO_TERMS, null);
        
        if(anyIsSet(title,description,version,terms) || contact!=null || license!=null){
        
            if(anyIsSet(title))info.setTitle(title);
            if(anyIsSet(description))info.setDescription(description);
            if(anyIsSet(version))info.setVersion(version);
            if(anyIsSet(terms))info.setTermsOfService(terms);
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
        
        if(anyIsSet(name,email,url)){
            Contact contact = new Contact();
            if(anyIsSet(email))contact.setEmail(email);
            if(anyIsSet(name))contact.setName(name);
            if(anyIsSet(url))contact.setUrl(url);
            return contact;
        }
        return null;
    }
    
    private License getSwaggerLicense(){
        String name = whiteLabel.getProperty(INFO_LICENSE_NAME, null);
        String url = whiteLabel.getProperty(INFO_LICENSE_URL, null);
        
        if(anyIsSet(name,url)){
            License license = new License();
            if(anyIsSet(name))license.setName(name);
            if(anyIsSet(url))license.setUrl(url);
            return license;
        }
        return null;
    }
    
    private boolean anyIsSet(String... value){
        for(String v:value){
            if(v!=null && !v.isEmpty())return true;
        }
        return false;
    }
    
    private boolean allIsSet(String... value){
        for(String v:value){
            if(v==null || v.isEmpty())return false;
        }
        return true;
    }
    
    private static final String APIEE_CONTEXT = "/apiee/";
    private static final String DOUBLE_POINT = ":";
    private static final String SLASH = "/";
    private static final String COMMA = ",";
    
    private static final String INFO_TITLE = "infoTitle";
    private static final String INFO_DESC = "infoDescription";
    private static final String INFO_VERSION = "infoVersion";
    private static final String INFO_TERMS = "infoTermsOfService";
    
    private static final String INFO_CONTACT_NAME = "infoContactName";
    private static final String INFO_CONTACT_EMAIL = "infoContactEmail";
    private static final String INFO_CONTACT_URL = "infoContactUrl";    
    private static final String INFO_LICENSE_NAME = "infoLicenseName";
    private static final String INFO_LICENSE_URL = "infoLicenseUrl";
    private static final String CONSUMES = "consumes";
    private static final String PRODUCES = "produces";
    private static final String BASE_PATH = "basePath";
    private static final String SCHEMES = "schemes";
    private static final String HOST = "host";
    private static final String TAGS = "tags";
    
    private static final String BASIC_AUTH_KEY = "securityBasicAuthKey";
    private static final String BASIC_AUTH_DESC = "securityBasicAuthDesc";
    
    private static final String API_KEY_KEY = "securityApiKeyKey";
    private static final String API_KEY_IN = "securityApiKeyIn";
    private static final String API_KEY_DESC = "securityApiKeyDesc";
    private static final String API_KEY_NAME = "securityApiKeyName";
    
    private static final String OAUTH2_KEY = "securityOAuth2Key";
    private static final String OAUTH2_AUTH_URL = "securityOAuth2AuthUrl";
    private static final String OAUTH2_TOKEN_URL = "securityOAuth2TokenUrl";
    private static final String OAUTH2_FLOW = "securityOAuth2Flow"; // ACCESS_CODE,APPLICATION,IMPLICIT,PASSWORD
    private static final String OAUTH2_SCOPES = "securityOAuth2Scopes"; // Comma-seperated with name:description
    private static final String OAUTH2_DESC = "securityOAuth2Desc";
            
}