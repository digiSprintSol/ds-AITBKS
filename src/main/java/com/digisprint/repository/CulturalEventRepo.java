package com.digisprint.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.digisprint.bean.CulturalEvents;

public interface CulturalEventRepo extends MongoRepository<CulturalEvents,String> {

	List<CulturalEvents> findByAnnouncement(boolean b);

	List<CulturalEvents> findByImageURLsIn(List<String> imageUrl);
//	List<CulturalEvents> findByGalleryTrue();
	
//	List<CulturalEvents> findByAwardsTrue();

}
