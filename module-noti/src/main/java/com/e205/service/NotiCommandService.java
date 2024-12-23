package com.e205.service;

import com.e205.CommentSaveCommand;
import com.e205.ConfirmItemCommand;
import com.e205.CreateNotificationCommand;
import com.e205.DeleteNotificationsCommand;
import com.e205.ItemCommandService;
import com.e205.NotifiedMembersCommand;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.member.query.FindMembersByIdQuery;
import com.e205.command.member.service.MemberQueryService;
import com.e205.entity.Notification;
import com.e205.exception.GlobalException;
import com.e205.repository.NotificationRepository;
import com.e205.util.NotificationFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class NotiCommandService implements com.e205.NotiCommandService {

  private final NotificationRepository notificationRepository;
  private final ItemCommandService itemCommandService;
  private final MemberQueryService memberQueryService;

  public void createNotification(CreateNotificationCommand command) {
    Notification notification = NotificationFactory.createNotification(
        command.getNotiType(), command.getResourceId());
    notification.setMemberId(command.getMemberId());
    notification.setTitle(command.getTitle());
    notification.setCreatedAt(command.getCreatedAt());

    this.notificationRepository.save(notification);
  }

  public void deleteNotifications(DeleteNotificationsCommand command) {
    this.notificationRepository.deleteAllByIdAndMemberId(command.memberId(), command.notificationIds());
  }

  public void notifiedMembersCommand(final List<NotifiedMembersCommand> command) {
    this.itemCommandService.saveNotifiedMembers(command);
  }

  public void confirmItemNotification(ConfirmItemCommand command) {
    Notification notification = this.notificationRepository.findById(command.itemId())
        .orElseThrow(() -> new GlobalException("E801"));
    notification.confirmRead();
  }

  public void createCommentNotification(CommentSaveCommand command) {
    final List<MemberPayload> members = memberQueryService.findMembers(
        new FindMembersByIdQuery(new ArrayList<>(command.senders())));

    for (final MemberPayload member : members) {
      if(!command.writerId().equals(member.id())) {
        Notification notification = NotificationFactory.createNotification(command.type(),
            command.commentId());
        notification.setMemberId(member.id());
        notification.setTitle(command.type());
        notification.setCreatedAt(LocalDateTime.now());

        this.notificationRepository.save(notification);
      }
    }
  }
}
