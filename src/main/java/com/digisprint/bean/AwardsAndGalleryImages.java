package com.digisprint.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "AwardsAndGalleryImages")
public class AwardsAndGalleryImages {
	
	@Id
	private String id;
	
	private String awardTitle;
	
	private String awardImageURL;
	
	private String awardDescription;
	
}
