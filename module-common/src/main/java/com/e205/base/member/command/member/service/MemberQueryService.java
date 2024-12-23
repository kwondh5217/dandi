package com.e205.base.member.command.member.service;

import com.e205.base.member.command.bag.payload.MemberPayload;
import com.e205.base.member.command.bag.query.FindMemberQuery;
import com.e205.base.member.command.member.payload.MemberAuthPayload;
import com.e205.base.member.command.member.query.FindMemberByEmailQuery;
import com.e205.base.member.command.member.query.FindMembersByIdQuery;
import java.util.List;

public interface MemberQueryService {

  MemberPayload findMember(FindMemberQuery findMemberQuery);

  MemberAuthPayload findMemberByEmail(FindMemberByEmailQuery findMemberByEmailQuery);

  String findMemberFcmById(Integer memberId);

  String checkPastPassword(Integer memberId);

  List<MemberPayload> findMembers(FindMembersByIdQuery findMembersByIdQuery);
}
