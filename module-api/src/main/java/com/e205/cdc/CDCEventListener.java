package com.e205.cdc;

import com.e205.NotificationInsertEvent;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.bag.query.FindMemberQuery;
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
    MemberPayload member = this.memberQueryService.findMember(
        new FindMemberQuery(event.getMemberId()));
    String memberFcmById = member.fcmCode();

    if("lostitem".equalsIgnoreCase(event.getType()) && member.lostItemAlarm()
    || "founditem".equalsIgnoreCase(event.getType()) && member.foundItemAlarm()
    || "comment".equalsIgnoreCase(event.getType()) && member.commentAlarm()) {
      try {
        this.notifier.notify(memberFcmById, event.getTitle(), event.getBody());
      } catch (Exception e) {
        ObjectRecord<String, Object> record = ObjectRecord.create(
            NOTI_DLQ_STREAM,
            Map.of(
                "deviceToken", memberFcmById,
                "title", event.getTitle(),
                "body", event.getBody()
            )
        );
        log.warn("Failed event saved to DLQ: {}", event);
        this.streamOperations.add(record);
      }
    }
  }
}
