package com.e205.manager.service;

import com.e205.auth.jwt.JwtProvider;
import com.e205.command.RouteDummyCreateCommand;
import com.e205.command.member.command.CreateManagerCommand;
import com.e205.command.member.service.MemberManagerService;
import com.e205.dto.TrackPoint;
import com.e205.geo.dto.Point;
import com.e205.geo.service.VwolrdGeoClient;
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
  private final VwolrdGeoClient geoClient;
  private final JwtProvider jwtProvider;

  public String createManagerAccount(String nickname) {
    CreateManagerCommand comm = new CreateManagerCommand(nickname);
    Integer memberId = managerService.createManager(comm);
    return jwtProvider.generateAccessToken(memberId);
  }

  public void createRouteDummy(RouteDummyCreateRequest request) {
    Integer id = jwtProvider.getMemberId(request.token());
    List<TrackPoint> track = request.track();
    String startAddress = generateFullAddress(track.get(0));
    String endAddress = generateFullAddress(track.get(track.size() - 1));
    RouteDummyCreateCommand comm = RouteDummyCreateCommand.toCommand(
        id, track, request.snapshot(), startAddress, endAddress
    );
    commandService.createRouteDummy(comm);
  }

  private String generateFullAddress(TrackPoint point) {
    double lat = point.lat();
    double lon = point.lon();
    return geoClient.findFullAddress(new Point(lat, lon));
  }
}
