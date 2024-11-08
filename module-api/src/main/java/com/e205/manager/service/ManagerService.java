package com.e205.manager.service;

import com.e205.auth.jwt.JwtProvider;
import com.e205.command.RouteDummyCreateCommand;
import com.e205.command.member.command.CreateManagerCommand;
import com.e205.command.member.service.MemberManagerService;
import com.e205.dto.TrackPoint;
import com.e205.manager.dto.RouteDummyCreateRequest;
import com.e205.service.RouteDummyCommandService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ManagerService {

  private final RouteDummyCommandService commandService;
  private final MemberManagerService managerService;
  private final JwtProvider jwtProvider;

  public String createManagerAccount(String nickname) {
    CreateManagerCommand comm = new CreateManagerCommand(nickname);
    Integer id = managerService.createManager(comm);
    return jwtProvider.generateAccessToken(id);
  }

  public void createRouteDummy(RouteDummyCreateRequest request) {
    Integer id = jwtProvider.getMemberId(request.token());
    List<TrackPoint> track = request.track();
    RouteDummyCreateCommand comm = RouteDummyCreateCommand.toCommand(id, track, getSnapshot());
    commandService.createRouteDummy(comm);
  }

  private static String getSnapshot() {
    return
        """
            {
                "bagId": 1,
                "items": [
                    {"name": "지갑", "emoticon": "👛", "type": 1, "isChecked": true},
                    {"name": "반지", "emoticon": "💍", "type": 1, "isChecked": true},
                    {"name": "파우치", "emoticon": "👜", "type": 1, "isChecked": true},
                    {"name": "카드", "emoticon": "💳", "type": 1, "isChecked": true}
                ]
            }
            """;
  }
}
