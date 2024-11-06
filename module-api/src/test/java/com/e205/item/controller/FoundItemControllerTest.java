package com.e205.item.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e205.FoundItemType;
import com.e205.auth.dto.MemberDetails;
import com.e205.domain.member.entity.Member;
import com.e205.item.dto.FoundItemCreateRequest;
import com.e205.item.dto.FoundItemResponse;
import com.e205.item.dto.Point;
import com.e205.item.service.FoundItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(FoundItemController.class)
public class FoundItemControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FoundItemService foundItemService;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    MemberDetails userDetails = new MemberDetails(1, "", "");

    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
    );
  }

  @DisplayName("습득물 저장 컨트롤러 테스트")
  @Test
  void createFoundItem() throws Exception {
    // given
    FoundItemCreateRequest request = new FoundItemCreateRequest(FoundItemType.OTHER,
        new Point(39.329034, 128.349023), LocalDateTime.now(), "프론트 데스크에 맡겨두었어요.", "빨간색 우산입니다.");

    MockMultipartFile foundItemRequest = new MockMultipartFile("foundItemRequest",
        "foundItemRequest.json", "application/json",
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

    MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg",
        "dummy image content".getBytes());

    // when
    ResultActions action = mockMvc.perform(
        multipart("/founds")
            .file(foundItemRequest)
            .file(image)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .characterEncoding("UTF-8"));

    // then
    action.andExpect(status().isCreated());
    verify(foundItemService).save(1, request, image);
  }

  @DisplayName("습득물 상세 조회 테스트")
  @Test
  void getFoundItem() throws Exception {
    // given
    int foundId = 1;
    Principal principal = () -> "1";
    FoundItemResponse response = new FoundItemResponse(1, 1, new Point(39.329034, 128.349023),
        "프론트 데스크에 맡겨두었어요.", "FOUND", FoundItemType.OTHER, "djfalksfda.png");

    given(foundItemService.get(1, foundId)).willReturn(response);

    // when
    ResultActions action = mockMvc.perform(get("/founds/{foundId}", foundId).principal(principal)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    action.andExpect(status().isOk()).andExpect(jsonPath("$.type").value("OTHER"))
        .andExpect(jsonPath("$.foundLocation.lat").value(39.329034))
        .andExpect(jsonPath("$.foundLocation.lon").value(128.349023))
        .andExpect(jsonPath("$.description").value("프론트 데스크에 맡겨두었어요."))
        .andExpect(jsonPath("$.savePoint").value("FOUND"));

    verify(foundItemService).get(1, foundId);
  }

  @DisplayName("습득물 삭제 로직 테스트")
  @Test
  void deleteFoundItem() throws Exception {
    // given
    int foundId = 1;
    Principal principal = () -> "1";

    // when
    ResultActions action = mockMvc.perform(delete("/founds/{foundId}", foundId).principal(principal)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    action.andExpect(status().isNoContent());
    verify(foundItemService).delete(1, foundId);
  }
}