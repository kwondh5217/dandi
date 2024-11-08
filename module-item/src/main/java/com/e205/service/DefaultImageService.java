package com.e205.service;

import com.e205.command.ImageSaveCommand;
import com.e205.entity.FoundImage;
import com.e205.entity.LostImage;
import com.e205.repository.FileRepository;
import com.e205.repository.ItemImageRepository;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultImageService implements ImageService {

  private final static Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "bmp",
      "webp", "tiff");

  private final FileRepository fileRepository;
  private final ItemImageRepository imageRepository;

  @Override
  public String save(ImageSaveCommand command) {
    Resource image = command.image();
    String extension = FilenameUtils.getExtension(image.getFilename());

    if (!IMAGE_EXTENSIONS.contains(extension)) {
      throw new RuntimeException("이미지 확장자가 잘못되었습니다.");
    }

    String filename = fileRepository.save(image);
    UUID id = UUID.fromString(FilenameUtils.getBaseName(filename));

    try {
      switch (command.type()) {
        case LOST -> imageRepository.save(new LostImage(id, extension, null));
        case FOUND -> imageRepository.save(new FoundImage(id, extension, null));
        default -> throw new RuntimeException("이미지 타입이 잘못되었습니다.");
      }
      return filename;
    } catch (Exception e) {
      fileRepository.delete(filename);
      throw new RuntimeException("이미지 저장에 실패했습니다.", e);
    }
  }

  @Override
  public void delete(String imageName) {
    fileRepository.delete(imageName);
  }
}
