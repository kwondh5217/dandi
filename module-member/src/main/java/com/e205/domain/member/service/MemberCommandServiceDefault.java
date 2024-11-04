package com.e205.domain.member.service;

import com.e205.command.member.command.MemberRegistrationCommand;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.member.entity.EmailStatus;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberCommandServiceDefault implements MemberCommandService {

  private final MemberRepository memberRepository;
  private final BagRepository bagRepository;
  private final EmailCommandServiceDefault emailService;
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void registerMember(MemberRegistrationCommand memberRegistrationCommand) {
    // TODO: <홍성우> Exception 상세화
    if (memberRepository.existsByEmail(memberRegistrationCommand.email())) {
      throw new IllegalArgumentException("이미 등록된 이메일입니다.");
    }

    Member newMember = Member.builder()
        .password(memberRegistrationCommand.password())
        .email(memberRegistrationCommand.email())
        .nickname(memberRegistrationCommand.nickname())
        .status(EmailStatus.PENDING)
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
    requestEmailVerification(member.getId(), member.getEmail());
  }

  @Override
  public void changePasswordWithVerificationNumber(String email, String verificationNumber, String newPassword) {
    String redisKey = "verification:" + email;
    String storedVerificationNumber = redisTemplate.opsForValue().get(redisKey);

    if (storedVerificationNumber == null) {
      throw new IllegalArgumentException("인증 번호가 만료되었거나 존재하지 않습니다.");
    }

    if (!storedVerificationNumber.equals(verificationNumber)) {
      throw new IllegalArgumentException("인증 번호가 올바르지 않습니다.");
    }

    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 존재하지 않습니다."));

    member.updatePassword(newPassword);

    redisTemplate.delete(redisKey);
  }

  @Override
  public void requestEmailVerification(Integer userId, String email) {
    String token = emailService.createAndStoreToken(userId, email);
    emailService.sendVerificationEmail(email, token);
  }
}
