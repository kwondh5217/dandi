package com.e205.domain.member.service;

import com.e205.MemberWithFcm;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.bag.query.FindMemberQuery;
import com.e205.command.member.payload.MemberAuthPayload;
import com.e205.command.member.query.FindMemberByEmailQuery;
import com.e205.command.member.service.MemberQueryService;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
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
  public MemberAuthPayload findMemberByEmail(
      FindMemberByEmailQuery findMemberByEmailQuery) {
    return memberRepository.findByEmail(findMemberByEmailQuery.email())
        .orElseThrow(RuntimeException::new)
        .toAuthPayload();
  }

  @Override
  public List<MemberWithFcm> membersWithFcmQuery(List<Integer> members) {
    List<Member> membersByIds = this.memberRepository.findMembersByIds(members);
    return membersByIds.stream()
        .map(member -> new MemberWithFcm(member.getId(), member.getFcmCode()))
        .collect(Collectors.toList());
  }

  @Override
  public String findMemberFcmById(Integer memberId) {
    Member member = this.memberRepository.findById(memberId)
        .orElseThrow(RuntimeException::new);
    return member.getFcmCode();
  }
}
