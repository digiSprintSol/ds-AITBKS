package com.digisprint.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.digisprint.bean.Image;

import java.util.UUID;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {
}
