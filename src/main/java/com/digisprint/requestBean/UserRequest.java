package com.digisprint.requestBean;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.stereotype.Component;

import com.digisprint.bean.Address;
import com.digisprint.bean.FamilyDetails;

import lombok.Data;

@Data
@Component
public class UserRequest {

	/**
	 * PERSONAL DETAILS
	 */
	@NotEmpty(message = "Please, do give your profile photo")
	@NotNull(message = "Requried")
	private String profilePic;

	@Indexed
	@NotEmpty(message = "Don't pass an empty String")
	@NotNull(message = "Requried")
	private String firstName;

	@NotEmpty(message = "Don't pass an empty String")
	@NotNull(message = "Requried")
	private String lastName;

	@NotNull(message = "Required")
	@Past(message = "Date of birth must be in the past")
	private LocalDateTime dateOfBirth;

	@NotEmpty(message = "Don't pass an empty String")
	@NotNull(message = "Requried")
	private String gender; // {DROPDOWN}

	@NotEmpty(message = "Don't pass an empty String")
	@NotNull(message = "Requried")
	private String category; // {DROPDOWN}

	/**
	 * PRESENT ADDRESS
	 */
	private List<Address> address;

	@Email
	@NotEmpty(message = "Don't pass an empty String")
	@NotNull(message = "Requried")
	private String emailAddress;

	@NotEmpty(message = "Don't pass an empty String")
	@NotNull(message = "Requried")
	@Size(min = 10, max = 10, message = "PhoneNumber")
	private String phoneNumber;

	@NotEmpty(message = "Don't pass an empty String")
	@NotNull(message = "Requried")
	private String education;// {DROPDOWN}

	@NotEmpty(message = "Don't pass an empty String")
	@NotNull(message = "Requried")
	private String profession; // {DROPDOWN}

	/**
	 * FAMILY DETAILS
	 */
	private FamilyDetails familyDetails;

	/**
	 * IDENTITY DETAILS
	 */
	private String aadharCard;

	private String voterIdCard;

	@NotEmpty(message = "Don't pass an empty String")
	@NotNull(message = "Requried")
	private String occupation;

	@NotEmpty(message = "Don't pass an empty String")
	@NotNull(message = "Requried")
	private String brieflyTellAboutYourself;

	@NotEmpty(message = "Don't pass an empty String")
	@NotNull(message = "Requried")
	private String reasonToJoinAITBKS;

	@NotEmpty(message = "Don't pass an empty String")
	@NotNull(message = "Requried")
	private String reference1;

	private String categoryOfMembership;

	private boolean member;

	private String membershipId;

}
