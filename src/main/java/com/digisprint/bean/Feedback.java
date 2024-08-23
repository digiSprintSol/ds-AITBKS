package com.digisprint.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "feedbacks")
public class Feedback {

	@Id
	private String id;

	private String url;

	private String yourName;

	private String description;

}
