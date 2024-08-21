package com.digisprint.service;

import com.digisprint.bean.ImageModel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ImageService {

    public ResponseEntity<Map> uploadImage(MultipartFile file, String folderName);
    
    public ResponseEntity deleteImage(String id);
    
    public ResponseEntity<Map> uploadImages(List<MultipartFile> files, String folderName);
    
}
