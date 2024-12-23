package com.e205.cdc;

import com.e205.cdc.BinlogMappingUtils.NotificationInsertEvent;
import com.e205.event.FoundItemSaveEvent;
import com.e205.event.LostItemSaveEvent;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import java.io.Serializable;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CDCEventPublisher {

  private final TableMetadataCache tableMetadataCache;
  private final ApplicationEventPublisher eventPublisher;

  public CDCEventPublisher(TableMetadataCache tableMetadataCache,
      ApplicationEventPublisher eventPublisher) {
    this.tableMetadataCache = tableMetadataCache;
    this.eventPublisher = eventPublisher;
  }

  public void saveTableInfo(Event event) {
    this.tableMetadataCache.saveTableInfo(event);
  }

  public void processRowEvent(Event event, EventType eventType) {
    RowsEventData data = new RowsEventData(event);
    String tableName = this.tableMetadataCache.getTableName(data.getTableId());

    if ("BinlogPosition".equals(tableName)) {
      return;
    }

    if(eventType == EventType.WRITE_ROWS || eventType == EventType.EXT_WRITE_ROWS) {
      switch (tableName) {
        case "Notification" :
          publishNotificationInsertEvent(data, tableName);
          break;
        case "FoundItem" :
          publishFoundItemSaveEvent(data, tableName);
          break;
        case "LostItem" :
          publishLostItemSaveEvent(data, tableName);
          break;
      }
    }
  }

  private void publishLostItemSaveEvent(RowsEventData data, String tableName) {
    Map<String, Object> rowList = getRowList(data, tableName);
    LostItemSaveEvent lostItemSaveEvent = BinlogMappingUtils.mapToLostItemSaveEvent(rowList);
    this.eventPublisher.publishEvent(lostItemSaveEvent);
    log.info("Publishing LostItemSaveEvent: {}", lostItemSaveEvent);

  }
  private void publishFoundItemSaveEvent(RowsEventData data, String tableName) {
    Map<String, Object> rowList = getRowList(data, tableName);
    FoundItemSaveEvent foundItemSaveEvent = BinlogMappingUtils.mapToFoundItemSaveEvent(rowList);
    this.eventPublisher.publishEvent(foundItemSaveEvent);
    log.info("Publishing FoundItemSaveEvent: {}", foundItemSaveEvent);
  }

  private void publishNotificationInsertEvent(RowsEventData data, String tableName) {
    Map<String, Object> rowList = getRowList(data, tableName);
    NotificationInsertEvent notificationInsertEvent = BinlogMappingUtils.mapToNotificationEvent(rowList);
    this.eventPublisher.publishEvent(notificationInsertEvent);
    log.info("Publishing notification event {}", notificationInsertEvent);
  }

  private Map<String, Object> getRowList(RowsEventData data, String tableName) {
    Map<String, Object> rowList = new HashMap<>();
    List<Serializable[]> rows = data.getRows();

    for (Serializable[] rowValues : rows) {
      rowList = mapRowData(rowValues, tableName);
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

  private Map<String, Object> mapRowData(Serializable[] rowValues, String tableName) {
    Map<String, Object> row = new HashMap<>();
    for (int i = 0; i < rowValues.length; i++) {
      String columnName = this.tableMetadataCache.getColumnInfo(tableName, i).name();
      row.put(columnName, rowValues[i]);
    }
    return row;
  }
}
