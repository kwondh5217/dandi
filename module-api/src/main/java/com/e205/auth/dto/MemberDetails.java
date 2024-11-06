package com.e205.auth.dto;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record MemberDetails(
    Integer id,
    String password,
    String email
) implements UserDetails {

  public MemberDetails(Integer id) {
    this(id, null, null);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  public Integer getId() {
    return id;
  }
}
