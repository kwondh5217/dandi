package com.e205.domain.member.service;

import com.e205.command.bag.payload.EmailStatus;
import com.e205.command.member.command.CheckEmailProgressCommand;
import com.e205.command.member.command.CheckVerificationNumberCommand;
import com.e205.command.member.command.CreateEmailTokenCommand;
import com.e205.command.member.command.CreateVerificationNumberCommand;
import com.e205.command.member.command.SendVerificationEmailCommand;
import com.e205.command.member.command.VerifyEmailToken;
import com.e205.command.member.service.EmailCommandService;
import com.e205.domain.member.repository.MemberRepository;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
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
  private static final Duration TOKEN_EXPIRATION = Duration.ofMinutes(15);
  private static final Duration VERIFICATION_EXPIRATION = Duration.ofMinutes(20);
  private static final String EMAIL_SUBJECT_NUMBER = "단디 비밀번호 이메일인증번호";

  private final JavaMailSender mailSender;
  private final RedisTemplate<String, String> redisTemplate;

  @Value("${spring.mail.username}")
  private String fromEmail;
  @Value("${backend-url}")
  private String backendUrl;

  @Override
  public String createAndStoreToken(CreateEmailTokenCommand createEmailTokenCommand) {
    // 고유한 토큰 생성
    String token = UUID.randomUUID().toString();
    String redisKey = "verifyEmail:" + createEmailTokenCommand.email();
    redisTemplate.opsForHash().put(redisKey, "token", token);
    redisTemplate.expire(redisKey, TOKEN_EXPIRATION);

    return token;
  }

  @Override
  public void sendVerificationEmail(SendVerificationEmailCommand sendVerificationEmailCommand) {
    validateEmailFormat(sendVerificationEmailCommand.toEmail());
    try {
      ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
      String content = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

      String verificationLink = backendUrl + "/auth/verify?email="
          + URLEncoder.encode(sendVerificationEmailCommand.toEmail(), StandardCharsets.UTF_8)
          + "&token=" + sendVerificationEmailCommand.token();
      content = content.replace("{{verificationLink}}", verificationLink);

      MimeMessage message = mailSender.createMimeMessage();
      message.setFrom(fromEmail);
      message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(sendVerificationEmailCommand.toEmail()));
      message.setSubject(EMAIL_SUBJECT);
      message.setContent(content, EMAIL_CONTENT_TYPE);

      mailSender.send(message);
    } catch (Exception e) {
      // TODO: <홍성우> Exception 상세화
      throw new RuntimeException("이메일 전송 실패");
    }
  }

  @Override
  public void createAndStoreVerificationNumber(
      CreateVerificationNumberCommand createVerificationNumberCommand) {
    String verificationNumber = generateVerificationNumber();

    String redisKey = "verification:" + createVerificationNumberCommand.email();
    redisTemplate.opsForValue().set(redisKey, verificationNumber, VERIFICATION_EXPIRATION);
    sendVerificationNumberEmail(createVerificationNumberCommand.email(), verificationNumber);
  }

  @Override
  public void checkVerificationNumber(CheckVerificationNumberCommand checkVerificationNumberCommand) {
    String redisKey = "verification:" + checkVerificationNumberCommand.email();
    String storedVerificationNumber = redisTemplate.opsForValue().get(redisKey);

    // TODO: <홍성우> Exception 상세화
    if (storedVerificationNumber == null) {
      throw new IllegalArgumentException("인증 번호가 만료되었거나 존재하지 않습니다.");
    }

    if (!storedVerificationNumber.equals(checkVerificationNumberCommand.verificationNumber())) {
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
