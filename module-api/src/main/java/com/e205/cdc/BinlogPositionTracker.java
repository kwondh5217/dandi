package com.e205.cdc;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class BinlogPositionTracker {

  public static final int ID = 1;
  private final BinlogPositionRepository positionRepository;
  private final JdbcTemplate jdbcTemplate;

  @Transactional
  @PostConstruct
  public void init() {
    positionRepository.findById(ID).orElseGet(this::initBinlogPosition);
  }

  @Transactional(readOnly = true)
  public BinlogPosition loadPosition() {
    return positionRepository.findDefaultBinlogPosition()
        .orElseGet(this::initBinlogPosition);
  }

  @Transactional
  public void updatePosition(String currentBinlogFile, long currentPosition) {
    String sql = "UPDATE BinlogPosition SET binlogFileName = ?, binlogPosition = ? WHERE id = ?";
    jdbcTemplate.update(sql, currentBinlogFile, currentPosition, ID);
  }

  @Transactional
  protected BinlogPosition initBinlogPosition() {
    String latestBinlogFile = getLatestBinlogFile();
    BinlogPosition newPosition = new BinlogPosition();
    newPosition.setBinlogFileName(latestBinlogFile);
    newPosition.setBinlogPosition(4L);
    return positionRepository.save(newPosition);
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
