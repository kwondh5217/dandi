package com.e205.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import com.e205.MemberWithFcm;
import com.e205.command.member.payload.EmailStatus;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class MemberQueryServiceDefaultTest {

  @Test
  void membersWithFcmQuery() {
    // given
    MemberRepository repository = mock(MemberRepository.class);
    List<Member> members = createMemberList();
    List<Integer> membersId = createMemberIds();
    given(repository.findMembersByIds(any())).willReturn(members);
    var memberQueryServiceDefault = new MemberQueryServiceDefault(repository);

    // when
    List<MemberWithFcm> memberWithFcms = memberQueryServiceDefault.membersWithFcmQuery(
        membersId);

    // then
    assertAll(
        () -> assertThat(memberWithFcms.size()).isEqualTo(members.size()),
        () -> assertThat(memberWithFcms).hasSize(members.size()),
        () -> IntStream.range(0, members.size()).forEach(i -> {
          Member member = members.get(i);
          MemberWithFcm memberWithFcm = memberWithFcms.get(i);

          assertThat(memberWithFcm.memberId())
              .isEqualTo(member.getId());

          assertThat(memberWithFcm.fcmToken())
              .isEqualTo(member.getFcmCode());
        })
    );
  }

  private static List<Integer> createMemberIds() {
    return IntStream.range(1, 10)
        .map(i -> i)
        .boxed()
        .toList();
  }

  private static List<Member> createMemberList() {
    return IntStream.range(1, 10)
        .mapToObj(i -> createTestMember(i, i,
            "test" + i,
            "password" + i,
            "email" + i,
            EmailStatus.VERIFIED,
            "fcm" + i)).toList();
  }

  private static Member createTestMember(Integer id, Integer bagId, String nickname,
      String password,
      String email, EmailStatus status, String fcmCode) {
    return Member.builder()
        .id(id)
        .bagId(bagId)
        .nickname(nickname)
        .password(password)
        .email(email)
        .status(status)
        .fcmCode(fcmCode)
        .build();
  }

}