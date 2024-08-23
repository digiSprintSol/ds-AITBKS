package com.digisprint.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="progress_bar_report")
public class ProgressBarReport {

	@Id
	private String userId;
	
	private boolean registrationOneFormCompleted;
	
	private boolean committeeApproval;
	
	private boolean presidentFillingRegistrationTwoForm;
	
	private boolean presidentApproval;
	
	private boolean registrationThreeFormCompleted;
	
	private boolean payment;
	
	private boolean accountantAcknowledgement;
	
	private boolean member;
	
}
