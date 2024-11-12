package com.e205.member.service;

import com.e205.command.member.payload.MemberStatus;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberTempService {

  private final MemberRepository memberRepository;
  private final BagRepository bagRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public Member generateTempMember(String nickname) {
    Optional<Member> existingMember = memberRepository.findByEmail(nickname + "@example.com");

    if (existingMember.isPresent()) {
      return existingMember.get();
    }

    String password = passwordEncoder.encode("password");
    Member tempMember = Member.builder()
        .nickname(nickname)
        .password(password)
        .email(nickname + "@example.com")
        .status(com.e205.command.member.payload.EmailStatus.VERIFIED)
        .memberStatus(MemberStatus.ACTIVE)
        .lostItemAlarm(true)
        .foundItemAlarm(true)
        .commentAlarm(true)
        .build();

    Member savedMember = memberRepository.save(tempMember);

    Bag tempBag = Bag.builder()
        .memberId(savedMember.getId())
        .enabled('Y')
        .bagOrder((byte) 1)
        .name("현재 가방")
        .build();
    bagRepository.save(tempBag);
    savedMember.updateBagId(tempBag.getId());
    return savedMember;
  }
}
