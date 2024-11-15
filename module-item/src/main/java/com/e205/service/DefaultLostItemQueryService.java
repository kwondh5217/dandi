package com.e205.service;

import com.e205.entity.LostItem;
import com.e205.entity.LostItemAuth;
import com.e205.event.LostItemReadEvent;
import com.e205.events.EventPublisher;
import com.e205.exception.ItemError;
import com.e205.payload.ItemImagePayload;
import com.e205.payload.LostItemPayload;
import com.e205.query.LostItemListQuery;
import com.e205.query.LostItemQuery;
import com.e205.query.MembersInRouteQuery;
import com.e205.repository.ItemImageRepository;
import com.e205.repository.LostItemAuthRepository;
import com.e205.repository.LostItemRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultLostItemQueryService implements LostItemQueryService {

  private final LostItemAuthRepository lostItemAuthRepository;
  private final RouteQueryService routeQueryService;
  private final ItemImageRepository itemImageRepository;
  private final EventPublisher itemEventPublisher;
  private final LostItemRepository lostItemRepository;

  @Override
  public LostItemPayload find(LostItemQuery query) {
    LostItemAuth lostItemAuth = getLostItemAuth(query);

    LostItem lostItem = lostItemAuth.getLostItem();

    if (lostItemAuth.getMemberId().equals(lostItem.getMemberId())) {
      return lostItem.toPayload();
    }

    if (lostItem.isEnded()) {
      throw ItemError.LOST_ALREADY_ENDED.getGlobalException();
    }

    // TODO <fosong98> 분실물 조회 시 이전에 읽지 않았으면 적절한 위치에 있어야 읽을 수 있다.
//    if (!lostItemAuth.isRead() && !isReadablePosition(query.memberId(), lostItem)) {
//      ItemError.LOST_NOT_VALID_POSITION.throwGlobalException();
//    }

    if (!lostItemAuth.isRead()) {
      lostItemAuth.read();
      itemEventPublisher.publishAtLeastOnce(new LostItemReadEvent(lostItem.getId(), LocalDateTime.now()));
    }
    return lostItem.toPayload();
  }

  @Override
  public List<LostItemPayload> find(LostItemListQuery query) {
    return lostItemRepository.findAllByMemberId(query.memberId()).stream()
        .map(LostItem::toPayload)
        .toList();
  }

  @Override
  public List<ItemImagePayload> findImages(Integer lostId) {
    return itemImageRepository.findAllByLostItemId(lostId).stream()
        .map(image -> new ItemImagePayload(image.getName()))
        .toList();
  }

  private LostItemAuth getLostItemAuth(LostItemQuery query) {
    return lostItemAuthRepository.findLostItemAuthByMemberIdAndLostItemId(query.memberId(),
        query.lostItemId()).orElseThrow(ItemError.LOST_NOT_AUTH::getGlobalException);
  }

  private boolean isReadablePosition(Integer memberId, LostItem lostItem) {
    MembersInRouteQuery query = new MembersInRouteQuery(lostItem.getStartRouteId(),
        lostItem.getEndRouteId(), lostItem.getLostAt());

    List<Integer> memberList = routeQueryService.findUserIdsNearPath(query);
    return memberList.contains(memberId);
  }
}
