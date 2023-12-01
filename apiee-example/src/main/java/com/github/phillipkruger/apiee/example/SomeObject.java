package com.github.phillipkruger.apiee.example;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@XmlRootElement  
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor @NoArgsConstructor
public class SomeObject {
    // @ApiModelProperty(value = "The Name !!", dataType = "string") TODO: Map this to new v3
    
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String url;
}
