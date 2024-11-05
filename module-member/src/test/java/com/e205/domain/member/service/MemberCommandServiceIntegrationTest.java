package com.e205.domain.member.service;

import com.e205.command.bag.payload.EmailStatus;
import com.e205.command.member.command.ChangePasswordWithVerifNumber;
import com.e205.command.member.command.RequestEmailVerificationCommand;
import com.e205.command.member.service.MemberCommandService;
import com.e205.command.member.command.RegisterMemberCommand;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

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

  @DisplayName("회원 등록 및 이메일 인증 요청 테스트")
  @Test
  void testRegisterMemberAndRequestEmailVerification() {
    // Given
    String email = "testuser@example.com";
    String password = "password123";
    String nickname = "testUser";
    RegisterMemberCommand command = new RegisterMemberCommand(email, password, nickname);

    // When
    memberCommandService.registerMember(command);

    // Then
    Member savedMember = memberRepository.findById(1).orElse(null);
    assertThat(savedMember).isNotNull();
    assertThat(savedMember.getEmail()).isEqualTo("testuser@example.com");
    assertThat(savedMember.getStatus()).isEqualTo(EmailStatus.PENDING);
    assertThat(password).isEqualTo(savedMember.getPassword());
    assertThat(savedMember.getBagId()).isEqualTo(1);
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
        new RequestEmailVerificationCommand(userId, email));

    // Then
    String tokenKey = redisTemplate.keys("token:*").stream().findFirst().orElse(null);
    assertThat(tokenKey).isNotNull();

    String storedEmail = (String) redisTemplate.opsForHash().get(tokenKey, "email");
    assertThat(storedEmail).isEqualTo(email);
  }
}