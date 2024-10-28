package com.e205.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.entity.CommentNotification;
import com.e205.entity.FoundItemNotification;
import com.e205.entity.LostItemNotification;
import com.e205.entity.Notification;
import com.e205.entity.RouteNotification;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class NotificationRepositoryTest {

  @Autowired
  private NotificationRepository notificationRepository;

  @BeforeEach
  void setUp() {
    // 테스트 데이터 생성
    LostItemNotification lostItemNotification = new LostItemNotification();
    lostItemNotification.setMemberId(1);
    lostItemNotification.setLostItemId(101);
    lostItemNotification.setCreatedAt(LocalDateTime.now().minusDays(1));
    lostItemNotification.setTitle("Lost Item Notification");
    notificationRepository.save(lostItemNotification);

    FoundItemNotification foundItemNotification = new FoundItemNotification();
    foundItemNotification.setMemberId(1);
    foundItemNotification.setFoundItemId(102);
    foundItemNotification.setCreatedAt(LocalDateTime.now());
    foundItemNotification.setTitle("Found Item Notification");
    notificationRepository.save(foundItemNotification);

    CommentNotification commentNotification = new CommentNotification();
    commentNotification.setMemberId(2);
    commentNotification.setCommentId(201);
    commentNotification.setCreatedAt(LocalDateTime.now().minusHours(2));
    commentNotification.setTitle("Comment Notification");
    notificationRepository.save(commentNotification);

    RouteNotification routeNotification = new RouteNotification();
    routeNotification.setMemberId(1);
    routeNotification.setRouteId(301);
    routeNotification.setCreatedAt(LocalDateTime.now().minusHours(3));
    routeNotification.setTitle("Route Notification");
    notificationRepository.save(routeNotification);
  }

  @Test
  void findByMemberIdWithCursor() {
    int memberId = 1;
    int lastResourceId = 1;
    int limit = 3;
    Pageable pageable = PageRequest.of(0, limit);

    List<Notification> notifications = notificationRepository.findByMemberIdWithCursor(
        memberId, lastResourceId, pageable);

    assertThat(notifications).isNotEmpty();
    assertThat(notifications.size()).isLessThanOrEqualTo(limit);

    notifications.forEach(
        notification -> assertThat(notification.getMemberId()).isEqualTo(memberId)
    );

    assertThat(notifications).extracting(Notification::getId)
        .isSortedAccordingTo(Integer::compareTo);
  }
}
