package com.digisprint.bean;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "feedbacks")
public class Feedback {

	@Id
	private String id; 

	private String url;

	private String name;
	
	private String profession;

	private String description;
	
	private String createdBy;
	
	@CreatedDate
	private LocalDateTime createdDate;
	
	private String modifiedBy;
	
	private String modifiedDate;

}
