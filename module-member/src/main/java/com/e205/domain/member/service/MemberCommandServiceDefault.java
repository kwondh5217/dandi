package com.e205.domain.member.service;

import com.e205.command.member.command.MemberRegistrationCommand;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.member.entity.EmailStatus;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberCommandServiceDefault implements MemberCommandService {

  private final MemberRepository memberRepository;
  private final BagRepository bagRepository;
  private final EmailCommandServiceDefault emailService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void registerMember(MemberRegistrationCommand memberRegistrationCommand) {
    String encryptedPassword = passwordEncoder.encode(memberRegistrationCommand.password());
    // TODO: <홍성우> Exception 상세화
    if (memberRepository.existsByEmail(memberRegistrationCommand.email())) {
      throw new IllegalArgumentException("이미 등록된 이메일입니다.");
    }

    Member newMember = Member.builder()
        .password(encryptedPassword)
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
  public void requestEmailVerification(Integer userId, String email) {
    String token = emailService.createAndStoreToken(userId, email);
    emailService.sendVerificationEmail(email, token);
  }
}
