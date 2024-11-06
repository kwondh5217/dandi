package com.e205.auth.dto;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record MemberDetails(
    AuthenticationMember member
) implements UserDetails {

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getPassword() {
    return member.password();
  }

  @Override
  public String getUsername() {
    return member.email();
  }

  public Integer getId() {
    return member.id();
  }
}
