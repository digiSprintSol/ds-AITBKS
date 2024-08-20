package com.digisprint.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.bean.Image;
import com.digisprint.bean.ImageModel;
import com.digisprint.repository.ImageRepository;
import com.digisprint.repository.MarketPlaceRepository;
import com.digisprint.service.CloudinaryService;
import com.digisprint.service.ImageService;

import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private CloudinaryService cloudinaryService;
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private MarketPlaceRepository marketPlaceRepository;


    @Override
    public ResponseEntity<Map> uploadImage(String name, MultipartFile file, String folderName) {
        try {
            if (name.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            Image image = new Image();
            image.setName(name+"");
            image.setUrl(cloudinaryService.uploadFile(file, folderName));
            if(image.getUrl() == null) {
                return ResponseEntity.badRequest().build();
            }
            imageRepository.save(image);
            return ResponseEntity.ok().body(Map.of("url", image.getUrl()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
}
