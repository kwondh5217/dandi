package com.e205.domain.member.service;

import com.e205.command.member.command.ChangePasswordCommand;
import com.e205.command.member.command.ChangePasswordWithVerifNumber;
import com.e205.command.member.command.CompleteSignUpCommand;
import com.e205.command.member.command.CreateEmailTokenCommand;
import com.e205.command.member.command.DeleteMemberCommand;
import com.e205.command.member.command.RegisterMemberCommand;
import com.e205.command.member.command.RequestEmailVerificationCommand;
import com.e205.command.member.command.SendVerificationEmailCommand;
import com.e205.command.member.command.UpdateFcmCodeCommand;
import com.e205.command.member.command.VerifyEmailAndRegisterCommand;
import com.e205.command.member.payload.EmailStatus;
import com.e205.command.member.payload.MemberStatus;
import com.e205.command.member.service.MemberCommandService;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberCommandServiceDefault implements MemberCommandService {

  private static final Duration TOKEN_EXPIRATION = Duration.ofMinutes(20);
  private final MemberRepository memberRepository;
  private final BagRepository bagRepository;
  private final EmailCommandServiceDefault emailService;
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void registerMember(RegisterMemberCommand memberRegistrationCommand) {
    if (memberRepository.existsByEmail(memberRegistrationCommand.email())) {
      throw new IllegalArgumentException("이미 등록된 이메일입니다.");
    }

    String redisKey = "registration:" + memberRegistrationCommand.email();
    redisTemplate.opsForHash().put(redisKey, "password", memberRegistrationCommand.password());
    redisTemplate.opsForHash().put(redisKey, "email", memberRegistrationCommand.email());
    redisTemplate.opsForHash().put(redisKey, "nickname", memberRegistrationCommand.nickname());
    redisTemplate.opsForHash().put(redisKey, "authenticated", "false");

    redisTemplate.expire(redisKey, TOKEN_EXPIRATION);

    // 인증 이메일 발송을 위한 토큰 생성 및 발송
    String token = emailService.createAndStoreToken(
        new CreateEmailTokenCommand(memberRegistrationCommand.email()));
    emailService.sendVerificationEmail(
        new SendVerificationEmailCommand(memberRegistrationCommand.email(), token));
  }

  @Override
  public void verifyEmailAndCompleteRegistration(
      VerifyEmailAndRegisterCommand verifyEmailAndRegisterCommand) {
    String redisKey = "verifyEmail:" + verifyEmailAndRegisterCommand.email();
    Map<Object, Object> storedData = redisTemplate.opsForHash().entries(redisKey);
    // 저장된 데이터가 없는 경우 예외 처리
    if (storedData.isEmpty()) {
      throw new IllegalArgumentException("유효하지 않은 또는 만료된 토큰입니다.");
    }
    // 저장된 토큰 가져와서 요청 토큰과 비교
    String storedToken = (String) storedData.get("token");
    if (!verifyEmailAndRegisterCommand.token().equals(storedToken)) {
      throw new IllegalArgumentException("토큰이 일치하지 않습니다.");
    }
    String registrationKey = "registration:" + verifyEmailAndRegisterCommand.email();
    Map<Object, Object> registrationData = redisTemplate.opsForHash().entries(registrationKey);

    if (registrationData.isEmpty()) {
      throw new IllegalArgumentException("회원가입 정보가 만료되었습니다.");
    }
    redisTemplate.opsForHash().put(registrationKey, "authenticated", "true");
  }

  @Override
  public void changePasswordWithVerificationNumber(
      ChangePasswordWithVerifNumber changePasswordWithVerificationNumber) {
    String redisKey = "verification:" + changePasswordWithVerificationNumber.email();
    String storedVerificationNumber = redisTemplate.opsForValue().get(redisKey);

    if (storedVerificationNumber == null) {
      throw new IllegalArgumentException("인증 번호가 만료되었거나 존재하지 않습니다.");
    }

    if (!storedVerificationNumber.equals(
        changePasswordWithVerificationNumber.verificationNumber())) {
      throw new IllegalArgumentException("인증 번호가 올바르지 않습니다.");
    }

    Member member = memberRepository.findByEmail(changePasswordWithVerificationNumber.email())
        .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 존재하지 않습니다."));

    member.updatePassword(changePasswordWithVerificationNumber.newPassword());

    redisTemplate.delete(redisKey);
  }

  @Override
  public void completeSignUp(CompleteSignUpCommand command) {
    String email = command.email();
    String registrationKey = "registration:" + email;

    // Redis에서 인증 여부 확인
    String isAuthenticated = (String) redisTemplate.opsForHash()
        .get(registrationKey, "authenticated");
    if (isAuthenticated == null || !isAuthenticated.equals("true")) {
      throw new IllegalStateException("사용자가 이메일 인증을 완료하지 않았습니다.");
    }

    Map<Object, Object> registrationData = redisTemplate.opsForHash().entries(registrationKey);
    if (registrationData.isEmpty()) {
      throw new IllegalArgumentException("회원가입 정보가 만료되었습니다.");
    }

    String password = (String) registrationData.get("password");
    String nickname = (String) registrationData.get("nickname");
    Member newMember = Member.builder()
        .password(password)
        .email(email)
        .nickname(nickname)
        .status(EmailStatus.VERIFIED)
        .memberStatus(MemberStatus.ACTIVE)
        .build();

    Member member = memberRepository.save(newMember);
    Bag bag = Bag.builder()
        .memberId(member.getId())
        .enabled('Y')
        .bagOrder((byte) 1)
        .name("기본가방")
        .build();
    bagRepository.save(bag);
    member.updateBagId(bag.getId());
    redisTemplate.delete(registrationKey);
  }

  @Override
  public void requestEmailVerification(
      RequestEmailVerificationCommand requestEmailVerificationCommand) {
    String token = emailService.createAndStoreToken(
        new CreateEmailTokenCommand(requestEmailVerificationCommand.email()));
    emailService.sendVerificationEmail(
        new SendVerificationEmailCommand(requestEmailVerificationCommand.email(), token));
  }

  @Override
  public void updateFcmCode(UpdateFcmCodeCommand command) {
    Member member = memberRepository.findById(command.memberId())
        .orElseThrow(RuntimeException::new);
    member.updateFcmCode(command.fcmCode());
  }

  @Override
  public void changePassword(ChangePasswordCommand command) {
    Member member = memberRepository.findById(command.memberId())
        .orElseThrow(RuntimeException::new);
    member.updatePassword(command.newPassword());
  }

  @Override
  public void deleteMember(DeleteMemberCommand command) {
    Member member = memberRepository.findById(command.memberId())
        .orElseThrow(RuntimeException::new);
    member.updateEmail(null);
    member.updateFcmCode(null);
    member.updatePassword(null);
    member.updateMemberStatus(MemberStatus.DISABLED);
  }
}
