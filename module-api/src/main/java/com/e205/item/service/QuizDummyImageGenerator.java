package com.e205.item.service;

import com.e205.FoundItemType;
import com.e205.auth.jwt.JwtProvider;
import com.e205.entity.FoundImage;
import com.e205.entity.FoundItem;
import com.e205.manager.service.ManagerService;
import com.e205.repository.FileRepository;
import com.e205.repository.FoundItemCommandRepository;
import com.e205.repository.ItemImageRepository;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@RequiredArgsConstructor
@Component
public class QuizDummyImageGenerator {

  @Value("${spring.jpa.hibernate.ddl-auto}")
  private String ddlAuto;

  private final ManagerService managerService;
  private final FoundItemCommandRepository foundItemCommandRepository;
  private final ItemImageRepository itemImageRepository;
  private final ResourceLoader resourceLoader;
  private final FileRepository fileRepository;
  private final PlatformTransactionManager ptm;
  private final JwtProvider jwtProvider;

  @PostConstruct
  public void generateDummyImage() throws IOException {
    if (!ddlAuto.contains("create")) {
      return;
    }

    TransactionStatus transaction = ptm.getTransaction(new DefaultTransactionDefinition());
    String token = managerService.createManagerAccount("DummyUser");
    Integer memberId = jwtProvider.getMemberId(token);

    FoundItem foundItem = foundItemCommandRepository.save(
        new FoundItem(memberId, 1D, 1D, "더미입니다.", "더미입니다.", FoundItemType.OTHER,
            LocalDateTime.now()));

    foundItemCommandRepository.save(foundItem);

    Resource resource = resourceLoader.getResource("classpath:dummies/");
    Path path = resource.getFile().toPath();

    try (Stream<Path> paths = Files.walk(path)) {
      paths.filter(Files::isRegularFile).map(FileSystemResource::new).map(fileRepository::save)
          .map(filename -> filename.split("\\."))
          .map(parts -> new FoundImage(UUID.fromString(parts[0]), parts[1], foundItem))
          .forEach(itemImageRepository::save);
    }

    ptm.commit(transaction);
  }
}
