package com.e205;

import com.e205.base.member.command.member.payload.EmailStatus;
import com.e205.base.member.command.member.payload.MemberStatus;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import com.e205.entity.CommentNotification;
import com.e205.entity.FoundItemNotification;
import com.e205.entity.LostItemNotification;
import com.e205.entity.Notification;
import com.e205.entity.RouteNotification;
import com.e205.repository.NotificationRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AppRunner implements ApplicationRunner {

  private final NotificationRepository notificationRepository;
  private final MemberRepository memberRepository;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    Member build = Member.builder()
        .nickname("daeho")
        .fcmToken("hi")
        .email("test@test.com")
        .status(EmailStatus.VERIFIED)
        .memberStatus(MemberStatus.ACTIVE)
        .foundItemAlarm(true)
        .lostItemAlarm(true)
        .commentAlarm(true)
        .build();
    Member save = this.memberRepository.save(build);

    LostItemNotification lostItemNotification = new LostItemNotification();
    lostItemNotification.setMemberId(save.getId());
    lostItemNotification.setLostItemId(101);
    lostItemNotification.setCreatedAt(LocalDateTime.now().minusDays(1));
    lostItemNotification.setTitle("Lost Item Notification");
    Notification notification = lostItemNotification;
    notification.setBody("Lost Item Notification");
    this.notificationRepository.save(notification);

    FoundItemNotification foundItemNotification = new FoundItemNotification();
    foundItemNotification.setMemberId(save.getId());
    foundItemNotification.setFoundItemId(102);
    foundItemNotification.setCreatedAt(LocalDateTime.now());
    foundItemNotification.setTitle("Found Item Notification");
    Notification notification2 = foundItemNotification;
    notification2.setBody("Found Item Notification");
    this.notificationRepository.save(notification2);

    CommentNotification commentNotification = new CommentNotification();
    commentNotification.setMemberId(save.getId());
    commentNotification.setCommentId(201);
    commentNotification.setCreatedAt(LocalDateTime.now().minusHours(2));
    commentNotification.setTitle("Comment Notification");
    Notification notification3 = commentNotification;
    notification3.setBody("Comment Notification");
    this.notificationRepository.save(notification3);

    RouteNotification routeNotification = new RouteNotification();
    routeNotification.setMemberId(save.getId());
    routeNotification.setRouteId(301);
    routeNotification.setCreatedAt(LocalDateTime.now().minusHours(3));
    routeNotification.setTitle("Route Notification");
    Notification notification4 = routeNotification;
    notification4.setBody("Route Notification");
    this.notificationRepository.save(notification4);
  }
}
