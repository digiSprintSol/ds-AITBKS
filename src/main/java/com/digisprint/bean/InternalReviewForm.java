package com.digisprint.bean;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@Document(collection= "internal_review_from")
public class InternalReviewForm {
	
	@Id
	private Long id;

	private boolean isTrustee;

	private boolean isPatron;
	
	private boolean isLifeMember;
	
	private boolean approvedByPersident;
	
	private boolean approvedBySecretary;
	
	private boolean approvedByChairman;
	
	private boolean approvedByMembers;
	
	private LocalDateTime approvedOn;
	
	private String approvedBy;
	
	private LocalDateTime modifiedOn;
	
	private String modifiedBy;
}
