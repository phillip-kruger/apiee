package com.github.phillipkruger.apiee.providers;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
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