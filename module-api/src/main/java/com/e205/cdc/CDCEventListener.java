package com.e205.cdc;

import com.e205.cdc.BinlogMappingUtils.NotificationInsertEvent;
import com.e205.command.member.service.MemberQueryService;
import com.e205.service.Notifier;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CDCEventListener {

  private final MemberQueryService memberQueryService;
  private final Notifier notifier;
  private final StreamOperations<String, String, Object> streamOperations;
  private final String NOTI_DLQ_STREAM = "notification:dlq";

  public CDCEventListener(MemberQueryService memberQueryService, Notifier notifier,
      RedisTemplate<String, Object> redisTemplate) {
    this.memberQueryService = memberQueryService;
    this.notifier = notifier;
    this.streamOperations = redisTemplate.opsForStream();
  }

  @EventListener
  public void handleNotificationInsertEvent(NotificationInsertEvent event) {
    String memberFcmById = this.memberQueryService.findMemberFcmById(event.memberId());
    try {
      this.notifier.notify(memberFcmById, event.title(), event.body());
    } catch (Exception e) {
      ObjectRecord<String, Object> record = ObjectRecord.create(
          NOTI_DLQ_STREAM,
          Map.of(
              "deviceToken", memberFcmById,
              "title", event.title(),
              "body", event.body()
          )
      );
      log.warn("Failed event saved to DLQ: {}", event);
      this.streamOperations.add(record);
    }
  }
}
