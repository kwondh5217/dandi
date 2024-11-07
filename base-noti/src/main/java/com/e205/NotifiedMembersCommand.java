package com.e205;

import java.time.LocalDateTime;

public record NotifiedMembersCommand(Integer memberId, Integer resourceId,
                                     LocalDateTime createdAt, String type) {

}
