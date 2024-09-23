package com.digisprint.bean;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "images")
public class Image {

	@Id
    private String id;

    private String name;
    
    private String folderName;
    
    private String folderPath;

    private String url;
    
    private LocalDate createdDate;
    
    

}
