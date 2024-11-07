package com.e205.member.controller;

import com.e205.member.service.RecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/recovery")
@RestController
public class EventRecoveryController {

  private final RecoveryService recoveryService;

  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{eventId}")
  public void recovery(@PathVariable String eventId) {
    this.recoveryService.recovery(eventId);
  }
}
