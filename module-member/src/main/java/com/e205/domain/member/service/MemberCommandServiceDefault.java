package com.e205.domain.member.service;

import com.e205.command.member.command.ChangeNicknameCommand;
import com.e205.command.member.command.ChangePasswordCommand;
import com.e205.command.member.command.ChangePasswordWithVerifNumber;
import com.e205.command.member.command.CompleteSignUpCommand;
import com.e205.command.member.command.CreateEmailTokenCommand;
import com.e205.command.member.command.DeleteMemberCommand;
import com.e205.command.member.command.RegisterMemberCommand;
import com.e205.command.member.command.MemberVerificationLinkCommand;
import com.e205.command.member.command.SendVerificationEmailCommand;
import com.e205.command.member.command.UpdateFcmCodeCommand;
import com.e205.command.member.command.VerifyEmailAndRegisterCommand;
import com.e205.command.member.payload.EmailStatus;
import com.e205.command.member.payload.MemberStatus;
import com.e205.command.member.service.MemberCommandService;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.exception.MemberError;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
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
      MemberError.EMAIL_ALREADY_USED.throwGlobalException();
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
    if (storedData.isEmpty()) {
      MemberError.VERIFICATION_TOKEN_INVALID.throwGlobalException();
    }

    String storedToken = (String) storedData.get("token");
    if (!verifyEmailAndRegisterCommand.token().equals(storedToken)) {
      MemberError.VERIFICATION_TOKEN_INVALID.throwGlobalException();
    }
    String registrationKey = "registration:" + verifyEmailAndRegisterCommand.email();
    Map<Object, Object> registrationData = redisTemplate.opsForHash().entries(registrationKey);

    if (registrationData.isEmpty()) {
      MemberError.VERIFICATION_INFO_EXPIRED.throwGlobalException();
    }
    redisTemplate.opsForHash().put(registrationKey, "authenticated", "true");
  }

  @Override
  public void changePasswordWithVerificationNumber(
      ChangePasswordWithVerifNumber changePasswordWithVerificationNumber) {
    String redisKey = "verification:" + changePasswordWithVerificationNumber.email();
    String storedVerificationNumber = redisTemplate.opsForValue().get(redisKey);

    if (storedVerificationNumber == null) {
      MemberError.VERIFICATION_EXPIRED_OR_NOT_FOUND.throwGlobalException();
    }

    if (!storedVerificationNumber.equals(
        changePasswordWithVerificationNumber.verificationNumber())) {
      MemberError.VERIFICATION_NUMBER_INVALID.throwGlobalException();
    }

    Member member = memberRepository.findByEmail(changePasswordWithVerificationNumber.email())
        .orElseThrow(MemberError.USER_NOT_FOUND::getGlobalException);

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
      MemberError.VERIFICATION_PROCESS_NOT_COMPLETE.throwGlobalException();
    }

    Map<Object, Object> registrationData = redisTemplate.opsForHash().entries(registrationKey);
    if (registrationData.isEmpty()) {
      MemberError.VERIFICATION_INFO_EXPIRED.throwGlobalException();
    }

    String password = (String) registrationData.get("password");
    String nickname = (String) registrationData.get("nickname");
    Member newMember = Member.builder()
        .password(password)
        .email(email)
        .nickname(nickname)
        .status(EmailStatus.VERIFIED)
        .memberStatus(MemberStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .build();

    Member member = memberRepository.save(newMember);
    Bag bag = Bag.builder()
        .memberId(member.getId())
        .enabled('Y')
        .bagOrder((byte) 1)
        .name("현재 가방")
        .createdAt(LocalDateTime.now())
        .build();
    bagRepository.save(bag);
    member.updateBagId(bag.getId());
    redisTemplate.delete(registrationKey);
  }

  @Override
  public void requestEmailVerification(
      MemberVerificationLinkCommand requestEmailVerificationCommand) {

    if (Boolean.FALSE.equals(redisTemplate.hasKey("verifyEmail:"))) {
      MemberError.INVALID_SIGNUP.throwGlobalException();
    }

    String token = emailService.createAndStoreToken(
        new CreateEmailTokenCommand(requestEmailVerificationCommand.email()));
    emailService.sendVerificationEmail(
        new SendVerificationEmailCommand(requestEmailVerificationCommand.email(), token));
  }

  @Override
  public void updateFcmCode(UpdateFcmCodeCommand command) {
    Member member = memberRepository.findById(command.memberId())
        .orElseThrow(MemberError.USER_NOT_FOUND::getGlobalException);
    member.updateFcmCode(command.fcmCode());
  }

  @Override
  public void changePassword(ChangePasswordCommand command) {
    Member member = memberRepository.findById(command.memberId())
        .orElseThrow(MemberError.USER_NOT_FOUND::getGlobalException);
    member.updatePassword(command.newPassword());
  }

  @Override
  public void deleteMember(DeleteMemberCommand command) {
    Member member = memberRepository.findById(command.memberId())
        .orElseThrow(MemberError.USER_NOT_FOUND::getGlobalException);
    member.updateEmail(null);
    member.updateFcmCode(null);
    member.updatePassword(null);
    member.updateMemberStatus(MemberStatus.DISABLED);
  }

  @Override
  public void changeNickname(ChangeNicknameCommand command) {
    Member member = memberRepository.findById(command.memberId())
        .orElseThrow(MemberError.USER_NOT_FOUND::getGlobalException);

    member.updateNickname(command.nickname());
  }
}
