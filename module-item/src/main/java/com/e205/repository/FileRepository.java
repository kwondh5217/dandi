package com.e205.repository;

import org.springframework.core.io.Resource;

public interface FileRepository {

  String save(Resource file);

  void delete(String filename);
}
