package com.e205.repository;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("!prod")
@Component
public class MockFileRepository implements FileRepository {

  @Override
  public String save(Resource file) {
    String filename = UUID.randomUUID() + ".png";
    log.debug("file save: {}", file.getFilename());
    return filename;
  }

  @Override
  public void delete(String filename) {
    log.debug("file delete: {}", filename);
  }
}
