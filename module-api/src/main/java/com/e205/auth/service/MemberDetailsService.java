package com.e205.auth.service;

import com.e205.auth.dto.MemberDetails;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.member.query.FindMemberByEmailQuery;
import com.e205.command.member.service.MemberQueryService;
import com.e205.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberDetailsService implements UserDetailsService {

  private final MemberQueryService memberQueryService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    FindMemberByEmailQuery query = new FindMemberByEmailQuery(username);
    MemberPayload payload = memberQueryService.findMemberByEmail(query);

    Member member = Member.builder()
        .id(payload.id())
        .bagId(payload.bagId())
        .email(payload.email())
        .nickname(payload.nickname())
        .status(payload.status())
        .build();

    return new MemberDetails(member);
  }
}
