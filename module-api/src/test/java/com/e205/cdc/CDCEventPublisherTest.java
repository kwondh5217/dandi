//package com.e205.cdc;
//
//import com.e205.cdc.BinlogMappingUtils.NotificationInsertEvent;
//import com.e205.event.FoundItemSaveEvent;
//import com.e205.event.LostItemSaveEvent;
//import com.github.shyiko.mysql.binlog.event.Event;
//import com.github.shyiko.mysql.binlog.event.EventType;
//import java.io.Serializable;
//import java.util.Collections;
//import java.util.List;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.context.ApplicationEventPublisher;
//
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class CDCEventPublisherTest {
//
//  @Mock
//  private TableMetadataCache tableMetadataCache;
//
//  @Mock
//  private ApplicationEventPublisher eventPublisher;
//
//  private CDCEventPublisher cdcEventPublisher;
//
//  @BeforeEach
//  void setUp() {
//    MockitoAnnotations.openMocks(this);
//    cdcEventPublisher = new CDCEventPublisher(tableMetadataCache, eventPublisher);
//  }
//
//  @Test
//  void testPublishNotificationInsertEvent() {
//    // Arrange
//    Event mockEvent = mock(Event.class);
//    RowsEventData mockRowsEventData = mock(RowsEventData.class);
//
//    // Mock the behavior of RowsEventData for the Event
//    doReturn(mockRowsEventData).when(mockEvent).getData();
//    when(tableMetadataCache.getTableName(anyLong())).thenReturn("Notification");
//
//    // Mock the rows data
//    List<Serializable[]> rows = Collections.singletonList(new Serializable[]{"1", "Test Title", "Test Body"});
//    when(mockRowsEventData.getRows()).thenReturn(rows);
//
//    Map<String, Object> mockRow = Map.of("memberId", 1, "title", "Test Title", "body", "Test Body");
//    doReturn(mockRow).when(cdcEventPublisher).mapRowData(any(), eq("Notification"));
//
//    // Act
//    cdcEventPublisher.processRowEvent(mockEvent, EventType.WRITE_ROWS);
//
//    // Assert
//    ArgumentCaptor<NotificationInsertEvent> captor = ArgumentCaptor.forClass(NotificationInsertEvent.class);
//    verify(eventPublisher, times(1)).publishEvent(captor.capture());
//    NotificationInsertEvent publishedEvent = captor.getValue();
//    assertEquals(1, publishedEvent.memberId());
//    assertEquals("Test Title", publishedEvent.title());
//    assertEquals("Test Body", publishedEvent.body());
//  }
//
////  @Test
////  void testPublishFoundItemSaveEvent() {
////    // Arrange
////    Event mockEvent = mock(Event.class);
////    RowsEventData mockRowsEventData = mock(RowsEventData.class);
////    when(tableMetadataCache.getTableName(anyLong())).thenReturn("FoundItem");
////    when(mockRowsEventData.getRows()).thenReturn(
////        (List<Serializable[]>) List.of(new Serializable[]{"1", "Item Description"}));
////
////    Map<String, Object> mockRow = Map.of("id", 1, "description", "Item Description");
////    doReturn(mockRow).when(cdcEventPublisher).mapRowData(any(), eq("FoundItem"));
////
////    // Act
////    cdcEventPublisher.processRowEvent(mockEvent, EventType.WRITE_ROWS);
////
////    // Assert
////    ArgumentCaptor<FoundItemSaveEvent> captor = ArgumentCaptor.forClass(FoundItemSaveEvent.class);
////    verify(eventPublisher, times(1)).publishEvent(captor.capture());
////    FoundItemSaveEvent publishedEvent = captor.getValue();
////    assertEquals(1, publishedEvent.saved().id());
////    assertEquals("Item Description", publishedEvent.saved().description());
////  }
////
////  @Test
////  void testPublishLostItemSaveEvent() {
////    // Arrange
////    Event mockEvent = mock(Event.class);
////    RowsEventData mockRowsEventData = mock(RowsEventData.class);
////    when(tableMetadataCache.getTableName(anyLong())).thenReturn("LostItem");
////    when(mockRowsEventData.getRows()).thenReturn(
////        (List<Serializable[]>) List.of(new Serializable[]{"1", "Lost Description"}));
////
////    Map<String, Object> mockRow = Map.of("id", 1, "description", "Lost Description");
////    doReturn(mockRow).when(cdcEventPublisher).mapRowData(any(), eq("LostItem"));
////
////    // Act
////    cdcEventPublisher.processRowEvent(mockEvent, EventType.WRITE_ROWS);
////
////    // Assert
////    ArgumentCaptor<LostItemSaveEvent> captor = ArgumentCaptor.forClass(LostItemSaveEvent.class);
////    verify(eventPublisher, times(1)).publishEvent(captor.capture());
////    LostItemSaveEvent publishedEvent = captor.getValue();
////    assertEquals(1, publishedEvent.saved().id());
////    assertEquals("Lost Description", publishedEvent.saved().situationDescription());
////  }
//}
