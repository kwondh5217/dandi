package com.e205.repository;

import com.e205.entity.Notification;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

  @Query("SELECT n FROM Notification n " +
         "WHERE n.memberId = :memberId " +
         "AND n.id > :lastResourceId " +
         "AND (TYPE(n) IN :types) " +
         "ORDER BY n.id ASC")
  List<Notification> findByMemberIdWithCursor(
      @Param("memberId") Integer memberId,
      @Param("lastResourceId") Integer lastResourceId,
      @Param("types") List<Class<? extends Notification>> types,
      Pageable pageable);

  @Modifying
  @Query("DELETE FROM Notification n WHERE n.memberId = :memberId AND n.id IN :notificationIds")
  void deleteAllByIdAndMemberId(@Param("memberId") Integer memberId,
      @Param("notificationIds") List<Integer> notificationIds);
}
