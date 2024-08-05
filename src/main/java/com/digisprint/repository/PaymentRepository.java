package com.digisprint.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.digisprint.bean.PaymentInfo;

@Repository
public interface PaymentRepository extends MongoRepository<PaymentInfo, String> {

}
