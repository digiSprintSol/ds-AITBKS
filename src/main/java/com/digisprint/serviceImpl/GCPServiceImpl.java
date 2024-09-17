package com.digisprint.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class GCPServiceImpl {

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucketName;

	@Value("${spring.cloud.gcp.project-id}")
	private String projectId;

	public Storage getCloudStorageService() throws IOException {
		Storage storage = StorageOptions.newBuilder().setProjectId(projectId)
				.setCredentials(ServiceAccountCredentials.fromStream(Files
						.newInputStream(Paths.get("D:/Community-Portal/cloudstoragejson/aitbks-ad514bb0822d.json"))))
				.build().getService();
		return storage;
	}

	public String uploadFile(MultipartFile culturalEventImage, String folderpath) throws IOException {
		Storage storage = getCloudStorageService();

		String fileName = null;
		fileName = "culturalevents" + "/" + folderpath + "/" + culturalEventImage.getOriginalFilename();
		BlobId blobId = BlobId.of(bucketName, fileName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(culturalEventImage.getContentType())
				.setAcl(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))).build();
		Blob blob = storage.create(blobInfo, culturalEventImage.getBytes());
		return "https://storage.googleapis.com/aitbksimages" + "/" + fileName;
	}

	public Boolean deleteFile(String filepath) {

		Boolean deleted = false;
		try {
			Storage storage = getCloudStorageService();
			BlobId blobid = BlobId.of(bucketName, filepath);
			deleted = storage.delete(blobid);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return deleted;
	}

//	public String uploadImages(List<MultipartFile> culturalEventImages, String folderName) throws IOException {
//		Storage storage = getCloudStorageService();
//
//		String fileName = null;
//
//		for (MultipartFile cutularEventImage : culturalEventImages) {
//			fileName = folderName + "/" + cutularEventImage.getOriginalFilename();
//			BlobId blobId = BlobId.of(bucketName, fileName);
//			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jfif")
//					.setAcl(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))).build();
//			storage.create(blobInfo, cutularEventImage.getBytes());
//		}
//		return "Files uploaded successfully";
//	}
}
