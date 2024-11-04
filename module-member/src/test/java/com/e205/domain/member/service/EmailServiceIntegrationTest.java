package com.e205.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.domain.member.entity.EmailStatus;
import com.e205.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmailServiceIntegrationTest {

  private static final String TEST_EMAIL = "sungwoo166@gmail.com";
  private static final Integer TEST_USER_ID = 1;

  @Autowired
  private EmailCommandServiceDefault emailService;

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Autowired
  private MemberRepository memberRepository;

  @BeforeEach
  void setUp() {
    redisTemplate.getConnectionFactory().getConnection().flushAll();
  }

  @Test
  @DisplayName("이메일 인증 요청 시 토큰 생성 및 Redis 저장")
  void testSendVerificationEmailAndTokenStorage() {
    // given
    String token = emailService.createAndStoreToken(TEST_USER_ID, TEST_EMAIL);

    // when
    emailService.sendVerificationEmail(TEST_EMAIL, token);

    // then
    String tokenKey = "token:" + token;
    String storedUserId = (String) redisTemplate.opsForHash().get(tokenKey, "userId");
    String storedEmail = (String) redisTemplate.opsForHash().get(tokenKey, "email");

    assertThat(storedUserId).isNotNull();
    assertThat(storedEmail).isNotNull();
    assertThat(storedUserId).isEqualTo(String.valueOf(TEST_USER_ID));
    assertThat(storedEmail).isEqualTo(TEST_EMAIL);
  }

  @Test
  @DisplayName("토큰 검증 시 사용자 ID 반환 및 상태 업데이트 확인")
  void testVerifyToken() {
    // given
    String token = emailService.createAndStoreToken(TEST_USER_ID, TEST_EMAIL);

    // when
    String verifiedUserId = emailService.verifyToken(token);

    // then
    assertThat(verifiedUserId).isEqualTo(String.valueOf(TEST_USER_ID));

    memberRepository.findById(TEST_USER_ID).ifPresent(member -> {
      assertThat(member.getStatus()).isEqualTo(EmailStatus.VERIFIED);
    });

    String tokenKey = "token:" + token;
    assertThat(redisTemplate.opsForHash().entries(tokenKey)).isEmpty();
  }
}
