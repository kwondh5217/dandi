package com.e205.command.member.query;

import java.util.List;

public record FindMembersByIdQuery(
    List<Integer> memberId
) {

}
