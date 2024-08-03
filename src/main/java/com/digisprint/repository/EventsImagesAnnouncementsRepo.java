package com.digisprint.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.digisprint.bean.EventsImagesAnnouncements;

public interface EventsImagesAnnouncementsRepo extends MongoRepository<EventsImagesAnnouncements,String> {

	List<EventsImagesAnnouncements> findByAnnouncement(boolean b);

}
