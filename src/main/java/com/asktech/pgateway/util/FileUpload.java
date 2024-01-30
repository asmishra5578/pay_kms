package com.asktech.pgateway.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import com.asktech.pgateway.enums.FormValidationExceptionEnums;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import com.asktech.pgateway.exception.ExcelFileNotFoundException;
import com.asktech.pgateway.exception.FileStorageException;

@Component
public class FileUpload {
	static Logger logger = LoggerFactory.getLogger(FileUpload.class);

	@Value("${file.uploaddir}")
	String fileStorage;
	
	Path fileStorageLocation;
	


	public String storeFile(MultipartFile file) throws NoSuchAlgorithmException {
		// Normalize file name
		this.fileStorageLocation = Paths.get(fileStorage).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not store file.",
					FormValidationExceptionEnums.FILE_STORAGE_EXCEPTION	);
		}
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		logger.info("FILENAME::" + fileName);
		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Invalid Sequence",
						FormValidationExceptionEnums.FILE_STORAGE_EXCEPTION	);
			}
			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			//Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			String checksum = ChecksumUtils.getFileChecksum(targetLocation.toFile());
			return fileName+"|"+checksum;
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", 
					FormValidationExceptionEnums.FILE_STORAGE_EXCEPTION);
		}
	}
	
	public String storeFile(MultipartFile file, String storeName) throws NoSuchAlgorithmException {
		// Normalize file name
		this.fileStorageLocation = Paths.get(fileStorage).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not store file.",
					FormValidationExceptionEnums.FILE_STORAGE_EXCEPTION	);
		}
		
		String fileName = storeName;
		logger.info("FILENAME::" + fileName);
		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName, 
						FormValidationExceptionEnums.FILE_STORAGE_EXCEPTION);
			}
			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			//Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			String checksum = ChecksumUtils.getFileChecksum(targetLocation.toFile());
			return fileName+"|"+checksum;
			
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", FormValidationExceptionEnums.FILE_STORAGE_EXCEPTION);
		}
	}

	public Resource loadFileAsResource(String fileName) {
		this.fileStorageLocation = Paths.get(fileStorage).toAbsolutePath().normalize();
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new ExcelFileNotFoundException("File not found " + fileName, 
						FormValidationExceptionEnums.FILE_NOT_FOUND);
			}
		} catch (MalformedURLException ex) {
			throw new ExcelFileNotFoundException("File not found " + fileName, FormValidationExceptionEnums.FILE_NOT_FOUND);
		}
	}
	public File getFileAsFileObject(String fileName) {
		Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
		
		return filePath.toFile();
	}
	

	
	
}
