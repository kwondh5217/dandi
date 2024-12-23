package com.e205.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.command.member.command.ChangePasswordWithVerifNumber;
import com.e205.command.member.command.RegisterMemberCommand;
import com.e205.command.member.command.MemberVerificationLinkCommand;
import com.e205.command.member.command.VerifyEmailAndRegisterCommand;
import com.e205.command.member.payload.EmailStatus;
import com.e205.command.member.service.MemberCommandService;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import java.util.Map;
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
class MemberCommandServiceIntegrationTest extends AbstractRedisTestContainer {

  @Autowired
  private MemberCommandService memberCommandService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private BagRepository bagRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Autowired
  private JavaMailSender mailSender;

  @BeforeEach
  void setUp() {
    redisTemplate.getConnectionFactory().getConnection().flushAll();
    memberRepository.deleteAll();
  }

  @DisplayName("인증번호로 비밀번호 변경 테스트")
  @Test
  void testChangePasswordWithVerificationNumber() {
    // Given
    String email = "testuser@example.com";
    String originalPassword = "password123";
    String newPassword = "newPassword456";
    String verificationNumber = "123456";

    Member member = Member.builder()
        .email(email)
        .password(originalPassword)
        .nickname("testUser")
        .status(EmailStatus.PENDING)
        .bagId(1)
        .build();
    memberRepository.save(member);

    String redisKey = "verification:" + email;
    redisTemplate.opsForValue().set(redisKey, verificationNumber);

    // When
    memberCommandService.changePasswordWithVerificationNumber(
        new ChangePasswordWithVerifNumber(email, verificationNumber, newPassword));

    // Then
    Member updatedMember = memberRepository.findByEmail(email).orElse(null);
    assertThat(updatedMember).isNotNull();
    assertThat(newPassword).isEqualTo(updatedMember.getPassword());
    assertThat(redisTemplate.opsForValue().get(redisKey)).isNull();
  }

  @DisplayName("이메일 인증 요청 테스트")
  @Test
  void testRequestEmailVerification() {
    // Given
    String email = "sungwoo166@gmail.com";
    Integer userId = 1;

    Member member = Member.builder()
        .id(userId)
        .email(email)
        .password("password123")
        .nickname("testUser")
        .status(EmailStatus.PENDING)
        .bagId(1)
        .build();
    memberRepository.save(member);

    // When
    memberCommandService.requestEmailVerification(
        new MemberVerificationLinkCommand(email));

    // Then
    String redisKey = "verifyEmail:" + email;
    String storedToken = (String) redisTemplate.opsForHash().get(redisKey, "token");
    assertThat(storedToken).isNotNull();
  }

  @DisplayName("회원가입 요청 테스트")
  @Test
  void testRegisterMember() {
    // Given
    String email = "newuser@example.com";
    String password = "password123";
    String nickname = "newUser";

    RegisterMemberCommand command = new RegisterMemberCommand(email, password, nickname);

    String redisKey = "registration:" + email;

    // When
    memberCommandService.registerMember(command); // 토큰을 반환하도록 수정
    // Then
    Map<Object, Object> registrationData = redisTemplate.opsForHash().entries(redisKey);
    assertThat(registrationData).isNotEmpty();
    assertThat(registrationData).containsEntry("password", password);
    assertThat(registrationData).containsEntry("email", email);
    assertThat(registrationData).containsEntry("nickname", nickname);
  }

  @DisplayName("이메일 인증 후")
  @Test
  void testVerifyEmailAndCompleteRegistration() {
    // Given
    String email = "newuser@example.com";
    String token = "verification-token";
    String password = "password123";
    String nickname = "newUser";

    // Redis에 임시 회원 정보와 토큰을 저장
    String registrationKey = "registration:" + email;
    redisTemplate.opsForHash().put(registrationKey, "password", password);
    redisTemplate.opsForHash().put(registrationKey, "nickname", nickname);
    redisTemplate.opsForHash().put(registrationKey, "authenticated", "false");  // 초기값은 "false"

    // 토큰 저장 (변경된 형식)
    String tokenKey = "verifyEmail:" + email;
    redisTemplate.opsForHash().put(tokenKey, "token", token);

    // When: 이메일 인증 완료
    memberCommandService.verifyEmailAndCompleteRegistration(
        new VerifyEmailAndRegisterCommand(email, token));

    // Then
    String isAuthenticated = (String) redisTemplate.opsForHash()
        .get(registrationKey, "authenticated");
    assertThat(isAuthenticated).isEqualTo("true");
    String storedPassword = (String) redisTemplate.opsForHash().get(registrationKey, "password");
    String storedNickname = (String) redisTemplate.opsForHash().get(registrationKey, "nickname");
    assertThat(storedPassword).isEqualTo(password);
    assertThat(storedNickname).isEqualTo(nickname);
  }
}