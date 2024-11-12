package com.e205.member.controller;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e205.auth.jwt.JwtProvider;
import com.e205.auth.jwt.handler.JwtAuthenticationEntryPoint;
import com.e205.auth.jwt.repository.JwtRepository;
import com.e205.config.SecurityConfig;
import com.e205.exception.ExceptionLoader;
import com.e205.exception.GlobalExceptionHandler;
import com.e205.member.service.RecoveryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class,
    AuthenticationConfiguration.class, JwtProvider.class, GlobalExceptionHandler.class,
    ExceptionLoader.class})
@WebMvcTest(controllers = {EventRecoveryController.class})
class EventRecoveryControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private RecoveryService recoveryService;
  @MockBean
  private JwtRepository jwtRepository;

  @Test
  void recoveryEvent() throws Exception {
    doNothing().when(recoveryService).recovery(any());

    this.mockMvc.perform(put("/recovery/{eventId}", "1"))
        .andExpect(status().isOk());
  }
}