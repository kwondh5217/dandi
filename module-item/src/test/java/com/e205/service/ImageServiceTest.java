package com.e205.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.e205.base.item.ImageType;
import com.e205.base.item.command.ImageSaveCommand;
import com.e205.base.item.service.ImageService;
import com.e205.repository.FileRepository;
import com.e205.repository.ItemImageRepository;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

  ImageService imageService;
  @Mock
  FileRepository fileRepository;
  @Mock
  ItemImageRepository imageRepository;

  @BeforeEach
  void setUp() {
    imageService = new DefaultImageService(fileRepository, imageRepository);
  }

  @DisplayName("이미지의 확장자가 아니면 예외가 발생한다.")
  @Test
  void When_InvalidImageExtension_Then_ThrowException() {
    Resource pdf = mock(Resource.class);
    given(pdf.getFilename()).willReturn("notImage.pdf");
    ImageType imageType = ImageType.FOUND;

    ThrowingCallable expectThrow = () -> imageService.save(new ImageSaveCommand(pdf, imageType));

    assertThatThrownBy(expectThrow).cause().hasMessage("이미지 확장자가 잘못되었습니다.");
  }
}