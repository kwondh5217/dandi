package com.e205.communication;

import com.e205.MemberWithFcm;
import java.util.List;

public interface MemberQueryService {

  List<MemberWithFcm> membersWithFcmQuery(List<Integer> members);
  String findMemberFcmById(Integer memberId);


}
