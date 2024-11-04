package com.e205.domain.member.service;

import com.e205.command.member.command.MemberRegistrationCommand;

public interface MemberCommandService {
  void requestEmailVerification(Integer userId, String email);
  void registerMember(MemberRegistrationCommand memberRegistrationCommand);
}
