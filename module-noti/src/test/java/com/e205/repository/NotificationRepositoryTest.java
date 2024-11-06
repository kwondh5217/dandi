package com.e205.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.entity.CommentNotification;
import com.e205.entity.FoundItemNotification;
import com.e205.entity.LostItemNotification;
import com.e205.entity.Notification;
import com.e205.entity.RouteNotification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
    this.notificationRepository.save(lostItemNotification);

    FoundItemNotification foundItemNotification = new FoundItemNotification();
    foundItemNotification.setMemberId(1);
    foundItemNotification.setFoundItemId(102);
    foundItemNotification.setCreatedAt(LocalDateTime.now());
    foundItemNotification.setTitle("Found Item Notification");
    this.notificationRepository.save(foundItemNotification);

    CommentNotification commentNotification = new CommentNotification();
    commentNotification.setMemberId(2);
    commentNotification.setCommentId(201);
    commentNotification.setCreatedAt(LocalDateTime.now().minusHours(2));
    commentNotification.setTitle("Comment Notification");
    this.notificationRepository.save(commentNotification);

    RouteNotification routeNotification = new RouteNotification();
    routeNotification.setMemberId(1);
    routeNotification.setRouteId(301);
    routeNotification.setCreatedAt(LocalDateTime.now().minusHours(3));
    routeNotification.setTitle("Route Notification");
    this.notificationRepository.save(routeNotification);
  }

  @AfterEach
  void tearDown() {
    this.notificationRepository.deleteAll();
  }

  @Test
  void findByMemberIdWithCursor() {
    int memberId = 1;
    int lastResourceId = 1;
    int limit = 3;
    Pageable pageable = PageRequest.of(0, limit);
    List<Class<? extends Notification>> classes = List.of(LostItemNotification.class,
        FoundItemNotification.class, CommentNotification.class, RouteNotification.class);

    List<Notification> notifications = this.notificationRepository.findByMemberIdWithCursor(
        memberId, lastResourceId, classes, pageable);

    Assertions.assertAll(
        () -> assertThat(notifications).isNotEmpty(),
        () -> assertThat(notifications.size()).isLessThanOrEqualTo(limit),
        () -> assertThat(notifications).extracting(Notification::getId)
            .isSortedAccordingTo(Integer::compareTo),
        () -> notifications.forEach(
            notification -> assertThat(notification.getMemberId()).isEqualTo(memberId)
        )
    );
  }

  @Test
  void deleteAllByIdAndMemberIdInBatch() {
    List<Integer> notificationIds = this.notificationRepository.findAll()
        .stream()
        .map(Notification::getId)
        .collect(Collectors.toList());
    assertThat(notificationIds.size()).isEqualTo(4);

    this.notificationRepository.deleteAllByIdAndMemberId(1, notificationIds);

    List<Notification> notifications = this.notificationRepository.findAll();
    assertThat(notifications.size()).isEqualTo(1);
  }
}
