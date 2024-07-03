package com.digisprint.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
public class Childern {
	
	@Id
	private Long id;

	private String name;

	private String education;

	private String profession;
	
	private String isMarried;
	
	
}
