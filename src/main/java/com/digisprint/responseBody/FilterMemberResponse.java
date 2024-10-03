package com.digisprint.responseBody;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class FilterMemberResponse {

	private String profilePic;

	private String membershipId;

	private String firstName;

	private String lastName;	
	
	private String profession;

	private String email;

	private String district;
}