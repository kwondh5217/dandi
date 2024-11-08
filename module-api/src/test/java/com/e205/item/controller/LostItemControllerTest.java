package com.e205.item.controller;

import static java.time.LocalDateTime.now;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e205.auth.dto.MemberDetails;
import com.e205.exception.ExceptionLoader;
import com.e205.item.dto.LostItemCreateRequest;
import com.e205.item.dto.LostItemResponse;
import com.e205.item.service.LostItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(LostItemController.class)
@Import(ExceptionLoader.class)
public class LostItemControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private LostItemService lostItemService;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    MemberDetails userDetails = new MemberDetails(1, "", "");

    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
  }

  @DisplayName("분실물 생성 요청 테스트")
  @Test
  void createLostItem() throws Exception {
    // given
    List<String> images = Stream.generate(this::generateImageString).limit(3).toList();
    LostItemCreateRequest request = LostItemCreateRequest.builder().situationDesc("상황묘사")
        .itemDesc("물건묘사").images(images).startRoute(1).endRoute(2).lostAt(now()).build();

    // when
    ResultActions action = mockMvc.perform(post("/losts").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    action.andExpect(status().isCreated());
    verify(lostItemService).createLostItem(1, request);
  }

  @DisplayName("분실물 종료 요청 테스트")
  @Test
  void finishLostItem() throws Exception {
    // given
    int lostId = 1;

    // when
    ResultActions action = mockMvc.perform(
        delete("/losts/{lostId}", lostId).contentType(MediaType.APPLICATION_JSON));

    // then
    action.andExpect(status().isOk());
    verify(lostItemService).finishLostItem(1, lostId);
  }

  @DisplayName("분실물 상세 조회 요청 테스트")
  @Test
  void getLostItem() throws Exception {
    // given
    int lostItemId = 1;
    LostItemResponse response = new LostItemResponse(lostItemId, "검정색 지갑", "집에 없음", List.of(), now());

    given(lostItemService.getLostItem(1, lostItemId)).willReturn(response);

    // when
    ResultActions action = mockMvc.perform(
        get("/losts/{lostItemId}", lostItemId).contentType(MediaType.APPLICATION_JSON));

    // then
    action.andExpect(status().isOk()).andExpect(jsonPath("$.itemDescription").value("집에 없음"))
        .andExpect(jsonPath("$.situationDescription").value("검정색 지갑"));
    verify(lostItemService).getLostItem(1, lostItemId);
  }

  private String generateImageString() {
    return String.format("%s.%s", UUID.randomUUID(), "png");
  }
}