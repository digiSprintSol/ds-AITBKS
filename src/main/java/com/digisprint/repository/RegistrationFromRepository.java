package com.digisprint.repository;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import com.digisprint.bean.RegistrationFrom;

@Repository
public interface RegistrationFromRepository extends MongoRepository<RegistrationFrom, String> {

	RegistrationFrom findByPhoneNumber(String phoneNumber);

	Optional<RegistrationFrom> findByEmailAddress(String emailAddress);
	
	List<RegistrationFrom> findByUserIdIn(List<String> userIds);

	
}
