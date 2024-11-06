package com.e205.auth.service;

import com.e205.auth.dto.MemberDetails;
import com.e205.command.member.payload.MemberAuthPayload;
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
    return new MemberDetails(payload.id(), payload.password(), payload.email());
  }
}
