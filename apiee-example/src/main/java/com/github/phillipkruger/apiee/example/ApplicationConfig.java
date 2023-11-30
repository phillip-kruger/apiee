package com.github.phillipkruger.apiee.example;

import jakarta.ws.rs.ApplicationPath;

import jakarta.ws.rs.core.Application;

/**
 * Enable REST
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@ApplicationPath("/api")
//You can define the definition here or in apiee.properties.
//@SwaggerDefinition (info = @Info (
//                        title = "Example Service",
//                        description = "A simple example of apiee",
//                        version = "1.0.0",
//                        contact = @Contact (
//                            name = "Phillip Kruger", 
//                            email = "apiee@phillip-kruger.com", 
//                            url = "http://phillip-kruger.com"
//                        )
//                    )//,securityDefinition = @SecurityDefinition(
                     //       apiKeyAuthDefinitions = @ApiKeyAuthDefinition(key = "api" , name="Authorization", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER)
                     //       ,basicAuthDefinitions = @BasicAuthDefinition(key = "basic",description = "foo")
//                            oAuth2Definitions = @OAuth2Definition(key="oauth2",
//                                    authorizationUrl = "/auth",
//                                    description = "Bla bla bla",
//                                    flow = OAuth2Definition.Flow.PASSWORD,
//                                    scopes = @Scope(name = "scopename",description = "the scope"),
//                                    tokenUrl = "/auth/token"
//                                    
//                                    )
//                    )
//                )
public class ApplicationConfig extends Application {

}