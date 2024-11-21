package com.e205.cdc;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.shyiko.mysql.binlog.event.EventType;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MySQLBinlogReader implements BinlogReader, ApplicationRunner {

  private final DataSourceProperties dataSourceProperties;
  private final BinlogPositionTracker tracker;
  private final BinaryLogClient client;
  private final ExecutorService executorService;
  private final CDCEventPublisher eventPublisher;

  public MySQLBinlogReader(DataSourceProperties dataSourceProperties,
      BinlogPositionTracker tracker, CDCEventPublisher eventPublisher) {
    this.dataSourceProperties = dataSourceProperties;
    this.tracker = tracker;
    this.eventPublisher = eventPublisher;

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
    this.executorService.submit(this::start);
  }

  @Override
  public void start() {
    this.client.registerEventListener(this::processEvent);
    try {
      this.client.connect();
    } catch (IOException e) {
      log.error("Failed to connect to Binlog server", e);
    }
  }

  private void processEvent(Event event) {
    EventHeaderV4 header = event.getHeader();
    String currentBinlog = this.client.getBinlogFilename();
    long currentPosition = header.getPosition();
    EventType eventType = header.getEventType();
    this.tracker.updatePosition(currentBinlog, currentPosition);

    switch (eventType) {
      case TABLE_MAP -> this.eventPublisher.saveTableInfo(event);
      case WRITE_ROWS, EXT_WRITE_ROWS,
           UPDATE_ROWS, EXT_UPDATE_ROWS,
           DELETE_ROWS, EXT_DELETE_ROWS ->
          this.eventPublisher.processRowEvent(event, eventType);
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
      client.disconnect();
    } catch (IOException e) {
      log.error("Error disconnecting from Binlog server", e);
      throw new RuntimeException("Failed to disconnect from MySQL Binlog", e);
    }
  }
}
