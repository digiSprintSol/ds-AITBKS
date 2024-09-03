package com.digisprint.repository;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import com.digisprint.bean.RegistrationForm;

@Repository
public interface RegistrationFromRepository extends MongoRepository<RegistrationForm, String> {

	RegistrationForm findByPhoneNumber(String phoneNumber);

	Optional<RegistrationForm> findByEmailAddress(String emailAddress);
	
	List<RegistrationForm> findByUserIdIn(List<String> userIds);

	RegistrationForm findTopByMembershipIdStartingWithOrderByMembershipIdDesc(String prefix);

	
}
