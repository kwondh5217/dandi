package com.e205.domain.member.service;

import com.e205.command.member.command.CreateManagerCommand;
import com.e205.command.member.payload.EmailStatus;
import com.e205.command.member.payload.MemberStatus;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberManagerService implements com.e205.command.member.service.MemberManagerService {

  private final MemberRepository memberRepository;

  @Override
  public Integer createManager(CreateManagerCommand command) {
    return memberRepository.findByEmail(command.nickname() + "@example.com")
        .map(Member::getId)
        .orElseGet(() -> {
          Member member = Member.builder()
              .bagId(null)
              .nickname(command.nickname())
              .password("manager")
              .email(command.nickname() + "@example.com")
              .status(EmailStatus.VERIFIED)
              .memberStatus(MemberStatus.ACTIVE)
              .createdAt(LocalDateTime.now())
              .build();

          memberRepository.save(member);
          return member.getId();
        });
  }
}
