package com.e205.domain.member.service;

import com.e205.domain.member.entity.EmailStatus;
import com.e205.domain.member.repository.MemberRepository;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class EmailCommandServiceDefault implements EmailCommandService {

  private static final String EMAIL_SUBJECT = "단디 이메일 인증";
  private static final String TEMPLATE_PATH = "templates/verification-email.html";
  private static final String TEMPLATE_PATH_VERFICATION = "templates/verification-number-email.html";
  private static final String EMAIL_CONTENT_TYPE = "text/html; charset=utf-8";
  private static final Duration TOKEN_EXPIRATION = Duration.ofMinutes(10);
  private static final Duration VERIFICATION_EXPIRATION = Duration.ofMinutes(20);
  private static final String EMAIL_SUBJECT_NUMBER = "단디 비밀번호 이메일인증번호";

  private final MemberRepository memberRepository;
  private final JavaMailSender mailSender;
  private final RedisTemplate<String, String> redisTemplate;

  @Value("${spring.mail.username}")
  private String fromEmail;
  @Value("${backend-url}")
  private String backendUrl;

  public String createAndStoreToken(Integer userId, String email) {
    // TODO: <홍성우> Exception 상세화
    String token = UUID.randomUUID().toString();
    String tokenKey = "token:" + token;
    redisTemplate.opsForHash().put(tokenKey, "userId", String.valueOf(userId));
    redisTemplate.opsForHash().put(tokenKey, "email", email);
    redisTemplate.expire(tokenKey, TOKEN_EXPIRATION);

    return token;
  }

  public void sendVerificationEmail(String toEmail, String token) {
    validateEmailFormat(toEmail);

    try {
      ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
      String content = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

      String verificationLink = backendUrl + "/verify?token=" + token;
      content = content.replace("{{verificationLink}}", verificationLink);

      MimeMessage message = mailSender.createMimeMessage();
      message.setFrom(fromEmail);
      message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toEmail));
      message.setSubject(EMAIL_SUBJECT);
      message.setContent(content, EMAIL_CONTENT_TYPE);

      mailSender.send(message);
    } catch (Exception e) {
      // TODO: <홍성우> Exception 상세화
      throw new RuntimeException("이메일 전송 실패");
    }
  }

  public void checkEmailVerificationInProgress(String email) {
    Set<String> keys = redisTemplate.keys("token:*");
    if (keys != null && !keys.isEmpty()) {
      boolean inProgress = keys.stream()
          .anyMatch(key -> {
            String storedEmail = (String) redisTemplate.opsForHash().get(key, "email");
            return email.equals(storedEmail);
          });
      if (inProgress) {
        // TODO: <홍성우> Exception 상세화
        throw new IllegalStateException("해당 이메일은 이미 인증 중입니다.");
      }
    }
  }

  public String verifyToken(String token) {
    String tokenKey = "token:" + token;
    String userId = (String) redisTemplate.opsForHash().get(tokenKey, "userId");
    String email = (String) redisTemplate.opsForHash().get(tokenKey, "email");

    if (userId != null && email != null) {
      redisTemplate.delete(tokenKey);

      memberRepository.findById(Integer.parseInt(userId)).ifPresent(member -> {
        member.updateStatus(EmailStatus.VERIFIED);
        memberRepository.save(member);
      });
      return userId;
    }
    throw new RuntimeException("유효하지 않은 인증 토큰입니다.");
  }

  @Override
  public void createAndStoreVerificationNumber(String email) {
    String verificationNumber = generateVerificationNumber();

    String redisKey = "verification:" + email;
    redisTemplate.opsForValue().set(redisKey, verificationNumber, VERIFICATION_EXPIRATION);
    sendVerificationNumberEmail(email, verificationNumber);
  }

  @Override
  public void checkVerificationNumber(String email, String verificationNumber) {
    String redisKey = "verification:" + email;
    String storedVerificationNumber = redisTemplate.opsForValue().get(redisKey);

    // TODO: <홍성우> Exception 상세화
    if (storedVerificationNumber == null) {
      throw new IllegalArgumentException("인증 번호가 만료되었거나 존재하지 않습니다.");
    }

    if (!storedVerificationNumber.equals(verificationNumber)) {
      throw new IllegalArgumentException("인증 번호가 올바르지 않습니다.");
    }
  }

  private String generateVerificationNumber() {
    Random random = new Random();
    int number = 100000 + random.nextInt(900000);
    return String.valueOf(number);
  }

  private void sendVerificationNumberEmail(String toEmail, String verificationNumber) {
    try {
      ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH_VERFICATION);
      String content = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

      // 인증번호 치환
      content = content.replace("{{verificationNumber}}", verificationNumber);

      MimeMessage message = mailSender.createMimeMessage();
      message.setFrom(fromEmail);
      message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toEmail));
      message.setSubject(EMAIL_SUBJECT_NUMBER);
      message.setContent(content, EMAIL_CONTENT_TYPE);

      mailSender.send(message);
    } catch (Exception e) {
      // TODO: <홍성우> Exception 상세화
      throw new RuntimeException("이메일 전송 실패");
    }
  }

  private void validateEmailFormat(String email) {
    String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    Pattern pattern = Pattern.compile(emailRegex);
    // TODO: <홍성우> Exception 상세화
    if (!pattern.matcher(email).matches()) {
      throw new RuntimeException("유효하지 않은 이메일 형식입니다");
    }
  }
}
