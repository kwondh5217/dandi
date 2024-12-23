package com.e205.base.noti;

import java.util.List;

public interface NotiCommandService {
  void createNotification(CreateNotificationCommand command);

  void deleteNotifications(DeleteNotificationsCommand command);

  void notifiedMembersCommand(List<NotifiedMembersCommand> command);

  void confirmItemNotification(ConfirmItemCommand command);

  void createCommentNotification(CommentSaveCommand command);
}
