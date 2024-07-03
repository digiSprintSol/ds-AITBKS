package com.digisprint.bean;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "registration_from")
public class RegistrationFrom {

	@Id
	private String userId;

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String name;

	private LocalDateTime dob;

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String gender;

	@Email
	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String emailAddress;

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	@Size(min=10, max=10, message="PhoneNumber")
	private String mobileNumber;

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String education;

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String profession;

	private boolean isMarried;
	
	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String parentName;
	
	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String spouseName;
	
	private List<Childern> childern;

	private List<Address> address;

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	@Size(min=16, max=16, message="aadharCard")
	private String aadharCard;

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	@Size(min=10, max=10, message="voterIdCard")
	private String voterIdCard;

	private boolean isMemberOfOtherCommunity;

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String nameOfCommunity;

	private Category category;
	
	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String categoryOfMembership;

	@CreatedDate
	private LocalDateTime createdDate;	

	private LocalDateTime lastModifiedDate;
	
	private PaymentInfo paymentInfo;
	
	private boolean stageOneApproval;
	
	private boolean stageTwoApproval;
	
	private boolean stageOneApproved;
	
	private boolean stageTwoApproved;

	
	

}
