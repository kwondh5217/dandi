package com.e205.base.item.service;

import com.e205.base.item.command.ImageSaveCommand;

public interface ImageService {

  String save(ImageSaveCommand command);

  void delete(String imageName);
}
