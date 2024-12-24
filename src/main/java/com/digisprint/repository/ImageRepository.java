package com.digisprint.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.digisprint.bean.Image;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {

	boolean existsByNameAndFolderName(String string, String folderName);

	Image findByUrl(String imageUrl);
	
	List<Image> findByFolderPath(String folderPath);

	void deleteByFolderPath(String folderPath);
}
