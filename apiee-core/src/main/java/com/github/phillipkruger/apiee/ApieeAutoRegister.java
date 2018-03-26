package com.github.phillipkruger.apiee;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import lombok.extern.java.Log;


import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
@Log
public class ApieeAutoRegister implements DynamicFeature {
    
    @Produces @ApieeClasses @ApplicationScoped 
    private final Set<Class> classes = new HashSet<>();
    
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        Class<?> resourceClass = resourceInfo.getResourceClass();
        if(!classes.contains(resourceClass)){
            classes.add(resourceClass);
            log.log(Level.FINEST, "Apiee adding class [{0}]", resourceClass);
        }
    }
}