package com.e205.command.member.service;

import com.e205.MemberWithFcm;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.bag.query.FindMemberQuery;
import com.e205.command.member.payload.MemberAuthPayload;
import com.e205.command.member.query.FindMemberByEmailQuery;
import java.util.List;

public interface MemberQueryService {

  MemberPayload findMember(FindMemberQuery findMemberQuery);

  MemberAuthPayload findMemberByEmail(FindMemberByEmailQuery findMemberByEmailQuery);

  List<MemberWithFcm> membersWithFcmQuery(List<Integer> members);

  String findMemberFcmById(Integer memberId);

  String checkPastPassword(Integer memberId);
}
