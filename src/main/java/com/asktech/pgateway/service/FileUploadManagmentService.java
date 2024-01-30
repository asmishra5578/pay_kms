package com.asktech.pgateway.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.asktech.pgateway.constants.BucketNameConstant;
import com.asktech.pgateway.dto.FileResponseDto;

@Service
public class FileUploadManagmentService implements BucketNameConstant{

    private AmazonS3 s3client;
	@Value("${awsCredential.s3FileUrl}")
	String s3FileUrl;
	@Value("${awsCredential.s3AccessKey}")
	String s3AccessKey;
	@Value("${awsCredential.s3SecretAccessKey}")
	String s3SecretAccessKey;
	@Value("${awsCredential.s3TxnStsBucketName}")
	String s3TxnStsBucketName;

	@SuppressWarnings("deprecation")
	@PostConstruct
	private void initializeAmazon() throws IOException {
		AWSCredentials credentials = new BasicAWSCredentials(s3AccessKey, s3SecretAccessKey);
		this.s3client = new AmazonS3Client(credentials);
	}

    public FileResponseDto accountAndUpiTransferFileUpload(MultipartFile multipartFile, String fileUploadName) throws IOException {
        String fileUrl = "";
		File file = convertMultiPartToFile(multipartFile);
		String fileName = generateFileName(multipartFile);
		if (fileUploadName.equals(ACCOUNT_AND_UPI_TSF_BULK_UPLOAD)) {
			String folderName = s3TxnStsBucketName + "/accountAndUPITsfFileBucket";
			fileUrl = s3FileUrl + "/" + folderName + "/" + fileName;
			uploadFileTos3bucket(folderName, fileName, file);
		}
		FileResponseDto rdto = new FileResponseDto();
		rdto.setFileUrl(fileUrl);
		rdto.setFileData(file);
		rdto.setFileName(fileName);
		return rdto;
    }
    
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	private String generateFileName(MultipartFile multiPart) {
		return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
	}

    private void uploadFileTos3bucket(String s3BucketName, String fileName, File file) throws IOException {
		s3client.putObject(
				new PutObjectRequest(s3BucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
	}
}
