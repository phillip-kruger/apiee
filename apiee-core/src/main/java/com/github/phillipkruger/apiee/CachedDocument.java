package com.github.phillipkruger.apiee;

import java.net.URL;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class CachedDocument {
    private int hash;
    private URL url;
    private String document;
    private Date generatedOn = new Date();
    
    public CachedDocument(int hash,URL url,String document){
        this.hash = hash;
        this.url = url;
        this.document = document;
    }
}
