package com.e205.domain.member.service;

import com.e205.command.bag.payload.EmailStatus;
import com.e205.command.member.command.ChangePasswordWithVerifNumber;
import com.e205.command.member.command.RequestEmailVerificationCommand;
import com.e205.command.member.command.VerifyEmailAndRegisterCommand;
import com.e205.command.member.service.MemberCommandService;
import com.e205.command.member.command.RegisterMemberCommand;
import com.e205.domain.bag.entity.Bag;
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

  @DisplayName("이메일 인증 후 회원 가입 완료 테스트")
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

    // 변경된 형식으로 토큰 저장
    String tokenKey = "verifyEmail:" + email;
    redisTemplate.opsForHash().put(tokenKey, "token", token);

    // When
    memberCommandService.verifyEmailAndCompleteRegistration(
        new VerifyEmailAndRegisterCommand(email, token));

    // Then
    // 1. 회원 정보가 DB에 저장되었는지 확인
    Member registeredMember = memberRepository.findByEmail(email).orElse(null);
    assertThat(registeredMember).isNotNull();
    assertThat(registeredMember.getEmail()).isEqualTo(email);
    assertThat(registeredMember.getPassword()).isEqualTo(password);
    assertThat(registeredMember.getNickname()).isEqualTo(nickname);
    assertThat(registeredMember.getStatus()).isEqualTo(EmailStatus.VERIFIED);

    // 2. 기본 가방이 생성되었는지 확인
    Bag bag = bagRepository.findById(1).orElse(null);
    assertThat(bag).isNotNull();
    assertThat(bag.getMemberId()).isEqualTo(registeredMember.getId());
    assertThat(bag.getName()).isEqualTo("기본가방");
    assertThat(bag.getEnabled()).isEqualTo('Y');

    // 3. Redis에서 회원가입 정보와 토큰이 삭제되었는지 확인
    assertThat(redisTemplate.hasKey(registrationKey)).isFalse();
    assertThat(redisTemplate.hasKey(tokenKey)).isFalse();
  }
}