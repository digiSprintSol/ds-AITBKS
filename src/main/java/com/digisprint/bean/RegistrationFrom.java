package com.digisprint.bean;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Document(collection = "user_details")
public class RegistrationFrom {

	@Id
	private String userId;
	
	/**
	 * PERSONAL DETAILS
	 */
	@NotEmpty(message="Please, do give your profile photo")
	@NotNull(message="Requried")
	private String profilePic;

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String fullName;

	@NotNull(message="Required")
    @Past(message="Date of birth must be in the past")
	private LocalDateTime dateOfBirth;

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String gender; //{DROPDOWN}

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String category; //{DROPDOWN}

	/**
	 * PRESENT ADDRESS
	 */
	private List<Address> address;

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
	private String education;//{DROPDOWN}

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String profession; //{DROPDOWN}

	/**
	 * FAMILY DETAILS
	 */
	private FamilyDetails familyDetails;

	/**
	 * IDENTITY DETAILS
	 */
	private String aadharCard;

	private String voterIdCard;

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String occupation;

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String brieflyTellAboutYourself; 

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String reasonToJoinAITBKS; 

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String reference1;

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String reference2;

	private String categoryOfMembership;

	/**
	 * DECLERATION
	 */
	@AssertTrue(message="Membership application must be requested")
	private boolean requestForMembershipApplicationFromDeclaration;	

	private String password;

	private String confrimPassowrd;

	@CreatedDate
	private LocalDateTime createdDate;	

	private LocalDateTime lastModifiedDate;
	
	private String nativePlace; //R3
	
	private String status; //from R1
	
	private boolean isApplicationForMembershipDeclaration; //R3
	
	private boolean isMemberOfOtherCommunity; //R3

	private PaymentInfo paymentInfo; //R3
	
	/**
	 * MEMBERSHIP INFO
	 */
	
	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String applicantChoosenMembership;
	
	private String committeeChoosenMembershipForApplicant;
	
	private String presidentChoosenMembershipForApplicant; //R2
	
	private String presidentRemarksForApplicant; //R2

}
