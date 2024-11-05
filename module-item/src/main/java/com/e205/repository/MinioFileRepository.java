package com.e205.repository;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Profile("prod")
@RequiredArgsConstructor
@Component
public class MinioFileRepository implements FileRepository {

  @Value("${minio.bucket-name}")
  private String bucketName;
  private final MinioClient minioClient;

  @Override
  public String save(Resource file) {
    String fileId = UUID.randomUUID().toString();
    String extension = FilenameUtils.getExtension(file.getFilename());
    String filename = String.format("%s.%s", fileId, extension);

    try (InputStream inputStream = file.getInputStream()) {
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucketName)
              .object(filename)
              .stream(inputStream, file.contentLength(), -1)
              .contentType(Files.probeContentType(Path.of(filename)))
              .build()
      );
      return filename;
    } catch (Exception e) {
      throw new RuntimeException("파일 저장에 실패했습니다.");
    }
  }

  @Override
  public void delete(String filename) {
    try {
      minioClient.removeObject(
          RemoveObjectArgs.builder()
              .bucket(bucketName)
              .object(filename)
              .build()
      );
    } catch (Exception e) {
      throw new RuntimeException("파일 삭제에 실패했습니다.");
    }
  }
}
