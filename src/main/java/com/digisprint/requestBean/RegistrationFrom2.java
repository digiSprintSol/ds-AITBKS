package com.digisprint.requestBean;

import lombok.Data;

@Data
public class RegistrationFrom2 {

	private boolean isMemberOfOtherCommunity;
	
	private boolean isApplicationForMembershipDeclaration;
	
	private String nativePlace;
	
	private String communityName;
}
