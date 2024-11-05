package com.e205.domain.member.service;

import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.bag.query.FindMemberQuery;
import com.e205.command.member.query.FindMemberByEmailQuery;
import com.e205.command.member.service.MemberQueryService;
import com.e205.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberQueryServiceDefault implements MemberQueryService {

  private final MemberRepository memberRepository;

  @Override
  public MemberPayload findMember(FindMemberQuery findMemberQuery) {
    // TODO: <홍성우> Exception 상세화
    return memberRepository.findById(findMemberQuery.memberId())
        .orElseThrow(RuntimeException::new)
        .toPayload();
  }

  @Override
  public MemberPayload findMemberByEmail(FindMemberByEmailQuery findMemberByEmailQuery) {
    return memberRepository.findByEmail(findMemberByEmailQuery.email())
        .orElseThrow(RuntimeException::new)
        .toPayload();
  }
}
