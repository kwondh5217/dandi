package com.e205.domain.member.service;

import com.e205.base.member.command.member.command.CreateManagerCommand;
import com.e205.base.member.command.member.payload.EmailStatus;
import com.e205.base.member.command.member.payload.MemberStatus;
import com.e205.base.member.command.member.service.MemberManagerService;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultMemberManagerService implements MemberManagerService {

  private final MemberRepository memberRepository;
  private final BagRepository bagRepository;

  @Override
  public Integer createManager(CreateManagerCommand command) {
    return memberRepository.findByEmail(command.nickname() + "@example.com")
        .map(Member::getId)
        .orElseGet(() -> {
          Member manager = createManagerMember(command);
          memberRepository.save(manager);
          Bag managerBag = createManagerBag(manager);
          bagRepository.save(managerBag);
          manager.updateBagId(managerBag.getId());
          return memberRepository.save(manager).getId();
        });
  }

  private Member createManagerMember(CreateManagerCommand command) {
    return Member.builder()
        .bagId(null)
        .nickname(command.nickname())
        .password("password")
        .email(command.nickname() + "@example.com")
        .status(EmailStatus.VERIFIED)
        .memberStatus(MemberStatus.ACTIVE)
        .commentAlarm(true)
        .foundItemAlarm(true)
        .lostItemAlarm(true)
        .createdAt(LocalDateTime.now())
        .build();
  }

  private Bag createManagerBag(Member member) {
    return Bag.builder()
        .memberId(member.getId())
        .enabled('Y')
        .bagOrder((byte) 1)
        .name("현재 가방")
        .build();
  }
}
