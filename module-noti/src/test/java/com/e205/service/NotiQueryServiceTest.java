package com.e205.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.e205.QueryNotificationWithCursor;
import com.e205.repository.NotificationRepository;
import org.junit.jupiter.api.Test;

class NotiQueryServiceTest {

  @Test
  void queryNotificationWithCursor() {
    var query = mock(QueryNotificationWithCursor.class);
    var repository = mock(NotificationRepository.class);
    NotiQueryService notiQueryService = new NotiQueryService(repository);

    notiQueryService.queryNotificationWithCursor(query);

    verify(repository).findByMemberIdWithCursor(any(), any(), any(), any());
  }

}