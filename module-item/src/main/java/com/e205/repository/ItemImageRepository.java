package com.e205.repository;

import com.e205.entity.FoundImage;
import com.e205.entity.LostImage;
import java.util.List;

public interface ItemImageRepository {

  LostImage save(LostImage lostImage);

  FoundImage save(FoundImage foundImage);

  List<LostImage> findAllByLostItemId(Integer lostItemId);
}
