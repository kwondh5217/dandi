package com.e205.communication.direct;

import com.e205.MemberWithFcm;
import com.e205.communication.MemberQueryService;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DirectMemberQueryService implements MemberQueryService {

  @Override
  public List<MemberWithFcm> membersWithFcmQuery(List<Integer> members) {
    return List.of();
  }

  @Override
  public String findMemberFcmById(Integer memberId) {
    return "0";
  }
}
