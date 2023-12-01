package com.github.phillipkruger.apiee.providers;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Simple POJO that defines a list of errors
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@Data
@XmlRootElement  
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor @NoArgsConstructor
public class ValidationErrors {
    
    @XmlElementRef(type = ValidationError.class)
    @NotNull
    private List<ValidationError> validationError = new ArrayList<>();
    
}