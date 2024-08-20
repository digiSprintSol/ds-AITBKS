package com.digisprint.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.repository.ImageRepository;
import com.digisprint.service.ImageService;

@RestController
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;

    @PostMapping(value="/upload",consumes = { "multipart/form-data" })
    public ResponseEntity<Map> upload(@RequestParam(name="name",required =false) String name,
    		@RequestParam(name="file",required =false) MultipartFile file,
    		@RequestParam(name="folderName",required =false) String folderName) {
        try {
           return imageService.uploadImage(name, file, folderName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
