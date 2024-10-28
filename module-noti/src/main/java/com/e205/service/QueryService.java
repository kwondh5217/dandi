package com.e205.service;

import com.e205.QueryNotificationWithCursor;
import com.e205.entity.Notification;
import com.e205.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class QueryService {

  private static final int DEFAULT_LIMIT = 20;
  private final NotificationRepository notificationRepository;

  public List<Notification> queryNotificationWithCursor(QueryNotificationWithCursor query) {
    Pageable pageable = PageRequest.of(0, DEFAULT_LIMIT);
    return notificationRepository.findByMemberIdWithCursor(query.memberId(), query.lastResourceId(), pageable);
  }

}
