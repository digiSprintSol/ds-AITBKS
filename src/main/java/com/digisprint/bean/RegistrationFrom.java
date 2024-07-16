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
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Document(collection = "user_details")
public class RegistrationFrom {

	@Id
	private String userId;
	
	private String profilePic;  //*

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String fullName; //*

	private LocalDateTime dateOfBirth; //*

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String gender; //(it should be dropdrown)

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String category; //(it should be dropdrown nameOfCommunity) //*

	private List<Address> address; //*

	@Email
	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String emailAddress; //*

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	@Size(min=10, max=10, message="PhoneNumber")
	private String mobileNumber; //*

	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String education;//(it should be dropdrown) //*
 
	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String profession; //(it should be dropdrown) if others -user should type  //*

	private FamilyDetails familyDetails; //*

	private String aadharCard; //*

	private String voterIdCard;

	private String occupation; //*

	private String password;

	private String confrimPassowrd; //(forgot password)

	private String brieflyTellAboutYourself; 
	
	private String reasonToJoinAITBKS; 
	
	private String reference1; //store member id //*
	
	private String reference2; //*
	
	private boolean requestForMembershipApplicationFromDeclaration;
	
	@NotEmpty(message="Don't pass an empty String")
	@NotNull(message="Requried")
	private String categoryOfMembership; //it should be button trustee/patron/life member 

	@CreatedDate
	private LocalDateTime createdDate;	

	private LocalDateTime lastModifiedDate;
	
	private String nativePlace; //R3
	
	private String status; //from R1
	
	private boolean isApplicationForMembershipDeclaration; //R3
	
	private boolean isMemberOfOtherCommunity; //R3

	private PaymentInfo paymentInfo; //R3

	


}
