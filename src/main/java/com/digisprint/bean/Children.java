package com.digisprint.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
public class Children {

	private String name;

	private String education;

	private String profession;
	
	private String isMarried;
	
	private String childAge;
	
	private String gender;
	
	
}
