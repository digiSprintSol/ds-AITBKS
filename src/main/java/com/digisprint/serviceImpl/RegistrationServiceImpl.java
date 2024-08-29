package com.digisprint.serviceImpl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.digisprint.EmailUtils.EmailService;
import com.digisprint.EmailUtils.EmailTemplates;
import com.digisprint.EmailUtils.LoadHtmlTemplates;
import com.digisprint.bean.AccessBean;
import com.digisprint.bean.EmailUpload;
import com.digisprint.bean.PaymentInfo;
import com.digisprint.bean.ProgressBarReport;
import com.digisprint.bean.RegistrationFrom;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.filter.JwtTokenUtil;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.repository.PaymentRepository;
import com.digisprint.repository.ProgressBarRepository;
import com.digisprint.repository.RegistrationFromRepository;
import com.digisprint.requestBean.ApprovalFrom;
import com.digisprint.requestBean.RegistrationFrom2;
import com.digisprint.requestBean.UploadPaymentReceipt;
import com.digisprint.responseBody.FilterMemberResponse;
import com.digisprint.responseBody.IdentityCard;
import com.digisprint.service.RegistrationService;
import com.digisprint.utils.ApplicationConstants;
import com.digisprint.utils.EmailConstants;
import com.digisprint.utils.ErrorResponseConstants;
import com.digisprint.utils.GeneratingCredentials;
import com.digisprint.utils.RegistrationFormConstants;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

	private RegistrationFromRepository registrationFromRepository;

	private ProgressBarRepository progressBarRepository;

	private EmailService email;

	private EmailTemplates emailTemplates;

	private GeneratingCredentials generatingCredentials;

	private PaymentRepository paymentRepository;

	private JwtTokenUtil jwtTokenUtil;

	private LoadHtmlTemplates htmlTemplates;

	private AccessBeanRepository accessBeanRepository;

	public RegistrationServiceImpl(RegistrationFromRepository registrationFromRepository,
			ProgressBarRepository progressBarRepository, EmailService email, EmailTemplates emailTemplates,
			GeneratingCredentials generatingCredentials, PaymentRepository paymentRepository, JwtTokenUtil jwtTokenUtil,
			LoadHtmlTemplates htmlTemplates, AccessBeanRepository accessBeanRepository) {
		super();
		this.registrationFromRepository = registrationFromRepository;
		this.progressBarRepository = progressBarRepository;
		this.email = email;
		this.emailTemplates = emailTemplates;
		this.generatingCredentials = generatingCredentials;
		this.paymentRepository = paymentRepository;
		this.jwtTokenUtil = jwtTokenUtil;
		this.htmlTemplates = htmlTemplates;
		this.accessBeanRepository = accessBeanRepository;
	}

	@Value("${spring.mail.username}")
	private String ADMIN_USERNAME;

	@Override
	public RegistrationFrom registerUser(RegistrationFrom registrationForm) throws IOException, MessagingException {

		Optional<RegistrationFrom> existingUser = registrationFromRepository
				.findByEmailAddress(registrationForm.getEmailAddress());
		if (existingUser.isPresent()) {
			throw new IllegalArgumentException("The entered email id already exists. Please enter another email id.");
		}

		// Sending mail to user.
		List<String> membersList = new ArrayList<>();
		String body = htmlTemplates.loadTemplate(emailTemplates.getWelcomeMailAfterFillingFirstRegistrationFrom());
		body = body.replaceAll(EmailConstants.REPLACE_PLACEHOLDER_NAME, registrationForm.getFullName());
		membersList.add(registrationForm.getEmailAddress());
		String[] newUser = new String[1];
		newUser[0] = registrationForm.getEmailAddress();
		email.MailSendingService(ADMIN_USERNAME, newUser, body, EmailConstants.REGISTRATOIN_1_EMAIL_SUBJECT);
		// Sending mail to committee members
		body = htmlTemplates.loadTemplate(emailTemplates.getNewUserNotifyToCommittee());

		List<AccessBean> committeList = accessBeanRepository.findByCommitee(true);
		List<String> emailOfCommittee = committeList.stream().map(object -> object.getEmail())
				.collect(Collectors.toList());
		String[] emailsForCommiteeArray = new String[emailOfCommittee.size()];
		for (int i = 0; i < emailOfCommittee.size(); i++) {
			emailsForCommiteeArray[i] = emailOfCommittee.get(i);
		}
		email.MailSendingService(ADMIN_USERNAME, emailsForCommiteeArray, body,
				EmailConstants.NEW_USER_REGISTERED_SUBJECT);

		registrationForm.setApplicantChoosenMembership(registrationForm.getCategoryOfMembership());
		registrationForm.setCreatedDate(LocalDateTime.now());
		RegistrationFrom userDeatils = registrationFromRepository.save(registrationForm);
		ProgressBarReport progressBarReport = new ProgressBarReport();
		progressBarReport.setUserId(userDeatils.getUserId());
		progressBarReport.setRegistrationOneFormCompleted(RegistrationFormConstants.TRUE);
		progressBarRepository.save(progressBarReport);
		return registrationForm;
	}

	@Override
	public ResponseEntity getAllRegisteredUsers(String token) {

		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("Token cannot be null or empty");
		}

		JSONObject jsonObject = decodeToken(token);
		if (!jsonObject.has("userId") || !jsonObject.has("access")) {
			throw new IllegalArgumentException("Token must contain userId and access fields");
		}
		String identityNumber = jsonObject.getString("userId");
		List accessList = jwtTokenUtil.getAccessList(token);

		List<RegistrationFrom> allUsersList = registrationFromRepository.findAll();
		if (allUsersList.size() == 0) {
			return new ResponseEntity("No data present", HttpStatus.NOT_FOUND);
		} else {
			if (accessList.contains(ApplicationConstants.PRESIDENT)) {
				allUsersList = allUsersList.stream()
						.filter(p -> p.getCommitteeOneRemarksForApplicant() != null
								&& p.getCommitteeTwoRemarksForApplicant() != null
								&& p.getCommitteeThreeRemarksForApplicant() != null)
						.toList();

				allUsersList = allUsersList.stream()
						.filter(p -> !p.getCommitteeOneRemarksForApplicant().isEmpty()
								&& !p.getCommitteeTwoRemarksForApplicant().isEmpty()
								&& !p.getCommitteeThreeRemarksForApplicant().isEmpty())
						.collect(Collectors.toList());
				if (allUsersList.size() == 0) {
					return new ResponseEntity("No data present", HttpStatus.NOT_FOUND);
				} else {
					return new ResponseEntity(allUsersList, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity(allUsersList, HttpStatus.OK);
			}
		}
	}

	public JSONObject decodeToken(String jwtToken) {
		return JwtTokenUtil.decodeUserToken(jwtToken);
	}

	@Override
	public ResponseEntity committeePresidentAccountantApproval(String token, String userId, ApprovalFrom from)
			throws Exception {

		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("Token cannot be null or empty");
		}

		JSONObject jsonObject = decodeToken(token);
		if (!jsonObject.has("userId") || !jsonObject.has("access")) {
			throw new IllegalArgumentException("Token must contain userId and access fields");
		}

		String identityNumber = jsonObject.getString("userId");
		List accessList = jwtTokenUtil.getAccessList(token);
		String userType = null;

		if (accessList.contains(ApplicationConstants.PRESIDENT)) {
			userType = ApplicationConstants.PRESIDENT;
		}

		else if (accessList.contains(ApplicationConstants.COMMITEE)) {
			userType = ApplicationConstants.COMMITEE;
		} else {
			userType = ApplicationConstants.ACCOUNTANT;
		}

		RegistrationFrom specificUserDetails = registrationFromRepository.findById(userId).get();
		if (specificUserDetails == null) {
			return new ResponseEntity("No user found with the user id", HttpStatus.NOT_FOUND);
		}

		Optional<ProgressBarReport> optionalProgressBarReport = progressBarRepository
				.findById(specificUserDetails.getUserId());
		if (!optionalProgressBarReport.isPresent()) {
			return new ResponseEntity("No progress bar report found for the user", HttpStatus.NOT_FOUND);
		}
		String[] user = new String[1];
		user[0] = specificUserDetails.getEmailAddress();
		ProgressBarReport progressBarReport = optionalProgressBarReport.get();

		boolean approvalStatus = false;

		switch (from.getStatusOfApproval()) {

		case RegistrationFormConstants.APPROVAL:
			approvalStatus = true;
			break;

		case RegistrationFormConstants.REJECTED:
			approvalStatus = false;
			break;

		default:
			approvalStatus = false;
		}
		if (userType.equalsIgnoreCase(ApplicationConstants.COMMITEE)) {

			if (progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
					&& specificUserDetails.getCommitteeOneRemarksForApplicant() == null
					&& specificUserDetails.isCommitteeOneApproval() == RegistrationFormConstants.FALSE) {
				System.out.println("");
				specificUserDetails.setCommitteeOneApproval(approvalStatus);
				specificUserDetails.setCommitteeOneChoosenMembershipForApplicant(from.getMembership());
				specificUserDetails.setCommitteeOneRemarksForApplicant(from.getRemarks());

			} else if (progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
					&& specificUserDetails.getCommitteeOneRemarksForApplicant() != null
					&& specificUserDetails.getCommitteeTwoRemarksForApplicant() == null
					&& specificUserDetails.getCommitteeThreeRemarksForApplicant() == null) {
				specificUserDetails.setCommitteeTwoApproval(approvalStatus);
				specificUserDetails.setCommitteeTwoChoosenMembershipForApplicant(from.getMembership());
				specificUserDetails.setCommitteeTwoRemarksForApplicant(from.getRemarks());
			}
			// best of 3 committee members should true
			else if (progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
					&& from.getStatusOfApproval().equalsIgnoreCase(RegistrationFormConstants.APPROVAL)
					&& specificUserDetails.isCommitteeOneApproval() == RegistrationFormConstants.TRUE
					&& specificUserDetails.isCommitteeTwoApproval() == RegistrationFormConstants.TRUE
					&& specificUserDetails.getCommitteeThreeRemarksForApplicant() == null) {
				System.out.println("inside c3 approvals");
				specificUserDetails.setCommitteeThreeApproval(RegistrationFormConstants.TRUE);
				specificUserDetails.setCommitteeThreeChoosenMembershipForApplicant(from.getMembership());
				specificUserDetails.setCommitteeThreeRemarksForApplicant(from.getRemarks());

				progressBarReport.setCommitteeApproval(RegistrationFormConstants.TRUE);
				String body = null;
				// Sending credentials to the Applicant as Committee approved.
				String username = specificUserDetails.getEmailAddress();
				String passcode = generatingCredentials.generatePasscode(specificUserDetails.getCategory(),
						specificUserDetails.getPhoneNumber());
				body = htmlTemplates.loadTemplate(emailTemplates.getLoginCredentialsEmail());
				body = body.replace("[UserName]", username).replace("[Password]", passcode);
				email.MailSendingService(ADMIN_USERNAME, user, body, EmailConstants.LOGIN_CREDENTIALS_SUBJECT);
				specificUserDetails.setCommitteeThreeApproval(true);

				AccessBean accessBean = new AccessBean();
				accessBean.setAccessId(specificUserDetails.getUserId());
				accessBean.setAccountant(false);
				accessBean.setUser(true);
				accessBean.setDeleted(false);
				accessBean.setEmail(username);
				accessBean.setPassword(passcode);
				accessBeanRepository.save(accessBean);
			}

			else if (specificUserDetails.isCommitteeOneApproval() == RegistrationFormConstants.FALSE
					&& specificUserDetails.isCommitteeTwoApproval() == RegistrationFormConstants.FALSE
					&& from.getStatusOfApproval().equalsIgnoreCase(RegistrationFormConstants.REJECTED)) {

				progressBarReport.setCommitteeApproval(RegistrationFormConstants.FALSE);
				specificUserDetails.setCommitteeThreeApproval(false);
				specificUserDetails.setCommitteeThreeApproval(approvalStatus);
				specificUserDetails.setCommitteeThreeChoosenMembershipForApplicant(from.getMembership());
				specificUserDetails.setCommitteeThreeRemarksForApplicant(from.getRemarks());
				System.out.println("inside c3 rejection");
				String body = null;
				body = htmlTemplates.loadTemplate(emailTemplates.getCommitteeRejectEmail());

				email.MailSendingService(ADMIN_USERNAME, user, body, EmailConstants.COMMITTEE_REJECTED_SUBJECT);
			} else {
				System.out.println("inside waiting rejection");
				// waiting email
			}

		} // if commitee not approved, prsident should send the email after approval
		else if (userType.equalsIgnoreCase(ApplicationConstants.PRESIDENT)) {

			if (progressBarReport.isCommitteeApproval() == RegistrationFormConstants.FALSE
					&& from.getStatusOfApproval().equalsIgnoreCase(RegistrationFormConstants.APPROVAL)) {

				progressBarReport.setPresidentApproval(RegistrationFormConstants.TRUE);
				progressBarReport.setPresidentFillingRegistrationTwoForm(RegistrationFormConstants.TRUE);
				specificUserDetails.setPresidentRemarksForApplicant(from.getRemarks());
				specificUserDetails.setPresidentChoosenMembershipForApplicant(from.getMembership());
				specificUserDetails.setPresidentApproval(RegistrationFormConstants.TRUE);
				String body = null;
				// Sending credentials to the Applicant as Committee approved.
				String username = specificUserDetails.getEmailAddress();
				String passcode = generatingCredentials.generatePasscode(specificUserDetails.getCategory(),
						specificUserDetails.getPhoneNumber());
				body = htmlTemplates.loadTemplate(emailTemplates.getLoginCredentialsEmail());
				body = body.replace("[UserName]", username).replace("[Password]", passcode);

				email.MailSendingService(ADMIN_USERNAME, user, body, EmailConstants.LOGIN_CREDENTIALS_SUBJECT);

			}

			else if (specificUserDetails != null && progressBarReport != null
					&& progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
					&& progressBarReport.isCommitteeApproval() == RegistrationFormConstants.TRUE
					&& from.getStatusOfApproval().equalsIgnoreCase(RegistrationFormConstants.APPROVAL)) {
				String body = null;
				progressBarReport.setPresidentApproval(RegistrationFormConstants.TRUE);
				progressBarReport.setPresidentFillingRegistrationTwoForm(RegistrationFormConstants.TRUE);
				specificUserDetails.setPresidentRemarksForApplicant(from.getRemarks());
				specificUserDetails.setPresidentChoosenMembershipForApplicant(from.getMembership());
				specificUserDetails.setPresidentApproval(RegistrationFormConstants.TRUE);
				body = htmlTemplates.loadTemplate(emailTemplates.getPresidentApprovalEmail());
				email.MailSendingService(ADMIN_USERNAME, user, body, EmailConstants.PRESIDENT_APPROVED_SUBJECT);

			} else if (from.getStatusOfApproval().equalsIgnoreCase(RegistrationFormConstants.REJECTED)) {
				progressBarReport.setPresidentFillingRegistrationTwoForm(RegistrationFormConstants.TRUE);
				progressBarReport.setPresidentApproval(RegistrationFormConstants.FALSE);
				String body = null;
				// rejection mail from president
				specificUserDetails.setPresidentApproval(RegistrationFormConstants.FALSE);
				body = htmlTemplates.loadTemplate(emailTemplates.getPresidentRejectionEmail());
				email.MailSendingService(ADMIN_USERNAME, user, body, EmailConstants.PRESIDENT_REJECTED_SUBJECT);
			} else {
				progressBarReport.setPresidentApproval(RegistrationFormConstants.FALSE);
				specificUserDetails.setPresidentApproval(RegistrationFormConstants.FALSE);
				// waiting mail from president
			}

		} else if (userType.equalsIgnoreCase(ApplicationConstants.ACCOUNTANT)) {
			if (progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
					&& progressBarReport.isPayment() && progressBarReport.isPresidentApproval()
					&& progressBarReport.isRegistrationThreeFormCompleted() == RegistrationFormConstants.TRUE) {

				progressBarReport.setAccountantAcknowledgement(RegistrationFormConstants.TRUE);
				progressBarReport.setMember(RegistrationFormConstants.TRUE);
				String memberIdentityNumber = generatingCredentials.generateMemberId(userId);
				// send congratulations mail with generated memberID
				String body = null;
				body = htmlTemplates.loadTemplate(emailTemplates.getMembershipApproved());
				email.MailSendingService(ADMIN_USERNAME, user, body, EmailConstants.MEMBERSHIP_APPROVED);
				specificUserDetails.setMembershipId(memberIdentityNumber);
				specificUserDetails.setMember(RegistrationFormConstants.TRUE);

			} else {
				return new ResponseEntity("All conditions for accountant approval are not met",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} else {
			return new ResponseEntity("You don't have access !!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		specificUserDetails.setStatus(from.getStatusOfApproval());
		specificUserDetails.setLastModifiedDate(LocalDateTime.now());
		registrationFromRepository.save(specificUserDetails);
		progressBarRepository.save(progressBarReport);
		return new ResponseEntity("Status updated", HttpStatus.OK);

	}

	@Override
	public ProgressBarReport progressBarForAUser(String token) {
		JSONObject tokenObject = decodeToken(token);
		String userId = tokenObject.getString("userId");
		return progressBarRepository.findById(userId).get();
	}

	@Override
	public List<RegistrationFrom> committeePresidentAccountantViewListOfApplicants(String token) {

		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("Token cannot be null or empty");
		}

		JSONObject jsonObject = decodeToken(token);
		if (!jsonObject.has("userId") || !jsonObject.has("access")) {
			throw new IllegalArgumentException("Token must contain 'id' and 'access' fields");
		}

		List accessList = jwtTokenUtil.getAccessList(token);

		if (accessList.contains(ApplicationConstants.COMMITEE) || accessList.contains(ApplicationConstants.PRESIDENT)) {
			return registrationFromRepository.findAll();
		} else if (accessList.contains(ApplicationConstants.ACCOUNTANT)) {
			return registrationFromRepository.findAll().stream()
					.filter(registrationForm -> registrationForm.getPaymentInfo() != null).collect(Collectors.toList());
		} else {
			throw new IllegalArgumentException("Invalid user type");
		}

	}

	@Override
	public RegistrationFrom userFillingRegistrationThreeForm(String token, RegistrationFrom2 registrationFrom2) {
		JSONObject tokenObject = decodeToken(token);
		String userId = tokenObject.getString("userId");
		RegistrationFrom specificUserDetails = registrationFromRepository.findById(userId).get();
		if (specificUserDetails == null) {
			throw new IllegalArgumentException("No user found with the provided phone number");
		}

		Optional<ProgressBarReport> optionalProgressBarReport = progressBarRepository
				.findById(specificUserDetails.getUserId());
		if (!optionalProgressBarReport.isPresent()) {
			throw new IllegalArgumentException("No progress bar report found for the user");
		}

		BeanUtils.copyProperties(registrationFrom2, specificUserDetails);
		registrationFromRepository.save(specificUserDetails);

		ProgressBarReport progressBarReport = optionalProgressBarReport.get();
		progressBarReport.setRegistrationThreeFormCompleted(RegistrationFormConstants.TRUE);
		progressBarRepository.save(progressBarReport);

		return specificUserDetails;
	}

	@Override
	public List<RegistrationFrom> accountFirstView() {
		return registrationFromRepository.findAll().stream().filter(p -> p.getPaymentInfo() != null)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity getIDOfUser(String token) throws MalformedURLException, UserNotFoundException {
		JSONObject tokenObject = decodeToken(token);
		String userId = tokenObject.getString("userId");
		RegistrationFrom user = registrationFromRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND));
		IdentityCard card = new IdentityCard();
		card.setImage(user.getProfilePic());
		card.setMembershipId(user.getMembershipId());
		card.setNameofTheApplicant(user.getFullName());
		card.setTypeOfMemberShip(user.getPresidentChoosenMembershipForApplicant());

		return new ResponseEntity(card, HttpStatus.OK);
	}

	@Override
	public ResponseEntity uploadTranscationRecepit(String token, UploadPaymentReceipt uploadPaymentReceipt)
			throws IOException, MessagingException {
		JSONObject tokenObject = decodeToken(token);
		String userId = tokenObject.getString("userId");
		RegistrationFrom user = registrationFromRepository.findById(userId).get();

		PaymentInfo paymentInfo = new PaymentInfo();
		paymentInfo.setTransactionDate(LocalDate.now());
		paymentInfo.setPaymentDetailDocument(uploadPaymentReceipt.getPaymentImageUrl());
		BeanUtils.copyProperties(uploadPaymentReceipt, paymentInfo);
		user.setPaymentInfo(paymentInfo);
		registrationFromRepository.save(user);

		ProgressBarReport progressBarReport = progressBarRepository.findById(userId).get();
		progressBarReport.setPayment(true);
		progressBarRepository.save(progressBarReport);
		String[] sendEmail = new String[1];
		sendEmail[0] = user.getEmailAddress();
		String body = htmlTemplates.loadTemplate(emailTemplates.getPaymentApprovalEmail());

		email.MailSendingService(ADMIN_USERNAME, sendEmail, body, EmailConstants.PAYMENT_RECIEVED_SUBJECT);

		return new ResponseEntity("Recepit Uploaded successfully", HttpStatus.OK);
	}

	@Override
	public ResponseEntity getPaymentReceipt(String userId) throws MalformedURLException {
		RegistrationFrom user = registrationFromRepository.findById(userId).get();

		if (user != null) {

			return new ResponseEntity(user.getPaymentInfo().getPaymentDetailDocument(), HttpStatus.OK);
		} else {
			return new ResponseEntity("No recepit found with the user", HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity getUserDetails(String token) {
		JSONObject tokenObject = decodeToken(token);
		String userId = tokenObject.getString("userId");
		RegistrationFrom specificUserDetails = registrationFromRepository.findById(userId).get();
		if (specificUserDetails == null) {
			return new ResponseEntity("No user found", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity(specificUserDetails, HttpStatus.OK);
		}
	}

	@Override
	public List<String> referenceOneDropdown() {

		List<ProgressBarReport> trueMembers = progressBarRepository.findByMemberTrue();

		List<String> userIds = trueMembers.stream().map(ProgressBarReport::getUserId).collect(Collectors.toList());

		List<RegistrationFrom> registrationForms = new ArrayList<>();
		for (String userId : userIds) {
			Optional<RegistrationFrom> optionalForm = registrationFromRepository.findById(userId);
			if (optionalForm.isPresent()) {
				registrationForms.add(optionalForm.get());
			} else {
				System.out.println("No RegistrationForm found for User ID: " + userId);
			}
		}

		List<String> memberNames = registrationForms.stream().map(RegistrationFrom::getFullName)
				.collect(Collectors.toList());

		return memberNames;

	}

	@Override
	public ResponseEntity getAllFilteredMembers(String categoryOfMember) {

		List<RegistrationFrom> allUsers = registrationFromRepository.findAll();
		if (allUsers.size() == 0) {
			return new ResponseEntity("No data found", HttpStatus.NOT_FOUND);
		} else {
			switch (categoryOfMember) {

			case RegistrationFormConstants.TRUSTEE:
				allUsers = allUsers.stream().filter(
						p -> p.getPresidentChoosenMembershipForApplicant().equals(RegistrationFormConstants.TRUSTEE))
						.collect(Collectors.toList());
				break;

			case RegistrationFormConstants.PATRON:
				allUsers = allUsers.stream().filter(
						p -> p.getPresidentChoosenMembershipForApplicant().equals(RegistrationFormConstants.PATRON))
						.collect(Collectors.toList());
				break;

			case RegistrationFormConstants.LIFE_MEMBER:
				allUsers = allUsers.stream().filter(p -> p.getPresidentChoosenMembershipForApplicant()
						.equals(RegistrationFormConstants.LIFE_MEMBER)).collect(Collectors.toList());
				break;
			}
		}

		if (allUsers.size() == 0) {
			return new ResponseEntity("No data found", HttpStatus.NOT_FOUND);
		}

		else {
			List<FilterMemberResponse> filterMemberResponsesList = allUsers.stream().map(eachUser -> {
				FilterMemberResponse filteredResponseBean = new FilterMemberResponse();
				BeanUtils.copyProperties(eachUser, filteredResponseBean);
				return filteredResponseBean;
			}).collect(Collectors.toList());
			return new ResponseEntity(filterMemberResponsesList, HttpStatus.OK);
		}

	}

	@Override
	public ResponseEntity bulkEmailUpload(EmailUpload emailUpload) throws IOException, MessagingException {

		List<RegistrationFrom> allusers = registrationFromRepository.findAll();

		switch (emailUpload.getToEmail()) {
		case EmailConstants.TRUSTEE:

			List<RegistrationFrom> filterByTrustee = allusers.stream()
					.filter(r -> EmailConstants.TRUSTEE.equalsIgnoreCase(r.getPresidentChoosenMembershipForApplicant()))
					.collect(Collectors.toList());
			List<String> trusteeMails = filterByTrustee.stream().map((trustee) -> {
				return trustee.getEmailAddress();
			}).collect(Collectors.toList());
			String[] trusteeEmails = trusteeMails.toArray(String[]::new);
			email.MailSendingService(emailUpload.getToEmail(), trusteeEmails, emailUpload.getBody(), emailUpload.getSubject());

			break;
		case EmailConstants.PATRON:

			List<RegistrationFrom> filterByPatron = allusers.stream()
					.filter(r -> EmailConstants.PATRON.equalsIgnoreCase(r.getPresidentChoosenMembershipForApplicant()))
					.collect(Collectors.toList());

			List<String> patronMails = filterByPatron.stream().map((patron) -> {
				return patron.getEmailAddress();
			}).collect(Collectors.toList());
			String[] patronEmails = patronMails.toArray(String[]::new);
			email.MailSendingService(emailUpload.getToEmail(), patronEmails, emailUpload.getBody(), emailUpload.getSubject());

			break;
		case EmailConstants.LIFE_MEMBER:

			List<RegistrationFrom> filterByLifeMember = allusers.stream().filter(
					r -> EmailConstants.LIFE_MEMBER.equalsIgnoreCase(r.getPresidentChoosenMembershipForApplicant()))
					.collect(Collectors.toList());

			List<String> lifeMemberMails = filterByLifeMember.stream().map((lifeMember) -> {
				return lifeMember.getEmailAddress();
			}).collect(Collectors.toList());
			String[] lifeMemberEmails = lifeMemberMails.toArray(String[]::new);
			email.MailSendingService(emailUpload.getToEmail(), lifeMemberEmails, emailUpload.getBody(), emailUpload.getSubject());

			break;
		case EmailConstants.ACCOUNTANT:
			List<AccessBean> accountant = accessBeanRepository.findAll();
			List<AccessBean> ac = accountant.stream().filter(AccessBean::isAccountant).collect(Collectors.toList());

			List<String> accountantMails = ac.stream().map((eachAccountant) -> {
				return eachAccountant.getEmail();
			}).collect(Collectors.toList());

			String[] accountantEmails = accountantMails.toArray(String[]::new);
			email.MailSendingService(emailUpload.getToEmail(), accountantEmails, emailUpload.getBody(), emailUpload.getSubject());

			break;
		case EmailConstants.COMMITTEE_MEMBER:

			List<AccessBean> committee = accessBeanRepository.findAll();

			List<AccessBean> cm = committee.stream().filter(AccessBean::isAccountant).collect(Collectors.toList());

			List<String> committeeMembetMails = cm.stream().map((committeeMember) -> {
				return committeeMember.getEmail();
			}).collect(Collectors.toList());
			String[] committeeMemberEmails = committeeMembetMails.toArray(String[]::new);
			email.MailSendingService(emailUpload.getToEmail(), committeeMemberEmails, emailUpload.getBody(), emailUpload.getSubject());

			break;

		default:
			break;
		}

		return null;
	}

}
