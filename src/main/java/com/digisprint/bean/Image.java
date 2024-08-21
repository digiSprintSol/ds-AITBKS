package com.digisprint.bean;

import lombok.Data;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "images")
public class Image {

	@Id
    private String id;

    private String name;
    
    private String folderName;

    private String url;

}
