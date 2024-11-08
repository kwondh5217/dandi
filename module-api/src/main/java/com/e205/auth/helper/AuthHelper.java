package com.e205.auth.helper;

import com.e205.auth.dto.MemberDetails;
import com.e205.exception.GlobalException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthHelper {

  public Integer getMemberId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      throw new GlobalException("E001");
    }

    MemberDetails details = (MemberDetails) authentication.getPrincipal();
    return details.getId();
  }
}
