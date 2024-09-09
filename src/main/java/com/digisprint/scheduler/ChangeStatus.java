package com.digisprint.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.digisprint.repository.RegistrationFromRepository;

@Component
public class ChangeStatus {

	@Autowired
	RegistrationFromRepository registrationFromRepository;

	@Scheduled(cron = "0 0 0 * * *")
	public void changeStatusOfUser() {
		registrationFromRepository.findAll().stream().filter(user -> user.getCreatedDate().atZone(ZoneId.of("UTC"))
				.toLocalDate().compareTo(LocalDate.now().minusDays(15)) < 0).forEach(user -> {
					if (user.getStatus() != "overdue") {
						if (user.getCommitteeOneApproval().isBlank() || user.getCommitteeOneApproval() == null) {
							user.setCommitteeOneApproval("overdue");
						}
						if (user.getCommitteeTwoApproval().isBlank() || user.getCommitteeTwoApproval() == null) {
							user.setCommitteeTwoApproval("overdue");
						}
						if (user.getCommitteeThreeApproval().isBlank() || user.getCommitteeThreeApproval() == null) {
							user.setCommitteeThreeApproval("overdue");
						}
						if (user.getCommitteeOneApproval().equals("overdue")
								&& user.getCommitteeTwoApproval().equals("overdue")
								&& user.getCommitteeThreeApproval().equals("overdue")) {
							user.setStatus("overdue");
						}
					}
					registrationFromRepository.save(user);
				});

	}

}
