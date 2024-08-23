package com.digisprint.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.digisprint.bean.Donation;

@Repository
public interface DonationRepository extends MongoRepository<Donation, String> {

}
