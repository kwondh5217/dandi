package com.e205.cdc;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.metamodel.spi.MappingMetamodelImplementor;
import org.hibernate.metamodel.spi.RuntimeMetamodelsImplementor;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ClassInfoCache {

  private final Map<String, Class<?>> classMap;
  private final Map<String, List<String>> columnMap;
  private final EntityManagerFactory emf;
  private final JdbcTemplate jdbcTemplate;
  private final Map<String, List<ColumnInfo>> tableColumnCache;

  public ClassInfoCache(EntityManagerFactory emf, JdbcTemplate jdbcTemplate) {
    this.emf = emf;
    this.jdbcTemplate = jdbcTemplate;
    this.classMap = new HashMap<>();
    this.columnMap = new HashMap<>();
    this.tableColumnCache = new HashMap<>();
  }

  @PostConstruct
  public void init() {
    try {
      jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
        java.sql.DatabaseMetaData metaData = connection.getMetaData();

        ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
        while (tables.next()) {
          String tableName = tables.getString("TABLE_NAME");

          ResultSet columns = metaData.getColumns(null, null, tableName, "%");
          List<ColumnInfo> columnInfos = new ArrayList<>();
          while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnType = columns.getString("TYPE_NAME");
            boolean isPrimaryKey = isPrimaryKey(metaData, tableName, columnName);

            columnInfos.add(new ColumnInfo(columnName, columnType, isPrimaryKey));
          }
          tableColumnCache.put(tableName, columnInfos);
        }
        return null;
      });
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize ClassInfoCache", e);
    }



    SessionFactoryImpl sessionFactory = this.emf.unwrap(SessionFactoryImpl.class);
    RuntimeMetamodelsImplementor runtimeMetamodels = sessionFactory.getRuntimeMetamodels();
    MappingMetamodelImplementor mappingMetamodel = runtimeMetamodels.getMappingMetamodel();

    mappingMetamodel.forEachEntityDescriptor(entityPersister -> {
      String[] split = entityPersister.getEntityName().split("\\.");
      String entityName = split[split.length - 1];
      Class<?> mappedClass = entityPersister.getMappedClass();
      this.classMap.put(entityName, mappedClass);

      List<String> columnNames = new ArrayList<>(List.of(entityPersister.getIdentifierColumnNames()));
      columnNames.addAll(List.of(entityPersister.getPropertyNames()));
      this.columnMap.put(entityName, columnNames);
    });
  }


  public Class<?> getMappedClass(String entityName) {
    Class<?> aClass = this.classMap.get(entityName);
    if (aClass == null) {
      throw new EntityNotFoundException("엔티티를 찾을 수 없습니다: " + entityName);
    }
    return aClass;
  }

  public String getColumnNameFromSchema(String tableName, int columnIndex) {
    List<String> columns = this.columnMap.get(tableName);
    if (columns == null) {
      throw new EntityNotFoundException("테이블 정보를 찾을 수 없습니다: " + tableName);
    }
    if (columnIndex < 0 || columnIndex >= columns.size()) {
      throw new IllegalArgumentException("컬럼 인덱스가 잘못되었습니다: " + columnIndex);
    }
    return columns.get(columnIndex);
  }

  private boolean isPrimaryKey(DatabaseMetaData metaData, String tableName, String columnName) {
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

  public List<ColumnInfo> getTableColumns(String tableName) {
    List<ColumnInfo> columns = tableColumnCache.get(tableName);
    if (columns == null) {
      throw new IllegalArgumentException("Table not found: " + tableName);
    }
    return columns;
  }

  public List<String> getColumnNames(String tableName) {
    List<ColumnInfo> columns = getTableColumns(tableName);
    List<String> columnNames = new ArrayList<>();
    for (ColumnInfo columnInfo : columns) {
      columnNames.add(columnInfo.getName());
    }
    return columnNames;
  }

  public List<String> getPrimaryKeyColumns(String tableName) {
    List<ColumnInfo> columns = getTableColumns(tableName);
    List<String> primaryKeys = new ArrayList<>();
    for (ColumnInfo columnInfo : columns) {
      if (columnInfo.isPrimaryKey()) {
        primaryKeys.add(columnInfo.getName());
      }
    }
    return primaryKeys;
  }

  public static class ColumnInfo {
    private final String name;
    private final String type;
    private final boolean primaryKey;

    public ColumnInfo(String name, String type, boolean primaryKey) {
      this.name = name;
      this.type = type;
      this.primaryKey = primaryKey;
    }

    public String getName() {
      return name;
    }

    public String getType() {
      return type;
    }

    public boolean isPrimaryKey() {
      return primaryKey;
    }
  }
}
