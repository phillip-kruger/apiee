package com.github.phillipkruger.apiee;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
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
        
    }
    
    public String getSwaggerUIHtml(HttpServletRequest request){
        if(this.swaggerUIHtml==null){
            this.swaggerUIHtml = parseHtmlTemplate(request);
        }
        return this.swaggerUIHtml;
    }
    
    public byte[] getFavicon(int size){
        if(size>24)return getFavicon32();
        return getFavicon16();
    }
    
    private String parseHtmlTemplate(HttpServletRequest request){
        try(InputStream template = this.getClass().getClassLoader().getResourceAsStream(FILE_TEMPLATE)){
            
            String html = toString(template);
            html = html.replaceAll(VAR_CONTEXT_ROOT, request.getContextPath());
            
            return html;
        } catch (IOException ex) {
            return ex.getMessage();
        }
    
    }
    
    private String toString(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining(NL));
        }
    }
    
    private BufferedImage getLogo(){
        try(InputStream template = this.getClass().getClassLoader().getResourceAsStream(FILE_LOGO)){
            return ImageIO.read(template);    
        } catch (IOException ex) {
            return null;
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
    
    private static final String PNG = "png";
    private static final String NL = "\n";
    private static final String VAR_CONTEXT_ROOT = "%contextRoot%";
    private static final String FILE_TEMPLATE = "META-INF/resources/apiee/template.html";
    private static final String FILE_LOGO = "META-INF/resources/apiee/logo.png";
}