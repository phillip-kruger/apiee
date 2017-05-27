package com.phillipkruger.library.apiee;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Diagnostics service for all REST services. Returns some info about the JSON implementation in use.
 */
@Path("/diag")
@Api(value = "Diagnostics")
public class DiagnoseRestService {

    @Context
    private Providers providers;

    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @ApiOperation ("Returns information on the JSON implementation in use")
    public String info() {
        MessageBodyWriter<?> messageBodyWriter = providers.getMessageBodyWriter(GregorianCalendar.class, GregorianCalendar.class, new Annotation[0], MediaType.APPLICATION_JSON_TYPE);
        MessageBodyWriter<?> messageBodyWriterList = providers.getMessageBodyWriter(List.class, String.class, new Annotation[0], MediaType.APPLICATION_JSON_TYPE);
        MessageBodyWriter<?> messageBodyWriterMap = providers.getMessageBodyWriter(Map.class, String.class, new Annotation[0], MediaType.APPLICATION_JSON_TYPE);
        MessageBodyWriter<?> messageBodyWriterArray = providers.getMessageBodyWriter(String[].class, String.class, new Annotation[0], MediaType.APPLICATION_JSON_TYPE);
        StringBuilder response = new StringBuilder();
        response.append("REST DIAGNOSTICS INFO\n");
        response.append("=====================\n");
        response.append("JSON Provider: " + messageBodyWriter.getClass().getName() + "\n");
        response.append("Supports list: " + (messageBodyWriterList == messageBodyWriter));
        if (messageBodyWriterList != messageBodyWriter) {
            response.append(" (using " + messageBodyWriterList.getClass().getName() + " instead)");
        }
        response.append("\n");

        response.append("Supports map: " + (messageBodyWriterMap == messageBodyWriter));
        if (messageBodyWriterMap != messageBodyWriter) {
            response.append(" (using " + messageBodyWriterMap.getClass().getName() + " instead)");
        }
        response.append("\n");

        response.append("Supports array: " + (messageBodyWriterArray == messageBodyWriter));
        if (messageBodyWriterArray != messageBodyWriter) {
            response.append(" (using " + messageBodyWriterArray.getClass().getName() + " instead)");
        }
        response.append("\n");

        ContextResolver<ObjectMapper> objectMapperContextResolver = providers.getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE);
        response.append("ObjectMapper context resolver: " + ((objectMapperContextResolver == null) ? "-" : objectMapperContextResolver.getClass().getName()));
        return response.toString();
    }

    @GET
    @Path("list")
    @ApiOperation ("Returns a list of strings")
    public List<String> list() {
        return Arrays.asList("item A", "item B", "item C");
    }

    @GET
    @Path("map")
    @ApiOperation ("Returns a map of string => string")
    public Map<String, String> map() {
        Map<String, String> map = new HashMap<>();
        map.put("key A", "item A");
        map.put("key B", "item B");
        map.put("key C", "item C");
        return map;
    }

    @GET
    @Path("array")
    @ApiOperation ("Returns an array of strings")
    public String[] array() {
        return new String[] { "item A", "item B", "item C" };
    }

    @GET
    @Path("calendar")
    @ApiOperation ("Returns a GregorianCalendar")
    public GregorianCalendar calendar() {
        return new GregorianCalendar();
    }

}
