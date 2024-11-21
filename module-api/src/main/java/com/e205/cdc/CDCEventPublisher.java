package com.e205.cdc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import java.io.Serializable;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CDCEventPublisher {

  private final EventChannel eventChannel;
  private final TableMetadataCache tableMetadataCache;
  private final ObjectMapper objectMapper;

  public CDCEventPublisher(EventChannel eventChannel, TableMetadataCache tableMetadataCache) {
    this.eventChannel = eventChannel;
    this.tableMetadataCache = tableMetadataCache;
    this.objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public void saveTableInfo(Event event) {
    tableMetadataCache.saveTableInfo(event);
  }

  public void processRowEvent(Event event, EventType eventType) {
    RowsEventData data = new RowsEventData(event);
    String tableName = tableMetadataCache.getTableName(data.getTableId());

    if ("BinlogPosition".equals(tableName)) {
      return;
    }

    List<Map<String, Object>> rowList = generateRowList(data, tableName, eventType);

    try {
      String jsonPayload = objectMapper.writeValueAsString(rowList);
      log.info("Publishing Event: {}", jsonPayload);
      eventChannel.publish(tableName, jsonPayload);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize row event data", e);
    }
  }

  private List<Map<String, Object>> generateRowList(RowsEventData data, String tableName, EventType eventType) {
    List<Map<String, Object>> rowList = new ArrayList<>();

    if (eventType == EventType.UPDATE_ROWS || eventType == EventType.EXT_UPDATE_ROWS) {
      List<Map<String, Object>> updateRows = generateUpdateRows(data, tableName);
      rowList.addAll(updateRows);
    } else {
      List<Map<String, Object>> rows = generateInsertOrDeleteRows(data, tableName, eventType);
      rowList.addAll(rows);
    }

    return rowList;
  }

  private List<Map<String, Object>> generateUpdateRows(RowsEventData data, String tableName) {
    List<Map<String, Object>> rowList = new ArrayList<>();
    List<Map.Entry<Serializable[], Serializable[]>> updateRows = data.getUpdateRows();

    for (Map.Entry<Serializable[], Serializable[]> entry : updateRows) {
      Map<String, Object> beforeRow = mapRowData(entry.getKey(), tableName);
      Map<String, Object> afterRow = mapRowData(entry.getValue(), tableName);

      Map<String, Object> rowMap = Map.of("before", beforeRow, "after", afterRow);
      rowList.add(rowMap);
    }

    return rowList;
  }

  private List<Map<String, Object>> generateInsertOrDeleteRows(RowsEventData data, String tableName, EventType eventType) {
    List<Map<String, Object>> rowList = new ArrayList<>();
    List<Serializable[]> rows = data.getRows();

    String key = (eventType == EventType.WRITE_ROWS || eventType == EventType.EXT_WRITE_ROWS) ? "after" : "before";
    for (Serializable[] rowValues : rows) {
      Map<String, Object> row = mapRowData(rowValues, tableName);
      rowList.add(Map.of(key, row));
    }

    return rowList;
  }

  private Map<String, Object> mapRowData(Serializable[] rowValues, String tableName) {
    Map<String, Object> row = new HashMap<>();
    for (int i = 0; i < rowValues.length; i++) {
      String columnName = tableMetadataCache.getColumnInfo(tableName, i).name();
      row.put(columnName, rowValues[i]);
    }
    return row;
  }
}
