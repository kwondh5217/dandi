package com.e205;

import java.time.LocalDateTime;

public record NotifiedMembersCommand(Integer memberId, LocalDateTime createdAt,
                                     String type) {

}
