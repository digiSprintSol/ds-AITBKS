package com.digisprint.bean;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "user_details")
@CompoundIndexes({
    @CompoundIndex(name = "name_lastModifiedDate_index", def = "{'firstName': 1, 'lastModifiedDate': -1}")
})
public class RegistrationForm {

	@Id
	private String userId;

	/**
	 * PERSONAL DETAILS
	 */
	@NotEmpty(message="Please, do give your profile photo")
	@NotNull(message="Requried")
	private String profilePic;

	@Indexed
	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String firstName;
	
	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String lastName;

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
	private String phoneNumber;

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

	private String categoryOfMembership;

	/**
	 * DECLERATION
	 */
	private boolean decalartionForRegOne;	

	private String password;

	private String confrimPassowrd;

	@CreatedDate
	private LocalDateTime createdDate;	

	@Indexed
	private LocalDateTime lastModifiedDate;

	private String nativePlace; //R3

	private String status; //from R1

	private boolean decalarationForRegThree; //R3

	private boolean isMemberOfOtherCommunity; //R3

	private String communityName;

	private PaymentInfo paymentInfo; //R3

	/**
	 * MEMBERSHIP INFO
	 */

	private String applicantChoosenMembership;

	private String committeeOneChoosenMembershipForApplicant;

//	private String committeeOneRemarksForApplicant;

	private String committeeTwoChoosenMembershipForApplicant;

//	private String committeeTwoRemarksForApplicant;

	private String committeeThreeChoosenMembershipForApplicant;

//	private String committeeThreeRemarksForApplicant;

	private String presidentChoosenMembershipForApplicant; //R2

	private String presidentRemarksForApplicant; //R2
	
	private String committeeOneApproval;

	private String committeeTwoApproval;

	private String committeeThreeApproval;	
	
	private String presidentApproval;
	
	private String committeeMemberOneId;
	
	private String committeeMemberTwoId;
	
	private String committeeMemberThreeId;
	
	private String presidentId;
	
	private String casteStatus; //oc/bc

	/**
	 * Membership id
	 */
	private boolean member;
	
	private String membershipId;

}
