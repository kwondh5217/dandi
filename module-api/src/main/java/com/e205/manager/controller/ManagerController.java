package com.e205.manager.controller;

import com.e205.manager.dto.RouteDummyCreateRequest;
import com.e205.manager.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/manager")
@RestController
public class ManagerController {

  private final ManagerService managerService;

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.CREATED)
  public String login(@RequestParam("nickname") String nickname) {
    return managerService.createManagerAccount(nickname);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/route")
  public void createDummyRoute(@RequestBody RouteDummyCreateRequest request) {
    managerService.createRouteDummy(request);
  }
}
