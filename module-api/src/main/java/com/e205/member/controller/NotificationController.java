package com.e205.member.controller;

import com.e205.DeleteNotificationsCommand;
import com.e205.QueryNotificationWithCursor;
import com.e205.ConfirmItemCommand;
import com.e205.dto.NotificationResponse;
import com.e205.entity.Notification;
import com.e205.service.NotiCommandService;
import com.e205.service.NotiQueryService;
import com.e205.service.NotificationType;
import com.e205.util.NotificationFactory;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/noti")
@RestController
public class NotificationController {

  private final NotiQueryService notiQueryService;
  private final NotiCommandService notiCommandService;
  private static final String NOTI_CACHE_KEY_PREFIX = "noti:";
  private final RedisTemplate<String, Object> redisTemplate;

  @GetMapping
  public ResponseEntity<List<NotificationResponse>> findNotifications(
      @AuthenticationPrincipal(expression = "id") final Integer memberId,
      @RequestParam(value = "resourceId", defaultValue = "0") Integer resourceId,
      @RequestParam("types") final List<String> types
  ) {
    checkTypes(types);

    if (resourceId == 0) {
      return getFromCacheOrLoad(memberId, resourceId, types);
    }

    return ResponseEntity.ok(getNotificationResponses(memberId, resourceId, types));
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping
  public void deleteNotifications(
      @AuthenticationPrincipal(expression = "id") final Integer memberId,
      @RequestBody final List<Integer> notificationIds
  ) {
    this.notiCommandService.deleteNotifications(
        new DeleteNotificationsCommand(memberId, notificationIds));
  }

  @PutMapping
  public ResponseEntity<Void> confirmNotifications(
      @AuthenticationPrincipal(expression = "id") final Integer memberId,
      @RequestBody ConfirmItemCommand confirmItemCommand
  ) {
    if (this.notiQueryService.isOwner(memberId, confirmItemCommand.itemId())) {
      this.notiCommandService.confirmItemNotification(confirmItemCommand);
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  }

  private List<NotificationResponse> getNotificationResponses(Integer memberId,
      Integer resourceId, List<String> types) {
    List<Notification> notifications = this.notiQueryService.queryNotificationWithCursor(
        new QueryNotificationWithCursor(memberId, resourceId, types));

    List<NotificationResponse> notificationResponses = notifications.stream()
        .map(NotificationFactory::convertToDto)
        .collect(Collectors.toList());
    return notificationResponses;
  }

  private void checkTypes(final List<String> types) {
    types.forEach(NotificationType::fromString);
  }

  private ResponseEntity<List<NotificationResponse>> getFromCacheOrLoad(
      Integer memberId, Integer resourceId, List<String> types) {
    String redisKey = NOTI_CACHE_KEY_PREFIX + memberId;

    Map<Object, Object> cachedNotifications = redisTemplate.opsForHash().entries(redisKey);

    if (!cachedNotifications.isEmpty()) {
      List<NotificationResponse> filteredNotifications = cachedNotifications.values().stream()
          .map(obj -> (NotificationResponse) obj)
          .sorted(Comparator.comparing(NotificationResponse::getId))
          .collect(Collectors.toList());

      if (!filteredNotifications.isEmpty()) {
        return ResponseEntity.ok(filteredNotifications);
      }
    }

    List<NotificationResponse> notificationResponses = getNotificationResponses(
        memberId, resourceId, types);

    if (!notificationResponses.isEmpty()) {
      Map<String, NotificationResponse> cacheMap = notificationResponses.stream()
          .collect(Collectors.toMap(
              response -> response.getId().toString(),
              response -> response
          ));
      redisTemplate.opsForHash().putAll(redisKey, cacheMap);

      redisTemplate.expire(redisKey, 1, TimeUnit.HOURS);
    }

    return ResponseEntity.ok(notificationResponses);
  }
}
