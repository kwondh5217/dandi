package com.e205.domain.member.service;

import com.e205.base.member.command.bag.payload.MemberPayload;
import com.e205.base.member.command.bag.query.FindMemberQuery;
import com.e205.base.member.command.member.payload.MemberAuthPayload;
import com.e205.base.member.command.member.query.FindMemberByEmailQuery;
import com.e205.base.member.command.member.query.FindMembersByIdQuery;
import com.e205.base.member.command.member.service.MemberQueryService;
import com.e205.domain.exception.MemberError;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberQueryServiceDefault implements MemberQueryService {

  private final MemberRepository memberRepository;

  @Override
  public MemberPayload findMember(FindMemberQuery findMemberQuery) {
    return memberRepository.findById(findMemberQuery.memberId())
        .orElseThrow(MemberError.USER_NOT_FOUND::getGlobalException)
        .toPayload();
  }

  @Override
  public MemberAuthPayload findMemberByEmail(
      FindMemberByEmailQuery findMemberByEmailQuery) {
    return memberRepository.findByEmail(findMemberByEmailQuery.email())
        .orElseThrow(MemberError.USER_NOT_FOUND::getGlobalException)
        .toAuthPayload();
  }

  @Override
  public String findMemberFcmById(Integer memberId) {
    Member member = this.memberRepository.findById(memberId)
        .orElseThrow(MemberError.USER_NOT_FOUND::getGlobalException);
    return member.getFcmToken();
  }

  @Override
  public String checkPastPassword(Integer memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(MemberError.USER_NOT_FOUND::getGlobalException);
    return member.getPassword();
  }

  @Override
  public List<MemberPayload> findMembers(FindMembersByIdQuery findMembersByIdQuery) {
    List<Integer> memberIds = findMembersByIdQuery.memberId();

    return memberRepository.findAllById(memberIds)
        .stream()
        .map(Member::toPayload)
        .toList();
  }
}
