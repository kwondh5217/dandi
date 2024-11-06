package com.e205.auth.helper;

import com.e205.auth.dto.MemberDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthHelper {

  public Integer getMemberId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      throw new RuntimeException("인증 정보를 찾을 수 없습니다.");
    }

    MemberDetails details = (MemberDetails) authentication.getPrincipal();
    return details.getId();
  }
}
