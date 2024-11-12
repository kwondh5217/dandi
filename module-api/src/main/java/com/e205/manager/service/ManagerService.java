package com.e205.manager.service;

import com.e205.auth.jwt.JwtProvider;
import com.e205.command.RouteDummyCreateCommand;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.member.command.CreateManagerCommand;
import com.e205.command.member.service.MemberManagerService;
import com.e205.dto.TrackPoint;
import com.e205.manager.dto.RouteDummyCreateRequest;
import com.e205.service.RouteDummyCommandService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ManagerService {

  private final RouteDummyCommandService commandService;
  private final MemberManagerService managerService;
  private final JwtProvider jwtProvider;

  public String createManagerAccount(String nickname) {
    CreateManagerCommand comm = new CreateManagerCommand(nickname);
    Integer memberId = managerService.createManager(comm);
    return jwtProvider.generateAccessToken(memberId);
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
