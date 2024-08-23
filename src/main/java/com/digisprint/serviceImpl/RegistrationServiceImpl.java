package com.digisprint.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.bson.json.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.EmailUtils.EmailService;
import com.digisprint.EmailUtils.EmailTemplates;
import com.digisprint.EmailUtils.LoadHtmlTemplates;
import com.digisprint.bean.AccessBean;
import com.digisprint.bean.EventsImagesAnnouncements;
import com.digisprint.bean.PaymentInfo;
import com.digisprint.bean.ProgressBarReport;
import com.digisprint.bean.RegistrationFrom;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.filter.JwtTokenUtil;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.repository.EventsImagesAnnouncementsRepo;
import com.digisprint.repository.PaymentRepository;
import com.digisprint.repository.ProgressBarRepository;
import com.digisprint.repository.RegistrationFromRepository;
import com.digisprint.requestBean.ApprovalFrom;
import com.digisprint.requestBean.RegistrationFrom2;
import com.digisprint.responseBody.GetDocumentURL;
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
public class RegistrationServiceImpl  implements RegistrationService{

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
	public RegistrationFrom registerUser(RegistrationFrom registrationForm, String imageUrl) throws IOException, MessagingException {

		Optional<RegistrationFrom> existingUser = registrationFromRepository.findByEmailAddress(registrationForm.getEmailAddress());
		if (existingUser.isPresent()) {
			throw new IllegalArgumentException("The entered email id already exists. Please enter another email id.");
		}

		//Sending mail to user.
		List<String> membersList = new ArrayList<>();
		String body = htmlTemplates.loadTemplate(emailTemplates.getWelcomeMailAfterFillingFirstRegistrationFrom());
		body = body.replaceAll(EmailConstants.REPLACE_PLACEHOLDER_NAME, registrationForm.getFullName());
		membersList.add(registrationForm.getEmailAddress());
		String[] newUser= new String[1];
		newUser[0] = registrationForm.getEmailAddress();
		email.MailSendingService(ADMIN_USERNAME, newUser, body, EmailConstants.REGISTRATOIN_1_EMAIL_SUBJECT);
		//Sending mail to committee members
		body = htmlTemplates.loadTemplate(emailTemplates.getNewUserNotifyToCommittee());

		List<AccessBean> committeList= accessBeanRepository.findByCommitee(true);
		List<String> emailOfCommittee= committeList.stream().map(object->object.getEmail()).collect(Collectors.toList());
		String[] emailsForCommiteeArray = new String[emailOfCommittee.size()];
		for(int i = 0 ; i<emailOfCommittee.size();i++) {
			emailsForCommiteeArray[i] = emailOfCommittee.get(i);
		}
		email.MailSendingService(ADMIN_USERNAME, emailsForCommiteeArray, body, EmailConstants.NEW_USER_REGISTERED_SUBJECT);

		registrationForm.setApplicantChoosenMembership(registrationForm.getCategoryOfMembership());
		registrationForm.setCreatedDate(LocalDateTime.now());
		registrationForm.setProfilePic(imageUrl);
		RegistrationFrom userDeatils = registrationFromRepository.save(registrationForm);
		ProgressBarReport progressBarReport = new ProgressBarReport();
		progressBarReport.setUserId(userDeatils.getUserId());
		progressBarReport.setRegistrationOneFormCompleted(RegistrationFormConstants.TRUE);
		progressBarRepository.save(progressBarReport);
		return registrationForm;
	}

	@Override
	public Page<RegistrationFrom> getAllRegisteredUsers(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return registrationFromRepository.findAll(pageable); 
	}

	public JSONObject decodeToken(String jwtToken) {
		return JwtTokenUtil.decodeUserToken(jwtToken);
	}

	@Override
	public ResponseEntity committeePresidentAccountantApproval(String token, String userId,ApprovalFrom from  ) throws Exception {

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

		if(accessList.contains(ApplicationConstants.PRESIDENT)) {
			userType = ApplicationConstants.PRESIDENT;
		}

		else if(accessList.contains(ApplicationConstants.COMMITEE)) {
			userType = ApplicationConstants.COMMITEE;	
		}
		else {
			userType = ApplicationConstants.ACCOUNTANT;
		}

		RegistrationFrom specificUserDetails = registrationFromRepository.findById(userId).get();
		if (specificUserDetails == null) {
			return new ResponseEntity("No user found with the user id",HttpStatus.NOT_FOUND);
		}

		Optional<ProgressBarReport> optionalProgressBarReport = progressBarRepository.findById(specificUserDetails.getUserId());
		if (!optionalProgressBarReport.isPresent()) {
			return new ResponseEntity("No progress bar report found for the user",HttpStatus.NOT_FOUND);
		}
		String[] user= new String[1];
		user[0] = specificUserDetails.getEmailAddress();
		ProgressBarReport progressBarReport = optionalProgressBarReport.get();

		if (userType.equalsIgnoreCase(ApplicationConstants.COMMITEE)) {
			if (progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
					&& from.getStatusOfApproval().equalsIgnoreCase(RegistrationFormConstants.APPROVAL) ) {
				specificUserDetails.setCommitteeChoosenMembershipForApplicant(from.getMembership());
				specificUserDetails.setCommitteeRemarksForApplicant(from.getRemarks());
				String body = null;
				// Sending credentials to the Applicant as Committee approved.
				String username = specificUserDetails.getEmailAddress();
				String passcode = generatingCredentials.generatePasscode(specificUserDetails.getCategory(), specificUserDetails.getPhoneNumber());
				body = htmlTemplates.loadTemplate(emailTemplates.getLoginCredentialsEmail());
				body = body.replace("[UserName]", username)
						.replace("[Password]", passcode);

				email.MailSendingService(ADMIN_USERNAME,user , body, EmailConstants.LOGIN_CREDENTIALS_SUBJECT);
				progressBarReport.setCommitteeApproval(true);

				AccessBean accessBean = new AccessBean();
				accessBean.setAccessId(specificUserDetails.getUserId());
				accessBean.setAccountant(false);
				accessBean.setUser(true);
				accessBean.setDeleted(false);
				accessBean.setEmail(username);
				accessBean.setPassword(passcode);
				accessBeanRepository.save(accessBean);

			} else if (from.getStatusOfApproval().equalsIgnoreCase(RegistrationFormConstants.REJECTED)) {
				progressBarReport.setCommitteeApproval(false);
				String body = null;
				body = htmlTemplates.loadTemplate(emailTemplates.getCommitteeRejectEmail());

				email.MailSendingService(ADMIN_USERNAME, user, body, EmailConstants.COMMITTEE_REJECTED_SUBJECT);
				progressBarReport.setCommitteeApproval(false);

			} else {
				progressBarReport.setCommitteeApproval(false);

				// waiting email
			}
		} else if (userType.equalsIgnoreCase(ApplicationConstants.PRESIDENT)) {
			if (specificUserDetails != null && progressBarReport != null 
					&& progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE 
					&& from.getStatusOfApproval().equalsIgnoreCase(RegistrationFormConstants.APPROVAL) ) {
				String body = null;
				progressBarReport.setPresidentApproval(RegistrationFormConstants.TRUE);
				progressBarReport.setPresidentFillingRegistrationTwoForm(RegistrationFormConstants.TRUE);
				specificUserDetails.setPresidentRemarksForApplicant(from.getRemarks());
				specificUserDetails.setPresidentChoosenMembershipForApplicant(from.getMembership());
				body = htmlTemplates.loadTemplate(emailTemplates.getPresidentApprovalEmail());
				email.MailSendingService(ADMIN_USERNAME, user, body, EmailConstants.PRESIDENT_APPROVED_SUBJECT);

			} else if (from.getStatusOfApproval().equalsIgnoreCase(RegistrationFormConstants.REJECTED)) {
				progressBarReport.setPresidentFillingRegistrationTwoForm(RegistrationFormConstants.TRUE);
				progressBarReport.setPresidentApproval(RegistrationFormConstants.FALSE);
				String body = null;
				// rejection mail from president
				body = htmlTemplates.loadTemplate(emailTemplates.getPresidentRejectionEmail());
				email.MailSendingService(ADMIN_USERNAME, user, body, EmailConstants.PRESIDENT_REJECTED_SUBJECT);
			} else {
				progressBarReport.setPresidentApproval(RegistrationFormConstants.FALSE);
				// waiting mail from president
			}

		} else if (userType.equalsIgnoreCase(ApplicationConstants.ACCOUNTANT)) {
			if (progressBarReport.isCommitteeApproval() && progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
					&& progressBarReport.isPayment() && progressBarReport.isPresidentApproval()
					&& progressBarReport.isRegistrationThreeFormCompleted() == RegistrationFormConstants.TRUE) {

				progressBarReport.setAccountantAcknowledgement(RegistrationFormConstants.TRUE);
				progressBarReport.setMember(RegistrationFormConstants.TRUE);
				String memberIdentityNumber = generatingCredentials.generateMemberId(userId);
				// send congratulations mail with generated memberID 
				String body = null;
				body = htmlTemplates.loadTemplate(emailTemplates.getMembershipApproved());
				email.MailSendingService(ADMIN_USERNAME, user, body, EmailConstants.MEMBERSHIP_APPROVED);

			} else {
				return new ResponseEntity("All conditions for accountant approval are not met",HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} else {
			return new ResponseEntity("You don't have access !!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		specificUserDetails.setStatus(from.getStatusOfApproval());
		specificUserDetails.setLastModifiedDate(LocalDateTime.now());
		registrationFromRepository.save(specificUserDetails);
		progressBarRepository.save(progressBarReport);
		return new ResponseEntity("Status updated",HttpStatus.OK);

	}

	@Override
	public ProgressBarReport progressBarForAUser(String token) {
		JSONObject tokenObject = decodeToken(token);
		String userId= tokenObject.getString("userId");
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

		if(accessList.contains(ApplicationConstants.COMMITEE) || accessList.contains(ApplicationConstants.PRESIDENT)) {
			return registrationFromRepository.findAll();
		}else if(accessList.contains(ApplicationConstants.ACCOUNTANT)) {
			return registrationFromRepository.findAll().stream()
					.filter(registrationForm -> registrationForm.getPaymentInfo() != null)
					.collect(Collectors.toList());
		}else {
			throw new IllegalArgumentException("Invalid user type");
		}

	}

	@Override
	public RegistrationFrom userFillingRegistrationThreeForm(String token, RegistrationFrom2 registrationFrom2 ) {
		JSONObject tokenObject = decodeToken(token);
		String userId= tokenObject.getString("userId");
		RegistrationFrom specificUserDetails = registrationFromRepository.findById(userId).get();
		if (specificUserDetails == null) {
			throw new IllegalArgumentException("No user found with the provided phone number");
		}

		Optional<ProgressBarReport> optionalProgressBarReport = progressBarRepository.findById(specificUserDetails.getUserId());
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
	public List<RegistrationFrom> accountFirstView(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return registrationFromRepository.findAll(pageable).stream()
				.filter(p->p.getPaymentInfo()!=null)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity getIDOfUser(String userId) throws MalformedURLException {

		RegistrationFrom user= registrationFromRepository.findById(userId).get();
		IdentityCard card = new IdentityCard();
		
		card.setImage(user.getProfilePic());
		card.setMembershipId(user.getMembershipId());
		card.setNameofTheApplicant(user.getFullName());
		card.setTypeOfMemberShip(user.getPresidentChoosenMembershipForApplicant());

		return new ResponseEntity(card,HttpStatus.OK);
	}

	@Override
	public ResponseEntity uploadTranscationRecepit(String token, String imageUrl,String transcationId) throws IOException, MessagingException {
		JSONObject tokenObject = decodeToken(token);
		String userId= tokenObject.getString("userId");
		RegistrationFrom user = registrationFromRepository.findById(userId).get();

		PaymentInfo paymentInfo= new PaymentInfo();
		paymentInfo.setTransactionDate(LocalDate.now());
		paymentInfo.setPaymentDetailDocument(imageUrl);
		paymentInfo.setTrasactionId(transcationId);
		user.setPaymentInfo(paymentInfo);
		registrationFromRepository.save(user);

		ProgressBarReport progressBarReport = progressBarRepository.findById(userId).get();
		progressBarReport.setPayment(true);
		progressBarRepository.save(progressBarReport);
		String[] sendEmail= new String[1];
		sendEmail[0] = user.getEmailAddress();
		String body = htmlTemplates.loadTemplate(emailTemplates.getPaymentApprovalEmail());

		email.MailSendingService(ADMIN_USERNAME, sendEmail,body ,EmailConstants.PAYMENT_RECIEVED_SUBJECT );

		return new ResponseEntity("Recepit Uploaded successfully",HttpStatus.OK);
	}

	@Override
	public ResponseEntity getPaymentReceipt(String userId) throws MalformedURLException {
		RegistrationFrom user = registrationFromRepository.findById(userId).get();

		if(user!=null) {

			return new ResponseEntity(user.getPaymentInfo().getPaymentDetailDocument(),HttpStatus.OK);
		}
		else {
			return new ResponseEntity("No recepit found with the user",HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity getUserDetails(String token) {
		JSONObject tokenObject = decodeToken(token);
		String userId= tokenObject.getString("userId");
		RegistrationFrom specificUserDetails = registrationFromRepository.findById(userId).get();
		if (specificUserDetails == null) {
			return new ResponseEntity("No user found",HttpStatus.NOT_FOUND);
		}
		else {
			return new ResponseEntity(specificUserDetails,HttpStatus.OK);
		}
	}

	@Override
	public List<String> referenceOneDropdown() {

        List<ProgressBarReport> trueMembers = progressBarRepository.findByMemberTrue();

        List<String> userIds = trueMembers.stream()
                                          .map(ProgressBarReport::getUserId)
                                          .collect(Collectors.toList());

        List<RegistrationFrom> registrationForms = new ArrayList<>();
        for (String userId : userIds) {
            Optional<RegistrationFrom> optionalForm = registrationFromRepository.findById(userId);
            if (optionalForm.isPresent()) {
                registrationForms.add(optionalForm.get());
            } else {
                System.out.println("No RegistrationForm found for User ID: " + userId);
            }
        }

        List<String> memberNames = registrationForms.stream()
                                                    .map(RegistrationFrom::getFullName)
                                                    .collect(Collectors.toList());

        return memberNames;

	}

}
