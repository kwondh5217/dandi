package com.e205.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("prod")
@Configuration
public class MinioConfig {

	@Value("${minio.url}")
	private String url;

	@Value("${minio.access.key}")
	private String accessKey;

	@Value("${minio.access.secret}")
	private String secretKey;

	@Value("${minio.bucket-name}")
	private String bucketName;

	@Bean
	public MinioClient minioClient() {
		MinioClient minioClient = MinioClient.builder().endpoint(url).credentials(accessKey, secretKey).build();
		String policy = getAnonymousReadOnlyRootPolicy(bucketName);
		try {
			if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
				minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
				minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return minioClient;
	}

	private String getAnonymousReadOnlyRootPolicy(String bucketName) {
		return "{\n" +
			"  \"Version\": \"2012-10-17\",\n" +
			"  \"Statement\": [\n" +
			"    {\n" +
			"      \"Effect\": \"Allow\",\n" +
			"      \"Principal\": \"*\",\n" +
			"      \"Action\": [\"s3:GetObject\"],\n" +
			"      \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
			"    }\n" +
			"  ]\n" +
			"}";
	}

}
