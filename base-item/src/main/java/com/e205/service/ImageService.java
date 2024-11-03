package com.e205.service;

import org.springframework.core.io.Resource;

public interface ImageService {

  String save(Resource image);

  void delete(String imageName);
}
