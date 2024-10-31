package com.e205.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.e205.CreateNotificationCommand;
import com.e205.MemberWithFcm;
import com.e205.NotifiedMembersCommand;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class NotificationProcessorTest {

  private CommandService commandService;
  private RetryTemplate retryTemplate;
  private Notifier notifier;
  private PlatformTransactionManager transactionManager;
  private NotificationProcessor notificationProcessor;

  @BeforeEach
  void setUp() {
    TransactionSynchronizationManager.initSynchronization();
    this.commandService = mock(CommandService.class);
    this.retryTemplate = mock(RetryTemplate.class);
    this.notifier = mock(Notifier.class);
    this.transactionManager = mock(PlatformTransactionManager.class);
    this.notificationProcessor = new NotificationProcessor(commandService, retryTemplate,
        notifier, transactionManager);
  }

  @AfterEach
  void tearDown() {
    TransactionSynchronizationManager.clearSynchronization();
  }

  @Test
  void processNotifications_withChunks_andVerifyTransactions() {
    // given
    List<MemberWithFcm> membersWithFcm = List.of(
        new MemberWithFcm(1, "token1"),
        new MemberWithFcm(2, "token2"),
        new MemberWithFcm(3, "token3")
    );

    // when
    List<NotifiedMembersCommand> notifiedMembers = notificationProcessor.processNotifications(
        1, "Test Description", "lostItemSaveEvent", membersWithFcm);

    // then
    verify(commandService, times(membersWithFcm.size())).createNotification(
        any(CreateNotificationCommand.class));

    var synchronizations = TransactionSynchronizationManager.getSynchronizations();
    long notifierCallbackCount = synchronizations.stream()
        .filter(sync -> sync instanceof TransactionSynchronization)
        .count();

    assertAll(
        () -> assertThat(synchronizations).isNotEmpty(),
        () -> assertThat(notifierCallbackCount).isEqualTo(membersWithFcm.size()),
        () -> assertThat(notifiedMembers.size()).isEqualTo(membersWithFcm.size())
    );
  }

  @Test
  void notify_shouldInvokeNotifierDirectly() {
    // given
    String fcm = "fcmToken";
    String title = "Test Title";
    String body = "Test Body";

    // when
    notificationProcessor.notify(fcm, title, body);

    // then
    verify(notifier, times(1)).notify(eq(fcm), eq(title), eq(body));
  }

  @Test
  void processNotifications_whenTransactionFails_shouldRollback() {
    // given
    List<MemberWithFcm> membersWithFcm = List.of(
        new MemberWithFcm(1, "token1")
    );

    TransactionStatus transactionStatus = mock(TransactionStatus.class);
    given(transactionManager.getTransaction(any(TransactionDefinition.class)))
        .willReturn(transactionStatus);

    doThrow(new RuntimeException("Transaction Failure"))
        .when(commandService).createNotification(any(CreateNotificationCommand.class));

    // when
    try {
      notificationProcessor.processNotifications(1, "Test Description",
          "lostItemSaveEvent", membersWithFcm);
    } catch (Exception ignored) {
    }

    // then
    verify(transactionManager, times(1)).rollback(transactionStatus);
    verify(transactionManager, never()).commit(transactionStatus);
  }
}
