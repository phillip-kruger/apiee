package com.github.phillipkruger.apiee.mapper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
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
