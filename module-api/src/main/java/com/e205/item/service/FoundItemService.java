package com.e205.item.service;

import com.e205.command.FoundItemSaveCommand;
import com.e205.item.dto.FoundItemCreateRequest;
import com.e205.item.dto.FoundItemResponse;
import com.e205.service.FoundItemCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FoundItemService {

  private final FoundItemCommandService foundItemCommandService;

  @Transactional
  public void save(int memberId, FoundItemCreateRequest request, MultipartFile image) {
    FoundItemSaveCommand command = request.toCommand(memberId, image.getResource());
    foundItemCommandService.save(command);
  }

  @Transactional
  public FoundItemResponse get(int memberId, int foundId) {
    // TODO <fosong98> 습득물 상세 조회 로직 구현
    return null;
  }

  @Transactional
  public void delete(int memberId, int foundId) {
    // TODO <fosong98> 습득물 삭제 로직 구현
  }
}
