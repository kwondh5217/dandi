package com.e205.service;

import com.e205.QueryNotificationWithCursor;
import com.e205.entity.CommentNotification;
import com.e205.entity.FoundItemNotification;
import com.e205.entity.LostItemNotification;
import com.e205.entity.Notification;
import com.e205.entity.RouteNotification;
import com.e205.repository.NotificationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotiQueryService {

  private static final int DEFAULT_LIMIT = 20;
  private final NotificationRepository notificationRepository;

  public List<Notification> queryNotificationWithCursor(
      QueryNotificationWithCursor query) {
    Pageable pageable = PageRequest.of(0, DEFAULT_LIMIT);
    return notificationRepository.findByMemberIdWithCursor(query.memberId(),
        query.lastResourceId(), convertTypesToClass(query.types()), pageable);
  }

  private List<Class<? extends Notification>> convertTypesToClass(List<String> types) {
    return types.stream()
        .map(this::mapType)
        .collect(Collectors.toList());
  }

  private Class<? extends Notification> mapType(String type) {
    return switch (type) {
      case "comment" -> CommentNotification.class;
      case "lostItem" -> LostItemNotification.class;
      case "foundItem" -> FoundItemNotification.class;
      case "route" -> RouteNotification.class;
      default -> throw new IllegalArgumentException("Unknown notification type: " + type);
    };
  }

}
