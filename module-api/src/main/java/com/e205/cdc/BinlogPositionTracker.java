package com.e205.cdc;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class BinlogPositionTracker {

  private final BinlogPositionRepository positionRepository;
  private final JdbcTemplate jdbcTemplate;

  @Transactional
  @PostConstruct
  public void init() {
    Optional<BinlogPosition> position = positionRepository.findById(1);

    if (position.isEmpty()) {
      String latestBinlogFile = getLatestBinlogFile();
      BinlogPosition newPosition = new BinlogPosition();
      newPosition.setBinlogFileName(latestBinlogFile);
      newPosition.setBinlogPosition(4L);
      positionRepository.save(newPosition);
      System.out.println("Initialized Binlog position: " + latestBinlogFile + " at position 4");
    } else {
      System.out.println("Resuming from last position: " + position.get());
    }
  }

  @Transactional(readOnly = true)
  public BinlogPosition loadPosition() {
    return this.positionRepository.findById(1)
        .orElseThrow(() -> new RuntimeException("Position not found"));
  }

  @Transactional
  public void updatePosition(String currentBinlogFile, long currentPosition) {
    String sql = "UPDATE BinlogPosition SET binlogFileName = ?, binlogPosition = ? WHERE id = 1";
    this.jdbcTemplate.update(sql, currentBinlogFile, currentPosition);
  }

  @Transactional(readOnly = true)
  public String getLatestBinlogFile() {
    List<String> logs = this.jdbcTemplate.query(
        "SHOW BINARY LOGS",
        (rs, rowNum) -> rs.getString("Log_name")
    );

    if (logs.isEmpty()) {
      throw new IllegalStateException("No binary logs found on the server.");
    }

    return logs.get(logs.size() - 1);
  }

}
