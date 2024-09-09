package com.digisprint.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.digisprint.bean.ScholarShip;

@Repository
public interface ScholarShipRepository extends MongoRepository<ScholarShip, String>{

}
