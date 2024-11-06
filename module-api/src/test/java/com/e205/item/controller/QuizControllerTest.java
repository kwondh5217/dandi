package com.e205.item.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e205.auth.dto.MemberDetails;
import com.e205.auth.helper.AuthHelper;
import com.e205.domain.member.entity.Member;
import com.e205.item.dto.QuizOptionResponse;
import com.e205.item.dto.QuizResponse;
import com.e205.item.dto.QuizSubmitRequest;
import com.e205.item.service.QuizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(QuizController.class)
public class QuizControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private QuizService quizService;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    MemberDetails userDetails = new MemberDetails(1, "", "");

    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
    );
  }

  @DisplayName("퀴즈 조회 로직 테스트")
  @Test
  void getQuiz() throws Exception {
    // given
    int foundId = 1;
    Principal principal = () -> "1";
    List<QuizOptionResponse> options = Stream.generate(
        () -> new QuizOptionResponse(UUID.randomUUID().toString(),
            RandomString.make())).limit(4).toList();
    QuizResponse response = new QuizResponse(1, foundId, options);

    given(quizService.getQuiz(1, foundId)).willReturn(response);

    // when, then
    mockMvc.perform(get("/founds/{foundId}/quiz", foundId)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk());

    verify(quizService).getQuiz(1, foundId);
  }

  @DisplayName("퀴즈 제출 로직 테스트")
  @Test
  void submitQuiz() throws Exception {
    // given
    int quizId = 1;
    QuizSubmitRequest request = new QuizSubmitRequest(UUID.randomUUID() + ".png");

    given(quizService.submitQuiz(1, quizId, request)).willReturn(true);

    // when, then
    mockMvc.perform(post("/founds/{foundId}/quiz/{quizId}", 1, quizId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());

    verify(quizService).submitQuiz(1, quizId, request);
  }
}