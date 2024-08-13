package com.digisprint.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.digisprint.bean.MarketPlaces;

@Repository
public interface MarketPlaceRepository extends MongoRepository<MarketPlaces, String> {

}
