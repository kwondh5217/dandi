package com.e205.item.controller;

import static java.time.LocalDateTime.now;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e205.item.dto.LostItemCreateRequest;
import com.e205.item.dto.LostItemResponse;
import com.e205.item.service.LostItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(LostItemController.class)
public class LostItemControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private LostItemService lostItemService;

  @Autowired
  private ObjectMapper objectMapper;

  @DisplayName("분실물 생성 요청 테스트")
  @Test
  void createLostItem() throws Exception {
    // given
    LostItemCreateRequest request = new LostItemCreateRequest("집에 와서 보니 지갑이 없어요", "검정색 지갑", 90, 93,
        now());

    MockMultipartFile lostItemRequest = new MockMultipartFile("lostItemRequest", "lostItemRequest",
        MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

    MockMultipartFile image1 = new MockMultipartFile("images", "image1.jpg",
        MediaType.IMAGE_JPEG_VALUE, "dummy image content".getBytes());

    MockMultipartFile image2 = new MockMultipartFile("images", "image2.jpg",
        MediaType.IMAGE_JPEG_VALUE, "dummy image content".getBytes());

    Principal principal = () -> "1";

    // when
    ResultActions action = mockMvc.perform(
        multipart("/losts")
            .file(lostItemRequest)
            .file(image1)
            .file(image2)
            .principal(principal)
            .contentType(MediaType.MULTIPART_FORM_DATA));

    // then
    action.andExpect(status().isCreated());
    verify(lostItemService).createLostItem(1, request, List.of(image1, image2));
  }

  @DisplayName("분실물 종료 요청 테스트")
  @Test
  void finishLostItem() throws Exception {
    // given
    int lostId = 1;
    Principal principal = () -> "1";

    // when
    ResultActions action = mockMvc.perform(put("/losts/{lostId}", lostId)
        .principal(principal)
        .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    action.andExpect(status().isOk());
    verify(lostItemService).finishLostItem(1, lostId);
  }

  @DisplayName("분실물 상세 조회 요청 테스트")
  @Test
  void getLostItem() throws Exception {
    // given
    int lostItemId = 1;
    Principal principal = () -> "1";
    LostItemResponse response = new LostItemResponse("검정색 지갑", "집에 없음", List.of(), now());

    given(lostItemService.getLostItem(1, lostItemId)).willReturn(response);

    // when
    ResultActions action = mockMvc.perform(get("/losts/{lostItemId}", lostItemId)
        .principal(principal)
        .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    action
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.itemDescription").value("집에 없음"))
        .andExpect(jsonPath("$.situationDescription").value("검정색 지갑"));
    verify(lostItemService).getLostItem(1, lostItemId);
  }
}