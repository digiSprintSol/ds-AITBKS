package com.digisprint.service;

import com.digisprint.bean.ImageModel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImageService {

    public ResponseEntity<Map> uploadImage(String name, MultipartFile file, String folderName);
}
