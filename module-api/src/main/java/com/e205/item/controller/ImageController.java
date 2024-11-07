package com.e205.item.controller;

import com.e205.ImageType;
import com.e205.command.ImageSaveCommand;
import com.e205.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class ImageController {

  private final ImageService imageService;

  @PostMapping("/images/{type}")
  public ResponseEntity<String> createImage(@RequestPart("image") MultipartFile file, @PathVariable ImageType type) {
    ImageSaveCommand command = new ImageSaveCommand(file.getResource(), type);
    return ResponseEntity.ok(imageService.save(command));
  }
}
