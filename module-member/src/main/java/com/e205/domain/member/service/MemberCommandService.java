package com.e205.domain.member.service;

public interface MemberCommandService {
  void requestEmailVerification(Integer userId, String email);
}
