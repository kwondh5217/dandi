package com.e205.domain.member.service;

public interface EmailCommandService {
  String createAndStoreToken(Integer userId, String email);
  void sendVerificationEmail(String toEmail, String token);
  void checkEmailVerificationInProgress(String email);
  String verifyToken(String token);
}
