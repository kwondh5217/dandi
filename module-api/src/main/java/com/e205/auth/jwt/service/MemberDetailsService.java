package com.e205.auth.jwt.service;

import com.e205.auth.dto.MemberDetails;
import com.e205.command.member.payload.MemberAuthPayload;
import com.e205.command.member.payload.MemberStatus;
import com.e205.command.member.query.FindMemberByEmailQuery;
import com.e205.command.member.service.MemberQueryService;
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
    MemberAuthPayload payload = memberQueryService.findMemberByEmail(query);
    // TODO: <홍성우> DISALBED 된사용자는 로그인 불가
    return new MemberDetails(payload.id(), payload.password(), payload.email());
  }
}
