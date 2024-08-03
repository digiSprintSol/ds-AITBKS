package com.digisprint.bean;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@Document(collection= "internal_review_from")
public class InternalReviewForm {
	
	private String userId; //after approval from committe user id should set to this table.

	private String categoryOfMemberShip; // should look like recommend

	private String committeeRemarks;
	
	private String internalReviewForMemberAdmissionStatus;
	
	private LocalDateTime approvedOn;
	
	private String approvedBy;
	
	private LocalDateTime modifiedOn;
	
	private String modifiedBy;
	
	private String events;
	
	private String announcements;
	
	private String homePageImages;
	
}
