package com.e205.cdc;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j
public class BinlogPositionTracker {

  private final RedisTemplate<String, Object> redisTemplate;
  private final JdbcTemplate jdbcTemplate;
  private final String BINLOG_POSITION = "binlog_position";

  @PostConstruct
  public void onApplicationEvent() {
    Object o = this.redisTemplate.opsForValue().get(BINLOG_POSITION);
    if (o == null) {
      initBinlogPosition();
    }
  }

  public BinlogPosition loadPosition() {
    Object o = this.redisTemplate.opsForValue().get(BINLOG_POSITION);
    if (o == null) {
      throw new IllegalStateException("Binlog position does not exist");
    }
    String s = (String) o;
    String[] split = s.split(":");

    return new BinlogPosition(split[0], Long.parseLong(split[1]));
  }

  public void updatePosition(String currentBinlogFile, long currentPosition) {
    String position = currentBinlogFile + ":" + currentPosition;
    redisTemplate.opsForValue().set(BINLOG_POSITION, position);
  }

  protected BinlogPosition initBinlogPosition() {
    String latestBinlogFile = getLatestBinlogFile();
    this.redisTemplate.opsForValue().set(BINLOG_POSITION, latestBinlogFile + ":4" );
    return new BinlogPosition(latestBinlogFile, 4L);
  }

  @Transactional(readOnly = true)
  public String getLatestBinlogFile() {
    List<String> logs = jdbcTemplate.query(
        "SHOW BINARY LOGS",
        (rs, rowNum) -> rs.getString("Log_name")
    );

    if (logs.isEmpty()) {
      throw new IllegalStateException("No binary logs found on the server.");
    }

    return logs.get(logs.size() - 1);
  }
}
