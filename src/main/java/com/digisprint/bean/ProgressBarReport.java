package com.digisprint.bean;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="progress_bar_report")
public class ProgressBarReport {

	private String userId;
	
	private boolean registrationOneFormCompleted;
	
	private boolean committeeApproval;
	
	private boolean presidentApproval;
	
	private boolean registrationThreeFormCompleted;
	
	private boolean payment;
	
	private boolean accountantAcknowledgement;
	
	private boolean member;
	
}
