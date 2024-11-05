package com.e205.command.member.service;

import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.bag.query.FindMemberQuery;
import com.e205.command.member.query.FindMemberByEmailQuery;

public interface MemberQueryService {

  MemberPayload findMember(FindMemberQuery findMemberQuery);
  MemberPayload findMemberByEmail(FindMemberByEmailQuery findMemberByEmailQuery);
}
