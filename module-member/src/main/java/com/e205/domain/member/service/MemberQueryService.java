package com.e205.domain.member.service;

import com.e205.domain.member.entity.Member;

public interface MemberQueryService {

  Member findMember(Integer memberId);
}
