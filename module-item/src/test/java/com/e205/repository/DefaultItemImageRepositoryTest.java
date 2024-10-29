package com.e205.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.entity.LostImage;
import com.e205.entity.LostItem;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(DefaultItemImageRepository.class)
class DefaultItemImageRepositoryTest {

  @Autowired
  ItemImageRepository repository;
  @Autowired
  LostItemCommandRepository lostItemCommandRepository;

  @DisplayName("분실물 아이디로 이미지를 조회할 수 있다.")
  @Test
  void When_FindWithLostItemId_Then_ReturnAllImageForLostItem() {
    // given
    LostItem lostItem = new LostItem(1, 1, 2, "상황묘사", "물건묘사");
    lostItemCommandRepository.save(lostItem);

    List<LostImage> images = Stream.generate(UUID::randomUUID).limit(3)
        .map(uuid -> new LostImage(uuid, "png", lostItem))
        .map(repository::save).toList();

    // when
    List<LostImage> result = repository.findAllByLostItemId(lostItem.getId());

    // then
    assertThat(result).isNotEmpty().allMatch(images::contains);
  }
}