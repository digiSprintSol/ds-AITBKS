package com.digisprint.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.digisprint.bean.ProgressBarReport;

public interface ProgressBarRepository extends MongoRepository<ProgressBarReport, String> {

}
