package com.e205.cdc;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TableMetadataCache {

  private final JdbcTemplate jdbcTemplate;
  private final Map<String, List<String>> tableColumnCache;
  private final Map<Long, String> tableIdToNameMap;
  private Set<String> tables = Set.of("FountItem", "LostItem", "Notification");

  public TableMetadataCache(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.tableColumnCache = new HashMap<>();
    this.tableIdToNameMap = new HashMap<>();
  }

  @PostConstruct
  public void init() throws SQLException {
    try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
      ResultSet resultSet = connection.getMetaData()
          .getTables(null, null, "%", new String[]{"TABLE"});
      while (resultSet.next()) {
        String tableName = resultSet.getString("TABLE_NAME");

        if (!tables.contains(tableName)) {
          continue;
        }

        ResultSet columns = connection.getMetaData()
            .getColumns(null, null, tableName, "%");
        List<String> columnInfos = new ArrayList<>();
        while (columns.next()) {
          String columnName = columns.getString("COLUMN_NAME");
          columnInfos.add(columnName);
        }
        tableColumnCache.put(tableName, columnInfos);
      }
    }
  }

  public String getColumnInfo(String tableName, int index) {
    List<String> columnInfos = tableColumnCache.get(tableName);

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

  public boolean isEnabled(long tableId) {
    return this.tables.contains(tableIdToNameMap.get(tableId));
  }
}
