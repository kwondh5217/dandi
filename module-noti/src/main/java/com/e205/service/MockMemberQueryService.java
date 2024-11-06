package com.e205.service;

import com.e205.MemberWithFcm;
import com.e205.communication.MemberQueryService;
import java.util.List;

public class MockMemberQueryService implements MemberQueryService {

  @Override
  public List<MemberWithFcm> membersWithFcmQuery(List<Integer> members) {
    return List.of();
  }

  @Override
  public String findMemberFcmById(Integer memberId) {
    return "";
  }
}
