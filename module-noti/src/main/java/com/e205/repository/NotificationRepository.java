package com.e205.repository;

import com.e205.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

  @Query("SELECT n FROM Notification n WHERE n.memberId = :memberId " +
      "AND n.id > :lastResourceId " +
      "AND (TYPE(n) IN :types) " +
      "ORDER BY n.id ASC")
  List<Notification> findByMemberIdWithCursor(
      @Param("memberId") Integer memberId,
      @Param("lastResourceId") Integer lastResourceId,
      @Param("types") List<Class<? extends Notification>> types,
      Pageable pageable);
}
