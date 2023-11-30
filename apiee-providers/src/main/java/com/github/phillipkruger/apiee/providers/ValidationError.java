package com.github.phillipkruger.apiee.providers;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple POJO that defines a Error
 * @author Phillip Kruger (apiee@phillip-kruger.com)
 */
@Data
@AllArgsConstructor @NoArgsConstructor
@XmlRootElement  
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationError {
    
    @XmlAttribute
    @Getter @Setter
    private String message;
    
    @XmlAttribute
    @Getter @Setter
    private String path = "";
    
    @XmlAttribute
    @Getter @Setter
    private String invalidValue = "";
}
