package com.digisprint.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.repository.ImageRepository;
import com.digisprint.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;

    @PostMapping(value="/upload",consumes = { "multipart/form-data" })
    public ResponseEntity<Map> upload(@RequestParam(name="file",required =false) MultipartFile file,
    		@RequestParam(name="folderName",required =false) String folderName,
    		@RequestParam(name="folderPath",required =false) String folderPath) {
        try {
           return imageService.uploadImage(file, folderName,folderPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @DeleteMapping(value="/deleteImage/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable("id") String id) {
    	return imageService.deleteImage(id);
    }
    
    @Operation(summary = "Upload multiple images", description = "Upload multiple images to Cloudinary and save metadata in the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Images uploaded successfully", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", 
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(mediaType = "application/json"))
    })
    @PostMapping(value="/uploadMultipleImages",consumes = { "multipart/form-data" })
    public ResponseEntity<Map> uploadMultipleImages(
        @RequestParam("files") List<MultipartFile> files,
        @RequestParam("folderName") String folderName){    
        return imageService.uploadImages(files, folderName);
    }
    
}
