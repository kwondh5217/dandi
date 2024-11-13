package com.e205.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.e205.dto.CommentNotificationResponse;
import com.e205.dto.FoundItemNotificationResponse;
import com.e205.dto.LostItemNotificationResponse;
import com.e205.dto.RouteNotificationResponse;
import com.e205.entity.CommentNotification;
import com.e205.entity.FoundItemNotification;
import com.e205.entity.LostItemNotification;
import com.e205.entity.Notification;
import com.e205.entity.RouteNotification;
import com.e205.service.NotificationType;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NotificationFactoryTest {

  @DisplayName("타입에 맞게 Notification 이 생성된다.")
  @ParameterizedTest
  @MethodSource
  void createNotification(String type, Integer resourceId, Class<?> expectedClass) {
    String expectedBody = "{\"resourceId\":\"" + resourceId + "\",\"eventType\":\"" + type + "\"}";

    var notification = NotificationFactory.createNotification(type, resourceId);

    assertAll(
        () -> assertNotNull(notification),
        () -> assertEquals(expectedClass, notification.getClass()),
        () -> assertEquals(expectedBody, notification.getBody())
    );
  }

  @DisplayName("타입에 맞게 Notification DTO 가 생성된다.")
  @ParameterizedTest
  @MethodSource
  void convertToDto(Notification notification, Class<?> expectedResponseClass) {
    var response = NotificationFactory.convertToDto(notification);

    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(expectedResponseClass, response.getClass()),
        () -> assertEquals(notification.getId(), response.getId()),
        () -> assertEquals(notification.getMemberId(), response.getMemberId()),
        () -> assertEquals(notification.getCreatedAt(), response.getCreatedAt()),
        () -> assertEquals(notification.isConfirmed(), response.isConfirmation()),
        () -> assertEquals(notification.getTitle(), response.getTitle()),
        () -> assertEquals(notification.getBody(), response.getBody())
    );

    switch (NotificationType.fromString(notification.getEntityType())) {
      case COMMENT -> {
        var commentNotification = (CommentNotification) notification;
        assertEquals(commentNotification.getCommentId(),
            ((CommentNotificationResponse) response).getCommentId());
      }
      case LOST_ITEM -> {
        var lostItemNotification = (LostItemNotification) notification;
        assertEquals(lostItemNotification.getLostItemId(),
            ((LostItemNotificationResponse) response).getLostItemId());
      }
      case FOUND_ITEM -> {
        var foundItemNotification = (FoundItemNotification) notification;
        assertEquals(foundItemNotification.getFoundItemId(),
            ((FoundItemNotificationResponse) response).getFoundItemId());
      }
      case ROUTE -> {
        var routeNotification = (RouteNotification) notification;
        assertEquals(routeNotification.getRouteId(),
            ((RouteNotificationResponse) response).getRouteId());
      }
    }
  }

  private static Stream<Arguments> convertToDto() {
    LocalDateTime now = LocalDateTime.now();

    CommentNotification commentNotification = new CommentNotification();
    commentNotification.setId(1);
    commentNotification.setMemberId(100);
    commentNotification.setCreatedAt(now);
    commentNotification.setConfirmed(true);
    commentNotification.setTitle("Comment Title");
    commentNotification.setBody("Comment Body");
    commentNotification.setCommentId(10);

    LostItemNotification lostItemNotification = new LostItemNotification();
    lostItemNotification.setId(2);
    lostItemNotification.setMemberId(101);
    lostItemNotification.setCreatedAt(now);
    lostItemNotification.setConfirmed(false);
    lostItemNotification.setTitle("Lost Item Title");
    lostItemNotification.setBody("Lost Item Body");
    lostItemNotification.setLostItemId(20);

    FoundItemNotification foundItemNotification = new FoundItemNotification();
    foundItemNotification.setId(3);
    foundItemNotification.setMemberId(102);
    foundItemNotification.setCreatedAt(now);
    foundItemNotification.setConfirmed(true);
    foundItemNotification.setTitle("Found Item Title");
    foundItemNotification.setBody("Found Item Body");
    foundItemNotification.setFoundItemId(30);

    RouteNotification routeNotification = new RouteNotification();
    routeNotification.setId(4);
    routeNotification.setMemberId(103);
    routeNotification.setCreatedAt(now);
    routeNotification.setConfirmed(false);
    routeNotification.setTitle("Route Title");
    routeNotification.setBody("Route Body");
    routeNotification.setRouteId(40);

    return Stream.of(
        Arguments.of(commentNotification, CommentNotificationResponse.class),
        Arguments.of(lostItemNotification, LostItemNotificationResponse.class),
        Arguments.of(foundItemNotification, FoundItemNotificationResponse.class),
        Arguments.of(routeNotification, RouteNotificationResponse.class)
    );
  }

  private static Stream<Arguments> createNotification() {
    return Stream.of(
        Arguments.of("lostItem", 1, LostItemNotification.class),
        Arguments.of("foundItem", 1, FoundItemNotification.class),
        Arguments.of("foundComment", 1, CommentNotification.class),
        Arguments.of("lostComment", 1, CommentNotification.class),
        Arguments.of("route", 1, RouteNotification.class)
    );
  }
}
