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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
import com.digisprint.bean.RegistrationForm;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.filter.JwtTokenUtil;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.repository.PaymentRepository;
import com.digisprint.repository.ProgressBarRepository;
import com.digisprint.repository.RegistrationFromRepository;
import com.digisprint.requestBean.ApprovalFrom;
import com.digisprint.requestBean.RegistrationFrom2;
import com.digisprint.requestBean.UploadPaymentReceipt;
import com.digisprint.requestBean.UserRequest;
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
//@CacheConfig(cacheNames = { "usercache" })
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
//	@CachePut(value = "usercache", key = "#registrationForm.userId")
//	@CacheEvict(value = "usercache", key = "'allUsers'")
	public RegistrationForm registerUser(RegistrationForm registrationForm) throws IOException, MessagingException {

		Optional<RegistrationForm> existingUser = registrationFromRepository
				.findByEmailAddress(registrationForm.getEmailAddress());
		if (existingUser.isPresent()) {
			throw new IllegalArgumentException("The entered email id already exists. Please enter another email id.");
		}

		// Sending mail to user.
		List<String> membersList = new ArrayList<>();
		String body = htmlTemplates.loadTemplate(emailTemplates.getWelcomeMailAfterFillingFirstRegistrationFrom());
		body = body.replace(EmailConstants.REPLACE_PLACEHOLDER_NAME, registrationForm.getFirstName());
		membersList.add(registrationForm.getEmailAddress());
		String[] newUser = new String[1];
		newUser[0] = registrationForm.getEmailAddress();
		newUser[0] = registrationForm.getEmailAddress();
		email.MailSendingService(ADMIN_USERNAME, newUser, body, EmailConstants.REGISTRATOIN_1_EMAIL_SUBJECT);

		// Sending mail to committee members
		body = htmlTemplates.loadTemplate(emailTemplates.getNewUserNotifyToCommittee());
		body = body.replace(EmailConstants.REPLACE_PLACEHOLDER_NAME, registrationForm.getFirstName());
		List<AccessBean> committeList = accessBeanRepository.findByCommitee(true);
		List<String> emailOfCommittee = committeList.stream().map(object -> object.getEmail())
				.collect(Collectors.toList());
		String[] emailsForCommiteeArray = new String[emailOfCommittee.size()];
		for (int i = 0; i < emailOfCommittee.size(); i++) {
			emailsForCommiteeArray[i] = emailOfCommittee.get(i);
		}
		email.MailSendingService(ADMIN_USERNAME, emailsForCommiteeArray, body,
				EmailConstants.NEW_USER_REGISTERED_SUBJECT);

		registrationForm.setCreatedDate(LocalDateTime.now());
		RegistrationForm userDeatils = registrationFromRepository.save(registrationForm);
		ProgressBarReport progressBarReport = new ProgressBarReport();
		progressBarReport.setUserId(userDeatils.getUserId());
		progressBarReport.setRegistrationOneFormCompleted(RegistrationFormConstants.TRUE);
		progressBarRepository.save(progressBarReport);
		return registrationForm;
	}

	@Override
//	@Cacheable(value = "usercache", key = "'allUsers'")
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
		List<RegistrationForm> allUsersList = registrationFromRepository.findAll();
		allUsersList = allUsersList.stream().sorted((w1, w2) -> w2.getCreatedDate().compareTo(w1.getCreatedDate()))
				 	.collect(Collectors.toList());
		if (allUsersList.size() == 0) {
			return new ResponseEntity("No data present", HttpStatus.NOT_FOUND);
		} else if (accessList.contains(ApplicationConstants.ADMIN)) {
			return new ResponseEntity("Unauthorized access", HttpStatus.UNAUTHORIZED);
		} else {
			if (accessList.contains(ApplicationConstants.PRESIDENT)
					|| accessList.contains(ApplicationConstants.COMMITTEE_EXECUTIVE)) {
				allUsersList = allUsersList.stream().filter(p -> p.getCommitteeOneApproval() != null
						&& p.getCommitteeTwoApproval() != null && p.getCommitteeThreeApproval() != null)
						.collect(Collectors.toList());

				allUsersList = allUsersList
						.stream().filter(p -> !p.getCommitteeOneApproval().isEmpty()
								&& !p.getCommitteeTwoApproval().isEmpty() && !p.getCommitteeThreeApproval().isEmpty())
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
	public ProgressBarReport progressBarForAUser(String token) {
		JSONObject tokenObject = decodeToken(token);
		String userId = tokenObject.getString("userId");
		return progressBarRepository.findById(userId).get();
	}

	@Override
	public List<RegistrationForm> committeePresidentAccountantViewListOfApplicants(String token) {

		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("Token cannot be null or empty");
		}

		JSONObject jsonObject = decodeToken(token);
		if (!jsonObject.has("userId") || !jsonObject.has("access")) {
			throw new IllegalArgumentException("Token must contain 'id' and 'access' fields");
		}

		List accessList = jwtTokenUtil.getAccessList(token);

		if (accessList.contains(ApplicationConstants.COMMITEE) || accessList.contains(ApplicationConstants.PRESIDENT)
				|| accessList.contains(ApplicationConstants.COMMITTEE_EXECUTIVE)
				|| accessList.contains(ApplicationConstants.ADMIN)) {
			return registrationFromRepository.findAll();
		} else if (accessList.contains(ApplicationConstants.ACCOUNTANT)) {
			return registrationFromRepository.findAll().stream()
					.filter(registrationForm -> registrationForm.getPaymentInfo() != null).collect(Collectors.toList());
		} else {
			throw new IllegalArgumentException("Invalid user type");
		}

	}

	@Override
	public RegistrationForm userFillingRegistrationThreeForm(String token, RegistrationFrom2 registrationFrom2) {
		JSONObject tokenObject = decodeToken(token);
		String userId = tokenObject.getString("userId");
		RegistrationForm specificUserDetails = registrationFromRepository.findById(userId).get();
		if (specificUserDetails == null) {
			throw new IllegalArgumentException("No user found with the provided phone number");
		}

		Optional<ProgressBarReport> optionalProgressBarReport = progressBarRepository
				.findById(specificUserDetails.getUserId());
		if (!optionalProgressBarReport.isPresent()) {
			throw new IllegalArgumentException("No progress bar report found for the user");
		}

		BeanUtils.copyProperties(registrationFrom2, specificUserDetails);
		specificUserDetails.setLastModifiedDate(LocalDateTime.now());
		registrationFromRepository.save(specificUserDetails);

		ProgressBarReport progressBarReport = optionalProgressBarReport.get();
		progressBarReport.setRegistrationThreeFormCompleted(RegistrationFormConstants.TRUE);
		progressBarRepository.save(progressBarReport);

		return specificUserDetails;
	}

	@Override
	public List<RegistrationForm> accountFirstView() {
		return registrationFromRepository.findAll().stream().filter(p -> p.getPaymentInfo() != null)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity getIDOfUser(String token) throws MalformedURLException, UserNotFoundException {
		JSONObject tokenObject = decodeToken(token);
		String userId = tokenObject.getString("userId");
		RegistrationForm user = registrationFromRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND));
		IdentityCard card = new IdentityCard();
		card.setImage(user.getProfilePic());
		card.setMembershipId(user.getMembershipId());
		card.setNameofTheApplicant(user.getFirstName() + " " + user.getLastName());
		card.setTypeOfMemberShip(user.getPresidentChoosenMembershipForApplicant());

		return new ResponseEntity(card, HttpStatus.OK);
	}

	@Override
	public ResponseEntity uploadTranscationRecepit(String token, UploadPaymentReceipt uploadPaymentReceipt)
			throws IOException, MessagingException {
		JSONObject tokenObject = decodeToken(token);
		String userId = tokenObject.getString("userId");
		RegistrationForm user = registrationFromRepository.findById(userId).get();

		PaymentInfo paymentInfo = new PaymentInfo();
		paymentInfo.setTransactionDate(LocalDate.now());
		paymentInfo.setPaymentDetailDocument(uploadPaymentReceipt.getPaymentImageUrl());
		BeanUtils.copyProperties(uploadPaymentReceipt, paymentInfo);
		user.setPaymentInfo(paymentInfo);
		user.setLastModifiedDate(LocalDateTime.now());
		registrationFromRepository.save(user);

		ProgressBarReport progressBarReport = progressBarRepository.findById(userId).get();
		progressBarReport.setPayment(true);
		progressBarRepository.save(progressBarReport);
		String[] sendEmail = new String[1];
		sendEmail[0] = user.getEmailAddress();
		String body = htmlTemplates.loadTemplate(emailTemplates.getPaymentApprovalEmail());

		email.MailSendingService(ADMIN_USERNAME, sendEmail, body,
				EmailConstants.PAYMENT_RECIEVED_SUBJECT);

		return new ResponseEntity("Recepit Uploaded successfully", HttpStatus.OK);
	}

	@Override
	public ResponseEntity getPaymentReceipt(String userId) throws MalformedURLException {
		RegistrationForm user = registrationFromRepository.findById(userId).get();

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
		RegistrationForm specificUserDetails = registrationFromRepository.findById(userId).get();
		if (specificUserDetails == null) {
			return new ResponseEntity("No user found", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity(specificUserDetails, HttpStatus.OK);
		}
	}

	@Override
	public List<String> referenceOneDropdown() {

		List<RegistrationForm> trueMembers = registrationFromRepository.findByMemberTrue();

		List<String> memberNames = trueMembers.stream().filter(p->p.getFirstName()!=null)
				.map(p->{
					String concat= p.getFirstName();
					concat= concat + " "+p.getLastName();
					return concat;
				}).collect(Collectors.toList());
		return memberNames;

	}

	@Override
	@Cacheable("allmembers")
	public ResponseEntity getAllFilteredMembers(String categoryOfMember) {

		List<RegistrationForm> allUsers = registrationFromRepository.findAll();
		if (allUsers.size() == 0) {
			return new ResponseEntity("No data found", HttpStatus.NOT_FOUND);
		} else {
			switch (categoryOfMember) {

			case RegistrationFormConstants.TRUSTEE:
				System.out.println(categoryOfMember);
				allUsers = allUsers.stream().filter(p -> p.getPresidentChoosenMembershipForApplicant()
						.equalsIgnoreCase(RegistrationFormConstants.TRUSTEE)).collect(Collectors.toList());
				System.out.println("turstee::"+allUsers.size());
				break;

			case RegistrationFormConstants.PATRON:
				allUsers = allUsers.stream().filter(p -> p.getPresidentChoosenMembershipForApplicant()
						.equalsIgnoreCase(RegistrationFormConstants.PATRON)).collect(Collectors.toList());
				System.out.println("patron::"+allUsers.size());

				break;

			case RegistrationFormConstants.LIFE_MEMBER:
				allUsers = allUsers.stream().filter(p -> p.getPresidentChoosenMembershipForApplicant()
						.equalsIgnoreCase(RegistrationFormConstants.LIFE_MEMBER)).collect(Collectors.toList());
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

		List<RegistrationForm> allusers = registrationFromRepository.findAll();

		switch (emailUpload.getToEmail()) {
		case EmailConstants.TRUSTEE:

			List<RegistrationForm> filterByTrustee = allusers.stream()
			.filter(r -> EmailConstants.TRUSTEE.equalsIgnoreCase(r.getPresidentChoosenMembershipForApplicant()))
			.collect(Collectors.toList());
			List<String> trusteeMails = filterByTrustee.stream().map((trustee) -> {
				return trustee.getEmailAddress();
			}).collect(Collectors.toList());
			String[] trusteeEmails = trusteeMails.toArray(String[]::new);
			email.MailSendingService(emailUpload.getToEmail(), trusteeEmails, emailUpload.getBody(),
					emailUpload.getSubject());

			break;
		case EmailConstants.PATRON:

			List<RegistrationForm> filterByPatron = allusers.stream()
			.filter(r -> EmailConstants.PATRON.equalsIgnoreCase(r.getPresidentChoosenMembershipForApplicant()))
			.collect(Collectors.toList());

			List<String> patronMails = filterByPatron.stream().map((patron) -> {
				return patron.getEmailAddress();
			}).collect(Collectors.toList());
			String[] patronEmails = patronMails.toArray(String[]::new);
			email.MailSendingService(emailUpload.getToEmail(), patronEmails, emailUpload.getBody(),
					emailUpload.getSubject());

			break;
		case EmailConstants.LIFE_MEMBER:

			List<RegistrationForm> filterByLifeMember = allusers.stream().filter(
					r -> EmailConstants.LIFE_MEMBER.equalsIgnoreCase(r.getPresidentChoosenMembershipForApplicant()))
			.collect(Collectors.toList());

			List<String> lifeMemberMails = filterByLifeMember.stream().map((lifeMember) -> {
				return lifeMember.getEmailAddress();
			}).collect(Collectors.toList());
			String[] lifeMemberEmails = lifeMemberMails.toArray(String[]::new);
			email.MailSendingService(emailUpload.getToEmail(), lifeMemberEmails, emailUpload.getBody(),
					emailUpload.getSubject());

			break;
		default:
			allusers = allusers.stream().filter(p -> p.getPresidentChoosenMembershipForApplicant() != null)
			.collect(Collectors.toList());

			List<RegistrationForm> filterByAll = allusers.stream().filter(
					r -> EmailConstants.LIFE_MEMBER.equalsIgnoreCase(r.getPresidentChoosenMembershipForApplicant())
					|| EmailConstants.PATRON.equalsIgnoreCase(r.getPresidentChoosenMembershipForApplicant())
					|| EmailConstants.TRUSTEE.equalsIgnoreCase(r.getPresidentChoosenMembershipForApplicant()))
					.collect(Collectors.toList());
			System.out.println(filterByAll.size());
			List<String> allEmails = filterByAll.stream().map(RegistrationForm::getEmailAddress)
					.collect(Collectors.toList());

			String[] userEmails = allEmails.toArray(String[]::new);
			email.MailSendingService(emailUpload.getToEmail(), userEmails, emailUpload.getBody(),
					emailUpload.getSubject());

		}

		return null;
	}

	private boolean sendEmail(RegistrationForm specificUserDetails, ProgressBarReport progressBarReport)
			throws IOException, MessagingException {
		String[] user = new String[1];
		user[0] = specificUserDetails.getEmailAddress();

		if (progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
				&& specificUserDetails.getCommitteeOneApproval()!=null
				&& specificUserDetails.getCommitteeTwoApproval()!=null
				&& specificUserDetails.getCommitteeThreeApproval()!=null
				&& specificUserDetails.getCommitteeThreeApproval().equalsIgnoreCase(RegistrationFormConstants.APPROVAL)
				&& specificUserDetails.getCommitteeOneApproval().equalsIgnoreCase(RegistrationFormConstants.APPROVAL)
				&& specificUserDetails.getCommitteeTwoApproval().equalsIgnoreCase(RegistrationFormConstants.APPROVAL)
				) {
			progressBarReport.setCommitteeApproval(RegistrationFormConstants.TRUE);
			String body = null;
			// Sending credentials to the Applicant as Committee approved.
			String username = specificUserDetails.getEmailAddress();
			String passcode = generatingCredentials.generatePasscode(specificUserDetails.getCategory(),
					specificUserDetails.getPhoneNumber());
			body = htmlTemplates.loadTemplate(emailTemplates.getLoginCredentialsEmail());
			body = body.replace(EmailConstants.REPLACE_PLACEHOLDER_NAME, specificUserDetails.getFirstName()).replace("[UserName]", username).replace("[Password]", passcode);
			email.MailSendingService(ADMIN_USERNAME, user, body, EmailConstants.LOGIN_CREDENTIALS_SUBJECT);

			AccessBean accessBean = new AccessBean();
			accessBean.setAccessId(specificUserDetails.getUserId());
			accessBean.setAccountant(false);
			accessBean.setName(specificUserDetails.getFirstName() + " " + specificUserDetails.getLastName());
			accessBean.setUser(true);
			accessBean.setDeleted(false);
			accessBean.setEmail(username);
			accessBean.setPassword(passcode);
			accessBeanRepository.save(accessBean);
			return true;
		} else if (specificUserDetails.getCommitteeOneApproval()!=null
				&& specificUserDetails.getCommitteeTwoApproval()!=null
				&& specificUserDetails.getCommitteeThreeApproval()!=null
				&& specificUserDetails.getCommitteeOneApproval().equalsIgnoreCase(RegistrationFormConstants.REJECTED)
				&& specificUserDetails.getCommitteeTwoApproval().equalsIgnoreCase(RegistrationFormConstants.REJECTED)
				&& specificUserDetails.getCommitteeThreeApproval()
				.equalsIgnoreCase(RegistrationFormConstants.REJECTED)
				) {
			progressBarReport.setCommitteeApproval(RegistrationFormConstants.FALSE);
			String body = null;
			body = htmlTemplates.loadTemplate(emailTemplates.getCommitteeRejectEmail());
			email.MailSendingService(ADMIN_USERNAME, user, body, EmailConstants.COMMITTEE_REJECTED_SUBJECT);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public ResponseEntity updateUser(UserRequest user, String userId) {
		Optional<AccessBean> OptionalAccessBean = accessBeanRepository.findById(userId);
		if (OptionalAccessBean.isPresent()) {
			RegistrationForm registrationForm = new RegistrationForm();
			AccessBean accessBean= OptionalAccessBean.get();
			if(!user.getEmailAddress().isBlank())
			{
				accessBean.setEmail(user.getEmailAddress());	
				accessBeanRepository.save(accessBean);
			}
			BeanUtils.copyProperties(user, registrationForm);
			registrationForm.setUserId(userId);
			RegistrationForm registrationFormresponse = registrationFromRepository.save(registrationForm);
			return new ResponseEntity(registrationFormresponse, HttpStatus.OK);
		}
		return new ResponseEntity("User not found", HttpStatus.OK);
	}


	@Override
//	@CachePut(value = "usercache", key = "#userId")
//	@CacheEvict(value = "usercache", key = "'allUsers'")
	public ResponseEntity committeePresidentAccountantApproval(String token, String userId, ApprovalFrom from)
			throws Exception {

		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("Token cannot be null or empty");
		}

		JSONObject jsonObject = decodeToken(token);
		if (!jsonObject.has("userId") || !jsonObject.has("access")) {
			throw new IllegalArgumentException("Token must contain userId and access fields");
		}

		String organisationUsers = jsonObject.getString("userId");
		Optional<AccessBean> optionalAccessBeanUser = accessBeanRepository.findById(organisationUsers);
		if (!optionalAccessBeanUser.isPresent()) {
			return new ResponseEntity<>("No access details found for user", HttpStatus.NOT_FOUND);
		}
		AccessBean accessBeanUser = optionalAccessBeanUser.get();

		String identityNumber = jsonObject.getString("userId");
		List accessList = jwtTokenUtil.getAccessList(token);
		String userType = determineUserType(accessList);

		Optional<RegistrationForm> optionalSpecificUserDetails = registrationFromRepository.findById(userId);
		if (!optionalSpecificUserDetails.isPresent()) {
			return new ResponseEntity<>("No user found with the user id", HttpStatus.NOT_FOUND);
		}
		RegistrationForm specificUserDetails = optionalSpecificUserDetails.get();

		Optional<ProgressBarReport> optionalProgressBarReport = progressBarRepository.findById(specificUserDetails.getUserId());
		if (!optionalProgressBarReport.isPresent()) {
			return new ResponseEntity<>("No progress bar report found for the user", HttpStatus.NOT_FOUND);
		}

		String approvalStatus = getApprovalStatus(from.getStatusOfApproval());
		String[] user = new String[1];
		user[0] = specificUserDetails.getEmailAddress();
		ProgressBarReport progressBarReport = optionalProgressBarReport.get();
		switch (userType) {
		case ApplicationConstants.COMMITEE:
			return handleCommitteeApproval(accessBeanUser, specificUserDetails, progressBarReport, from, approvalStatus);
		case ApplicationConstants.PRESIDENT:
			return handlePresidentApproval(accessBeanUser, specificUserDetails, progressBarReport, from, approvalStatus,user);
		case ApplicationConstants.ACCOUNTANT:
			return handleAccountantApproval(accessBeanUser, specificUserDetails, progressBarReport, userId,user);
		default:
			return new ResponseEntity<>("You don't have access !!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String determineUserType(List<String> accessList) {
		if (accessList.contains(ApplicationConstants.PRESIDENT) || accessList.contains(ApplicationConstants.COMMITTEE_EXECUTIVE)) {
			return ApplicationConstants.PRESIDENT;
		} else if (accessList.contains(ApplicationConstants.COMMITEE)) {
			return ApplicationConstants.COMMITEE;
		} else {
			return ApplicationConstants.ACCOUNTANT;
		}
	}

	private String getApprovalStatus(String statusOfApproval) {
		switch (statusOfApproval) {
		case RegistrationFormConstants.APPROVAL:
			return "accepted";
		case RegistrationFormConstants.REJECTED:
			return "rejected";
		case RegistrationFormConstants.WAITING:
			return "waiting";
		default:
			return "";
		}
	}

	private ResponseEntity handleCommitteeApproval(AccessBean accessBeanUser, RegistrationForm specificUserDetails,
			ProgressBarReport progressBarReport, ApprovalFrom from, String approvalStatus) throws IOException, MessagingException {

		if (progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
				&& specificUserDetails.getCommitteeOneApproval() == null
				&& accessBeanUser.getAccessId().equalsIgnoreCase(RegistrationFormConstants.COMMITTEEONE)) {
			specificUserDetails.setCommitteeOneApproval(approvalStatus);
			specificUserDetails.setCommitteeOneChoosenMembershipForApplicant(from.getMembership());
			specificUserDetails.setCommitteeMemberOneId(accessBeanUser.getAccessId());
		}
		else if (progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
				&& specificUserDetails.getCommitteeTwoApproval() == null
				&& accessBeanUser.getAccessId().equalsIgnoreCase(RegistrationFormConstants.COMMITEETWO)) {
			specificUserDetails.setCommitteeTwoApproval(approvalStatus);
			specificUserDetails.setCommitteeTwoChoosenMembershipForApplicant(from.getMembership());
			specificUserDetails.setCommitteeMemberTwoId(accessBeanUser.getAccessId());
		}
		else if (progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
				&& specificUserDetails.getCommitteeThreeApproval() == null
				&& accessBeanUser.getAccessId().equalsIgnoreCase(RegistrationFormConstants.COMMITTEETHREE)) {
			specificUserDetails.setCommitteeThreeApproval(approvalStatus);
			specificUserDetails.setCommitteeThreeChoosenMembershipForApplicant(from.getMembership());
			specificUserDetails.setCommitteeMemberThreeId(accessBeanUser.getAccessId());
		}
		specificUserDetails.setStatus(from.getStatusOfApproval());
		specificUserDetails.setLastModifiedDate(LocalDateTime.now());
		registrationFromRepository.save(specificUserDetails);
		progressBarRepository.save(progressBarReport);
		sendEmail(specificUserDetails, progressBarReport);
		return new ResponseEntity<>("Status updated with userId"+specificUserDetails.getUserId(), HttpStatus.OK);
	}

	private ResponseEntity handlePresidentApproval(AccessBean accessBeanUser, RegistrationForm specificUserDetails,
			ProgressBarReport progressBarReport, ApprovalFrom from, String approvalStatus,String[] user) throws IOException, MessagingException {

		progressBarReport.setPresidentFillingRegistrationTwoForm(RegistrationFormConstants.TRUE);
		specificUserDetails.setPresidentRemarksForApplicant(from.getRemarks());
		specificUserDetails.setPresidentChoosenMembershipForApplicant(from.getMembership());
		specificUserDetails.setPresidentId(accessBeanUser.getAccessId());
		specificUserDetails.setStatus(from.getStatusOfApproval());
		specificUserDetails.setLastModifiedDate(LocalDateTime.now());
		if (progressBarReport.isCommitteeApproval() == RegistrationFormConstants.FALSE
				&& from.getStatusOfApproval().equalsIgnoreCase(RegistrationFormConstants.APPROVAL)) {

			progressBarReport.setPresidentApproval(RegistrationFormConstants.TRUE);
			specificUserDetails.setPresidentApproval(RegistrationFormConstants.APPROVAL);
			progressBarReport.setCommitteeApproval(true);
			String body = null;
			// Sending credentials to the Applicant as President as committee rejected
			String username = specificUserDetails.getEmailAddress();
			String passcode = generatingCredentials.generatePasscode(specificUserDetails.getCategory(),
					specificUserDetails.getPhoneNumber());
			body = htmlTemplates.loadTemplate(emailTemplates.getLoginCredentialsEmail());
			body = body.replace(EmailConstants.REPLACE_PLACEHOLDER_NAME, specificUserDetails.getFirstName()).replace("[UserName]", username).replace("[Password]", passcode);

			email.MailSendingService(ADMIN_USERNAME, user, body,
					EmailConstants.LOGIN_CREDENTIALS_SUBJECT);

			AccessBean accessBean = new AccessBean();
			accessBean.setAccessId(specificUserDetails.getUserId());
			accessBean.setAccountant(false);
			accessBean.setName(specificUserDetails.getFirstName() + " " + specificUserDetails.getLastName());
			accessBean.setUser(true);
			accessBean.setDeleted(false);
			accessBean.setEmail(username);
			accessBean.setPassword(passcode);
			accessBeanRepository.save(accessBean);
		}
		else if (specificUserDetails != null && progressBarReport != null
				&& progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
				&& progressBarReport.isCommitteeApproval() == RegistrationFormConstants.TRUE
				&& from.getStatusOfApproval().equalsIgnoreCase(RegistrationFormConstants.APPROVAL)) {
			String body = null;
			progressBarReport.setPresidentApproval(RegistrationFormConstants.TRUE);
			specificUserDetails.setPresidentApproval(RegistrationFormConstants.APPROVAL);

			body = htmlTemplates.loadTemplate(emailTemplates.getPresidentApprovalEmail());
			email.MailSendingService(ADMIN_USERNAME, user, body,
					EmailConstants.PRESIDENT_APPROVED_SUBJECT);

		} else if (from.getStatusOfApproval().equalsIgnoreCase(RegistrationFormConstants.REJECTED)) {
			progressBarReport.setPresidentFillingRegistrationTwoForm(RegistrationFormConstants.TRUE);
			progressBarReport.setPresidentApproval(RegistrationFormConstants.FALSE);
			String body = null;
			// rejection mail from president
			specificUserDetails.setPresidentApproval(RegistrationFormConstants.REJECTED);
			body = htmlTemplates.loadTemplate(emailTemplates.getPresidentRejectionEmail());
			email.MailSendingService(ADMIN_USERNAME, user, body,
					EmailConstants.PRESIDENT_REJECTED_SUBJECT);
		} else {
			progressBarReport.setPresidentApproval(RegistrationFormConstants.FALSE);
			specificUserDetails.setPresidentApproval(RegistrationFormConstants.WAITING);
			// waiting mail from president
		}
		registrationFromRepository.save(specificUserDetails);
		progressBarRepository.save(progressBarReport);
		return new ResponseEntity<>("Status updated", HttpStatus.OK);
	}

	private ResponseEntity handleAccountantApproval(AccessBean accessBeanUser, RegistrationForm specificUserDetails,
			ProgressBarReport progressBarReport, String userId,String[] user) throws IOException, MessagingException {
		if (progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
				&& progressBarReport.isPayment() && progressBarReport.isPresidentApproval()
				&& progressBarReport.isRegistrationThreeFormCompleted() == RegistrationFormConstants.TRUE) {

			progressBarReport.setAccountantAcknowledgement(RegistrationFormConstants.TRUE);
			progressBarReport.setMember(RegistrationFormConstants.TRUE);
			String memberIdentityNumber = generatingCredentials.generateMemberId(userId);
			// send congratulations mail with generated memberID
			String body = null;
			body = htmlTemplates.loadTemplate(emailTemplates.getMembershipApproved());
			body = body.replace(EmailConstants.REPLACE_MEMEBER_ID, memberIdentityNumber);
			email.MailSendingService(ADMIN_USERNAME, user, body,
					EmailConstants.MEMBERSHIP_APPROVED);
			specificUserDetails.setMembershipId(memberIdentityNumber);
			specificUserDetails.setMember(RegistrationFormConstants.TRUE);
			specificUserDetails.setLastModifiedDate(LocalDateTime.now());
			registrationFromRepository.save(specificUserDetails);
			progressBarRepository.save(progressBarReport);

		} else {
			return new ResponseEntity("All conditions for accountant approval are not met",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>("Status updated", HttpStatus.OK);
	}

}
