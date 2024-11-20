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
import java.lang.reflect.Field;
import java.util.BitSet;
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
  private final Map<Long, String> tableIdToNameMap;
  private final ClassInfoCache classInfoCache;

  public BinlogReader(DataSourceProperties dataSourceProperties,
      BinlogPositionTracker tracker, ClassInfoCache classInfoCache) {
    this.dataSourceProperties = dataSourceProperties;
    this.tracker = tracker;
    this.classInfoCache = classInfoCache;
    this.tableIdToNameMap = new HashMap<>();

    String[] dbInfo = extractDBInfo(dataSourceProperties.getUrl());
    String host = dbInfo[0];
    int port = Integer.parseInt(dbInfo[1]);
    String schema = dbInfo[2];
    String username = dataSourceProperties.getUsername();
    String password = dataSourceProperties.getPassword();

    this.client = new BinaryLogClient(host, port, schema, username, password);
    BinlogPosition position = tracker.loadPosition();
    this.client.setBinlogFilename(position.getBinlogFileName());
    this.client.setBinlogPosition(position.getBinlogPosition());

    this.executorService = Executors.newSingleThreadExecutor();
  }

  @Override
  public void run(ApplicationArguments args) {
    this.executorService.submit(() -> {
      try {
        startBinlogClient();
      } catch (IOException e) {
        log.error("Error starting BinaryLogClient", e);
      }
    });
  }

  private void startBinlogClient() throws IOException {
    this.client.registerEventListener(this::processEvent);
    try {
      this.client.connect();
    } catch (IOException e) {
      log.error("Failed to connect to Binlog server", e);
      throw new IOException("Failed to connect to MySQL Binlog", e);
    }
  }

  private void processEvent(Event event) {
    EventHeaderV4 header = event.getHeader();
    String currentBinlog = this.client.getBinlogFilename();
    long currentPosition = header.getPosition();
    EventType eventType = header.getEventType();

    switch (eventType) {
      case TABLE_MAP -> {
        saveTableInfo(event);
      }
      case WRITE_ROWS, EXT_WRITE_ROWS -> {
        WriteRowsEventData data = event.getData();
        String tableName = this.tableIdToNameMap.get(data.getTableId());
        Class<?> mappedClass = this.classInfoCache.getMappedClass(tableName);

        log.info("Table: {}, Class: {}", tableName, mappedClass.getName());
      }
      case UPDATE_ROWS, EXT_UPDATE_ROWS -> {
        UpdateRowsEventData data = event.getData();
        List<Entry<Serializable[], Serializable[]>> updatedRows = data.getRows();
        String tableName = this.tableIdToNameMap.get(data.getTableId());
        Class<?> mappedClass = this.classInfoCache.getMappedClass(tableName);

        log.info("Table: {}, Class: {}", tableName, mappedClass.getName());
      }
      case DELETE_ROWS, EXT_DELETE_ROWS -> {
        DeleteRowsEventData data = event.getData();
        data.getIncludedColumns();
        List<Serializable[]> deletedRows = data.getRows();
        String tableName = this.tableIdToNameMap.get(data.getTableId());
        Class<?> mappedClass = this.classInfoCache.getMappedClass(tableName);

        log.info("Table: {}, Class: {}", tableName, mappedClass.getName());
      }
    }

    this.tracker.updatePosition(currentBinlog, currentPosition);
  }

  private Map<String, Object> mapRowToEntity(String tableName, Serializable[] row, BitSet includedColumns) {
    Map<String, Object> map = new HashMap<>();
    try {
      Class<?> mappedClass = classInfoCache.getMappedClass(tableName);
      Field[] declaredFields = mappedClass.getDeclaredFields();

      int idx = 0;
      for (int i = 0; i < row.length; i++) {
        if (includedColumns.get(i)) {
          String columnName = declaredFields[i].getName();
          map.put(columnName, row[idx++]);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Row 데이터를 Map으로 변환하는 중 오류 발생", e);
    }
    return map;
  }

  private void saveTableInfo(Event event) {
    TableMapEventData data = event.getData();
    long tableId = data.getTableId();
    String tableName = data.getTable();
    if (!this.tableIdToNameMap.containsKey(tableId)) {
      this.tableIdToNameMap.put(tableId, tableName);
    }
  }

  private String[] extractDBInfo(String mysqlUrl) {
    String[] urlSegments = mysqlUrl.split(":");
    String host = urlSegments[2].replaceAll("/", "");
    String[] portAndSchemaSegments = urlSegments[3].split("/");
    String port = portAndSchemaSegments[0];
    String schema = portAndSchemaSegments.length > 1 ? portAndSchemaSegments[1] : null;
    return new String[]{host, port, schema};
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
