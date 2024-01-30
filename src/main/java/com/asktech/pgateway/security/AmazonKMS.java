package com.asktech.pgateway.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CommitmentPolicy;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;

public class AmazonKMS {

	/*
	@Value("${awsConfiguration.arnEndPoint}")
	final static String keyArn;
	@Value("${awsConfiguration.contextKey}")
	final static String contextKey;
	@Value("${awsConfiguration.contextValue}")
	final static String contextValue;
	*/
	
	// final static String keyArn = "arn:aws:kms:ap-south-1:838635235256:key/47e45523-c789-4e5c-ba1d-36c4e71689a8";
	// final static AwsCrypto crypto = AwsCrypto.builder().withCommitmentPolicy(CommitmentPolicy.RequireEncryptRequireDecrypt).build();		
	// final static BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA4GQUOE64NBORSXGD", "uOCyNbgYRftaoJZjlXPJhM97mzT5IKeFLHVJ+Jk7");
	// final static KmsMasterKeyProvider keyProvider = KmsMasterKeyProvider.builder().withCredentials(awsCreds).buildStrict(keyArn);		
	// final static Map<String, String> encryptionContext = Collections.singletonMap("ExampleContextKey","ExampleContextValue");		
	
	
	final static String keyArn = "arn:aws:kms:ap-south-1:667262922539:key/e934219f-7e95-442c-9a0b-a9788b461ae4";
	final static AwsCrypto crypto = AwsCrypto.builder().withCommitmentPolicy(CommitmentPolicy.RequireEncryptRequireDecrypt).build();		
	final static BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAZWW7YF4V7GQ4HTJJ", "RgtKZhbR0I2zUtRMH2a3KGozAEXOqyEtqQKHjbb4");
	final static KmsMasterKeyProvider keyProvider = KmsMasterKeyProvider.builder().withCredentials(awsCreds).buildStrict(keyArn);		
	final static Map<String, String> encryptionContext = Collections.singletonMap("ExampleContextKey","ExampleContextValue");		

	public static String encryptionWithKMS(String inputStr) {
		
			
		final CryptoResult<byte[], KmsMasterKey> encryptResult = crypto.encryptData(keyProvider, inputStr.getBytes(StandardCharsets.UTF_8),encryptionContext);
		final byte[] ciphertext = encryptResult.getResult();
		
		return Base64.getEncoder().encodeToString(ciphertext);
		
	}
	
	public static String decryptionWithKMS(String inputStr) {		
		
		byte[] decode = Base64.getDecoder().decode(inputStr);
		final CryptoResult<byte[], KmsMasterKey> decryptResult = crypto.decryptData(keyProvider, decode);
		
		if (!encryptionContext.entrySet().stream()
				.allMatch(e -> e.getValue().equals(decryptResult.getEncryptionContext().get(e.getKey())))) {
			throw new IllegalStateException("Wrong Encryption Context!");
		}
		
		return new String(decryptResult.getResult());
	}
	
}
