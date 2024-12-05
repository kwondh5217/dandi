package com.e205.cdc;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import java.io.Serializable;
import java.util.BitSet;
import java.util.List;
import java.util.Map.Entry;
import lombok.Getter;

public class RowsEventData {

  private final Event event;
  @Getter
  private final long tableId;
  @Getter
  private final BitSet includedColumns;
  private final Object rows;

  public RowsEventData(Event event) {
    this.event = event;
    this.tableId = extractTableId();
    this.includedColumns = extractIncludedColumns();
    this.rows = extractRows();
  }

  @SuppressWarnings("unchecked")
  public List<Entry<Serializable[], Serializable[]>> getUpdateRows() {
    if (event.getData() instanceof UpdateRowsEventData) {
      return (List<Entry<Serializable[], Serializable[]>>) rows;
    }
    throw new IllegalStateException("Rows are not of type UpdateRowsEventData");
  }

  @SuppressWarnings("unchecked")
  public List<Serializable[]> getRows() {
    return (List<Serializable[]>) rows;
  }

  private long extractTableId() {
    if (event.getData() instanceof WriteRowsEventData data) {
      return data.getTableId();
    }
    if (event.getData() instanceof UpdateRowsEventData data) {
      return data.getTableId();
    }
    if (event.getData() instanceof DeleteRowsEventData data) {
      return data.getTableId();
    } else {
      throw new IllegalArgumentException(
          "Unsupported event type: " + event.getHeader().getEventType());
    }
  }

  private BitSet extractIncludedColumns() {
    if (event.getData() instanceof WriteRowsEventData data) {
      return data.getIncludedColumns();
    }
    if (event.getData() instanceof UpdateRowsEventData data) {
      return data.getIncludedColumns();
    }
    if (event.getData() instanceof DeleteRowsEventData data) {
      return data.getIncludedColumns();
    } else {
      throw new IllegalArgumentException(
          "Unsupported event type: " + event.getHeader().getEventType());
    }
  }

  private Object extractRows() {
    if (event.getData() instanceof WriteRowsEventData data) {
      return data.getRows();
    }
    if (event.getData() instanceof UpdateRowsEventData data) {
      return data.getRows();
    }
    if (event.getData() instanceof DeleteRowsEventData data) {
      return data.getRows();
    } else {
      throw new IllegalArgumentException(
          "Unsupported event type: " + event.getHeader().getEventType());
    }
  }
}
