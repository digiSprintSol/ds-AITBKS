package com.digisprint.bean;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class FamilyDetails {

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String fatherName;
	
	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String motherName;
	
	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String spouseName;
	
	private String spouseOccupation;
	
	private boolean isMarried;
	
	private List<Childern> childern;
}
