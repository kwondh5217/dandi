package com.e205.domain.member.service;

import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberQueryServiceDefault implements MemberQueryService {

  private final MemberRepository memberRepository;

  @Override
  public Member findMember(Integer memberId) {
    // TODO: <홍성우> Exception 상세화
    return memberRepository.findById(memberId)
        .orElseThrow(RuntimeException::new);
  }
}
