package com.github.phillipkruger.apiee.example;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@XmlRootElement  
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor @NoArgsConstructor
public class SomeObject {
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String url;
}
