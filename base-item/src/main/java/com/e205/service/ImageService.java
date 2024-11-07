package com.e205.service;

import com.e205.command.ImageSaveCommand;
import org.springframework.core.io.Resource;

public interface ImageService {

  String save(ImageSaveCommand command);

  void delete(String imageName);
}
