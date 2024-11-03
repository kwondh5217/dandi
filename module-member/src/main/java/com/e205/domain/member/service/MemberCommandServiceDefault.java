package com.e205.domain.member.service;

import com.e205.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberCommandServiceDefault implements MemberCommandService {

  private final MemberRepository memberRepository;
  private final EmailCommandServiceDefault emailService;

  public void requestEmailVerification(Integer userId, String email) {
    // TODO: <홍성우> Exception 상세화
    if (memberRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("이미 등록된 이메일입니다.");
    }

    emailService.checkEmailVerificationInProgress(email);
    String token = emailService.createAndStoreToken(userId, email);
    emailService.sendVerificationEmail(email, token);
  }
}
