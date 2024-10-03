package com.digisprint.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.bean.Image;
import com.digisprint.repository.ImageRepository;
import com.digisprint.repository.MarketPlaceRepository;
import com.digisprint.service.ImageService;

@Service
public class ImageServiceImpl implements ImageService {

	@Autowired
	private GCPServiceImpl gcpServiceImpl;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private MarketPlaceRepository marketPlaceRepository;

	@Override
	public ResponseEntity<Map> uploadImage(MultipartFile file, String folderName, String folderPath) {

		try {
			if (file.getOriginalFilename().isEmpty()) {
				return ResponseEntity.badRequest().build();
			}
			if (file.isEmpty()) {
				return ResponseEntity.badRequest().build();
			}

			Image image = new Image();

			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedDateTime = now.format(formatter);

			boolean imageExists = imageRepository
					.existsByNameAndFolderName(file.getOriginalFilename() + "_" + formattedDateTime, folderName);
			if (imageExists) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(Map.of("error", "An image with the same name already exists in this folder."));
			}

			image.setName(file.getOriginalFilename() + "_" + formattedDateTime);
			image.setUrl(gcpServiceImpl.uploadFile(file, folderPath));
			image.setFolderName(folderName);
			image.setFolderPath(folderPath);
			image.setCreatedDate(LocalDate.now());
			if (image.getUrl() == null) {
				return ResponseEntity.badRequest().build();
			}
			imageRepository.save(image);
			return ResponseEntity.ok().body(Map.of("url", image.getUrl()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public ResponseEntity<?> deleteImage(String id, String filepath) {
		try {
			Optional<Image> imageOptional = imageRepository.findById(id);

			if (imageOptional.isPresent()) {
				imageRepository.deleteById(id);
				Boolean isDeleted = gcpServiceImpl.deleteFile(filepath);
				if (isDeleted) {
					return ResponseEntity.ok("Image with id " + id + " has been successfully deleted.");
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found.");
				}
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image with id " + id + " not found.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while trying to delete the image.");
		}
	}

	@Override
	public ResponseEntity<Map> uploadImages(List<MultipartFile> files, String folderName, String folderPath) {
		try {
			if (files.isEmpty()) {
				return ResponseEntity.badRequest().body(Map.of("error", "No files uploaded."));
			}

			List<Map<String, String>> uploadedImages = new ArrayList<>();

			for (MultipartFile file : files) {
				if (file.getOriginalFilename().isEmpty() || file.isEmpty()) {
					return ResponseEntity.badRequest()
							.body(Map.of("error", "One or more files are empty or have no name."));
				}

				LocalDateTime now = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String formattedDateTime = now.format(formatter);
				String imageName = file.getOriginalFilename() + "_" + formattedDateTime;

				boolean imageExists = imageRepository.existsByNameAndFolderName(imageName, folderName);
				if (imageExists) {
					uploadedImages.add(Map.of("file", file.getOriginalFilename(), "status", "conflict", "message",
							"An image with the same name already exists."));
					continue;
				}

				String imageUrl = gcpServiceImpl.uploadFile(file, folderPath);

				if (imageUrl == null) {
					uploadedImages.add(Map.of("file", file.getOriginalFilename(), "status", "failed", "message",
							"Failed to upload to Cloud."));
					continue;
				}

				Image image = new Image();
				image.setName(imageName);
				image.setUrl(imageUrl);
				image.setFolderName(folderName);
				image.setFolderPath(folderPath);
				image.setCreatedDate(LocalDate.now());
				imageRepository.save(image);

				uploadedImages.add(Map.of("file", file.getOriginalFilename(), "status", "success", "url", imageUrl));
			}

			return ResponseEntity.ok().body(Map.of("uploadedImages", uploadedImages));

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "An error occurred while uploading images."));
		}

	}
}
