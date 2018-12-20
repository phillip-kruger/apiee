# Apiee

> Easy Swagger and Swagger UI in Java EE.

[![Build Status](https://travis-ci.org/phillip-kruger/apiee.svg?branch=master)](https://travis-ci.org/phillip-kruger/apiee)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.phillip-kruger/apiee/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.phillip-kruger/apiee)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/phillip-kruger/apiee/master/LICENSE.txt)
[![Javadocs](https://www.javadoc.io/badge/com.github.phillip-kruger/apiee-core.svg)](https://www.javadoc.io/doc/com.github.phillip-kruger/apiee-core)

![logo](https://raw.githubusercontent.com/phillip-kruger/apiee/master/apiee-core/src/main/webapp/apiee/logo.png) 

> apiee@phillip-kruger.com

[![Twitter URL](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/phillipkruger)

***
## Quick Start
Apiee creates swagger documentation from your JAX-RS and Swagger annotations in Runtime. It also give you a custom Swagger UI screen.
 
The apiee-core library is published to [maven central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22apiee-core%22) and artefacts is available in [Nexus OSS](https://oss.sonatype.org/#nexus-search;quick~apiee-core)

In your pom.xml:
 
     <!-- Apiee -->
     <dependency>
            <groupId>com.github.phillip-kruger</groupId>
            <artifactId>apiee-core</artifactId>
            <version>1.0.8</version>
     </dependency>

In you JAX-RS Application class:

    @ApplicationPath("/api")
    @SwaggerDefinition (info = @Info (
                        title = "Example Service",
                        description = "A simple example of apiee",
                        version = "1.0.0",
                        contact = @Contact (
                            name = "Phillip Kruger", 
                            email = "apiee@phillip-kruger.com", 
                            url = "http://phillip-kruger.com"
                        )
                    )
                )
    public class ApplicationConfig extends Application {

    }

You can also set the `@SwaggerDefinition` part in `apiee.properties` (more about that later)

In your JAX-RS Endpoint class:

    @Path("/example")
    @Produces({MediaType.APPLICATION_JSON}) 
    @Consumes({MediaType.APPLICATION_JSON})
    @Api(value = "Example service")
    @Log
    public class ExampleService {
        
        @GET
        @ApiOperation(value = "Retrieve some example content", notes = "This will return some json to the client",response = JsonObject.class)
        public Response getExample(){
            JsonObject jsonObject = Json.createObjectBuilder().add("name", "apiee example").add("url", "https://github.com/phillip-kruger/apiee-example").build();
            log.log(Level.INFO, "GET: {0}", jsonObject);
            return Response.ok(jsonObject).build();
        }
    }

You can then go to the apiee swagger-ui :
> http://localhost:8080/your-application-context/your-jaxrs-application-path/apiee/

## Security
You can add security using the normal Swagger annotation, or some properties in apiee.properties or a combination of both.

### Basic Auth example:

#### As annotation:

    @SwaggerDefinition (securityDefinition = 
        @SecurityDefinition(basicAuthDefinitions = @BasicAuthDefinition(key = "basic",description = "foo"))
    )

#### Or in apiee.properties:

    securityBasicAuthKey=basic
    securityBasicAuthDesc=foo # Optional

### API Key example

#### As annotation:

    @SwaggerDefinition (securityDefinition = 
        @SecurityDefinition(apiKeyAuthDefinitions = @ApiKeyAuthDefinition(key = "api" , name="Authorization", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER))
    )

#### Or in apiee.properties:
    
    securityApiKeyKey=Bearer
    securityApiKeyIn=HEADER # Optional. Default is HEADER. (HEADER/QUERY)
    securityApiKeyDesc=foo # Optional
    securityApiKeyName=Authorization # Optional. Authorization

### OAuth 2 example

#### As annotation:

    @SwaggerDefinition (securityDefinition = 
        @SecurityDefinition(oAuth2Definitions = @OAuth2Definition(key="oauth2",
                                    authorizationUrl = "/auth",
                                    description = "Bla bla bla",
                                    flow = OAuth2Definition.Flow.PASSWORD,
                                    scopes = @Scope(name = "scopename",description = "the scope"),
                                    tokenUrl = "/auth/token"))
    )

#### Or in apiee.properties:

    securityOAuth2Key=oauth2
    securityOAuth2AuthUrl=/auth
    securityOAuth2TokenUrl=/auth/token
    securityOAuth2Flow=PASSWORD # One of ACCESS_CODE,APPLICATION,IMPLICIT,PASSWORD
    securityOAuth2Scopes=scopename:the scope,anotherName:the other scope # Comma-seperated with name:description
    securityOAuth2Desc=bla bla bla

![](https://raw.githubusercontent.com/phillip-kruger/apiee/master/Screenshot5.png)
(above: an API key example)

## Whitelabel
Apiee comes out of the box with it's own themed swagger ui. However, you might not want a monkey's face on you 
API documentation, so Apiee makes it easy to whitelabel the UI.

![](https://raw.githubusercontent.com/phillip-kruger/apiee/master/Screenshot1.png)
(above: default theme out-of-the-box)

In your web app `/src/main/resources` you can include the following files:
* apiee.properties
* apiee.png
* apiee.css
* apiee.html

### Variables (apiee.properties)
The HTML template that creates the Swagger UI has some variables that you can define in a properties
>     copyrighBy=John Smith
>     title=Company X
>     jsonButtonCaption=download json
>     yamlButtonCaption=download yaml
>     swaggerUiTheme=monokai 

There are also some [Swagger UI parameters](https://github.com/swagger-api/swagger-ui/tree/v2.2.10#parameters) (with default values) that you can include changing (below contains the default values):

>     supportedSubmitMethods=['get', 'post', 'put', 'delete']
>     docExpansion=none
>     jsonEditor=true
>     defaultModelRendering=scheme
>     showRequestHeaders=true
>     showOperationIds=false
>     validatorUrl=null

And some OAuth parameters (below contains the default values):

>     oauthClientId=your-client-id
>     oauthClientSecret=your-client-secret-if-required
>     oauthRealm=your-realms
>     oauthAppName=your-app-name
>     oauthScopeSeparator=

If you want more customization, you can include your own template (apiee.html), see Template section below.  

You can also define the `@SwaggerDefinition` in the `apiee.properties` with the following properties (so then you can omit it from the source code annotation). NOTE: all properties are optional.

>     infoTitle=Company X Services
>     infoDescription=REST API of Company X
>     infoVersion=1.0.1
>     infoContactName=Phillip Kruger
>     infoContactEmail=apiee@phillip-kruger.com
>     infoContactUrl=http://phillip-kruger.com
>     infoLicenseName=Apache License, Version 2.0
>     infoLicenseUrl=http://www.apache.org/licenses/LICENSE-2.0
>     infoTermsOfService=Some terms here
>     consumes=application/json,application/xml
>     produces=application/json
>     basePath=/api
>     schemes=HTTP,HTTPS # valid values: HTTP,HTTPS,WS,WSS
>     host=myhost.com # if ommited, will figure this out based on headers and server
>     tags=example:example description,foo,bar

### Theme (apiee.css)
Apiee includes [swagger-ui-themes](http://meostrander.com/swagger-ui-themes/) and use the `muted` theme as default. You can override the theme by setting the `swaggerUiTheme` in the `apiee.properties` (see above). You can also include your own CSS called `apiee.css` to style the UI.
Themes available from [swagger-ui-themes](http://meostrander.com/swagger-ui-themes/):
* feeling-blue
* flattop
* material
* monokai
* muted
* newspaper
* outline

![](https://raw.githubusercontent.com/phillip-kruger/apiee/master/Screenshot2.png)
(above: some variables and theme changed)

### Logo (apiee.png)
To replace the default monkey face logo, include the apiee.png file

![](https://raw.githubusercontent.com/phillip-kruger/apiee/master/Screenshot3.png)
(above: logo changed)

### Template (apiee.html)
(Advanced) Lastly, if you want even more customization, you can provide you own HTML template to override the default default.
This allows you to really change the Look and Feel of you Swagger UI

![](https://raw.githubusercontent.com/phillip-kruger/apiee/master/Screenshot4.png)
(above: custome html template)

## Application Servers
Apiee has been tested using the following Java EE 7 application servers:

* [Wildfly 10.0.1](http://wildfly.org/)
* [Payara 172](http://www.payara.fish/)
* [Liberty 17.0.0.1](https://developer.ibm.com/assets/wasdev/#asset/runtimes-wlp-javaee7)

## Via a Proxy
You can set some headers to create the correct URL in swagger documents and to make the the UI works. This is handy if the request is going through a proxy.

* x-request-uri (if this is set, the `path` part of the URL will be set to this)
* x-forwarded-port (if this is set, the `port` part of the URL will be set to this)
* x-forwarded-host (if this is set, the `host` part of the URL will be set to this)
* x-forwarded-proto (if this is set, the `scheme` or `protocol` part of the URL will be set to this)

## Cache
The generation of the swagger document happens at first request and are cached per url for the lifetime of the running server (Application Scoped)

You can clear the cache in the following ways:
* Add a query parameter (clearCache=true) to the swagger UI URL, eg. http://localhost:8080/apiee-example/api/apiee/index.html?clearCache=true
* Do a HTTP DELETE on http://localhost:8080/apiee-example/api/apiee/

You can also see the time that the document has been created in the footer of the swagger UI screen.

Other usefull REST endpoints that might help with debugging:

* HTTP GET on http://localhost:8080/apiee-example/api/apiee/generatedOn.json will return the generation details, eg:

```json
{
  "date": "Fri Apr 20 14:06:00 SAST 2018",
  "formattedDate": "2018/04/20 2:06 PM"
}
```

* HTTP GET on http://localhost:8080/apiee-example/api/apiee/cacheMap.json will return the cached documents, eg:

```json
[
  {
    "hash": 1912183572,
    "generatedOn": "Fri Apr 20 14:06:00 SAST 2018",
    "url": "http://localhost:8080/apiee-example/api/apiee/swagger.json"
  }
]
```

## Blog entry
Also see [this blog](http://www.phillip-kruger.com/2017/06/apiee-easy-way-to-get-swagger-on-java-ee.html) entry

**Documentation available here:** [https://github.com/phillip-kruger/apiee/wiki](https://github.com/phillip-kruger/apiee/wiki)
