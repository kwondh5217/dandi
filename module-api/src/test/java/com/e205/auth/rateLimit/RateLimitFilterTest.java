package com.e205.auth.rateLimit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class RateLimitFilterTest {

  @Autowired
  private MockMvc mockMvc;

  @WithMockUser(username = "1")
  @Test
  public void givenFrequentRequests_whenRateLimitExceeded_thenTooManyRequests() throws Exception {
    String testUri = "/noti/test";

    int successCount = 0;
    int failCount = 0;
    for (int i = 0; i < 3; i++) {
      MvcResult result = mockMvc.perform(get(testUri)).andReturn();
      if (result.getResponse().getStatus() == HttpStatus.OK.value()) {
        successCount++;
      } else if (result.getResponse().getStatus() == HttpStatus.TOO_MANY_REQUESTS.value()) {
        failCount++;
      }
    }

    assertThat(successCount).isEqualTo(1);
    assertThat(failCount).isEqualTo(2);
  }
}
