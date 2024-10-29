package com.e205.service;

import com.e205.repository.FileRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DefaultImageService implements ImageService {

  private final static Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "bmp",
      "webp", "tiff");

  private final FileRepository fileRepository;

  @Override
  public String save(Resource image) {
    String extension = image.getFilename().split("\\.")[1];

    if (!IMAGE_EXTENSIONS.contains(extension)) {
      throw new RuntimeException("이미지 확장자가 잘못되었습니다.");
    }

    return fileRepository.save(image);
  }

  @Override
  public void delete(String imageName) {
    fileRepository.delete(imageName);
  }
}
