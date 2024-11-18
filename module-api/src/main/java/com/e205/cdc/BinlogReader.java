package com.e205.cdc;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BinlogReader implements ApplicationRunner {

  private final DataSourceProperties dataSourceProperties;
  private final BinlogPositionTracker tracker;
  private final BinaryLogClient client;
  private final ExecutorService executorService;
  private final Map<Long, String> tableIdToNameMap = new HashMap<>();

  public BinlogReader(DataSourceProperties dataSourceProperties, BinlogPositionTracker tracker) {
    this.dataSourceProperties = dataSourceProperties;
    this.tracker = tracker;

    String url = dataSourceProperties.getUrl();
    String[] hostAndPort = parseHostAndPort(url);

    this.client = new BinaryLogClient(hostAndPort[0], Integer.parseInt(hostAndPort[1]),
        dataSourceProperties.getUsername(), dataSourceProperties.getPassword());
    BinlogPosition position = tracker.loadPosition();
    client.setBinlogFilename(position.getBinlogFileName());
    client.setBinlogPosition(position.getBinlogPosition());

    this.executorService = Executors.newSingleThreadExecutor();
  }

  @Override
  public void run(ApplicationArguments args) {
    executorService.submit(() -> {
      try {
        startBinlogClient();
      } catch (IOException e) {
        log.error("Error starting BinaryLogClient", e);
      }
    });
  }

  private void startBinlogClient() throws IOException {
    client.registerEventListener(this::processEvent);
    try {
      client.connect();
    } catch (IOException e) {
      log.error("Failed to connect to Binlog server", e);
      throw new IOException("Failed to connect to MySQL Binlog", e);
    }
  }

  private void processEvent(Event event) {
    EventHeaderV4 header = event.getHeader();
    String currentBinlog = client.getBinlogFilename();
    long currentPosition = header.getPosition();
    EventType eventType = header.getEventType();
    log.info(eventType.toString());

    switch (eventType) {
      case TABLE_MAP -> {
        TableMapEventData data = event.getData();
        long tableId = data.getTableId();
        String tableName = data.getTable();
        tableIdToNameMap.put(tableId, tableName);
        log.info("Mapped Table ID {} to Table {}", tableId, tableName);
      }

      case WRITE_ROWS, EXT_WRITE_ROWS -> {
        WriteRowsEventData data = event.getData();
        List<Serializable[]> rows = data.getRows();
        for (Serializable[] row : rows) {
          String tableName = tableIdToNameMap.get(data.getTableId());
          System.out.println("Inserted Row in Table " + tableName + ": " + Arrays.toString(row));
        }
      }

      case UPDATE_ROWS, EXT_UPDATE_ROWS -> {
        UpdateRowsEventData data = event.getData();
        List<Entry<Serializable[], Serializable[]>> updatedRows = data.getRows();
        for (Entry<Serializable[], Serializable[]> entry : updatedRows) {
          String tableName = tableIdToNameMap.get(data.getTableId());
          System.out.println("Updated Row in Table " + tableName + ": Before: " + Arrays.toString(entry.getKey()) +
              ", After: " + Arrays.toString(entry.getValue()));
        }
      }

      case DELETE_ROWS, EXT_DELETE_ROWS -> {
        DeleteRowsEventData data = event.getData();
        List<Serializable[]> deletedRows = data.getRows();
        for (Serializable[] row : deletedRows) {
          String tableName = tableIdToNameMap.get(data.getTableId());  // 테이블 이름 가져오기
          System.out.println("Deleted Row in Table " + tableName + ": " + Arrays.toString(row));
        }
      }
    }

    tracker.updatePosition(currentBinlog, currentPosition);
  }

  private String[] parseHostAndPort(String url) {
    String[] urlParts = url.split(":");
    String host = urlParts[2].replaceAll("/", "");
    String port = urlParts[3].split("/")[0];
    return new String[]{host, port};
  }

  public void close() {
    try {
      this.client.disconnect();
    } catch (IOException e) {
      log.error("Error disconnecting from Binlog server", e);
      throw new RuntimeException("Failed to disconnect from MySQL Binlog", e);
    }
  }
}
