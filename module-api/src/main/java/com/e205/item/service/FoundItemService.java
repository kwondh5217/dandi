package com.e205.item.service;

import com.e205.command.FoundItemDeleteCommand;
import com.e205.command.FoundItemSaveCommand;
import com.e205.item.dto.FoundItemCreateRequest;
import com.e205.item.dto.FoundItemResponse;
import com.e205.payload.FoundItemPayload;
import com.e205.query.FoundItemQuery;
import com.e205.service.FoundItemCommandService;
import com.e205.service.FoundItemQueryService;
import com.e205.service.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.ReactiveStreamCommands.DeleteCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FoundItemService {

  private final FoundItemCommandService foundItemCommandService;
  private final FoundItemQueryService foundItemQueryService;

  @Transactional
  public void save(int memberId, FoundItemCreateRequest request, MultipartFile image) {
    FoundItemSaveCommand command = request.toCommand(memberId, image.getResource());
    foundItemCommandService.save(command);
  }

  @Transactional
  public FoundItemResponse get(int memberId, int foundId) {
    FoundItemPayload payload = foundItemQueryService.find(new FoundItemQuery(memberId, foundId));
    // TODO <fosong98> 습득물 이미지 조회 로직 구현
    return FoundItemResponse.from(payload, null);
  }

  @Transactional
  public void delete(int memberId, int foundId) {
    FoundItemDeleteCommand command = new FoundItemDeleteCommand(memberId, foundId);
    FoundItemPayload payload = foundItemQueryService.find(new FoundItemQuery(memberId, foundId));
    if (payload.memberId() != memberId) {
      throw new RuntimeException("습득물을 삭제할 권한이 없습니다.");
    }
    foundItemCommandService.delete(command);
  }
}
