package com.digisprint.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "feedbacks")
public class Feedbacks {

	@Id
	private String id;
	
	private String yourName;
	
//	private boolean aitbksMember;
	
	private String description;
	
}
