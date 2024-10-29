package com.e205.repository;

import com.e205.entity.LostImage;
import java.util.List;

public interface ItemImageRepository {

  LostImage save(LostImage lostImage);

  List<LostImage> findAllByLostItemId(Integer lostItemId);
}
