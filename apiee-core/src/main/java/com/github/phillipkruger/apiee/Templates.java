package com.github.phillipkruger.apiee;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;
import lombok.Getter;
import lombok.extern.java.Log;

/**
 * Helping with the templates (
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@Log
@ApplicationScoped
public class Templates {  
 
    @Getter
    private byte[] originalLogo = null;
    @Getter
    private byte[] favicon32 = null;
    @Getter
    private byte[] favicon16 = null;
    @Getter
    private String style = null;
    
    @Inject
    private WhiteLabel whiteLabel;
    
    private String swaggerUIHtml = null;
    
    @PostConstruct
    public void afterCreate() {
        BufferedImage image = getLogo();
        BufferedImage image16 = getFavicon(16, image);
        BufferedImage image32 = getFavicon(32, image);
        try {
            this.originalLogo = toBytes(image);
            log.finest("Apiee UI: Created logo");
            this.favicon16 = toBytes(image16);
            this.favicon32 = toBytes(image32);
            log.finest("Apiee UI: Created favicons");
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        
        this.style = getCss();
        
    }
    
    public String getSwaggerUIHtml(UriInfo uriInfo,HttpServletRequest request){
        if(this.swaggerUIHtml==null){
            this.swaggerUIHtml = parseHtmlTemplate(uriInfo,request);
        }
        return this.swaggerUIHtml;
    }
    
    public byte[] getFavicon(int size){
        if(size>24)return getFavicon32();
        return getFavicon16();
    }
    
    private String parseHtmlTemplate(UriInfo uriInfo, HttpServletRequest request){
        String html = getHTMLTemplate();
        // System properties.
        html = html.replaceAll(VAR_CONTEXT_ROOT, getOriginalContextPath(uriInfo,request));
        html = html.replaceAll(VAR_CURRENT_YEAR, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        // Whitelabel properties.
        if(whiteLabel.hasProperties()){
            // First do all unknown properties.
            Map<String, String> properties = whiteLabel.getProperties();
            Set<Map.Entry<String, String>> pes = properties.entrySet();
            for(Map.Entry<String,String> p:pes){
                String key = PERSENTAGE + p.getKey() + PERSENTAGE;
                html = html.replaceAll(key, p.getValue());
            }

        } else {
            log.warning("No white label properties, returning vanilla template");
        }
        // Then properties with defaults.
        html = html.replaceAll(VAR_COPYRIGHT_BY, VAL_COPYRIGHT_BY);
        html = html.replaceAll(VAR_TITLE, VAL_TITLE);
        html = html.replaceAll(VAR_JSON_BUTTON, VAL_JSON_BUTTON);
        html = html.replaceAll(VAR_YAML_BUTTON, VAL_YAML_BUTTON);
        html = html.replaceAll(VAR_SWAGGER_THEME, VAL_SWAGGER_THEME);
        return html;
    }
    
    private static final String X_REQUEST_URI = "x-request-uri";
    private String getOriginalContextPath(UriInfo uriInfo,HttpServletRequest request){
        String fromHeader = request.getHeader(X_REQUEST_URI);
        
        if(fromHeader!=null && !fromHeader.isEmpty()){
            return getContextPathPart(uriInfo,request,fromHeader);
        }
        return request.getContextPath();
    }
    
    private String getContextPathPart(UriInfo uriInfo,HttpServletRequest request, String fromHeader){
        
        String restBase = request.getServletPath();
        String restUrl = restBase + uriInfo.getPath();
        
        int restUrlStart = fromHeader.indexOf(restUrl);
        
        if(restUrlStart>0){
            return fromHeader.substring(0, restUrlStart);
        }else{
            return fromHeader;
        }
        
    }
    
    private String toString(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining(NL));
        }
    }
    
    private BufferedImage getLogo(){
        if(whiteLabel.hasLogo())return whiteLabel.getLogo();
        
        try(InputStream logo = this.getClass().getClassLoader().getResourceAsStream(FILE_LOGO)){
            return ImageIO.read(logo);    
        } catch (IOException ex) {
            return null;
        }
    }
    
    private String getCss() {
        if(whiteLabel.hasCss())return whiteLabel.getCss();
        
        try(InputStream css = this.getClass().getClassLoader().getResourceAsStream(FILE_STYLE)){
            return toString(css);    
        } catch (IOException ex) {
            return EMPTY;
        }
    }
    
    private String getHTMLTemplate() {
        if(whiteLabel.hasHtml())return whiteLabel.getHtml();
        try(InputStream template = this.getClass().getClassLoader().getResourceAsStream(FILE_TEMPLATE)){
            return toString(template);    
        } catch (IOException ex) {
            return EMPTY;
        }
    }
    
    private BufferedImage getFavicon(int size,BufferedImage original){
        int type = original.getType() == 0? BufferedImage.TYPE_INT_ARGB : original.getType();
        return resizeImage(size,original, type);
    }
    
    private BufferedImage resizeImage(int size,BufferedImage originalImage, int type){
	BufferedImage resizedImage = new BufferedImage(size, size, type);
	Graphics2D g = resizedImage.createGraphics();
	g.drawImage(originalImage, 0, 0, size, size, null);
	g.dispose();

	return resizedImage;
    }
    
    private byte[] toBytes(BufferedImage bufferedImage) throws IOException{
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            ImageIO.write(bufferedImage, PNG, baos);
            return baos.toByteArray();
        }
    }
    
    private static final String VAR_CONTEXT_ROOT = "%contextRoot%";
    private static final String VAR_CURRENT_YEAR = "%currentYear%";
    
    private static final String VAR_COPYRIGHT_BY = "%copyrighBy%";
    private static final String VAL_COPYRIGHT_BY = "Phillip Kruger";
    
    private static final String VAR_TITLE = "%title%";
    private static final String VAL_TITLE = "Apiee - Swagger for Java EE";
    
    private static final String VAR_JSON_BUTTON = "%jsonButtonCaption%";
    private static final String VAL_JSON_BUTTON = "json";
    
    private static final String VAR_YAML_BUTTON = "%yamlButtonCaption%";
    private static final String VAL_YAML_BUTTON = "yaml";
    
    private static final String VAR_SWAGGER_THEME = "%swaggerUiTheme%";
    private static final String VAL_SWAGGER_THEME = "muted";
    
    private static final String PERSENTAGE = "%";
    
    private static final String PNG = "png";
    private static final String NL = "\n";
    private static final String EMPTY = "";
    private static final String FILE_TEMPLATE = "META-INF/resources/apiee/template.html";
    private static final String FILE_LOGO = "META-INF/resources/apiee/logo.png";
    private static final String FILE_STYLE = "META-INF/resources/apiee/style.css";

}