package com.digisprint.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.digisprint.bean.Donation;

@Repository
public interface DonationRepository extends MongoRepository<Donation, String> {

	List<Donation> findByAcknowledgeTrue();
}
