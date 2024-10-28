package com.e205.communication;

import com.e205.MemberWithFcm;
import java.util.List;

public interface MemberQueryService {

  List<MemberWithFcm> memberWithFcmQuery(List<Integer> members);


}
