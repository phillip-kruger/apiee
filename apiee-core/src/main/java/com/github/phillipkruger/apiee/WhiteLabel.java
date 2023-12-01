package com.github.phillipkruger.apiee;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.extern.java.Log;

/**
 * White label the UI
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@Log
@ApplicationScoped
public class WhiteLabel {
    private final Properties props = new Properties();
    private final String PROPERTIES_FILE_NAME = "apiee.properties";
    private final String LOGO_FILE_NAME = "apiee.png";
    private final String CSS_FILE_NAME = "apiee.css";
    private final String HTML_FILE_NAME = "apiee.html";
    
    @Getter
    private BufferedImage logo = null;
    
    @Getter
    private String css = null;
    @Getter
    private String html = null;
    
    @PostConstruct
    public void init(){
        // Properties
        try (InputStream propertiesStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)){
            if(propertiesStream!=null){
                props.load(propertiesStream);
            }else{
                log.log(Level.FINEST, "Can not load whilelable properties [apiee.properties]");
            }
        } catch (NullPointerException | IOException ex) {
            log.log(Level.FINEST, "Can not load whilelable properties [apiee.properties] - {0}", ex.getMessage());
        }
        
        // Logo
        try(InputStream logoStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(LOGO_FILE_NAME)){
            if(logoStream!=null){
                this.logo = ImageIO.read(logoStream);
            }else{
                log.log(Level.FINEST, "Can not load whilelable logo [apiee.png]");
            }
        } catch (IOException ex) {
            log.log(Level.FINEST, "Can not load whilelable logo [apiee.png] - {0}", ex.getMessage());
        }
        
        // Css
        try(InputStream cssStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CSS_FILE_NAME)){
            if(cssStream!=null){
                this.css = toString(cssStream);
            }else{
                log.log(Level.FINEST, "Can not load whilelable css [apiee.css]");
            }
        } catch (IOException ex) {
            log.log(Level.FINEST, "Can not load whilelable css [apiee.css] - {0}", ex.getMessage());
        }
        
        // Html
        try(InputStream htmlStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(HTML_FILE_NAME)){
            if(htmlStream!=null){
                this.html = toString(htmlStream);
            }else{
                log.log(Level.FINEST, "Can not load whilelable html [apiee.html]");
            }
        } catch (IOException ex) {
            log.log(Level.FINEST, "Can not load whilelable html [apiee.html] - {0}", ex.getMessage());
        }
    }
    
    public boolean hasLogo(){
        return logo!=null;
    }
    
    public boolean hasCss() {
        return css!=null;
    }
    
    public boolean hasHtml() {
        return html!=null;
    }
    
    public boolean hasProperties(){
        return this.props!=null && !this.props.isEmpty();
    }
    
    public boolean hasProperty(String key){
        return hasProperties() && props.containsKey(key);
    }
    
    public String getProperty(String key, String defaultValue){
        if(hasProperty(key)){
            return props.getProperty(key);
        }
        return defaultValue;
    }
    
    
    
    public Map<String,String> getProperties(){
        if(hasProperties()){
            return new HashMap<>((Map)props);
        }
        return null;
    }

    private String toString(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining(NL));
        }
    }
    private static final String NL = "\n";
}
