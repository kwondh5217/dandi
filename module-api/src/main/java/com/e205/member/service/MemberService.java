package com.e205.member.service;

import com.e205.command.FoundItemSaveCommand;
import com.e205.command.member.command.RegisterMemberCommand;
import com.e205.command.member.service.EmailCommandService;
import com.e205.command.member.service.MemberCommandService;
import com.e205.command.member.service.MemberQueryService;
import com.e205.item.dto.FoundItemCreateRequest;
import com.e205.member.dto.CreateMemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final EmailCommandService emailCommandService;
  private final MemberCommandService memberCommandService;
  private final MemberQueryService memberQueryService;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public void saveMember(CreateMemberRequest request) {
    // TODO: <홍성우> 암호화
    String encryptedPassword = passwordEncoder.encode(request.password());
    RegisterMemberCommand command = request.toCommand(encryptedPassword);
    memberCommandService.registerMember(command);
  }
}
