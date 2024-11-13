package com.e205.member.controller;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e205.auth.dto.MemberDetails;
import com.e205.entity.CommentNotification;
import com.e205.entity.FoundItemNotification;
import com.e205.entity.LostItemNotification;
import com.e205.entity.Notification;
import com.e205.entity.RouteNotification;
import com.e205.exception.ExceptionLoader;
import com.e205.exception.GlobalExceptionHandler;
import com.e205.service.NotiCommandService;
import com.e205.service.NotiQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;

@Import({GlobalExceptionHandler.class, ExceptionLoader.class})
@WebMvcTest(controllers = NotificationController.class)
class NotificationControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private NotiQueryService queryService;
  @MockBean
  private NotiCommandService notiCommandService;
  @MockBean
  private AuthenticationPrincipalArgumentResolver resolver;

  @WithMockUser(username = "1")
  @Test
  void findNotifications() throws Exception {
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(new MemberDetails(1), null, List.of())
    );
    Integer resourceId = 0;
    List<String> types = List.of("comment", "foundItem", "lostItem", "route");

    List<Notification> mockNotifications = List.of(
        createLostItemNotification(1, 1, "Lost Item Title", 101),
        createFoundItemNotification(2, 1, "Found Item Title", 102),
        createCommentNotification(3, 1, "Comment Title", 201),
        createRouteNotification(4, 1, "Route Title", 301)
    );
    given(queryService.queryNotificationWithCursor(any())).willReturn(mockNotifications);

    this.mockMvc.perform(get("/noti")
        .param("resourceId", resourceId.toString())
        .param("types", objectMapper.writeValueAsString(types))
    ).andExpect(status().isOk());
  }

  @WithMockUser(username = "1")
  @Test
  void deleteNotifications() throws Exception {
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(new MemberDetails(1), null, List.of())
    );
    List<Integer> notificationIds = List.of(1, 2, 3, 4, 5);

    this.mockMvc.perform(delete("/noti")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(this.objectMapper.writeValueAsString(notificationIds)))
        .andExpect(status().isOk());
    verify(this.notiCommandService).deleteNotifications(any());
  }

  private LostItemNotification createLostItemNotification(Integer id, Integer memberId,
      String title, Integer lostItemId) {
    LostItemNotification notification = new LostItemNotification();
    notification.setId(id);
    notification.setMemberId(memberId);
    notification.setTitle(title);
    notification.setCreatedAt(LocalDateTime.now());
    notification.setLostItemId(lostItemId);
    return notification;
  }

  private FoundItemNotification createFoundItemNotification(Integer id, Integer memberId,
      String title, Integer foundItemId) {
    FoundItemNotification notification = new FoundItemNotification();
    notification.setId(id);
    notification.setMemberId(memberId);
    notification.setTitle(title);
    notification.setCreatedAt(LocalDateTime.now());
    notification.setFoundItemId(foundItemId);
    return notification;
  }

  private CommentNotification createCommentNotification(Integer id, Integer memberId,
      String title, Integer commentId) {
    CommentNotification notification = new CommentNotification();
    notification.setId(id);
    notification.setMemberId(memberId);
    notification.setTitle(title);
    notification.setCreatedAt(LocalDateTime.now());
    notification.setCommentId(commentId);
    return notification;
  }

  private RouteNotification createRouteNotification(Integer id, Integer memberId,
      String title, Integer routeId) {
    RouteNotification notification = new RouteNotification();
    notification.setId(id);
    notification.setMemberId(memberId);
    notification.setTitle(title);
    notification.setCreatedAt(LocalDateTime.now());
    notification.setRouteId(routeId);
    return notification;
  }
}