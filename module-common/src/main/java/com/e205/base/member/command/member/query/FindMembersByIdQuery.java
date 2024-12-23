package com.e205.base.member.command.member.query;

import java.util.List;

public record FindMembersByIdQuery(
    List<Integer> memberId
) {

}
