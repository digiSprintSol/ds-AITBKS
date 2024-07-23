package com.digisprint.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import com.digisprint.bean.RegistrationFrom;


public interface RegistrationFromRepository extends MongoRepository<RegistrationFrom, String> {

	RegistrationFrom findByPhoneNumber(String phoneNumber);
	
}
