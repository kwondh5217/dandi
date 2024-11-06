package com.e205.config;

import com.e205.auth.jwt.JwtProvider;
import com.e205.auth.jwt.filter.JwtAuthorizationFilter;
import com.e205.auth.jwt.filter.LoginFilter;
import com.e205.auth.jwt.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

  private static final String[] AUTH_WHITELIST = {
      "/auth/**",
      "/h2-console/**",
      "/error/**"
  };

  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final AuthenticationConfiguration authenticationConfiguration;
  private final JwtProvider jwtProvider;

  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    return http
        .anonymous(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement((m) -> m.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(request -> request
            .requestMatchers(AUTH_WHITELIST).permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(
            new JwtAuthorizationFilter(jwtAuthenticationEntryPoint, jwtProvider), LoginFilter.class
        )
        .addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            (handler) -> handler.authenticationEntryPoint(jwtAuthenticationEntryPoint)
        )
        .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  private LoginFilter loginFilter() throws Exception {
    LoginFilter loginFilter = new LoginFilter(
        jwtAuthenticationEntryPoint, authenticationManager(authenticationConfiguration), jwtProvider
    );
    loginFilter.setFilterProcessesUrl("/auth/token");
    loginFilter.setPostOnly(true);
    return loginFilter;
  }
}
