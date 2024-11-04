package com.e205.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.e205.command.bag.payload.EmailStatus;
import com.e205.command.member.command.CheckVerificationNumberCommand;
import com.e205.command.member.command.CreateEmailTokenCommand;
import com.e205.command.member.command.CreateVerificationNumberCommand;
import com.e205.command.member.command.SendVerificationEmailCommand;
import com.e205.command.member.command.VerifyEmailToken;
import com.e205.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmailServiceIntegrationTest extends AbstractRedisTestContainer {

  private static final String TEST_EMAIL = "sungwoo166@gmail.com";
  private static final Integer TEST_USER_ID = 1;
  private static final String TEST_VERIFICATION_NUMBER = "123456";

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

  @DisplayName("이메일 인증 요청 시 토큰 생성 및 Redis 저장")
  @Test
  void testSendVerificationEmailAndTokenStorage() {
    // given
    String token = emailService.createAndStoreToken(
        new CreateEmailTokenCommand(TEST_USER_ID, TEST_EMAIL));

    // when
    emailService.sendVerificationEmail(new SendVerificationEmailCommand(TEST_EMAIL, token));

    // then
    String tokenKey = "token:" + token;
    String storedUserId = (String) redisTemplate.opsForHash().get(tokenKey, "userId");
    String storedEmail = (String) redisTemplate.opsForHash().get(tokenKey, "email");

    assertThat(storedUserId).isNotNull();
    assertThat(storedEmail).isNotNull();
    assertThat(storedUserId).isEqualTo(String.valueOf(TEST_USER_ID));
    assertThat(storedEmail).isEqualTo(TEST_EMAIL);
  }

  @DisplayName("토큰 검증 시 사용자 ID 반환 및 상태 업데이트 확인")
  @Test
  void testVerifyToken() {
    // given
    String token = emailService.createAndStoreToken(
        new CreateEmailTokenCommand(TEST_USER_ID, TEST_EMAIL));

    // when
    String verifiedUserId = emailService.verifyToken(new VerifyEmailToken(token));

    // then
    assertThat(verifiedUserId).isEqualTo(String.valueOf(TEST_USER_ID));

    memberRepository.findById(TEST_USER_ID).ifPresent(member -> {
      assertThat(member.getStatus()).isEqualTo(EmailStatus.VERIFIED);
    });

    String tokenKey = "token:" + token;
    assertThat(redisTemplate.opsForHash().entries(tokenKey)).isEmpty();
  }

  @DisplayName("이메일 인증 번호 생성 및 Redis 저장 확인")
  @Test
  void testCreateAndStoreVerificationNumber() {
    // when
    emailService.createAndStoreVerificationNumber(new CreateVerificationNumberCommand(TEST_EMAIL));

    // then
    String redisKey = "verification:" + TEST_EMAIL;
    String storedVerificationNumber = redisTemplate.opsForValue().get(redisKey);

    assertThat(storedVerificationNumber).isNotNull();
    assertThat(storedVerificationNumber).matches("\\d{6}");
  }

  @DisplayName("이메일 인증 번호 검증 성공")
  @Test
  void testCheckVerificationNumber() {
    // given
    emailService.createAndStoreVerificationNumber(new CreateVerificationNumberCommand(TEST_EMAIL));
    String redisKey = "verification:" + TEST_EMAIL;
    String storedVerificationNumber = redisTemplate.opsForValue().get(redisKey);

    // when
    emailService.checkVerificationNumber(
        new CheckVerificationNumberCommand(TEST_EMAIL, storedVerificationNumber));

    // then
  }

  @DisplayName("잘못된 인증 번호 검증 실패")
  @Test
  void testCheckInvalidVerificationNumber() {
    // given
    emailService.createAndStoreVerificationNumber(new CreateVerificationNumberCommand(TEST_EMAIL));
    String invalidVerificationNumber = "654321";

    // when & then
    assertThrows(IllegalArgumentException.class, () ->
        emailService.checkVerificationNumber(
            new CheckVerificationNumberCommand(TEST_EMAIL, invalidVerificationNumber)));
  }

  @DisplayName("인증 번호 만료 또는 존재하지 않을 때 검증 실패")
  @Test
  void testCheckExpiredOrNonexistentVerificationNumber() {
    // when & then
    assertThrows(IllegalArgumentException.class, () ->
        emailService.checkVerificationNumber(
            new CheckVerificationNumberCommand(TEST_EMAIL, TEST_VERIFICATION_NUMBER)));
  }
}
