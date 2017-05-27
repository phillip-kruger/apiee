package com.phillipkruger.library.apiee;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import lombok.extern.java.Log;

/**
 * Return some information on the request.
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@Log
@Path("/request-info")
@Api(value = "Request information")
public class RequestInfoService {  
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation("Return some information on the request")
    public Properties getRequestInfo(@Context HttpServletRequest request){  
        Properties properties = new Properties();
        log.log(Level.INFO, "Inspecting request [{0}]", request.getContextPath());

        setProperty(properties,"requestURL",request.getRequestURL());
        setProperty(properties,"requestURI",request.getRequestURI());
        setProperty(properties,"remoteAddr",request.getRemoteAddr());
        setProperty(properties,"remoteHost",request.getRemoteHost());
        setProperty(properties,"remotePort",request.getRemotePort());

        setProperty(properties,"localAddr",request.getLocalAddr());
        setProperty(properties,"localName",request.getLocalName());
        setProperty(properties,"localPort",request.getLocalPort());

        setProperty(properties,"serverName",request.getServerName());
        setProperty(properties,"serverPort",request.getServerPort());
        setProperty(properties,"servletPath",request.getServletPath());

        setProperty(properties,"contentType",request.getContentType());
        setProperty(properties,"method",request.getMethod());
        setProperty(properties,"pathInfo",request.getPathInfo());
        setProperty(properties,"queryString",request.getQueryString());
        setProperty(properties,"userPrincipal",request.getUserPrincipal());
        setProperty(properties,"requestedSessionId",request.getRequestedSessionId()); //NOSONAR
            
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            setProperty(properties,"header." + key,value);
        }
        return properties;
    }
    
    private void setProperty(Properties p,String name,Object requestProperty){
        
        if(requestProperty!=null){
            p.setProperty(name,requestProperty.toString());
        }else{
            p.setProperty(name,EMPTY);
        }
    }
    private static final String EMPTY = "";
    
}
