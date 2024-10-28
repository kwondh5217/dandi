package com.e205.service;

import com.e205.CreateNotificationCommand;
import com.e205.MemberWithFcm;
import com.e205.MembersInRouteQuery;
import com.e205.communication.MemberQueryService;
import com.e205.communication.RouteQueryService;
import com.e205.event.LostItemSaveEvent;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class EventService {

  @Value("${noti.until.time}")
  private int notificationWindowHours;

  private final RouteQueryService routeQueryService;
  private final Notifier notifier;
  private final MemberQueryService memberQueryService;
  private final CommandService commandService;


  @Transactional
  public void handleLostItemSaveEvent(LostItemSaveEvent event) {
    LocalDateTime eventTime = event.saved().createdAt();
    LocalDateTime since = eventTime.minusHours(notificationWindowHours);
    LocalDateTime until = eventTime.plusHours(notificationWindowHours);

    List<Integer> memberIds = routeQueryService.queryMembersInPoints(
        new MembersInRouteQuery(event.saved().route(), since, until)
    );

    notifyMembersAndCreateCommands(event, memberIds);
  }

  private void notifyMembersAndCreateCommands(LostItemSaveEvent event,
      List<Integer> memberIds) {
    List<MemberWithFcm> membersWithFcm = memberQueryService.memberWithFcmQuery(memberIds);

    membersWithFcm.forEach(member -> {
      notifier.notify(member.fcmToken());
      commandService.createNotification(createNotificationCommand(event, member));
    });
  }

  private CreateNotificationCommand createNotificationCommand(LostItemSaveEvent event,
      MemberWithFcm member) {
    String title = extractTitle(event);

    return CreateNotificationCommand.builder()
        .memberId(member.memberId())
        .resourceId(event.saved().id())
        .title(title)
        .createdAt(LocalDateTime.now())
        .type("lostItem")
        .build();
  }

  private String extractTitle(LostItemSaveEvent event) {
    String situationDescription = event.saved().situationDescription();
    if (situationDescription == null) {
      return "No description";
    }
    return situationDescription.length() >= 20
        ? situationDescription.substring(0, 20)
        : situationDescription;
  }
}
