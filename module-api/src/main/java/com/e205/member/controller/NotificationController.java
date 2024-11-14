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
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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

  @GetMapping
  public ResponseEntity<List<NotificationResponse>> findNotifications(
      @AuthenticationPrincipal(expression = "id") final Integer memberId,
      @RequestParam("resourceId") Integer resourceId,
      @RequestParam("types") final List<String> types
  ) {
    checkTypes(types);
    if(resourceId == null) {
      resourceId = 0;
    }

    List<Notification> notifications = this.notiQueryService.queryNotificationWithCursor(
        new QueryNotificationWithCursor(memberId, resourceId, types));

    var notificationResponses = notifications.stream()
        .map(NotificationFactory::convertToDto)
        .collect(Collectors.toList());

    return ResponseEntity.ok(notificationResponses);
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

  private static void checkTypes(final List<String> types) {
    types.forEach(NotificationType::fromString);
  }
}
