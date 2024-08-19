package com.digisprint.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.digisprint.bean.Feedbacks;

@Repository
public interface FeedbackRepository extends MongoRepository<Feedbacks, String>{

}
