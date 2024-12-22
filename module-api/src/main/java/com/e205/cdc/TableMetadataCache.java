package com.e205.cdc;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TableMetadataCache {

  private final JdbcTemplate jdbcTemplate;
  private final Map<String, List<ColumnInfo>> tableColumnCache;
  private final Map<Long, String> tableIdToNameMap;

  public TableMetadataCache(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.tableColumnCache = new HashMap<>();
    this.tableIdToNameMap = new HashMap<>();
  }

  @PostConstruct
  public void init() throws SQLException {
    try (Connection connection = jdbcTemplate.getDataSource().getConnection();
        ResultSet tables = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"})) {

      while (tables.next()) {
        String tableName = tables.getString("TABLE_NAME");

        try (ResultSet columns = connection.getMetaData().getColumns(null, null, tableName, "%")) {
          List<ColumnInfo> columnInfos = new ArrayList<>();
          while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnType = columns.getString("TYPE_NAME");
            boolean isPrimaryKey = isPrimaryKey(connection.getMetaData(), tableName, columnName);

            columnInfos.add(new ColumnInfo(columnName, columnType, isPrimaryKey));
          }
          tableColumnCache.put(tableName, columnInfos);
        }
      }
    }
  }
  public ColumnInfo getColumnInfo(String tableName, int index) {
    List<ColumnInfo> columnInfos = tableColumnCache.get(tableName);

    if (columnInfos.size() <= index) {
      throw new ArrayIndexOutOfBoundsException(index);
    }

    return columnInfos.get(index);
  }

  public String getTableName(long tableId) {
    String tableName = tableIdToNameMap.get(tableId);
    if (tableName == null) {
      throw new NoSuchElementException("No table found for tableId: " + tableId);
    }
    return tableName;
  }

  public void saveTableInfo(Event event) {
    TableMapEventData data = event.getData();
    tableIdToNameMap.put(data.getTableId(), data.getTable());
  }

  private boolean isPrimaryKey(DatabaseMetaData metaData, String tableName,
      String columnName) {
    try {
      ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
      while (primaryKeys.next()) {
        String pkColumn = primaryKeys.getString("COLUMN_NAME");
        if (columnName.equals(pkColumn)) {
          return true;
        }
      }
    } catch (Exception e) {
      // Ignore or log if necessary
    }
    return false;
  }

  public record ColumnInfo(String name, String type, boolean primaryKey) {

  }
}
