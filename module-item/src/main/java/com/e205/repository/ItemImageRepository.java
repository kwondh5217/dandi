package com.e205.repository;

import com.e205.entity.FoundImage;
import com.e205.entity.LostImage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemImageRepository {

  LostImage save(LostImage lostImage);

  FoundImage save(FoundImage foundImage);

  List<LostImage> findAllByLostItemId(Integer lostItemId);

  List<FoundImage> findTopFoundImagesByCreateAtDesc(Integer count);

  Optional<LostImage> findLostImageById(UUID id);

  Optional<FoundImage> findFoundImageById(UUID id);
}
